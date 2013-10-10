package com.atomikos.icatch.event;


public class TransactionCreatedEvent extends TransactionEvent {
	
	private static final long serialVersionUID = 1L;

	public TransactionCreatedEvent(String transactionId) {
		super(transactionId);
	}
}
