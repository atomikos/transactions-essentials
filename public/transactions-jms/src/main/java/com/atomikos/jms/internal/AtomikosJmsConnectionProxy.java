/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XASession;

import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.DynamicProxySupport;
import com.atomikos.util.Proxied;

public class AtomikosJmsConnectionProxy extends DynamicProxySupport<XAConnection>
        implements SessionHandleStateChangeListener {

    private static Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsConnectionProxy.class);

    private List<Session> sessions;
    private XATransactionalResource jmsTransactionalResource;
    private boolean ignoreSessionTransactedFlag;
    private ConnectionPoolProperties props;
    private SessionHandleStateChangeListener owner;
    private boolean erroneous;

    public AtomikosJmsConnectionProxy(XAConnection delegate, boolean ignoreSessionTransactedFlag,
            XATransactionalResource jmsTransactionalResource, SessionHandleStateChangeListener owner,
            ConnectionPoolProperties props) {
        super(delegate);
        this.sessions = new ArrayList<Session>();
        this.jmsTransactionalResource = jmsTransactionalResource;
        this.closed = false;
        this.owner = owner;
        this.props = props;
        this.ignoreSessionTransactedFlag = ignoreSessionTransactedFlag;

    }

    @Override
    protected void throwInvocationAfterClose(String methodName) throws Exception {
        String msg = "Connection is closed already - calling method " + methodName + " no longer allowed.";
        LOGGER.logWarning(this + ": " + msg);
        throw new javax.jms.IllegalStateException(msg);

    }

    @Override
    public void onTerminated() {
        //a session has terminated -> remove it from the list of sessions to enable GC
        List<Session> sessionsToCheck = cloneSessionsToAvoidDeadlock();
        List<Session> sessionsToRemove = new ArrayList<Session>();
        //call member sessions outside sync to avoid deadlock
        Iterator<Session> it = sessionsToCheck.iterator();
        while ( it.hasNext() ) {
            Session handle = it.next();
            AbstractJmsSessionProxy session = (AbstractJmsSessionProxy) Proxy.getInvocationHandler(handle);
            if ( session.isAvailable() ) {
                sessionsToRemove.add(handle);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug ( this + ": removing " + sessionsToRemove.size() + " session(s)...");        
        }
        synchronized (sessions) {
            for (Session s : sessionsToRemove) {
                Iterator<Session> pendingIt = sessions.iterator();
                while (pendingIt.hasNext()) {
                    Session pending = pendingIt.next();
                    if (pending == s) {  // cf case 139936: don't rely on equals for dynamic proxy!
                        pendingIt.remove();
                    }
                }
                
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug ( this + ": keeping " + sessions.size() + " sessions...");
        }
    }

    @Proxied
    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {

        Session session = null;
        synchronized (sessions) { //cf case 123502: deadlock
            if (closed) { 
                //cf case 123502: make it safe to clone sessions on close... 
                throw new JMSException("Connection was closed already - creating new sessions is no longer allowed.");
            }
            if (createXaSession(transacted)) {
                session = recycleSession();
                if (session == null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.logDebug(this + ": creating XA-capable session...");
                    }
                    forceConnectionIntoXaMode(delegate);
                    XASession wrapped = delegate.createXASession();
                    session = AtomikosJmsXaSessionProxy.newInstance(wrapped, jmsTransactionalResource, owner, this);
                    addSession(session);
                }
            } else {
                CompositeTransaction ct = null;
                CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
                if (ctm != null) {
                    ct = ctm.getCompositeTransaction();
                }
                if (ct != null && TransactionManagerImp.isJtaTransaction(ct)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.logDebug(this + ": creating NON-XA session - the resulting JMS work will NOT be part of the JTA transaction!");
                    }
                }

                Session wrapped = delegate.createSession(transacted, acknowledgeMode);
                session = (Session) AtomikosJmsNonXaSessionProxy.newInstance(wrapped, owner, this);
                addSession(session);
            }
        }
        return session;

    }

    private void addSession(Session session) {
        // fix for case 62041: synchronized!
        synchronized (sessions) {
            sessions.add(session);
        }
    }

    private static void forceConnectionIntoXaMode(Connection c) {
        // ORACLE AQ WORKAROUND:
        // force connection into global tx mode
        // cf ISSUE 10095
        Session s = null;
        try {
            s = c.createSession(true, Session.AUTO_ACKNOWLEDGE);
            s.rollback();
        } catch (Exception e) {
            // ignore: workaround code
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace("JMS: driver complains while enforcing XA mode - ignore if no later errors:", e);
            }
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    // ignore: workaround code
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.logTrace("JMS: driver complains while enforcing XA mode - ignore if no later errors:", e);
                    }
                }
            }
        }
    }

    private boolean createXaSession(boolean sessionTransactedFlag) {
        if (ignoreSessionTransactedFlag) {
            return !props.getLocalTransactionMode();
        } else {
            return sessionTransactedFlag && !props.getLocalTransactionMode();
        }
    }

    private synchronized Session recycleSession() {
        CompositeTransactionManager tm = Configuration.getCompositeTransactionManager();
        if (tm == null)
            return null;

        CompositeTransaction current = tm.getCompositeTransaction();
        if (current != null && TransactionManagerImp.isJtaTransaction(current)) {
            synchronized (sessions) {
                for (int i = 0; i < sessions.size(); i++) {
                    Session session = sessions.get(i);
                    AbstractJmsSessionProxy proxy = (AbstractJmsSessionProxy) Proxy.getInvocationHandler(session);

                    // recycle if either inactive in this tx, OR if active
                    // (since a new session will be created anyway, and
                    // concurrent sessions are allowed on the same underlying
                    // connection!
                    if (proxy.isInactiveTransaction(current) || proxy.isInTransaction(current)) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.logDebug(this + ": recycling session " + proxy);
                        }
                        proxy.recycle();
                        return session;
                    }
                }
            } // synchronized (sessions)
        }
        return null;
    }

    public boolean isErroneous() {
        boolean ret = erroneous;
        if (!ret) {
            List<Session> sessionsToCheck = cloneSessionsToAvoidDeadlock();
            Iterator<Session> it = sessionsToCheck.iterator();
            while (it.hasNext() && !ret) {
                Object handle = it.next();
                AbstractJmsSessionProxy session = (AbstractJmsSessionProxy) Proxy.getInvocationHandler(handle);
                if (session.isErroneous()) {
                    ret = true;
                }
            }
        }
        return ret;
    }

    public boolean isInTransaction(CompositeTransaction ct) {
        boolean ret = false;
        synchronized (sessions) {
            Iterator<Session> it = sessions.iterator();
            while (it.hasNext() && !ret) {
                Session handle = it.next();
                AbstractJmsSessionProxy session = (AbstractJmsSessionProxy) Proxy.getInvocationHandler(handle);
                if (session.isInTransaction(ct))
                    ret = true;
            }
        }
        return ret;
    }

    boolean isInactiveInTransaction(CompositeTransaction ct) {
        if (!closed) { // cf case 174179
            return false;
        }
        boolean ret = false;
        synchronized (sessions) {
            Iterator<Session> it = sessions.iterator();
            while (it.hasNext() && !ret) {
                Session handle = it.next();
                AbstractJmsSessionProxy session = (AbstractJmsSessionProxy) Proxy.getInvocationHandler(handle);
                if (session.isInactiveTransaction(ct)) {
                    ret = true;
                }
            }
        }
        return ret;
    }

    public boolean isAvailable() {
        boolean ret = false;
        List<Session> sessionsToCheck = new ArrayList<Session>();
        synchronized (sessions) {
            if (closed) {
                ret = true; //tentatively, depends on sessionsToCheck! 
                sessionsToCheck = cloneSessionsToAvoidDeadlock(); //cf case 123502: deadlock
            }
        }
        Iterator<Session> it = sessionsToCheck.iterator(); //empty if not closed
        while (it.hasNext() && ret) {
            Object handle = it.next();
            AbstractJmsSessionProxy session = (AbstractJmsSessionProxy) Proxy.getInvocationHandler(handle);
            if (!session.isAvailable()) {
                ret = false; //wait for tx to terminate
            }
        }
        return ret;
    }

    // should only be called after ALL sessions are done, i.e. when the
    // connection can be pooled again
    synchronized void destroy() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": closing connection and all " + sessions.size() + " session(s)");
        }

        // close all sessions to make sure the session close notifications are done!
        synchronized (sessions) {
            for (int i = 0; i < sessions.size(); i++) {
                Session session = (Session) sessions.get(i);
                try {
                    session.close();
                } catch (JMSException ex) {
                    LOGGER.logWarning(this + ": error closing session " + session, ex);
                }
            }
        }

        sessions.clear();
    }

    @Proxied
    public void close() {
        if ( LOGGER.isDebugEnabled() ) {
            LOGGER.logDebug ( this + ": closing " + sessions.size() + " session(s)");
        }
        if (!closed) { // cf case 136113
            List<Session> sessionsToClose;
            //close all sessions to make sure the session close notifications are done!
            synchronized (sessions) {
                markClosed(); //no new sessions will be added => safe to clone session list
                sessionsToClose = cloneSessionsToAvoidDeadlock(); //cf case 123502: call close outside of sync to avoid deadlocks
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace(this + ": closing " + sessionsToClose.size() + " session(s)");
            }
            for (int i = 0; i < sessionsToClose.size(); i++) {
                Session session = (Session) sessionsToClose.get(i);
                try {
                    session.close();
                } catch (JMSException ex) {
                    LOGGER.logWarning(this + ": error closing session " + session, ex);
                }
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace(this + ": is available ? " + isAvailable());
            }
            if (isAvailable()) {
                owner.onTerminated();
            }
        }
        if ( LOGGER.isTraceEnabled() ) {
            LOGGER.logTrace ( this + ": closed." );  
        }
        //leave destroy to the owning pooled connection - that one knows when any and all 2PCs are done
    }

    public static Connection newInstance(boolean ignoreSessionTransactedFlag, XAConnection xaConnection,
            XATransactionalResource jmsTransactionalResource,
            SessionHandleStateChangeListener sessionHandleStateChangeListener, ConnectionPoolProperties props) {
        AtomikosJmsConnectionProxy proxy = new AtomikosJmsConnectionProxy(xaConnection, ignoreSessionTransactedFlag,
                jmsTransactionalResource, sessionHandleStateChangeListener, props);
        return proxy.createDynamicProxy();
    }

    @Override
    protected void handleInvocationException(Throwable e) throws Throwable {
        erroneous = true;
        throw e;
    }
    
    @Override
    public String toString() {
        return "atomikosJmsConnectionProxy (isAvailable = " + isAvailable() + ")  for vendor instance " + delegate;
    }
    
    private List<Session> cloneSessionsToAvoidDeadlock() { //cf case 123502: deadlock
        List<Session> sessionsToReturn = null;
        synchronized (sessions) {
            sessionsToReturn = new ArrayList<Session>(sessions);
        }
        return sessionsToReturn;
    }
    
    //should only be called after ALL sessions are done, i.e. when the connection can be pooled again
    void closeAllPendingSessions() {
        if ( LOGGER.isTraceEnabled() ) {
            LOGGER.logTrace ( this + ": closing connection and all " + sessions.size() + " session(s)" );
        }

        //close all sessions to make sure the session close notifications are done!
        List<Session> sessionsToClose = cloneSessionsToAvoidDeadlock();
        for (Session session : sessionsToClose) {
            try {
                session.close ();
            } catch (JMSException ex) {
                LOGGER.logWarning ( this + ": error closing session " + session, ex );
            }
        }
        synchronized (sessions) { //cf BugzID: 126795       
            sessions.removeAll(sessionsToClose);
        }


    }

	@Override
	protected Class<XAConnection> getRequiredInterfaceType() {
		return XAConnection.class;
	}

}
