/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

import java.util.Collection;

// cf case 230638; formally analysed under specs/architecture/formal/LogCloudRealRecoveryAlgorithm.tla + LogCloudRecoveryRace.tla
/**
 * Closes the recovery Atomicity gap: the FIRST decision ever recorded for a
 * coordinator, by any actor, on any branch, at any time, is binding for every
 * subsequent branch of that same coordinator - never re-decided from newer,
 * locally-visible information.
 */
public class RecoveryDecisionArbiter {

	private final RecoveryDecisionStore store;

	public RecoveryDecisionArbiter(RecoveryDecisionStore store) {
		this.store = store;
	}

	/**
	 * @param wantsToCommit what the caller currently believes is right for
	 *        this branch, based on its own (possibly stale) view.
	 * @return whether to actually commit - the coordinator's binding
	 *         decision, which may differ from {@code wantsToCommit} if
	 *         another branch or actor already decided otherwise.
	 */
	public boolean shouldCommit(String coordinatorId, boolean wantsToCommit) {
		Decision mine = wantsToCommit ? Decision.COMMIT : Decision.ABORT;
		Decision binding = store.tryRecordDecision(coordinatorId, mine);
		return binding == Decision.COMMIT;
	}

	/**
	 * Discards the binding decisions for the given coordinators - only safe
	 * once each is fully resolved everywhere (cf {@link RecoveryDecisionStore#forget}).
	 */
	public void forget(Collection<String> coordinatorIds) {
		store.forget(coordinatorIds);
	}

}
