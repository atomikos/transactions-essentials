package com.atomikos.datasource.pool.event;

import com.atomikos.datasource.pool.XPooledConnection;

public abstract class PooledConnectionEvent extends ConnectionPoolEvent {

	private static final long serialVersionUID = 1L;
	
	public String pooledConnectionId;

	public PooledConnectionEvent(String uniqueResourceName, XPooledConnection pc) {
		super(uniqueResourceName);
		this.pooledConnectionId = pc.toString();
	}

}