package com.atomikos.recovery;

import java.io.Serializable;

public class ParticipantLogEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The ID of the global transaction as known by the transaction core.
	 */
	
	public final String coordinatorId;
	
	/**
	 * Identifies the participant within the global transaction.
	 */
	
	public final String participantUri;

	/**
	 * When does this participant expire (expressed in millis since Jan 1, 1970)? 
	 */
	
	public long expires;
	
	public ParticipantLogEntry(String coordinatorId, String participantUri, long expires) {
		this.coordinatorId = coordinatorId;
		this.participantUri = participantUri;
	}
	
	
	
	@Override
	public boolean equals(Object other) {
		boolean ret = false;
		if (other instanceof ParticipantLogEntry) {
			ParticipantLogEntry o = (ParticipantLogEntry) other;
			if (o.coordinatorId.equals(coordinatorId) && o.participantUri.equals(participantUri)) ret = true;
		}
		return ret;
	}
	
	@Override
	public int hashCode() {
		return coordinatorId.hashCode();
	}
	
}
