/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

import java.util.Collection;

// cf case 230638
/**
 * Durable, cross-actor storage for one binding {@link Decision} per
 * coordinator. Implementations MUST make {@link #tryRecordDecision} atomic:
 * the first decision ever recorded for a given coordinator id wins and is
 * returned to every caller from then on, regardless of what they each
 * proposed.
 */
public interface RecoveryDecisionStore {

	/**
	 * Atomically records the given decision for the coordinator if none is
	 * recorded yet.
	 *
	 * @return the decision now in effect for this coordinator: the caller's
	 *         own proposal if it was the first, or whichever decision was
	 *         already recorded otherwise.
	 */
	Decision tryRecordDecision(String coordinatorId, Decision decision);

	/**
	 * Discards the decisions recorded for the given coordinators, if any.
	 * Callers must only do this once a coordinator is fully resolved
	 * everywhere - discarding early can reopen the race this store exists to
	 * close. Takes the whole batch so an implementation can delete in one
	 * round-trip.
	 */
	void forget(Collection<String> coordinatorIds);

}
