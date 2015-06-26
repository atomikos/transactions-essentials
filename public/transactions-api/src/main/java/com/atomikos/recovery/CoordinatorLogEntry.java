package com.atomikos.recovery;

import com.atomikos.icatch.TxState;

public class CoordinatorLogEntry {

	public final String coordinatorId;

	public final TxState state;

	public final boolean wasCommitted;

	public CoordinatorLogEntry(String coordinatorId, TxState state) {
		this(coordinatorId, state, false);
	}

	public CoordinatorLogEntry(String coordinatorId, TxState state,
			boolean wasCommitted) {
		this.coordinatorId = coordinatorId;
		this.state = state;
		this.wasCommitted = wasCommitted;
	}
}
