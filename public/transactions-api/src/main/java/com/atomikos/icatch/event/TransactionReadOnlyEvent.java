package com.atomikos.icatch.event;


public class TransactionReadOnlyEvent extends TransactionEvent {

	private static final long serialVersionUID = 1L;
	
	public TransactionReadOnlyEvent(String transactionId) {
		super(transactionId);
	}
}
