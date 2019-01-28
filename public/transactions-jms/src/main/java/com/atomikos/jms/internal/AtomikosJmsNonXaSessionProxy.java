/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import java.lang.reflect.Method;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.Proxied;

public class AtomikosJmsNonXaSessionProxy extends AbstractJmsSessionProxy {

    private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsNonXaSessionProxy.class);

    private boolean errorsOccurred = false;
    private final SessionHandleStateChangeListener owner;
    private final SessionHandleStateChangeListener connectionProxy;

    public AtomikosJmsNonXaSessionProxy(Session delegate, SessionHandleStateChangeListener owner,
            SessionHandleStateChangeListener connectionProxy) {
        super(delegate);
        this.owner = owner;
        this.connectionProxy = connectionProxy;
    }

    @Override
    protected void throwInvocationAfterClose(String methodName) throws Exception {
        String msg = "Session was closed already - calling " + methodName + " is no longer allowed.";
        LOGGER.logWarning(this + ": " + msg);
        throw new javax.jms.IllegalStateException(msg);
    }

    @Proxied
    public void close() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": close...");
        }
        destroy();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        checkForTransactionContextAndLogWarningIfSo();
        String methodName = method.getName();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.logDebug(this + ": calling " + methodName + " on vendor session...");
            }
            Object ret = super.invoke(proxy, method, args);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace(this + ": " + methodName + " returning " + ret);
            }
            return ret;
        } catch (Exception ex) {
            errorsOccurred = true;
            String msg = "Error delegating " + methodName + " call to JMS driver";
            JmsProxyHelper.convertProxyError(ex, msg);
        }

        // dummy return to make compiler happy
        return null;
    }

    private void checkForTransactionContextAndLogWarningIfSo() {
        TransactionManager tm = TransactionManagerImp.getTransactionManager();
        if (tm != null) {
            Transaction tx = null;
            try {
                tx = tm.getTransaction();
            } catch (SystemException e) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.logTrace(this + ": Failed to get transaction.", e);
                }
                // ignore
            }
            if (tx != null) {
                String msg = this
                        + ": WARNING - detected JTA transaction context while using non-transactional session." + "\n"
                        + "Beware that any JMS operations you perform are NOT part of the JTA transaction." + "\n"
                        + "To enable JTA, make sure to do all of the following:" + "\n"
                        + "1. Make sure that the AtomikosConnectionFactoryBean is configured with localTransactionMode=false, and"
                        + "\n" + "2. Make sure to call create JMS sessions with the transacted flag set to true.";
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.logDebug(msg);
                }
            }
        }
    }

    protected void destroy() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.logDebug(this + ": destroying session...");
            }
            if (!closed) {
                closed = true;
                delegate.close();
                owner.onTerminated();
                connectionProxy.onTerminated();
            }
        } catch (JMSException e) {
            LOGGER.logWarning(this + ": could not close JMS session", e);
        }

    }

    protected boolean isAvailable() {
        return closed;
    }

    protected boolean isErroneous() {
        return errorsOccurred;
    }

    protected boolean isInTransaction(CompositeTransaction ct) {
        return false;
    }

    public String toString() {
        return "atomikos non-xa session proxy for vendor instance " + delegate;
    }

    public static Session newInstance(Session wrapped, SessionHandleStateChangeListener owner,
            AtomikosJmsConnectionProxy atomikosJmsConnectionProxy) {
        AtomikosJmsNonXaSessionProxy proxy = new AtomikosJmsNonXaSessionProxy(wrapped, owner,
                atomikosJmsConnectionProxy);
        return proxy.createDynamicProxy();
    }

    @Override
    public void recycle() {
        LOGGER.logWarning(this + ": unexpected call of recycle() - this is probably a bug?");
    }

}
