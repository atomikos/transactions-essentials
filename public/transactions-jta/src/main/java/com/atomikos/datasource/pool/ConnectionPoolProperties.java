/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;


 /**
  * This interface describes connection
  * pool properties. 
  * 
  *
  */

public interface ConnectionPoolProperties 
{
	int DEFAULT_ISOLATION_LEVEL_UNSET = -1;
	int DEFAULT_POOL_SIZE = 1;
	int DEFAULT_BORROW_CONNECTION_TIMEOUT = 30;
	int DEFAULT_REAP_TIMEOUT = 0;
	int DEFAULT_MAX_IDLE_TIME = 60;
	int DEFAULT_MAINTENANCE_INTERVAL = 60;
	int DEFAULT_MAX_LIFETIME = 0;
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
	 * Gets the max time in seconds a connection can stay idle before being closed.
	 * @return
	 */
	int getMaxIdleTime();
	
	/**
	 * Gets the max time in seconds that a connection may be kept alive.
	 * @return
	 */
	int getMaxLifetime();
	
	/**
	 * Gets the reap timeout.
	 * @return
	 */
	int getReapTimeout();
	
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
