/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event.transaction;


/**
 * Signals heuristic outcomes. Multiple of these events can be raised for the same
 * transaction, since the transaction core will typically retry to terminate
 * heuristic transactions.
 */
public class TransactionHeuristicEvent extends TransactionEvent {
	
	private static final long serialVersionUID = 1L;

	public TransactionHeuristicEvent(String transactionId) {
		super(transactionId);
	}
}
