/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch;

import static com.atomikos.icatch.TxState.COMMITTING;
import static com.atomikos.icatch.TxState.HEUR_ABORTED;
import static com.atomikos.icatch.TxState.HEUR_COMMITTED;
import static com.atomikos.icatch.TxState.HEUR_MIXED;
import static com.atomikos.icatch.TxState.IN_DOUBT;
import static com.atomikos.icatch.TxState.TERMINATED;
import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class TxStateTestJUnit {
	@Test
	public void testCountStates() {
		TxState[] states = TxState.values();
		assertEquals(15, states.length);
	}

	@Test
	public void testRecoverableStates() throws Exception {
		Set<TxState> recoverableStates = EnumSet.of(IN_DOUBT, COMMITTING, HEUR_COMMITTED, HEUR_ABORTED, HEUR_MIXED);
		TxState[] states = TxState.values();
		for (TxState txState : states) {
			if (recoverableStates.contains(txState)) {
				Assert.assertTrue(txState.isRecoverableState());
			} else {
				Assert.assertFalse(txState.isRecoverableState());
			}
		}
	}

	@Test
	public void testFinalStates() {
		Set<TxState> finalStates = EnumSet.of(TERMINATED);
		TxState[] states = TxState.values();
		for (TxState txState : states) {
			if (finalStates.contains(txState)) {
				Assert.assertTrue(txState.isFinalState());
			} else {
				Assert.assertFalse(txState.isFinalState());
			}
		}

	}
}
