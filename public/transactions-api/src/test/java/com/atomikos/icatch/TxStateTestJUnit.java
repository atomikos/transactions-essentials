package com.atomikos.icatch;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class TxStateTestJUnit {
	@Test
	public void testCountStates() {
		TxState[] states = TxState.values();
		assertEquals(15, states.length);
	}

	@Test
	public void testRecoverableStates() throws Exception {
		Set<TxState> recoverableStates = new HashSet<TxState>();
		recoverableStates.add(TxState.ACTIVE);
		recoverableStates.add(TxState.IN_DOUBT);
		recoverableStates.add(TxState.COMMITTING);
		recoverableStates.add(TxState.HEUR_COMMITTED);
		recoverableStates.add(TxState.HEUR_ABORTED);
		recoverableStates.add(TxState.HEUR_HAZARD);
		recoverableStates.add(TxState.HEUR_MIXED);

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
		TxState[] states = TxState.values();
		for (TxState txState : states) {
			if (TxState.TERMINATED.equals(txState)) {
				Assert.assertTrue(txState.isFinalState());
			} else {
				Assert.assertFalse(txState.isFinalState());
			}
		}

	}
}
