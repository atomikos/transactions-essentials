package com.atomikos.icatch.event;


public class TransactionCommittedEvent extends TransactionEvent {

	public TransactionCommittedEvent(String transactionId) {
		super(transactionId);
	}

	private static final long serialVersionUID = 1L;

}
