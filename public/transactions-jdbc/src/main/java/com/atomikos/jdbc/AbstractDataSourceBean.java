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

package com.atomikos.jdbc;

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

import com.atomikos.datasource.pool.ConnectionPool;
import com.atomikos.datasource.pool.ConnectionPoolException;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.PoolExhaustedException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.jdbc.AtomikosSQLException;
import com.atomikos.jdbc.HeuristicDataSource;
import com.atomikos.util.IntraVmObjectFactory;
import com.atomikos.util.IntraVmObjectRegistry;
import com.atomikos.datasource.pool.ConnectionFactory;

 /**
  * 
  * 
  * Abstract data source bean with generic functionality.
  * 
  *
  */

public abstract class AbstractDataSourceBean 
implements HeuristicDataSource, ConnectionPoolProperties, Referenceable, Serializable
{
	
	static final int DEFAULT_ISOLATION_LEVEL_UNSET = -1;

	private int minPoolSize = 1;
	private int maxPoolSize = 1;
	private int borrowConnectionTimeout = 30;
	private int reapTimeout = 0;
	private int maxIdleTime = 60;
	private String testQuery;
	private int maintenanceInterval = 60;
	private int loginTimeout;
	private transient ConnectionPool connectionPool;
	private transient PrintWriter logWriter;
	private String resourceName;

	private int defaultIsolationLevel = DEFAULT_ISOLATION_LEVEL_UNSET;
	
	protected void throwAtomikosSQLException ( String msg ) throws AtomikosSQLException 
	{
		throwAtomikosSQLException ( msg , null );
	}
	
	protected void throwAtomikosSQLException ( String msg , Throwable cause ) throws AtomikosSQLException 
	{
		AtomikosSQLException.throwAtomikosSQLException ( msg  , cause );
	}

	/**
	 * Gets the minimum size of the pool. 
	 */
	public int getMinPoolSize() {
		return minPoolSize;
	}

	/**
	 * Sets the minimum pool size. The amount of pooled connections won't go
	 * below that value. The pool will open this amount of connections during
	 * initialization. Optional, defaults to 1.
	 * 
	 * @param minPoolSize
	 */
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	/**
	 * Get the maximum pool size. 
	 */
	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	/**
	 * Sets the maximum pool size. The amount of pooled connections won't go
	 * above this value. Optional, defaults to 1.
	 * 
	 * @param maxPoolSize
	 */
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	/**
	 * Sets both the minimal and maximal pool size. Required if the maxPoolSize is not set. 
	 */
	public void setPoolSize(int poolSize) {
		this.minPoolSize = poolSize; 
		this.maxPoolSize = poolSize;
	}

	/**
	 * Get the maximum amount of time in seconds the pool will block
	 * waiting for a connection to become available in the pool when it
	 * is empty. 
	 */
	public int getBorrowConnectionTimeout() {
		return borrowConnectionTimeout;
	}

	/**
	 * Sets the maximum amount of time in seconds the pool will block
	 * waiting for a connection to become available in the pool when it
	 * is empty. Optional.
	 * 
	 * @param borrowConnectionTimeout The time in seconds. Zero or negative means no waiting at all.
	 * Defaults to 30 seconds.
	 */
	public void setBorrowConnectionTimeout(int borrowConnectionTimeout) {
		this.borrowConnectionTimeout = borrowConnectionTimeout;
	}

	/**
	 * Get the amount of time in seconds the connection pool will allow a connection
	 * to be borrowed before claiming it back.
	 */
	public int getReapTimeout() {
		return reapTimeout;
	}

	/**
	 * Sets the amount of time (in seconds) that the connection pool will allow a connection
	 * to be in use, before claiming it back. Optional. 
	 * 
	 * @param reapTimeout The timeout in seconds. Zero means unlimited. Note that this value is 
	 * only an indication; the pool will check regularly as indicated by the maintenanceInteval property.
	 * Default is 0 (no timeout). 
	 */
	public void setReapTimeout(int reapTimeout) {
		this.reapTimeout = reapTimeout;
	}

	/**
	 * Sets the maintenance interval for the pool maintenance thread.
	 * Optional. 
	 * 
	 * @param maintenanceInterval The interval in seconds. If not set or not positive then the pool's default (60 secs) will be used.
	 */
	public void setMaintenanceInterval(int maintenanceInterval) {
		this.maintenanceInterval = maintenanceInterval;
	}

	/**
	 * Gets the maintenance interval as set.
	 */
	public int getMaintenanceInterval() {
		return this.maintenanceInterval;
	}

	/**
	 * Gets the maximum amount of time in seconds a connection can stay in the pool
	 * before being eligible for being closed during pool shrinking.
	 */
	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	/**
	 * Sets the maximum amount of seconds that unused excess connections should stay in the pool. Optional.
	 * 
	 * Note: excess connections are connections that are created above the minPoolSize limit.
	 * 
	 * @param maxIdleTime The preferred idle time for unused excess connections. Note that this value is 
	 * only an indication; the pool will check regularly as indicated by the maintenanceInteval property.
	 * The default is 60 seconds.
	 */
	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	/**
	 * Gets the SQL query used to test a connection before returning it. 
	 */
	public String getTestQuery() {
		return testQuery;
	}

	/**
	 * Sets the SQL query or statement used to validate a connection before returning it. Optional. 
	 * 
	 * @param testQuery - The SQL query or statement to validate the connection with. Note that 
	 * although you can specify updates here, these will NOT be part of any JTA transaction!
	 */
	public void setTestQuery(String testQuery) {
		this.testQuery = testQuery;
	}

	public int poolAvailableSize() {
		return connectionPool.availableSize();
	}

	public int poolTotalSize() {
		return connectionPool.totalSize();
	}

	public PrintWriter getLogWriter() throws SQLException {
		return logWriter;
	}

	public int getLoginTimeout() throws SQLException {
		return loginTimeout;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		this.logWriter = out;
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		this.loginTimeout = seconds;
	}

	public synchronized void init() throws AtomikosSQLException 
	{
		if ( Configuration.isInfoLoggingEnabled() ) Configuration.logDebug ( this + ": init..." );
		if (connectionPool != null)
			return;
		
		if (maxPoolSize < 1)
			throwAtomikosSQLException ( "Property 'maxPoolSize' must be greater than 0, was: " + maxPoolSize );
		if (minPoolSize < 0 || minPoolSize > maxPoolSize)
			throwAtomikosSQLException("Property 'minPoolSize' must be at least 0 and at most maxPoolSize, was: " + minPoolSize);
		if (getUniqueResourceName() == null)
			throwAtomikosSQLException("Property 'uniqueResourceName' cannot be null");
		
		try {
			//initialize JNDI infrastructure for lookup
			getReference();
			ConnectionFactory cf = doInit();
			connectionPool = new ConnectionPool(cf, this);
		
			
		
		} catch ( AtomikosSQLException e ) {
			//these are logged at creation time -> just rethrow
			throw e;
		} catch ( Exception ex) { 
			String msg =  "Cannot initialize AtomikosDataSourceBean";
			AtomikosSQLException.throwAtomikosSQLException ( msg , ex );
		}
		if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this + ": init done." );
	}
	
	public void close() 
	{
		if ( Configuration.isInfoLoggingEnabled() ) Configuration.logDebug ( this + ": close..." );
		if (connectionPool != null) {
			connectionPool.destroy();
		}
		connectionPool = null;
		doClose();
		try {
			IntraVmObjectRegistry.removeResource ( getUniqueResourceName() );
		} catch ( NameNotFoundException e ) {
			//ignore but log
			if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this + ": Error removing from JNDI" , e );
		}
		if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this + ": close done." );
	}
	
	protected abstract ConnectionFactory doInit() throws Exception;

	protected abstract void doClose();

	/* DataSource impl */

	public Connection getConnection ( HeuristicMessage msg ) throws SQLException 
	{
		if ( Configuration.isInfoLoggingEnabled() ) Configuration.logDebug ( this + ": getConnection ( " + msg + " )..." );
		Connection connection = null;
		
		init();
		
		try {
			connection = (Connection) connectionPool.borrowConnection ( msg );
			
		} catch (CreateConnectionException ex) {
			throwAtomikosSQLException("Failed to grow the connection pool", ex);
		} catch (PoolExhaustedException e) {
			throwAtomikosSQLException ("Connection pool exhausted - try increasing 'maxPoolSize' and/or 'borrowConnectionTimeout' on the DataSourceBean.");
		} catch (ConnectionPoolException e) {
			throwAtomikosSQLException("Error borrowing connection", e );
		}
		if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this + ": returning " + connection );
		return connection;
	}

	public Connection getConnection(String username, String password) throws SQLException 
	{
    	Configuration.logWarning ( this + ": getConnection ( user , password ) ignores authentication - returning default connection" );
		return getConnection();
	}

	/**
	 * Get the resource name. 
	 */
	public String getUniqueResourceName() {
		return resourceName;
	}

	/**
	 * Sets the resource name. Required.
	 * 
	 * @param resourceName An arbitrary user-specified value that identifies
	 * this datasource. It must be unique for recovery purposes.
	 */
	public void setUniqueResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	/**
	 * Tests whether local transactions are allowed - defaults to true
	 * for JDBC. This property is used by the pooling mechanism.
	 */
	public boolean getLocalTransactionMode() {
		return true;
	}

	public Reference getReference() throws NamingException 
	{	
		return IntraVmObjectFactory.createReference ( this , getUniqueResourceName() );		
	}
	
	public Connection getConnection() throws SQLException
	{
		StringHeuristicMessage m = null;
		return getConnection ( m );
	}

    public Connection getConnection ( String msg ) throws SQLException
    {
    	return getConnection ( new StringHeuristicMessage ( msg ) );
    }


    public Connection getConnection ( String user , String passwd , String msg )
            throws SQLException
    {
    	Configuration.logWarning ( this + ": getConnection ( user , password , msg ) ignores authentication - returning default connection" );
    	return getConnection ( msg );
    }

  
    public Connection getConnection ( String user , String passwd ,
            HeuristicMessage msg ) throws SQLException
    {
    	Configuration.logWarning ( this + ": getConnection ( user , password , msg ) ignores authentication - returning default connection" );
    	return getConnection ( msg );
    }

	/**
	 * Sets the default isolation level of connections returned by this datasource.
	 * Optional, defaults to the vendor-specific JDBC or DBMS settings.
	 * 
	 * @param defaultIsolationLevel The default isolation level. 
	 * Negative values are ignored and result in vendor-specific JDBC driver or DBMS internal defaults.
	 */
	public void setDefaultIsolationLevel(int defaultIsolationLevel) {
		this.defaultIsolationLevel = defaultIsolationLevel;
	}

	/**
	 * Gets the default isolation level for connections created by this datasource. 
	 * 
	 * @return The default isolation level, or -1 if no specific value was set.
	 * 
	 */
	public int getDefaultIsolationLevel() {
		return defaultIsolationLevel;
	}

	
}
