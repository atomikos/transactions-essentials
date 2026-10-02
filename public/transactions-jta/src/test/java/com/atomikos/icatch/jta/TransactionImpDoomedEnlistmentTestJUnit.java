/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.xa.XAResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.recovery.TxState;

/**
 * Guards that enlistment on a doomed transaction (timed out or rollback-only)
 * is refused at the JTA boundary with RollbackException (JTA spec).
 *
 * See Curriculum Bible: "XA Branch Lifecycle"; "Transaction Timeout Semantics".
 * Design decisions in decision-journal 2026-06-28-case-227307-doomed-enlistment.md.
 * cf case 227307
 */
public class TransactionImpDoomedEnlistmentTestJUnit {

    private static final String FLAG = "com.atomikos.icatch.feature.227307";

    @Mock private CompositeTransaction compositeTransaction;
    @Mock private XAResource xaResource;

    private TransactionImp sut;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(compositeTransaction.getState()).thenReturn(TxState.ACTIVE);
        when(compositeTransaction.getTimeout()).thenReturn(-1000L);
        sut = new TransactionImp(compositeTransaction);
        // guard is ON by default (defaults.properties sets flag=true)
        Configuration.getConfigProperties().setProperty(FLAG, "true");
    }

    @After
    public void tearDown() {
        Configuration.getConfigProperties().setProperty(FLAG, "true");
    }

    @Test(expected = RollbackException.class)
    public void givenTimedOutTransactionAndGuardOn_whenEnlistResource_thenRollbackException()
            throws Exception {
        sut.enlistResource(xaResource);
    }

    @Test(expected = RollbackException.class)
    public void givenRollbackOnlyTransactionAndGuardOn_whenEnlistResource_thenRollbackException()
            throws Exception {
        when(compositeTransaction.getTimeout()).thenReturn(10000L);
        when(compositeTransaction.getState()).thenReturn(TxState.MARKED_ABORT);
        sut.enlistResource(xaResource);
    }

    @Test
    public void givenTimedOutTransactionAndGuardDisabled_whenEnlistResource_thenNoRollbackException()
            throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "false"); // escape hatch: old behavior
        try {
            sut.enlistResource(xaResource);
            fail("Expected SystemException for unregistered resource");
        } catch (SystemException e) {
            // expected: unregistered resource, guard did not fire
        } catch (RollbackException e) {
            fail("Guard must not fire when disabled via escape hatch");
        }
    }

    @Test
    public void givenActiveTransactionWithTimeRemaining_whenEnlistResource_thenNoRollbackException()
            throws Exception {
        when(compositeTransaction.getTimeout()).thenReturn(30000L);
        when(compositeTransaction.getState()).thenReturn(TxState.ACTIVE);
        try {
            sut.enlistResource(xaResource);
            fail("Expected SystemException for unregistered resource");
        } catch (SystemException e) {
            // expected: unregistered resource, guard correctly did not fire
        } catch (RollbackException e) {
            fail("Guard must not fire for active transaction");
        }
    }
}
