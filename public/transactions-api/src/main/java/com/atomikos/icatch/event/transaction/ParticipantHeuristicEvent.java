/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event.transaction;

import com.atomikos.recovery.TxState;

/**
 * Signals heuristic outcome on behalf of a participant.
 */
public class ParticipantHeuristicEvent extends TransactionEvent {
	
	public final String participantUri;
	public final TxState state;
	
	public ParticipantHeuristicEvent(String transactionId, String participantUri, TxState state) {
		super(transactionId);
		this.participantUri = participantUri;
		this.state = state;
	}
	
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("Heuristic state detected: ").append(state).
			append(" for participant ").append(participantUri).
			append(" in transaction: ").append(transactionId).
            append(" (HINT: check https://www.atomikos.com/Documentation/HowToHandleHeuristics to learn more on how to handle heuristics...)");

		return ret.toString();
	}
}
