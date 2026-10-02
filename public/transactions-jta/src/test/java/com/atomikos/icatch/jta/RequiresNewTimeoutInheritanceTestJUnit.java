/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import javax.transaction.Transaction;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.imp.CompositeTransactionManagerImp;

/**
 * Guards the opt-in behaviour of capping a REQUIRES_NEW transaction's
 * timeout to the remaining time of the parent transaction it suspends
 * (feature flag com.atomikos.icatch.feature.230271), so that an
 * application's end-to-end deadline survives across a REQUIRES_NEW
 * sub-step even though Tx1 and Tx2 remain fully independent transactions.
 *
 * See Curriculum Bible: Annex AJ (Transaction Timeout).
 * cf case 230271 (feature request following up on case 227307's
 * doomed-enlistment guard)
 */
public class RequiresNewTimeoutInheritanceTestJUnit {

    private static final String FLAG = "com.atomikos.icatch.feature.230271";

    // Tick granularity is CoordinatorImp.DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS
    // (150ms) - allow generous slack for tick rounding and scheduling jitter.
    private static final long TOLERANCE_MILLIS = 400L;

    private JtaTransactionServicePlugin plugin;
    private TransactionManagerImp tm;
    private CompositeTransactionManager ctm;

    @Before
    public void setUp() throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "false"); // opt-in: off unless a test turns it on
        plugin = new JtaTransactionServicePlugin();
        plugin.beforeInit();
        Configuration.installCompositeTransactionManager(new CompositeTransactionManagerImp());
        Configuration.init();
        plugin.afterInit();
        tm = (TransactionManagerImp) TransactionManagerImp.getTransactionManager();
        ctm = Configuration.getCompositeTransactionManager();
    }

    @After
    public void tearDown() throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "false");
        Configuration.shutdown(true);
        plugin.afterShutdown();
    }

    // --- Scenario 1: feature off -> no behaviour change ---

    @Test
    public void givenFeatureOffAndParentSuspendedWithLessRemainingThanDefault_whenBegin_thenTimeoutIsPlainDefault()
            throws Exception {
        // flag is off by default from setUp()
        tm.begin(6); // Tx1, 6s
        Thread.sleep(1500); // ~4.5s remaining on Tx1
        tm.suspend();

        tm.begin(3); // Tx2 requests 3s default
        CompositeTransaction ct2 = ctm.getCompositeTransaction();
        assertTimeoutWithinTolerance("feature off: Tx2 must get the plain requested default, unaffected by Tx1",
                3000L, ct2);
    }

    // --- Scenario 2: feature on, parent has LESS remaining than the default ---

    @Test
    public void givenFeatureOnAndParentRemainingLessThanDefault_whenBegin_thenTimeoutIsCappedToParentRemaining()
            throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "true");
        tm.begin(3); // Tx1, 3s
        Thread.sleep(2000); // ~1s remaining on Tx1
        tm.suspend();

        tm.begin(5); // Tx2 requests a 5s default
        CompositeTransaction ct2 = ctm.getCompositeTransaction();
        assertTimeoutWithinTolerance("Tx2 must be capped to Tx1's remaining time, not the 5s default",
                1000L, ct2);
    }

    // --- Scenario 3: feature on, parent has MORE remaining than the default (cap-only, never extend) ---

    @Test
    public void givenFeatureOnAndParentRemainingMoreThanDefault_whenBegin_thenTimeoutStaysAtDefault()
            throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "true");
        tm.begin(10); // Tx1, 10s
        Thread.sleep(500); // ~9.5s remaining on Tx1
        tm.suspend();

        tm.begin(2); // Tx2 requests a 2s default
        CompositeTransaction ct2 = ctm.getCompositeTransaction();
        assertTimeoutWithinTolerance("Tx2 must stay at its own 2s default; the feature never extends beyond it",
                2000L, ct2);
    }

    // --- Scenario 4: feature on, no suspended ancestor -> no effect ---

    @Test
    public void givenFeatureOnAndNoSuspendedAncestor_whenBegin_thenTimeoutIsPlainDefault() throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "true");

        tm.begin(2); // no prior suspend on this thread
        CompositeTransaction ct = ctm.getCompositeTransaction();
        assertTimeoutWithinTolerance("with nothing suspended, there is nothing to inherit from",
                2000L, ct);
    }

    // --- Scenario 5: suspend/resume themselves never pause or alter the countdown ---

    @Test
    public void givenSuspendedTransaction_whenResumedLater_thenRemainingTimeReflectsFullElapsedWallClock()
            throws Exception {
        // Feature flag is irrelevant here: this guards CoordinatorImp's own
        // pre-existing countdown behaviour, not the new capping logic.
        tm.begin(6); // Tx1, 6s
        Thread.sleep(1000);
        Transaction suspended = tm.suspend();
        Thread.sleep(1500); // elapses WHILE suspended
        tm.resume(suspended);

        CompositeTransaction ct1 = ctm.getCompositeTransaction();
        assertTimeoutWithinTolerance(
                "elapsed time during suspend must count against the transaction just like active time",
                3500L, ct1);
    }

    // --- Scenario 6: sequential (non-nested) REQUIRES_NEW under the same still-suspended parent ---

    @Test
    public void givenTwoSequentialRequiresNewUnderSameSuspendedParent_whenSecondBegins_thenItInheritsTheSmallerCurrentRemaining()
            throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "true");
        tm.begin(6); // Tx1, 6s
        Thread.sleep(1000); // ~5s remaining
        tm.suspend();

        tm.begin(10); // Tx2, large default so the cap dominates -> expect ~5s
        CompositeTransaction ct2 = ctm.getCompositeTransaction();
        assertTimeoutWithinTolerance("Tx2 must inherit Tx1's remaining time at Tx2's own begin",
                5000L, ct2);
        ct2.rollback(); // end Tx2; Tx1 remains suspended (not resumed)

        Thread.sleep(1500); // Tx1 keeps aging while still suspended

        tm.begin(10); // Tx3, same large default -> expect ~3.5s, smaller than Tx2 got
        CompositeTransaction ct3 = ctm.getCompositeTransaction();
        assertTimeoutWithinTolerance(
                "Tx3 must inherit Tx1's CURRENT remaining time (re-queried), not a stale snapshot from Tx1's suspend",
                3500L, ct3);
    }

    // --- Scenario 7: nested REQUIRES_NEW inherits from the most-recently-suspended ancestor ---

    @Test
    public void givenTwoNestedSuspendedAncestors_whenBegin_thenItInheritsFromTheMostRecentlySuspendedOne()
            throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "true");
        tm.begin(8); // Tx1, 8s
        Thread.sleep(500); // ~7.5s remaining on Tx1
        tm.suspend();

        tm.begin(3); // Tx2, 3s - comfortably less than Tx1's ~7.5s remaining, so Tx2 itself is NOT capped
        CompositeTransaction ct2 = ctm.getCompositeTransaction();
        assertTimeoutWithinTolerance("sanity check: Tx2 must get its own uncapped 3s (< Tx1's remaining)",
                3000L, ct2);
        Thread.sleep(500); // ~2.5s remaining on Tx2
        tm.suspend(); // now both Tx1 (~7s) and Tx2 (~2.5s) are suspended, Tx2 on top

        tm.begin(10); // Tx3, large default so the cap dominates
        CompositeTransaction ct3 = ctm.getCompositeTransaction();
        assertTimeoutWithinTolerance(
                "Tx3 must inherit from Tx2 (most recently suspended), not from the Tx1 root (~7s would fail this)",
                2500L, ct3);
    }

    // --- Scenario 8: near-zero remaining time -> no artificial floor ---

    @Test
    public void givenParentRemainingIsNearZero_whenBegin_thenTimeoutIsNotBumpedToAnyFloor() throws Exception {
        Configuration.getConfigProperties().setProperty(FLAG, "true");
        tm.begin(1); // Tx1, 1s
        Thread.sleep(900); // ~100ms remaining on Tx1

        tm.suspend();
        tm.begin(5); // Tx2 requests a 5s default
        CompositeTransaction ct2 = ctm.getCompositeTransaction();

        long actual = ct2.getTimeout();
        Assert.assertTrue(
                "no floor: Tx2's capped timeout must stay small (near Tx1's ~100ms remaining), actual=" + actual + "ms",
                actual <= 300L);
        Assert.assertTrue(
                "capped timeout must not be negative, actual=" + actual + "ms",
                actual >= 0L);
    }

    // --- helper ---

    private static void assertTimeoutWithinTolerance(String message, long expectedMillis, CompositeTransaction ct) {
        long actual = ct.getTimeout();
        Assert.assertTrue(
                message + " (expected ~" + expectedMillis + "ms +/- " + TOLERANCE_MILLIS + "ms, actual=" + actual + "ms)",
                Math.abs(actual - expectedMillis) <= TOLERANCE_MILLIS);
    }
}
