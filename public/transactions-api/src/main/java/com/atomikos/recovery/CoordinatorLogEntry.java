package com.atomikos.recovery;

import com.atomikos.icatch.TxState;

public class CoordinatorLogEntry {

	public final String coordinatorId;

	public final boolean wasCommitted;

	public ParticipantLogEntry[] participantDetails;

	public CoordinatorLogEntry(String coordinatorId, 
			ParticipantLogEntry[] participantDetails) {
		this(coordinatorId, false, participantDetails);
	}

	public CoordinatorLogEntry(String coordinatorId, 
			boolean wasCommitted, ParticipantLogEntry[] participantDetails) {
		this.coordinatorId = coordinatorId;
		this.wasCommitted = wasCommitted;
		this.participantDetails = participantDetails;
	}
	
	public TxState getState() {
		TxState ret = TxState.IN_DOUBT;
		
		return ret;
	}
}
