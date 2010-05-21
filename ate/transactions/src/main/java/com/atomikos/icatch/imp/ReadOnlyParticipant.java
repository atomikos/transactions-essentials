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
	private String coordinatorId;
	
	public ReadOnlyParticipant ( CoordinatorImp coordinator ) 
	{
		this.coordinatorId = coordinator.getCoordinatorId();
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
