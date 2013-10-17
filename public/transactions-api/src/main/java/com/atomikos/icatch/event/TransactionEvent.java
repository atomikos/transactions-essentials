package com.atomikos.icatch.event;


/**
 * Domain event raised whenever something significant happens in the transaction life cycle.
 * 
 */

public abstract class TransactionEvent extends Event {

	private static final long serialVersionUID = 1L;
	
	public String transactionId;
	
	protected TransactionEvent(String transactionId) {
		this.transactionId = transactionId;
	}

}
