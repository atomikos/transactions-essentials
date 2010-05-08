package com.atomikos.datasource.pool;

public interface XPooledConnectionEventListener 
{
	
	/**
	 * fired when a connection changed its state to terminated 
	 * @param connection
	 */
	void onXPooledConnectionTerminated(XPooledConnection connection);

}
