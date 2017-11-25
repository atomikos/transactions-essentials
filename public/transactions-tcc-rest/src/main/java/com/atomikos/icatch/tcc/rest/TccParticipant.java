/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.tcc.rest;

import java.util.Map;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

class TccParticipant implements Participant {

	
	private static final long serialVersionUID = 1L;
	private ParticipantAdapter resource;

	public TccParticipant(ParticipantAdapter resource) {
		this.resource = resource;
	}

	@Override
	public String getURI() {
		return resource.getUri();
	}

	@Override
	public void setCascadeList(Map<String, Integer> allParticipants) throws SysException {
	}

	@Override
	public void setGlobalSiblingCount(int count) {
	}

	@Override
	public int prepare() throws RollbackException, HeurHazardException,
			HeurMixedException, SysException {
		long roundTripTime = checkRoundtripTime();
		if (insufficientTimeLeft(roundTripTime)) throw new RollbackException("Insufficient time left to confirm");
		return Participant.READ_ONLY + 1;
	}

	private boolean insufficientTimeLeft(long roundTripTime) {
		long now = System.currentTimeMillis();
		return (now + 2*roundTripTime) > getExpiryDateTime();
	}

	private long getExpiryDateTime() {
		return resource.getExpires();
	}

	private long checkRoundtripTime() {
		long start = System.currentTimeMillis();
		resource.options();
		long end = System.currentTimeMillis();
		return (end - start);
	}

	@Override
	public void commit(boolean onePhase)
			throws HeurRollbackException, HeurHazardException,
			HeurMixedException, RollbackException, SysException {
		resource.put();
	}

	@Override
	public void rollback() throws HeurCommitException,
			HeurMixedException, HeurHazardException, SysException {
		resource.delete();
	}

	@Override
	public void forget() {
	}
	
	@Override
	public String toString() {
		return getURI();
	}

	@Override
	public boolean isRecoverable() {
		return true;
	}

	@Override
	public String getResourceName() {
		return null;
	}

}
