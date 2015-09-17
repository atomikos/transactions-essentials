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
	ACTIVE 		 	(true, 	false),
	MARKED_ABORT 	(false, false),
	LOCALLY_DONE 	(false, false),
	PREPARING 	 	(false, false),
	IN_DOUBT  	 	(true, 	false),
	ABORTING 	 	(false, false),
	COMMITTING 		(true, 	false),
	SUSPENDED 		(false, false),
	HEUR_COMMITTED 	(true, 	false),
	HEUR_ABORTED 	(true, 	false),
	HEUR_MIXED 		(true, 	false),
	HEUR_HAZARD 	(true, 	false),
	TERMINATED 		(false, true),
	COMMITTED 		(false, false),
	ABORTED 		(false, false);

	private boolean recoverableState;
	
	private boolean finalState;
	
	TxState(boolean recoverableState, boolean finalState){
		this.finalState=finalState;
		this.recoverableState=recoverableState;
	}
	
	public boolean isFinalState() {
		return finalState;
	}
	
	public boolean isRecoverableState() {
		return recoverableState;
	}
	
}
