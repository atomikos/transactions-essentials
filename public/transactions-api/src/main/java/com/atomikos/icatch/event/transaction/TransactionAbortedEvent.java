package com.atomikos.icatch.event.transaction;


public class TransactionAbortedEvent extends TransactionEvent {

	private static final long serialVersionUID = 1L;

	public TransactionAbortedEvent(String transactionId) {
		super(transactionId);
	}
}
