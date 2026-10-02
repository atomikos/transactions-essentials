/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import java.lang.reflect.Proxy;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.XAConnection;

import com.atomikos.datasource.pool.AbstractXPooledConnection;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class AtomikosPooledJmsConnection extends AbstractXPooledConnection<Connection>
        implements SessionHandleStateChangeListener {
    private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosPooledJmsConnection.class);

    private XAConnection xaConnection;
    private XATransactionalResource jmsTransactionalResource;
    private Connection currentProxy;
    private ConnectionPoolProperties props;
    private boolean erroneous;
    private volatile ExceptionListener applicationExceptionListener;

    // cf case 230921 (ADR-084): termination is single-shot per borrow. Claimed in
    // onTerminated() and reset in doCreateConnectionProxy() when a new borrow begins,
    // both under this connection's monitor (guarded by 'this'). Guards against concurrent
    // teardown (deadlock) and a second availability announcement of an already-reissued
    // connection (double-borrow).
    private boolean terminating = false;

    private int sessionCreationMode;

    public AtomikosPooledJmsConnection(int sessionCreationMode, XAConnection xac,
            XATransactionalResource jmsTransactionalResource, ConnectionPoolProperties props) {
        super(props);
        this.jmsTransactionalResource = jmsTransactionalResource;
        this.xaConnection = xac;
        this.props = props;
        this.erroneous = false;
        this.sessionCreationMode = sessionCreationMode;
        registerBrokenConnectionListener(); // cf case 230781
    }

    // cf case 230781 (support case 230664): observe a break signalled ONLY asynchronously via
    // ExceptionListener.onException (e.g. IBM MQ MQRC_CONNECTION_BROKEN). Without this the break
    // is never seen - no synchronous JMSException reaches the proxy - so the broken connection is
    // handed back out of the pool and reused. Any application listener (set via the connection
    // proxy) is chained UNDER this one, so both observe the break and neither clobbers the other.
    private void registerBrokenConnectionListener() {
        try {
            xaConnection.setExceptionListener(new ExceptionListener() {
                public void onException(JMSException exception) {
                    markErroneous();
                    ExceptionListener appListener = applicationExceptionListener;
                    if (appListener != null) {
                        appListener.onException(exception);
                    }
                }
            });
        } catch (JMSException ex) {
            LOGGER.logWarning(this + ": could not register broken-connection listener - "
                    + "asynchronous connection failures may go undetected", ex);
        }
    }

    synchronized void markErroneous() {
        this.erroneous = true;
    }

    void setApplicationExceptionListener(ExceptionListener listener) {
        this.applicationExceptionListener = listener;
    }

    ExceptionListener getApplicationExceptionListener() {
        return this.applicationExceptionListener;
    }

    protected Connection doCreateConnectionProxy() throws CreateConnectionException {
        // new borrow generation (called under createConnectionProxy's monitor):
        // allow this connection to terminate once more. cf case 230921 / ADR-084.
        terminating = false;
        currentProxy = AtomikosJmsConnectionProxy.newInstance(sessionCreationMode, xaConnection,
                jmsTransactionalResource, this, props);
        return currentProxy;
    }

    protected void testUnderlyingConnection() throws CreateConnectionException {
        if (isErroneous()) {
            throw new CreateConnectionException(this + ": connection is erroneous");
        }
        if (maxLifetimeExceeded()) {
            throw new CreateConnectionException(this + ": connection too old - will be replaced");
        }
    }

    public void doDestroy() {
        if (xaConnection != null) {
            try {
                xaConnection.close();
            } catch (JMSException ex) {
                // ignore but log
                LOGGER.logWarning(this + ": error closing XAConnection: ", ex);
            }
        }
        xaConnection = null;
    }

    public synchronized boolean isAvailable() {
        boolean ret = true;
        if (currentProxy != null) {
            AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler(currentProxy);
            ret = proxy.isAvailable();
        }
        return ret;
    }

    public synchronized boolean isErroneous() {
        boolean ret = erroneous;
        if (currentProxy != null) {
            AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler(currentProxy);
            ret = ret || proxy.isErroneous();
        }
        return ret;
    }

    public synchronized boolean isInTransaction(CompositeTransaction ct) {
        boolean ret = false;
        if (currentProxy != null) {
            AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler(currentProxy);
            ret = proxy.isInTransaction(ct);
        }
        return ret;
    }

    public void onTerminated() {
        boolean fireTerminatedEvent = false;
        AtomikosJmsConnectionProxy proxy = null;
        synchronized ( this ) {
            //a session has terminated -> check reusability of all remaining
            boolean available = isAvailable();
            if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": a session has terminated, is connection now available ? " + available );
            // cf case 230921 / ADR-084: claim the single-shot termination for this borrow,
            // together with the availability decision, inside the monitor (serialised with
            // doCreateConnectionProxy's reset). So under concurrent close only one thread tears
            // down (no deadlock) and announces availability once (no double-borrow); the
            // announcement itself still runs outside the monitor (cf case 27614).
            fireTerminatedEvent = available && !terminating;
            if ( fireTerminatedEvent ) terminating = true;
            if ( currentProxy != null ) {
                proxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler(currentProxy);
                if ( proxy.isErroneous() ) erroneous = true;
            }
        }
        
        if ( fireTerminatedEvent ) {
            if (proxy != null) proxy.closeAllPendingSessions();
            //callbacks done outside synch to avoid deadlock in case 27614
            fireOnXPooledConnectionTerminated();
        }

        
    }

    public boolean canBeRecycledForCallingThread() {
        boolean ret = false;
        if (currentProxy != null) {
            CompositeTransactionManager tm = Configuration.getCompositeTransactionManager();

            CompositeTransaction current = tm.getCompositeTransaction();
            if (current != null && TransactionManagerImp.isJtaTransaction(current)) {
                AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler(currentProxy);
                // recycle only if inactive in this tx - i.e., if proxy was closed!
                ret = proxy.isInactiveInTransaction(current);
            }
        }

        return ret;
    }

    public String toString() {
        return "atomikosPooledJmsConnection for resource " + jmsTransactionalResource.getName();
    }

}
