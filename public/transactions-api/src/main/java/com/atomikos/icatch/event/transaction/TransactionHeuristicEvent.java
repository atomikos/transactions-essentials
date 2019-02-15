/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event.transaction;

import com.atomikos.recovery.TxState;

/**
 * Signals heuristic outcomes. Multiple of these events can be raised for the same
 * transaction, since the transaction core will typically retry to terminate
 * heuristic transactions.
 */
public class TransactionHeuristicEvent extends TransactionEvent {
	
	private static final long serialVersionUID = 1L;
	public final String participantUri;
	public final TxState state;
	
	public TransactionHeuristicEvent(String transactionId, String participantUri, TxState state) {
		super(transactionId);
		this.participantUri = participantUri;
		this.state = state;
	}
	
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("Heuristic state detected: ").append(state).
			append(" for participant ").append(participantUri).
			append(" in transaction: ").append(transactionId);
		return ret.toString();
	}
}
