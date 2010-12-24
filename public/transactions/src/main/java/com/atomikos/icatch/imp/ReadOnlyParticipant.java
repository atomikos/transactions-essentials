/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.imp;

import java.util.Dictionary;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;


public class ReadOnlyParticipant implements Participant {

	StringHeuristicMessage[] msgs;
	
	//keep coordinator ID for equality
	private final String coordinatorId;
	private final int hashCode;
	public ReadOnlyParticipant ( CoordinatorImp coordinator ) 
	{
		this.coordinatorId = coordinator.getCoordinatorId();
		int ret = 1;
		if ( coordinatorId != null ) ret = coordinatorId.hashCode();
		hashCode= ret;
		msgs = new StringHeuristicMessage[1];
		msgs[0] = new StringHeuristicMessage ( "ReadOnlyParticipant" );
	}
	
	public boolean recover() throws SysException {
		return true;
	}

	public String getURI() {
		
		return null;
	}

	public void setCascadeList(Dictionary allParticipants) throws SysException {
		

	}

	public void setGlobalSiblingCount(int count) {
		

	}

	public int prepare() throws RollbackException, HeurHazardException,
			HeurMixedException, SysException {
		return Participant.READ_ONLY;
	}

	public HeuristicMessage[] commit(boolean onePhase)
			throws HeurRollbackException, HeurHazardException,
			HeurMixedException, RollbackException, SysException {
		return msgs;
	}

	public HeuristicMessage[] rollback() throws HeurCommitException,
			HeurMixedException, HeurHazardException, SysException {
		return msgs;
	}

	public void forget() {
		

	}

	public HeuristicMessage[] getHeuristicMessages() {
		return msgs;
	}
	
	
	
	public boolean equals ( Object o ) {
		boolean ret = false;
		if ( o instanceof ReadOnlyParticipant && coordinatorId != null ) {
			ReadOnlyParticipant other = ( ReadOnlyParticipant ) o;
			ret = coordinatorId.equals ( other.coordinatorId );
		}
		return ret;
	}
	
	public int hashCode()
	{
		int ret = 1;
		if ( coordinatorId != null ) ret = coordinatorId.hashCode();
		return ret;
	}
	
	

}
