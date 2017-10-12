/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event.transaction;


public class TransactionCreatedEvent extends TransactionEvent {
	
	private static final long serialVersionUID = 1L;

	public TransactionCreatedEvent(String transactionId) {
		super(transactionId);
	}
}
