/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;


/**
 * A pooling-capable object wrapping a physical connection to an underlying resource.
 */
public interface XPooledConnection 
{
	
	/**
	 * Is the connection available to be taken out of the pool ? 
	 * @return connection isAvailable
	 */
	boolean isAvailable();
	
	
	/**
	 * Can the connection be recycled (if not available) for the calling thread? 
	 * @return connection canBeRecycled
	 */
  boolean canBeRecycledForCallingThread();
	
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
	 * @return lastTimeAcquired
	 */
	long getLastTimeAcquired();
	
	/**
	 * Get the last time the connection was released, i.e. the last time when
	 * it has become available.
	 * @return lastTimeReleased
	 */
	long getLastTimeReleased();
	
	/**
	 * Create a disposable connection object that acts a controller for
	 * the pooled connection. What exactly a connection object is depends
	 * on the implementation.
	 * @return disposable connection object
	 * @throws CreateConnectionException on exception
	 */
	Reapable createConnectionProxy() throws CreateConnectionException;
	
	/**
	 * Is the pooled connection broken ? 
	 * @return is pooled connection broken
	 */
	boolean isErroneous();
	
	/**
	 * Get the moment when the connection was created.
	 * @return connection creation time
	 */
	long getCreationTime();
	
	void registerXPooledConnectionEventListener(XPooledConnectionEventListener listener);
	
	@SuppressWarnings("unused")
  void unregisterXPooledConnectionEventListener(XPooledConnectionEventListener listener);
}
