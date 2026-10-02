/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;

// cf case 230638
/**
 * Behaviour spec for how {@link XARecoveryManager} picks its
 * {@link RecoveryDecisionStore}: a registered provider (e.g. a LogCloud
 * durable store) takes precedence, otherwise the in-memory default is used -
 * so open-source keeps its current behaviour with no provider present.
 */
public class XARecoveryManagerStoreSelectionTestJUnit {

	@Test
	public void withoutAProviderTheInMemoryDefaultIsUsed() {
		// given no RecoveryDecisionStore provider registered
		Iterator<RecoveryDecisionStore> noProviders = Collections.<RecoveryDecisionStore>emptyList().iterator();

		// when the store is selected
		RecoveryDecisionStore store = XARecoveryManager.selectStore(noProviders);

		// then the in-memory default is used
		assertTrue("expected the in-memory default when no provider is registered",
				store instanceof InMemoryRecoveryDecisionStore);
	}

	@Test
	public void aRegisteredProviderTakesPrecedenceOverTheDefault() {
		// given one registered RecoveryDecisionStore provider
		RecoveryDecisionStore provider = new FixedDecisionStore();
		Iterator<RecoveryDecisionStore> oneProvider = Collections.singletonList(provider).iterator();

		// when the store is selected
		RecoveryDecisionStore store = XARecoveryManager.selectStore(oneProvider);

		// then that provider is used, not the in-memory default
		assertSame("expected the registered provider to be used", provider, store);
	}

	private static class FixedDecisionStore implements RecoveryDecisionStore {
		@Override
		public Decision tryRecordDecision(String coordinatorId, Decision decision) {
			return decision;
		}

		@Override
		public void forget(Collection<String> coordinatorIds) {
		}
	}

}
