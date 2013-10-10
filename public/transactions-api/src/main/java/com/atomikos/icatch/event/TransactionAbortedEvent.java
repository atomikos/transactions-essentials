package com.atomikos.icatch.event;


public class TransactionAbortedEvent extends TransactionEvent {

	public TransactionAbortedEvent(String transactionId) {
		super(transactionId);
	}

	private static final long serialVersionUID = 1L;


}
