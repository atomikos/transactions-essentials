/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.datasource.pool;

import com.atomikos.icatch.HeuristicMessage;

/**
 * A pooling-capable object wrapping a physical connection to an underlying resource.
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
	
	/**
	 * Get the moment when the connection was created.
	 * @return
	 */
	long getCreationTime();
	
	void registerXPooledConnectionEventListener(XPooledConnectionEventListener listener);
	
	void unregisterXPooledConnectionEventListener(XPooledConnectionEventListener listener);


}
