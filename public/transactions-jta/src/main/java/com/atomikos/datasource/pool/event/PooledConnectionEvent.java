package com.atomikos.datasource.pool.event;

import com.atomikos.datasource.pool.XPooledConnection;

abstract class PooledConnectionEvent extends ConnectionPoolEvent {

	private static final long serialVersionUID = 1L;
	
	public PooledConnectionEvent(String uniqueResourceName, XPooledConnection pc) {
		super(uniqueResourceName);
	}

}