package com.atomikos.recovery;

import com.atomikos.icatch.TxState;

public class CoordinatorLogEntry {

	public final String coordinatorId;

	public final TxState state;

	public final boolean wasCommitted;

	public String[] participantDetails;

	public CoordinatorLogEntry(String coordinatorId, TxState state,
			String[] participantDetails) {
		this(coordinatorId, state, false, participantDetails);
	}

	public CoordinatorLogEntry(String coordinatorId, TxState state,
			boolean wasCommitted, String[] participantDetails) {
		this.coordinatorId = coordinatorId;
		this.state = state;
		this.wasCommitted = wasCommitted;
		this.participantDetails = participantDetails;
	}
}
