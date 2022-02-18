/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;
/**
 * The states for a distributed transaction system.
 */
//@formatter:off
public enum TxState {
	//OLTP States
	MARKED_ABORT 	(false, false),
	LOCALLY_DONE 	(false, false),
	COMMITTED 		(false, false),
	ABORTED 		(false, false),
	ABANDONED		(false, false),
	
	TERMINATED 		(true, true),
	HEUR_HAZARD 	(false, false, TERMINATED, ABANDONED),
	//Recoverable States
	HEUR_COMMITTED 	(false, false, TERMINATED, ABANDONED),
	HEUR_ABORTED 	(false, false, TERMINATED, ABANDONED),
	HEUR_MIXED 		(false, false, TERMINATED, ABANDONED),	
	COMMITTING 		(true, 	false, HEUR_ABORTED, HEUR_COMMITTED, HEUR_HAZARD, HEUR_MIXED, TERMINATED, ABANDONED),
	ABORTING 	 	(false, false, HEUR_ABORTED, HEUR_COMMITTED, HEUR_HAZARD, HEUR_MIXED, TERMINATED, ABANDONED),
	IN_DOUBT  	 	(true, 	false, ABORTING, COMMITTING, ABANDONED, TERMINATED),
	PREPARING 	 	(false, false, IN_DOUBT, ABORTING, TERMINATED, ABANDONED),
	ACTIVE 		 	(false, false, ABORTING, COMMITTING, PREPARING);

	private boolean recoverableState;
	
	private boolean finalState;
	
	private TxState[] legalNextStates;
	
	TxState (boolean recoverableState, boolean finalState, TxState... legalNextStates) {
		this.finalState=finalState;
		this.recoverableState=recoverableState;
		this.legalNextStates=legalNextStates;
	}
	
	
	public boolean isFinalState() {
		return finalState;
	}
	
	
	public boolean isFinalStateForOltp() {
		return isFinalState() || this == ABANDONED;
	}
	
	public boolean isRecoverableState() {
		return recoverableState;
	}
	
	public boolean transitionAllowedTo(TxState nextState) {
		//transition to the same state...
		if(nextState == this) {
			return true;
		}
			
		for (TxState txState : legalNextStates) {
			if(txState == nextState) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isOneOf(TxState... state) {
		for (int i = 0; i < state.length; i++) {
			if(this==state[i])
				return true;
		}
		return false;
	}
	
	public boolean isHeuristic() {
		return isOneOf(HEUR_ABORTED, HEUR_COMMITTED, HEUR_HAZARD, HEUR_MIXED);
	}
	
}
