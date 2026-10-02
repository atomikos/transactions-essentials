/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import static org.mockito.Mockito.when;

import javax.transaction.xa.XAResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atomikos.datasource.ResourceException;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.recovery.TxState;

/**
 * Guards that enlistment on a doomed transaction (timed out or rollback-only)
 * is refused at the XA layer before XA START is sent to the backend.
 *
 * See Curriculum Bible: "XA Branch Lifecycle" for the TMJOIN/TMNOFLAGS
 * distinction; "Transaction Timeout Semantics" for getTimeout() <= 0.
 * Design decisions in decision-journal 2026-06-28-case-227307-doomed-enlistment.md.
 * cf case 227307
 */
public class XAResourceTransactionDoomedEnlistmentTestJUnit {

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

    private XAResourceTransaction sut;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(compositeTransaction.getCompositeCoordinator()).thenReturn(compositeCoordinator);
        when(compositeCoordinator.getCoordinatorId()).thenReturn("testTid");
        when(compositeTransaction.getTimeout()).thenReturn(-1000L);
        when(compositeTransaction.getState()).thenReturn(TxState.ACTIVE);
        FixtureResource resource = new FixtureResource();
        sut = new XAResourceTransaction(resource, compositeTransaction, "testRoot");
        sut.setXAResource(xaResource);
        // guard is ON by default (defaults.properties sets flag=true)
        Configuration.getConfigProperties().setProperty(FLAG, "true");
    }

    @After
    public void tearDown() {
        Configuration.getConfigProperties().setProperty(FLAG, "true");
    }

    @Test(expected = ResourceException.class)
    public void givenTimedOutTransactionAndGuardOn_whenResume_thenResourceException()
            throws Exception {
        sut.resume();
    }

    @Test(expected = ResourceException.class)
    public void givenRollbackOnlyTransactionAndGuardOn_whenResume_thenResourceException()
            throws Exception {
        when(compositeTransaction.getTimeout()).thenReturn(10000L);
        when(compositeTransaction.getState()).thenReturn(TxState.MARKED_ABORT);
        sut.resume();
    }

    @Test
    public void givenTimedOutTransactionAndGuardDisabled_whenResume_thenNoGuardException()
            throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "false"); // escape hatch: old behavior
        try {
            sut.resume();
        } catch (ResourceException e) {
            if (e.getMessage() != null && e.getMessage().contains("doomed")) {
                throw new AssertionError("Guard must not fire when disabled via escape hatch", e);
            }
            // other ResourceException (e.g. XAException from mock XAResource) is acceptable
        }
    }

    @Test
    public void givenActiveTransactionWithTimeRemaining_whenResume_thenNoGuardException()
            throws Exception {
        when(compositeTransaction.getTimeout()).thenReturn(30000L);
        when(compositeTransaction.getState()).thenReturn(TxState.ACTIVE);
        try {
            sut.resume();
        } catch (ResourceException e) {
            if (e.getMessage() != null && e.getMessage().contains("doomed")) {
                throw new AssertionError("Guard must not fire for active transaction", e);
            }
        }
    }
}
