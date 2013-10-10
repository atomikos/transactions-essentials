package com.atomikos.icatch.event;


public class TransactionCreatedEvent extends TransactionEvent {
	public TransactionCreatedEvent(String transactionId) {
		super(transactionId);
	}

	private static final long serialVersionUID = 1L;

}
