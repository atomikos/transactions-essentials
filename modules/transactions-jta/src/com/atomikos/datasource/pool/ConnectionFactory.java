package com.atomikos.datasource.pool;


public interface ConnectionFactory 
{
	
	/**
	 * Opens a new physical connection to the underlying resource and wraps it in a
	 * pooling-capable {@link XPooledConnection}.
	 * 
	 * @return the {@link XPooledConnection} wrapping the physical connection.
	 * 
	 * @throws CreateConnectionException If no connection could be created.
	 *
	 */
	XPooledConnection createPooledConnection() throws CreateConnectionException;

}
