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

package com.atomikos.jdbc.nonxa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.atomikos.datasource.pool.AbstractXPooledConnection;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.jdbc.JdbcConnectionProxyHelper;
import com.atomikos.util.DynamicProxy;

 /**
  * 
  * 
  * An implementation of XPooledConnection for non-xa drivers.
  * 
  *
  */

class AtomikosNonXAPooledConnection extends AbstractXPooledConnection
{
	
	private Connection connection;
	
	private boolean erroneous;
	
	private boolean readOnly;
	
	private ConnectionPoolProperties props;
	
	public AtomikosNonXAPooledConnection ( Connection wrapped , ConnectionPoolProperties props  , boolean readOnly ) 
	{
		super ( props );
		this.connection = wrapped;
		this.erroneous = false;
		this.readOnly = readOnly;
		this.props = props;
	}	
	
	void setErroneous() 
	{
		Configuration.logDebug ( this + ": setErroneous" );
		this.erroneous = true;
	}

	public void destroy() 
	{
		Configuration.logDebug ( this + ": destroying..." );
		try {
			if ( connection != null ) connection.close();
		} catch ( SQLException e ) {
			//ignore, just log
			Configuration.logWarning ( this + ": Error closing JDBC connection: " , e );
		}

	}

	protected Reapable doCreateConnectionProxy ( HeuristicMessage hmsg ) throws CreateConnectionException 
	{
		Reapable ret = null;
		if ( canBeRecycledForCallingThread() ) {
			Configuration.logDebug ( this + ": reusing existing proxy for thread..." );
			ret = getCurrentConnectionProxy();
			DynamicProxy dproxy = ( DynamicProxy ) ret;
			AtomikosThreadLocalConnection previous = (AtomikosThreadLocalConnection) dproxy.getInvocationHandler();
			//DON't increment use count: see case 27793
			//previous.incUseCount();
		} else {
			Configuration.logDebug ( this + ": creating connection proxy..." );
			JdbcConnectionProxyHelper.setIsolationLevel ( connection, getDefaultIsolationLevel() );
			ret = ( Reapable ) AtomikosThreadLocalConnection.newInstance ( this , props.getUniqueResourceName() );
		}
		return ret;
	}
	
	
	
	Connection getConnection() 
	{
		return connection;
	}

	protected void testUnderlyingConnection() throws CreateConnectionException {
		String testQuery = getTestQuery();
		if ( isErroneous() ) throw new CreateConnectionException ( this + ": connection is erroneous" );
		if (testQuery != null) {
			Configuration.logDebug ( this + ": testing connection with query [" + testQuery + "]" );
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
			Configuration.logDebug ( this + ": connection tested OK" );
		}
		else {
			Configuration.logDebug ( this + ": no test query, skipping test" );
		}
	}

	public boolean isAvailable() {
		boolean ret = true;

		Reapable handle = getCurrentConnectionProxy();
		if ( handle != null ) {
			DynamicProxy dproxy = ( DynamicProxy ) handle;
			AtomikosThreadLocalConnection previous = (AtomikosThreadLocalConnection) dproxy.getInvocationHandler();
			ret = previous.isNoLongerInUse();
		}
		
		return ret;
		
	}

	public boolean isErroneous() {
		return erroneous;
	}

	public boolean isInTransaction ( CompositeTransaction ct ) {
		boolean ret = false;
		Reapable handle = getCurrentConnectionProxy();
		if ( handle != null ) {
			DynamicProxy dproxy = ( DynamicProxy ) handle;
			AtomikosThreadLocalConnection previous = (AtomikosThreadLocalConnection) dproxy.getInvocationHandler();
			ret = previous.isInTransaction ( ct );
		}
		return ret;
	}

	//overridden for package-use here
	protected void fireOnXPooledConnectionTerminated() 
	{
		super.fireOnXPooledConnectionTerminated();
		updateLastTimeReleased();
	}

	public String toString() 
	{
		return "AtomikosNonXAPooledConnection";
	}

	public boolean getReadOnly() {
		return readOnly;
	}
	
	public boolean canBeRecycledForCallingThread() 
	{
		boolean ret = false;
		Reapable handle = getCurrentConnectionProxy();
		if ( handle != null ) {
			 CompositeTransactionManager ctm = Configuration
             .getCompositeTransactionManager ();
			 CompositeTransaction ct = null;
			 if ( ctm != null ) ct = ctm.getCompositeTransaction ();
			 if ( ct != null && ct.getProperty (  TransactionManagerImp.JTA_PROPERTY_NAME ) != null ) {
				 DynamicProxy dproxy = ( DynamicProxy ) handle;
				 AtomikosThreadLocalConnection previous = (AtomikosThreadLocalConnection) dproxy.getInvocationHandler();
				 ret = previous.isInTransaction ( ct );
			 }
		}
		return ret;
	}
}
