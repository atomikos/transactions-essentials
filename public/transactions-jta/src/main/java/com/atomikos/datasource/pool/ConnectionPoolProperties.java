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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

 /**
  * This interface describes connection
  * pool properties. 
  * 
  *
  */

public interface ConnectionPoolProperties 
{
	
	/**
	 * Gets the unique resource name.
	 * 
	 * @return
	 */
	public String getUniqueResourceName();

 
	/**
	 * Gets the maximum pool size.
	 * @return
	 */
	int getMaxPoolSize();
	
	/**
	 * Gets the minimum pool size.
	 * @return
	 */
	int getMinPoolSize();
	
	/**
	 * Gets the borrow connection timeout.
	 * @return
	 */
	int getBorrowConnectionTimeout();
	
	/**
	 * Gets the reap timeout.
	 * @return
	 */
	int getReapTimeout();

	/**
	 * Gets the max time in seconds a connection can stay idle before being closed.
	 * @return
	 */
	int getMaxIdleTime();
	
	/**
	 * Gets the maintenance interval of the pool's maintenance thread.
	 * @return
	 */
	int getMaintenanceInterval();
	
	
	/**
	 * Gets the query used to test connections.
	 * @return
	 */
	String getTestQuery();
	
	/**
	 * Tests whether local transactions are to be allowed or not.
	 * @return
	 */
	boolean getLocalTransactionMode();
	
	/**
	 * Gets the default isolation level preference. 
	 * 
	 * @return The level, or -1 if not set.
	 */
	int getDefaultIsolationLevel();
	
}
