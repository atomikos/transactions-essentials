/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;


/**
 * A pooling-capable object wrapping a physical connection to an underlying resource.
 */
public interface XPooledConnection<ConnectionType> 
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
	 * on the implementation. Calling this method will also clear any
	 * flags set by markAsBeingAcquiredIfAvailable.
	 * 
	 * @return
	 * @throws CreateConnectionException 
	 */
	ConnectionType createConnectionProxy() throws CreateConnectionException;
	
	/**
	 * Is the pooled connection broken ? 
	 * @return
	 */
	boolean isErroneous();
	
	/**
	 * Get the moment when the connection was created.
	 * @return
	 */
	long getCreationTime();
	
	void registerXPooledConnectionEventListener(XPooledConnectionEventListener<ConnectionType> listener);
	
	void unregisterXPooledConnectionEventListener(XPooledConnectionEventListener<ConnectionType> listener);

	/**
	 * Attempt to claim this connection for use.
	 * 
	 * @return True if the connection is available, and could be claimed for the caller of this method. 
	 * <b>In that case, it is up to the caller to call createConnectionProxy at a later time</b> (which will in turn trigger 
	 * a round-trip to the resource if there is any testQuery set) <b>or the pool will be leaking</b>.
	 */
	boolean markAsBeingAcquiredIfAvailable();


}
