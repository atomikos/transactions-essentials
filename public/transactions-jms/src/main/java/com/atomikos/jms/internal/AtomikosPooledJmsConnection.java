/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import java.lang.reflect.Proxy;

import javax.jms.Connection;
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

    private boolean ignoreSessionTransactedFlag;

    public AtomikosPooledJmsConnection(boolean ignoreSessionTransactedFlag, XAConnection xac,
            XATransactionalResource jmsTransactionalResource, ConnectionPoolProperties props) {
        super(props);
        this.jmsTransactionalResource = jmsTransactionalResource;
        this.xaConnection = xac;
        this.props = props;
        this.erroneous = false;
        this.ignoreSessionTransactedFlag = ignoreSessionTransactedFlag;
    }

    protected Connection doCreateConnectionProxy() throws CreateConnectionException {
        currentProxy = AtomikosJmsConnectionProxy.newInstance(ignoreSessionTransactedFlag, xaConnection,
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

    public void doDestroy(boolean reap) {
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
            fireTerminatedEvent = isAvailable();
            if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": a session has terminated, is connection now available ? " + fireTerminatedEvent );           
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
