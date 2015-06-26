package com.atomikos.recovery;

import com.atomikos.icatch.TxState;

public class CoordinatorLogEntry {

	public final String coordinatorId;

	public final TxState state;

	public CoordinatorLogEntry(String coordinatorId, TxState state) {
		this.coordinatorId = coordinatorId;
		this.state = state;
	}
}
