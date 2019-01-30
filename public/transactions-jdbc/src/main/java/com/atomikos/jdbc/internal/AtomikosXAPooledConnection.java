/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.XAConnection;

import com.atomikos.datasource.pool.AbstractXPooledConnection;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.xa.jdbc.JdbcTransactionalResource;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.datasource.xa.session.SessionHandleStateChangeListener;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class AtomikosXAPooledConnection extends AbstractXPooledConnection<Connection>
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
				if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace( "SessionHandleState terminated, firing XPooledConnectionTerminated event for " + AtomikosXAPooledConnection.this);
				updateLastTimeReleased();
				fireOnXPooledConnectionTerminated();
			}
		});
		
	}

	

	public synchronized void doDestroy(boolean reap) 
	{
		if (connection != null) {
			try {
					connection.close();
			} catch (SQLException e ) {
				//ignore but log
				LOGGER.logWarning ( this + ": error closing Connection: " , e );
			}
		}
		if (xaConnection != null) {
			try {
				xaConnection.close();
			} catch (SQLException e ) {
				//ignore but log
				LOGGER.logWarning ( this + ": error closing XAConnection: " , e );
			}
		}
		
		connection = null;
		xaConnection = null;
	}


	
	protected Connection doCreateConnectionProxy() throws CreateConnectionException
	{
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": creating connection proxy..." );
		JdbcConnectionProxyHelper.setIsolationLevel ( connection , getDefaultIsolationLevel() );
		AtomikosJdbcConnectionProxy proxy = new AtomikosJdbcConnectionProxy(connection, sessionHandleState);
		return proxy.createDynamicProxy();
	}

	protected void testUnderlyingConnection() throws CreateConnectionException {
		if ( isErroneous() ) throw new CreateConnectionException ( this + ": connection is erroneous" );
		if ( maxLifetimeExceeded()) throw new CreateConnectionException ( this + ": connection too old - will be replaced"  );
		
		int queryTimeout = Math.max(0, getBorrowConnectionTimeout());
		String testQuery = getTestQuery();
		
		if (testQuery != null) {
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": testing connection with query [" + testQuery + "]" );
			try {
				PreparedStatement stmt = connection.prepareStatement(testQuery);
				stmt.setQueryTimeout(queryTimeout);
				//use execute instead of executeQuery - cf case 58830
				stmt.execute();
				stmt.close();
			} catch ( Exception e) {
				//catch any Exception - cf case 22198
				throw new CreateConnectionException ( "Error executing testQuery" ,  e );
			}
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": connection tested OK" );
		}
		else {
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": no test query, testing connection with ping" );
			try {
				if (!connection.isValid(0)) {
					throw new CreateConnectionException ( "Connection is not valid" );
				}
			} catch (SQLException e) {
				throw new CreateConnectionException ( "Connection is not valid" );
			}
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
	
	public boolean canBeRecycledForCallingThread ()
	{
		boolean ret = false;
		
		CompositeTransactionManager tm = Configuration.getCompositeTransactionManager();
		if ( tm != null ) { //null for non-JTA use where recycling is pointless anyway
			CompositeTransaction current = tm.getCompositeTransaction();
			if ( ( current != null ) && ( current.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME) != null )) {
				ret = sessionHandleState.isInactiveInTransaction(current);
			}
		}
		
		return ret;
	}
	

}
