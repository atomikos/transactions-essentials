/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import static com.atomikos.recovery.TxState.ABANDONED;
import static com.atomikos.recovery.TxState.COMMITTING;
import static com.atomikos.recovery.TxState.IN_DOUBT;
import static com.atomikos.recovery.TxState.TERMINATED;
import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.atomikos.recovery.TxState;

public class TxStateTestJUnit {
	@Test
	public void testCountStates() {
		TxState[] states = TxState.values();
		assertEquals(15, states.length);
	}

	@Test
	public void testRecoverableStates() throws Exception {
		Set<TxState> recoverableStates = EnumSet.of(IN_DOUBT, COMMITTING, TERMINATED);
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
	
	@Test
	public void testFinalStatesForOltp() {
	    Set<TxState> finalStates = EnumSet.of(TERMINATED, ABANDONED);
	    TxState[] states = TxState.values();
        for (TxState txState : states) {
            if (finalStates.contains(txState)) {
                Assert.assertTrue(txState.isFinalStateForOltp());
            } else {
                Assert.assertFalse(txState.isFinalStateForOltp());
            }
        }
	}
}
