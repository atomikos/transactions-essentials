/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// cf case 230638
/**
 * Default {@link RecoveryDecisionStore}: binding decisions are kept only for
 * the lifetime of this JVM. A cross-restart, cross-node backing store (e.g.
 * LogCloud-based) can replace this one where that durability is needed.
 */
public class InMemoryRecoveryDecisionStore implements RecoveryDecisionStore {

	private final Map<String, Decision> decisions = new ConcurrentHashMap<>();

	@Override
	public Decision tryRecordDecision(String coordinatorId, Decision decision) {
		Decision existing = decisions.putIfAbsent(coordinatorId, decision);
		return existing != null ? existing : decision;
	}

	@Override
	public void forget(Collection<String> coordinatorIds) {
		decisions.keySet().removeAll(coordinatorIds);
	}

}
