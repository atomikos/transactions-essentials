package com.atomikos.datasource.pool.event;

import com.atomikos.datasource.pool.XPooledConnection;


public class PooledConnectionDestroyedEvent extends PooledConnectionEvent {

	private static final long serialVersionUID = 1L;

	public PooledConnectionDestroyedEvent(String uniqueResourceName,
			XPooledConnection pc) {
		super(uniqueResourceName, pc);
	}
}
