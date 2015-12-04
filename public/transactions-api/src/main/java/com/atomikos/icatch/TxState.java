/**
 * Copyright (C) 2000-2013 Atomikos <info@atomikos.com>
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


/**
 * The states for a distributed transaction system.
 */

public enum TxState {
	//@formatter:off
	MARKED_ABORT 	(false, false),
	LOCALLY_DONE 	(false, false),
	SUSPENDED 		(false, false),
	TERMINATED 		(false, true),
	
	HEUR_COMMITTED 	(true, 	false, TERMINATED),
	
	/**
	 * @deprecated TODO replace by COMMITING or ABORTING where relevant
	 */
	HEUR_HAZARD 	(false, false, TERMINATED),
	HEUR_ABORTED 	(true, 	false, TERMINATED),
	HEUR_MIXED 		(true, 	false, TERMINATED),	
	COMMITTING 		(true, 	false, TERMINATED, HEUR_ABORTED, HEUR_COMMITTED, HEUR_HAZARD, HEUR_MIXED),
	ABORTING 	 	(false, false, TERMINATED, HEUR_ABORTED, HEUR_COMMITTED, HEUR_HAZARD, HEUR_MIXED),
	IN_DOUBT  	 	(true, 	false, ABORTING, COMMITTING, TERMINATED),
	PREPARING 	 	(false, false, TERMINATED, IN_DOUBT, ABORTING),
	ACTIVE 		 	(true, 	false, ABORTING, COMMITTING, PREPARING),
	
	COMMITTED 		(false, false),
	ABORTED 		(false, false);

	private boolean recoverableState;
	
	private boolean finalState;
	
	
	private TxState[] legalNextStates;
	
	TxState(boolean recoverableState, boolean finalState){
		this.finalState=finalState;
		this.recoverableState=recoverableState;
		legalNextStates = new TxState[0];
	}
	TxState(boolean recoverableState, boolean finalState,TxState... legalNextStates){
		this.finalState=finalState;
		this.recoverableState=recoverableState;
		this.legalNextStates=legalNextStates;
	}
	
	public boolean isFinalState() {
		return finalState;
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
	
}
