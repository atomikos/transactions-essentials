package com.atomikos.recovery;

import java.io.Serializable;

import com.atomikos.icatch.TxState;

public class ParticipantLogEntry implements Serializable {

	private static final long serialVersionUID = 1L;

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
	 * Useful description for administration purposes.
	 */
	public final String description;
	
	public ParticipantLogEntry(String coordinatorId, String uri, 
			long expires, String description, TxState state) {
		this.coordinatorId = coordinatorId;
		this.uri = uri;
		this.expires = expires;
		this.description = description;
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
				+ ", state=" + state + ", description=" + description + "]";
	}


}
