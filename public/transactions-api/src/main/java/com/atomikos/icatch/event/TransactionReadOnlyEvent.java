package com.atomikos.icatch.event;


public class TransactionReadOnlyEvent extends TransactionEvent {

	public TransactionReadOnlyEvent(String transactionId) {
		super(transactionId);
	}

	private static final long serialVersionUID = 1L;

}
