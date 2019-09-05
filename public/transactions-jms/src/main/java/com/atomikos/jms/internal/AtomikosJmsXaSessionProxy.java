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
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // MUST be synchronized: see case 126795
        return super.invoke(proxy, method, args);
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
        if (state.isTerminated()) {
            // only destroy if there is no pending 2PC - otherwise this is done
            // in the registered synchronization
            destroy(true);
        } else {
            // close this handle but keep vendor session open for 2PC
            // see case 71079
            destroy(false);
        }
        markClosed();
    }

    protected void destroy(boolean closeXaSession) {
        if (closeXaSession) {
            // see case 71079: don't close vendor session if transaction is not done yet
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace(this + ": closing underlying vendor session");
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
        MessageProducer vendorProducer = delegate.createProducer(destination);
        return new AtomikosJmsMessageProducerWrapper(vendorProducer, state);
    }

    @Proxied
    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        MessageConsumer vendorConsumer = delegate.createConsumer(destination);
        return new AtomikosJmsMessageConsumerWrapper(vendorConsumer, state);
    }

    @Proxied
    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        MessageConsumer vendorConsumer = delegate.createConsumer(destination, messageSelector);
        return new AtomikosJmsMessageConsumerWrapper(vendorConsumer, state);
    }

    @Proxied
    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean NoLocal)
            throws JMSException {
        MessageConsumer vendorConsumer = delegate.createConsumer(destination, messageSelector, NoLocal);
        return new AtomikosJmsMessageConsumerWrapper(vendorConsumer, state);
    }

    @Proxied
    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        TopicSubscriber vendorSubscriber = delegate.createDurableSubscriber(topic, name);
        return new AtomikosJmsTopicSubscriberWrapper(vendorSubscriber, state);
    }

    @Proxied
    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal)
            throws JMSException {
        TopicSubscriber vendorSubscriber = delegate.createDurableSubscriber(topic, name, messageSelector, noLocal);
        return new AtomikosJmsTopicSubscriberWrapper(vendorSubscriber, state);
    }

    @Override
    protected boolean isAvailable() {
        return state.isTerminated();
    }

    @Override
    protected boolean isErroneous() {
        return state.isErroneous();
    }

    @Override
    protected boolean isInTransaction(CompositeTransaction ct) {
        return state.isActiveInTransaction(ct);
    }

    @Override
    protected boolean isInactiveTransaction(CompositeTransaction ct) {
        return state.isInactiveInTransaction(ct);
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
            state.notifySessionBorrowed();
        }
    }

    @Override
    protected void handleInvocationException(Throwable e) throws Throwable {
        if (!(e instanceof TransactionInProgressException)) {
            state.notifySessionErrorOccurred();
        }
        throw e;
    }
    
    @Override
    public String toString() {
        return "atomikosJmsXaSessionProxy (state = " + state + ") for vendor instance " + delegate;
    }

}
