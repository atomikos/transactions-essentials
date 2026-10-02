/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;

/**
 * cf case 228933 - follow-up to case 227307.
 *
 * The 227307 guard at XAResourceTransaction.resume() refuses enlistment on a
 * doomed transaction by throwing com.atomikos.datasource.ResourceException
 * (unchecked). The common doomed cases (rollback-only / timed-out, detected
 * earlier via CompositeTransaction state) are caught as the checked
 * InvalidSessionHandleStateException and correctly translated to a
 * JMSException. But the guard's own ResourceException - reached when the
 * transaction becomes doomed only at the XA layer (e.g. certain cross-service
 * propagation cases) - was not caught here, so it escaped the JMS enlistment
 * path raw instead of being translated.
 */
public class ConsumerProducerSupportDoomedGuardExceptionTranslationTestJUnit {

    private CompositeTransactionManager ctm;

    @Before
    public void setUp() {
        ctm = mock(CompositeTransactionManager.class);
        Configuration.installCompositeTransactionManager(ctm);
    }

    @After
    public void tearDown() {
        Configuration.installCompositeTransactionManager(null);
    }

    @Test
    public void resourceExceptionFromTheDoomedEnlistmentGuardIsTranslatedToJMSException() throws Exception {
        // given a JTA transaction whose enlistment is refused only by the 227307
        // guard at the XA layer, which throws a raw, unchecked ResourceException -
        // not the checked InvalidSessionHandleStateException the earlier
        // state-based check throws for the common doomed cases.
        CompositeTransaction ct = mock(CompositeTransaction.class);
        when(ct.getProperty(TransactionManagerImp.JTA_PROPERTY_NAME)).thenReturn("true");
        when(ctm.getCompositeTransaction()).thenReturn(ct);

        SessionHandleState state = mock(SessionHandleState.class);
        doThrow(new ResourceException("transaction is doomed (timed out) - enlisting resource is not allowed"))
                .when(state).notifyBeforeUse(ct);

        MessageProducer delegate = mock(MessageProducer.class);
        AtomikosJmsMessageProducerWrapper producer = new AtomikosJmsMessageProducerWrapper(delegate, state);

        // when the doomed-transaction refusal reaches the JMS enlistment path
        try {
            producer.send(mock(Message.class));
            fail("expected the doomed-enlistment refusal to be reported as a JMSException");
        } catch (JMSException expected) {
            // then it must be translated - a JMS client catching by exception type
            // must see this, not a raw com.atomikos ResourceException
        }
    }
}
