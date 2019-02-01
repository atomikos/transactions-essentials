/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Map;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;


class ReadOnlyParticipant implements Participant {


	//force set UID for backward log compatibility
	private static final long serialVersionUID = -2141189205744565090L;


	//keep coordinator ID for equality
	private final String coordinatorId;
	public ReadOnlyParticipant() {
		this.coordinatorId = null;
	}

	ReadOnlyParticipant ( CoordinatorImp coordinator )
	{
		this.coordinatorId = coordinator.getCoordinatorId();
	}

	public String getURI() {
		return null;
	}

	public void setCascadeList(Map<String, Integer> allParticipants) throws SysException {
	}

	public void setGlobalSiblingCount(int count) {
	}

	public int prepare() throws RollbackException, HeurHazardException,
			HeurMixedException, SysException {
		return Participant.READ_ONLY;
	}

	public void commit(boolean onePhase)
			throws HeurRollbackException, HeurHazardException,
			HeurMixedException, RollbackException, SysException {
	}

	public void rollback() throws HeurCommitException,
			HeurMixedException, HeurHazardException, SysException {
	}

	public void forget() {
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

	@Override
	public String toString() {
		return "ReadOnlyParticipant";
	}
	
	@Override
	public boolean isRecoverable() {
		return false;
	}
	@Override
	public String getResourceName() {
		return null;
	}


}
