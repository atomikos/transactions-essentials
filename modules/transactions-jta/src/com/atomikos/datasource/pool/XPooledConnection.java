package com.atomikos.datasource.pool;

import com.atomikos.icatch.HeuristicMessage;

/**
 * A pooling-capable object wrapping a physical connection to an underlying resource.
 * @author lorban
 */
public interface XPooledConnection 
{
	
	/**
	 * Is the connection available to be taken out of the pool ? 
	 * @return 
	 */
	boolean isAvailable();
	
	
	/**
	 * Can the connection be recycled (if not available) for the calling thread? 
	 * @return
	 */
	public boolean canBeRecycledForCallingThread();
	
	/**
	 * Destroy the pooled connection by closing the underlying physical connection.
	 */
	void destroy();
	
	/**
	 * Invalidates (i.e., forces close) of the current connection proxy
	 * This does not mean that the connection can be reused immediately:
	 * pending transactions may exist that need to terminate. 
	 * 
	 * In other words: this acts as a forced close on the proxy.
	 */
	void reap();
	
	/**
	 * Get the last time the connection was acquired.
	 * @return
	 */
	long getLastTimeAcquired();
	
	/**
	 * Get the last time the connection was released, i.e. the last time when
	 * it has become available.
	 * @return
	 */
	long getLastTimeReleased();
	
	/**
	 * Create a disposable connection object that acts a controller for
	 * the pooled connection. What exactly a connection object is depends
	 * on the implementation.
	 * @param hmsg The heuristic message to show in the logs.
	 * @return
	 * @throws CreateConnectionException 
	 */
	Reapable createConnectionProxy ( HeuristicMessage hmsg ) throws CreateConnectionException;
	
	/**
	 * Is the pooled connection broken ? 
	 * @return
	 */
	boolean isErroneous();
	
	void registerXPooledConnectionEventListener(XPooledConnectionEventListener listener);
	
	void unregisterXPooledConnectionEventListener(XPooledConnectionEventListener listener);


}
