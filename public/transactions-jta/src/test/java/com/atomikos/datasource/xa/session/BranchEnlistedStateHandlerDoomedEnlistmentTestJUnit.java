/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;

import static org.mockito.Mockito.when;

import javax.transaction.xa.XAResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.XID;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.recovery.TxState;

/**
 * cf case 227307 (customer follow-up, S&P Global, 2026-07-22):
 * {@link XAResourceTransactionDoomedEnlistmentTestJUnit} proves the guard
 * fires on the FIRST enlist (the resume() call inside the
 * NotInBranchStateHandler -> BranchEnlistedStateHandler transition). It does
 * not prove anything about a SECOND enlist on an already-enlisted resource
 * within the same transaction, which is the customer's actual pattern (one
 * connection reused for several sequential statements; the transaction
 * becomes doomed between two of them, no close/reborrow in between).
 *
 * checkEnlistBeforeUse() on an already-BranchEnlistedStateHandler session
 * only checks "same transaction?" and never re-invokes resume() -- so the
 * doomed check (getTimeout() <= 0, not yet reflected in getState()) never
 * re-fires for the second and later statements. This test proves that gap.
 */
public class BranchEnlistedStateHandlerDoomedEnlistmentTestJUnit {

    private static final String FLAG = "com.atomikos.icatch.feature.227307";
    private static final String RESOURCE_NAME = "testResource";

    private static class FixtureResource extends XATransactionalResource {
        FixtureResource() { super(RESOURCE_NAME); }
        @Override protected XAResource refreshXAConnection() { return null; }
        @Override protected XID createXid(String tid) { return new XID(tid, "testBranch", RESOURCE_NAME); }
    }

    @Mock private CompositeTransaction compositeTransaction;
    @Mock private CompositeCoordinator compositeCoordinator;
    @Mock private XAResource xaResource;

    private BranchEnlistedStateHandler handler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(compositeTransaction.getCompositeCoordinator()).thenReturn(compositeCoordinator);
        when(compositeCoordinator.getCoordinatorId()).thenReturn("testTid");
        when(compositeTransaction.getTid()).thenReturn("testTid");
        when(compositeTransaction.getLineage()).thenReturn(null);
        when(compositeTransaction.isSerial()).thenReturn(false);
        when(compositeTransaction.isSameTransaction(compositeTransaction)).thenReturn(true);
        // Healthy at construction time -- represents the FIRST enlist having
        // already succeeded (construction mirrors the real
        // NotInBranchStateHandler -> BranchEnlistedStateHandler transition,
        // which uses this same 3-arg constructor and, via
        // XATransactionalResource.getResourceTransaction(), the real
        // SiblingMapper to build the XAResourceTransaction branch).
        when(compositeTransaction.getTimeout()).thenReturn(30000L);
        when(compositeTransaction.getState()).thenReturn(TxState.ACTIVE);

        FixtureResource resource = new FixtureResource();
        handler = new BranchEnlistedStateHandler(resource, compositeTransaction, xaResource);

        Configuration.getConfigProperties().setProperty(FLAG, "true");
    }

    @After
    public void tearDown() {
        Configuration.getConfigProperties().setProperty(FLAG, "true");
    }

    @Test(expected = InvalidSessionHandleStateException.class)
    public void givenTransactionBecomesDoomedBetweenStatements_whenSecondEnlist_thenRefused() throws Exception {
        // Timeout elapses between statement 1 and statement 2, on the SAME
        // connection/transaction -- no close, no state transition. getState()
        // has not caught up yet (still ACTIVE), matching the real race the
        // 227307 guard was built to close via a live getTimeout() check
        // rather than a state flag.
        when(compositeTransaction.getTimeout()).thenReturn(-1000L);

        handler.checkEnlistBeforeUse(compositeTransaction); // second enlist -- must be refused
    }

    @Test
    public void givenTransactionStillHealthy_whenSecondEnlist_thenAllowed() throws Exception {
        // Control case: time remaining, no state change expected (null == no transition).
        TransactionContextStateHandler result = handler.checkEnlistBeforeUse(compositeTransaction);
        if (result != null) {
            throw new AssertionError("expected no state transition for a healthy repeated enlist, got " + result);
        }
    }

    @Test
    public void givenGuardDisabled_whenSecondEnlistOnDoomedTransaction_thenOldBehaviorPreserved() throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "false"); // escape hatch: old behavior
        when(compositeTransaction.getTimeout()).thenReturn(-1000L);

        TransactionContextStateHandler result = handler.checkEnlistBeforeUse(compositeTransaction);
        if (result != null) {
            throw new AssertionError("expected no state transition when the guard is disabled, got " + result);
        }
    }
}
