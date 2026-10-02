/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

// cf case 230638
/**
 * Behaviour spec for {@link RecoveryDecisionArbiter}: the first decision ever
 * recorded for a coordinator wins and binds every subsequent call for that
 * coordinator - regardless of which branch, actor, or moment made it. Tested
 * against an in-memory {@link RecoveryDecisionStore} so these cases cover the
 * arbitration RULE only; the real, JDBC-backed store (with its own retention
 * requirement per LogCloudDecisionGC.tla) is a separate increment.
 */
public class RecoveryDecisionArbiterTestJUnit {

	private RecoveryDecisionArbiter arbiter;

	@Before
	public void setUp() {
		arbiter = new RecoveryDecisionArbiter(new InMemoryRecoveryDecisionStore());
	}

	@Test
	public void firstDecisionForACoordinatorWins() {
		// given a coordinator with no decision recorded yet
		// when its first branch wants to abort
		boolean result = arbiter.shouldCommit("coord-1", false);

		// then that abort becomes the binding outcome
		assertFalse(result);
	}

	@Test
	public void laterBranchOfSameCoordinatorFollowsTheEarlierAbortEvenIfItNowWantsToCommit() {
		// given a coordinator whose first branch already presumed-aborted (records ABORT)
		arbiter.shouldCommit("coord-1", false);

		// when a later branch now sees COMMITTING and wants to commit
		boolean b2Result = arbiter.shouldCommit("coord-1", true);

		// then it must follow the already-recorded ABORT, not its own newer belief
		assertFalse("b2 must follow the already-recorded ABORT, not its own newer belief", b2Result);
	}

	@Test
	public void onceCommittedAlwaysCommitsForThatCoordinator() {
		// given a coordinator whose first branch recorded COMMIT
		arbiter.shouldCommit("coord-2", true);

		// when a later branch wants to abort
		boolean laterResult = arbiter.shouldCommit("coord-2", false);

		// then it must follow the already-recorded COMMIT
		assertTrue("a later branch must follow the already-recorded COMMIT", laterResult);
	}

	@Test
	public void twoActorsRacingForTheSameCoordinatorEndUpWithTheSameDecision() {
		// given two actors deciding the same coordinator, wanting opposite outcomes
		// when they each record their proposal
		boolean actorAResult = arbiter.shouldCommit("coord-3", false); // actor A wants ABORT
		boolean actorBResult = arbiter.shouldCommit("coord-3", true); // actor B wants COMMIT

		// then both end up on the FIRST recorded decision (actor A's ABORT)
		assertEquals("both actors must agree on the same outcome", actorAResult, actorBResult);
		assertFalse("the FIRST recorded decision (actor A's ABORT) must win", actorBResult);
	}

	@Test
	public void decisionsForDifferentCoordinatorsAreIndependent() {
		// given one coordinator that recorded ABORT
		arbiter.shouldCommit("coord-4", false);

		// when a different coordinator with no prior decision wants to commit
		boolean otherCoordResult = arbiter.shouldCommit("coord-5", true);

		// then its own COMMIT is recorded and wins, unaffected by the other coordinator
		assertTrue("coord-5 has no prior decision, so its own COMMIT is recorded and wins", otherCoordResult);
	}

	@Test
	public void forgetDiscardsTheRecordedDecision() {
		// given a coordinator whose branch recorded ABORT
		arbiter.shouldCommit("coord-6", false);

		// when its decision is forgotten and a fresh proposal to commit arrives
		arbiter.forget(Collections.singletonList("coord-6"));
		boolean afterForget = arbiter.shouldCommit("coord-6", true);

		// then nothing bound it, so the fresh proposal is recorded as new
		assertTrue("forget() must leave no binding decision behind for the coordinator", afterForget);
	}

}
