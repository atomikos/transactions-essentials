/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;


 /**
  * Participant snapshot for logging and recovery purposes.
  *
  */

public class ParticipantLogEntry {

	/**
	 * The ID of the global transaction as known by the transaction core.
	 */
	
	public final String coordinatorId;
	
	/**
	 * Identifies the participant within the global transaction.
	 */
	
	public final String uri;

	/**
	 * When does this participant expire (expressed in millis since Jan 1, 1970)? 
	 */
	
	public final long expires;
	
	/**
	 * Best-known state of the participant.
	 */
	public final TxState state;
	
	/**
	 * For diagnostic purposes, null if not relevant.
	 */
	public final String resourceName;
	
	public ParticipantLogEntry(String coordinatorId, String uri, 
			long expires, String resourceName, TxState state) {
		this.coordinatorId = coordinatorId;
		this.uri = uri;
		this.expires = expires;
		this.resourceName = resourceName;
		this.state = state;
	}
	
	
	
	@Override
	public boolean equals(Object other) {
		boolean ret = false;
		if (other instanceof ParticipantLogEntry) {
			ParticipantLogEntry o = (ParticipantLogEntry) other;
			if (o.coordinatorId.equals(coordinatorId) && o.uri.equals(uri)) ret = true;
		}
		return ret;
	}
	
	@Override
	public int hashCode() {
		return coordinatorId.hashCode();
	}



	@Override
	public String toString() {
		return "ParticipantLogEntry [id=" + coordinatorId
				+ ", uri=" + uri + ", expires=" + expires
				+ ", state=" + state + ", resourceName=" + resourceName + "]";
	}


}
