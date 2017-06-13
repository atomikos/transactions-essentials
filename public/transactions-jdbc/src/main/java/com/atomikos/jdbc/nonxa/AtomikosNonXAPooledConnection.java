/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.nonxa;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.atomikos.datasource.pool.AbstractXPooledConnection;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.Reapable;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.jdbc.JdbcConnectionProxyHelper;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
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
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosNonXAPooledConnection.class);
	
//	private Connection connection;
	
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
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": setErroneous" );
		this.erroneous = true;
	}

	public void destroy() 
	{
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": destroying..." );
		try {
			if ( connection != null ) connection.close();
		} catch ( SQLException e ) {
			//ignore, just log
			LOGGER.logWarning ( this + ": Error closing JDBC connection: " , e );
		}

	}

	protected Reapable doCreateConnectionProxy() throws CreateConnectionException 
	{
		Reapable ret = null;
		if ( canBeRecycledForCallingThread() ) {
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": reusing existing proxy for thread..." );
			ret = getCurrentConnectionProxy();
			DynamicProxy dproxy = ( DynamicProxy ) ret;
			AtomikosThreadLocalConnection previous = (AtomikosThreadLocalConnection) dproxy.getInvocationHandler();
			//DON't increment use count: see case 27793
			//previous.incUseCount();
		} else {
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": creating connection proxy..." );
			JdbcConnectionProxyHelper.setIsolationLevel ( connection, getDefaultIsolationLevel() );
			ret = ( Reapable ) AtomikosThreadLocalConnection.newInstance ( this , props.getUniqueResourceName() );
		}
		return ret;
	}
	
	Connection getConnection() 
	{
		return connection;
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
			 CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager ();
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
