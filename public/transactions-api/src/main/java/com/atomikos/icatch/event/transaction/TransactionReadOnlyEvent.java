/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event.transaction;


public class TransactionReadOnlyEvent extends TransactionEvent {

	private static final long serialVersionUID = 1L;
	
	public TransactionReadOnlyEvent(String transactionId) {
		super(transactionId);
	}
}
