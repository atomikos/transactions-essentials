/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event.transaction;

import java.util.Collection;

import com.atomikos.icatch.event.Event;

// cf case 230638
/**
 * Signals that a batch of coordinators' recovery work is fully resolved, so
 * any recovery-decision bookkeeping kept for them elsewhere can be discarded.
 * Carries the whole batch so a listener can clean up in one pass rather than
 * one round-trip per coordinator.
 */
public class CoordinatorsResolvedEvent extends Event {

	public final Collection<String> coordinatorIds;

	public CoordinatorsResolvedEvent(Collection<String> coordinatorIds) {
		this.coordinatorIds = coordinatorIds;
	}

	@Override
	public String toString() {
		return "Coordinators resolved: " + coordinatorIds;
	}

}
