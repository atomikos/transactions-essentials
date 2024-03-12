/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event.transaction;

import com.atomikos.icatch.event.Event;


/**
 * Domain event raised whenever something significant happens in the transaction life cycle.
 * 
 */

public abstract class TransactionEvent extends Event {
	
	public final String transactionId;
	
	protected TransactionEvent(String transactionId) {
		this.transactionId = transactionId;
	}

}
