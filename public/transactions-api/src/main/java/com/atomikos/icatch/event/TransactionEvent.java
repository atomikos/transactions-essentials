package com.atomikos.icatch.event;

import java.io.Serializable;

/**
 * Domain event raised whenever something significant happens in the transaction life cycle.
 * 
 */

public abstract class TransactionEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String transactionId;
	
	public TransactionEvent(String transactionId) {
		this.transactionId = transactionId;
	}

}
