/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.recovery.TxState;

/**
 * Guards that sub-second transaction timeouts are not truncated to 0 when
 * forwarded to the XA resource via setTransactionTimeout().
 *
 * XA spec: setTransactionTimeout(0) means "use the resource manager's own
 * default timeout", silently discarding Atomikos's deadline. A transaction
 * with e.g. 500ms remaining would pass timeout=0 to the backend due to
 * integer truncation in (int) getTimeout() / 1000.
 *
 * cf case 228434
 */
public class XAResourceTransactionTimeoutTruncationTestJUnit {

    private static final String RESOURCE_NAME = "testResource";

    private static class FixtureResource extends XATransactionalResource {
        FixtureResource() { super(RESOURCE_NAME); }
        @Override protected XAResource refreshXAConnection() { return null; }
        @Override protected XID createXid(String tid) { return new XID(tid, "testBranch", RESOURCE_NAME); }
    }

    @Mock private CompositeTransaction compositeTransaction;
    @Mock private CompositeCoordinator compositeCoordinator;
    @Mock private XAResource xaResource;

    private XAResourceTransaction sut;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(compositeTransaction.getCompositeCoordinator()).thenReturn(compositeCoordinator);
        when(compositeCoordinator.getCoordinatorId()).thenReturn("testTid");
        when(compositeTransaction.getState()).thenReturn(TxState.ACTIVE);
    }

    @Test
    public void givenSubSecondTimeoutRemaining_whenSetXAResource_thenTimeoutIsAtLeastOne()
            throws XAException {
        // 500ms remaining: (int) 500 / 1000 = 0 without the fix
        when(compositeTransaction.getTimeout()).thenReturn(500L);
        sut = new XAResourceTransaction(new FixtureResource(), compositeTransaction, "testRoot");

        sut.setXAResource(xaResource);

        // must NOT call setTransactionTimeout(0) — that tells the backend to use its own default
        verify(xaResource).setTransactionTimeout(1);
    }

    @Test
    public void givenFullSecondTimeoutRemaining_whenSetXAResource_thenTimeoutPassedThrough()
            throws XAException {
        // 5000ms remaining: (int) 5000 / 1000 = 5 — no truncation, passes through unchanged
        when(compositeTransaction.getTimeout()).thenReturn(5000L);
        sut = new XAResourceTransaction(new FixtureResource(), compositeTransaction, "testRoot");

        sut.setXAResource(xaResource);

        verify(xaResource).setTransactionTimeout(5);
    }
}
