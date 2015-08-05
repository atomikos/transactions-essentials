package com.atomikos.datasource.pool.event;

public class ConnectionPoolExhaustedEvent extends ConnectionPoolEvent {

	private static final long serialVersionUID = 1L;

	public ConnectionPoolExhaustedEvent(String uniqueResourceName) {
		super(uniqueResourceName);
	}

}
