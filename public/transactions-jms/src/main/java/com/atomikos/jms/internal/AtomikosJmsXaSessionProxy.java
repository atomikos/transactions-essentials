/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import java.lang.reflect.Method;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.TransactionInProgressException;
import javax.jms.XASession;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.Proxied;

public class AtomikosJmsXaSessionProxy extends AbstractJmsSessionProxy implements SessionHandleStateChangeListener {

    private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsXaSessionProxy.class);
    private final SessionHandleState state;

    public AtomikosJmsXaSessionProxy(XASession delegate, XATransactionalResource jmsTransactionalResource,
            SessionHandleStateChangeListener pooledConnection, SessionHandleStateChangeListener connectionProxy) {
        super(delegate);
        this.state = new SessionHandleState(jmsTransactionalResource, delegate.getXAResource());
        state.registerSessionHandleStateChangeListener(pooledConnection);
        state.registerSessionHandleStateChangeListener(connectionProxy);
        state.registerSessionHandleStateChangeListener(this);
        // for JMS, session borrowed corresponds to creation of the session
        state.notifySessionBorrowed();

    }

    @Override
    protected void throwInvocationAfterClose(String methodName) throws Exception {
        String msg = "Session was closed already - calling " + methodName + " is no longer allowed.";
        LOGGER.logWarning(this + ": " + msg);
        throw new javax.jms.IllegalStateException(msg);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return super.invoke(proxy, method, args);
        } catch (Exception ex) {
            try {
                String msg = "Error delegating call to " + method.getName() + " on JMS driver";
                JmsProxyHelper.convertProxyError(ex, msg);
            } catch (TransactionInProgressException e) {
                throw e;
            } catch (Exception e) {
                state.notifySessionErrorOccurred();
                throw e;
            }
        }
        return null;
    }

    @Proxied
    public void commit() throws JMSException {
        String msg = "Calling commit/rollback is not allowed on a managed session!";
        // When using the Spring PlatformTransactionManager, there is always a
        // call to commit on the Session in a synchronization's afterCompletion.
        // The PlatformTransactionManager uses that mechanism for non-JTA TX
        // commit which happens because
        // DefaultMessageListenerContainer.sessionTransacted
        // must be set to true. Spring catches TransactionInProgressException in
        // case of JTA TX management.
        // This is fine except that we used to log this message at warning level
        // when this happens which is annoying as it repeats once per TX ->
        // lowered it to info.
        //
        // See:
        // org.springframework.jms.connection.ConnectionFactoryUtils$JmsResourceSynchronization.afterCommit()
        // and org.springframework.jms.connection.JmsResourceHolder.commitAll()
        // (as of Spring 2.0.8)
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": " + msg);
        }

        throw new javax.jms.TransactionInProgressException(msg);

    }

    @Proxied
    public void rollback() throws JMSException {
        String msg = "Calling commit/rollback is not allowed on a managed session!";
        // When using the Spring PlatformTransactionManager, there is always a
        // call to commit on the Session in a synchronization's afterCompletion.
        // The PlatformTransactionManager uses that mechanism for non-JTA TX
        // commit which happens because
        // DefaultMessageListenerContainer.sessionTransacted
        // must be set to true. Spring catches TransactionInProgressException in
        // case of JTA TX management.
        // This is fine except that we used to log this message at warning level
        // when this happens which is annoying as it repeats once per TX ->
        // lowered it to info.
        //
        // See:
        // org.springframework.jms.connection.ConnectionFactoryUtils$JmsResourceSynchronization.afterCommit()
        // and org.springframework.jms.connection.JmsResourceHolder.commitAll()
        // (as of Spring 2.0.8)
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": " + msg);
        }

        throw new javax.jms.TransactionInProgressException(msg);

    }

    @Proxied
    public void close() throws JMSException {
        state.notifySessionClosed();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": closing session " + this + " - is terminated ? " + state.isTerminated());
        }
        if (state.isTerminated()) {
            // only destroy if there is no pending 2PC - otherwise this is done
            // in the registered synchronization
            destroy(true);
        } else {
            // close this handle but keep vendor session open for 2PC
            // see case 71079
            destroy(false);
        }

    }

    protected void destroy(boolean closeXaSession) {
        if (closeXaSession) {
            // see case 71079: don't close vendor session if transaction is not done yet
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace(this + ": closing underlying vendor session " + this);
            }
            try {
                delegate.close();
            } catch (JMSException e) {
                LOGGER.logWarning(this + ": could not close underlying vendor session", e);
            }
        }
        closed = true;
    }

    @Proxied
    public MessageProducer createProducer(Destination destination) throws JMSException {
        MessageProducer vendorProducer = null;
        try {
            vendorProducer = delegate.createProducer(destination);
        } catch (Exception e) {
            String msg = "Failed to create MessageProducer: " + e.getMessage();
            state.notifySessionErrorOccurred();
            JmsProxyHelper.convertProxyError(e, msg);
        }
        return new AtomikosJmsMessageProducerWrapper(vendorProducer, state);
    }

    @Proxied
    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        MessageConsumer vendorConsumer = null;
        try {
            vendorConsumer = delegate.createConsumer(destination);
        } catch (Exception e) {
            String msg = "Failed to create MessageConsumer: " + e.getMessage();
            state.notifySessionErrorOccurred();
            JmsProxyHelper.convertProxyError(e, msg);
        }
        return new AtomikosJmsMessageConsumerWrapper(vendorConsumer, state);
    }

    @Proxied
    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        MessageConsumer vendorConsumer = null;
        try {
            vendorConsumer = delegate.createConsumer(destination, messageSelector);
        } catch (Exception e) {
            String msg = "Failed to create MessageConsumer: " + e.getMessage();
            state.notifySessionErrorOccurred();
            JmsProxyHelper.convertProxyError(e, msg);
        }
        return new AtomikosJmsMessageConsumerWrapper(vendorConsumer, state);
    }

    @Proxied
    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean NoLocal)
            throws JMSException {
        MessageConsumer vendorConsumer = null;
        try {
            vendorConsumer = delegate.createConsumer(destination, messageSelector, NoLocal);
        } catch (Exception e) {
            String msg = "Failed to create MessageConsumer: " + e.getMessage();
            state.notifySessionErrorOccurred();
            JmsProxyHelper.convertProxyError(e, msg);
        }
        return new AtomikosJmsMessageConsumerWrapper(vendorConsumer, state);
    }

    @Proxied
    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        TopicSubscriber vendorSubscriber = null;
        try {
            vendorSubscriber = delegate.createDurableSubscriber(topic, name);
        } catch (Exception e) {
            String msg = "Failed to create durable TopicSubscriber: " + e.getMessage();
            state.notifySessionErrorOccurred();
            JmsProxyHelper.convertProxyError(e, msg);
        }
        return new AtomikosJmsTopicSubscriberWrapper(vendorSubscriber, state);
    }

    @Proxied
    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal)
            throws JMSException {
        TopicSubscriber vendorSubscriber = null;
        try {
            vendorSubscriber = delegate.createDurableSubscriber(topic, name, messageSelector, noLocal);
        } catch (Exception e) {
            String msg = "Failed to create durable TopicSubscriber: " + e.getMessage();
            state.notifySessionErrorOccurred();
            JmsProxyHelper.convertProxyError(e, msg);
        }
        return new AtomikosJmsTopicSubscriberWrapper(vendorSubscriber, state);
    }

    @Override
    protected boolean isAvailable() {
        boolean ret = false;
        if (state != null) {
            ret = state.isTerminated();
        }
        return ret;
    }

    @Override
    protected boolean isErroneous() {
        boolean ret = false;
        if (state != null) {
            ret = state.isErroneous();
        }
        return ret;
    }

    @Override
    protected boolean isInTransaction(CompositeTransaction ct) {
        boolean ret = false;
        if (state != null) {
            ret = state.isActiveInTransaction(ct);
        }
        return ret;
    }

    @Override
    protected boolean isInactiveTransaction(CompositeTransaction ct) {
        boolean ret = false;
        if (state != null) {
            ret = state.isInactiveInTransaction(ct);
        }
        return ret;
    }

    public void onTerminated() {
        destroy(true);
    }

    public static Session newInstance(XASession wrapped, XATransactionalResource jmsTransactionalResource,
            SessionHandleStateChangeListener pooledConnection, SessionHandleStateChangeListener connectionProxy) {
        AtomikosJmsXaSessionProxy proxy = new AtomikosJmsXaSessionProxy(wrapped, jmsTransactionalResource,
                pooledConnection, connectionProxy);
        return proxy.createDynamicProxy();
    }

    @Override
    public void recycle() {
        synchronized (this) {
            this.closed = false;
            if (state != null) {
                state.notifySessionBorrowed();
            }
        }
    }

}
