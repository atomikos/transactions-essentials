package com.atomikos.icatch.event;


public class TransactionCommittedEvent extends TransactionEvent {

	private static final long serialVersionUID = 1L;

	public TransactionCommittedEvent(String transactionId) {
		super(transactionId);
	}
}
