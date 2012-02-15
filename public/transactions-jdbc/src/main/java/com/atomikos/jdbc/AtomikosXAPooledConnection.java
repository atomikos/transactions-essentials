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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.XAConnection;

import com.atomikos.datasource.pool.AbstractXPooledConnection;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.datasource.xa.jdbc.JdbcTransactionalResource;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;

public class AtomikosXAPooledConnection extends AbstractXPooledConnection
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosXAPooledConnection.class);
	

	

	private SessionHandleState sessionHandleState;
	private XAConnection xaConnection;
	private Connection connection;


	public AtomikosXAPooledConnection ( XAConnection xaConnection, 
			JdbcTransactionalResource jdbcTransactionalResource, 
			ConnectionPoolProperties props ) 
	throws SQLException 
	{
		super ( props );
		this.xaConnection = xaConnection;
		this.connection = xaConnection.getConnection();
		this.sessionHandleState = new SessionHandleState ( jdbcTransactionalResource, xaConnection.getXAResource());
		sessionHandleState.registerSessionHandleStateChangeListener(new SessionHandleStateChangeListener() {
			public void onTerminated() {
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug( "SessionHandleState terminated, firing XPooledConnectionTerminated event for " + AtomikosXAPooledConnection.this);
				updateLastTimeReleased();
				fireOnXPooledConnectionTerminated();
			}
		});
		
	}

	

	public void destroy() 
	{
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": destroying connection..." );
		if (connection != null) {
			try {
					connection.close();
			} catch (SQLException e ) {
				//ignore but log
				Configuration.logWarning ( this + ": error closing Connection: " , e );
			}
		}
		if (xaConnection != null) {
			try {
				xaConnection.close();
			} catch (SQLException e ) {
				//ignore but log
				Configuration.logWarning ( this + ": error closing XAConnection: " , e );
			}
		}
		
		connection = null;
		xaConnection = null;
	}


	
	protected Reapable doCreateConnectionProxy ( HeuristicMessage hmsg ) throws CreateConnectionException
	{
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": creating connection proxy..." );
		JdbcConnectionProxyHelper.setIsolationLevel ( connection , getDefaultIsolationLevel() );
		return AtomikosConnectionProxy.newInstance ( connection , sessionHandleState , hmsg );
	}

	protected void testUnderlyingConnection() throws CreateConnectionException {
		if ( isErroneous() ) throw new CreateConnectionException ( this + ": connection is erroneous" );
		String testQuery = getTestQuery();
		if (testQuery != null) {
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": testing connection with query [" + testQuery + "]" );
			Statement stmt = null;
			try {
				stmt = connection.createStatement();
				//use execute instead of executeQuery - cf case 58830
				stmt.execute(testQuery);
				stmt.close();
			} catch ( Exception e) {
				//catch any Exception - cf case 22198
				throw new CreateConnectionException ( "Error executing testQuery" ,  e );
			}
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": connection tested OK" );
		}
		else {
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": no test query, skipping test" );
		}
	}

	public boolean isAvailable() 
	{
		boolean available = false;
		available = sessionHandleState.isTerminated() && (xaConnection != null);
		return available;
	}

	public boolean isErroneous() 
	{
		return sessionHandleState.isErroneous();
	}

	public String toString() {
		return "an AtomikosXAPooledConnection with " + sessionHandleState;
	}

	public boolean isInTransaction ( CompositeTransaction ct ) 
	{
		return sessionHandleState.isActiveInTransaction ( ct );
	}
	
	
	
	public boolean canBeRecycledForCallingThread ()
	{
		boolean ret = false;
		
		CompositeTransactionManager tm = Configuration.getCompositeTransactionManager();
		
		CompositeTransaction current = tm.getCompositeTransaction();
		if ( ( current != null ) && ( current.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME) != null )) {
			ret = sessionHandleState.isInactiveInTransaction(current);
		}
		
		return ret;
	}
	

}
