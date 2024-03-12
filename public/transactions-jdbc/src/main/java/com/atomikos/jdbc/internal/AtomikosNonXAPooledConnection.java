/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.atomikos.datasource.pool.AbstractXPooledConnection;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.DynamicProxySupport;

 /**
  * 
  * 
  * An implementation of XPooledConnection for non-xa drivers.
  * 
  *
  */

public class AtomikosNonXAPooledConnection extends AbstractXPooledConnection<Connection>
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosNonXAPooledConnection.class);
	
	private static boolean isJdbc4Compatible = true; //optimistically assume we are compliant
	
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
		if ( LOGGER.isTraceEnabled() ) {
		    LOGGER.logTrace ( this + ": setErroneous" );
		}
		this.erroneous = true;
	}

	public void doDestroy() 
	{
		try {
			if ( connection != null ) {
			    connection.close();
			}
		} catch ( SQLException e ) {
			//ignore, just log
			LOGGER.logWarning ( this + ": Error closing JDBC connection: " , e );
		}

	}

	protected Connection doCreateConnectionProxy() throws CreateConnectionException 
	{
		Connection ret = null;
		if (canBeRecycledForCallingThread()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.logTrace(this + ": reusing existing proxy for thread...");
			}
			ret = getCurrentConnectionProxy();

		} else {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.logTrace(this + ": creating connection proxy...");
			}
			JdbcConnectionProxyHelper.setIsolationLevel(connection, getDefaultIsolationLevel());
			DynamicProxySupport<Connection> proxy;
			if (props.getIgnoreJtaTransactions()) {
				proxy = new JtaUnawareThreadLocalConnection(this);
			} else {
				if (props.getLocalTransactionMode()) {
					if (isInJtaTransaction()) {
						proxy = new JtaAwareThreadLocalConnection(this, props.getUniqueResourceName());
					} else {
						proxy = new JtaUnawareThreadLocalConnection(this);
					}
				} else {
					proxy = new JtaAwareThreadLocalConnection(this, props.getUniqueResourceName());
				}
			}
			ret = proxy.createDynamicProxy();
		}
		Object impl = Proxy.getInvocationHandler(ret);
		if (impl instanceof JtaAwareThreadLocalConnection) {
			JtaAwareThreadLocalConnection previous = (JtaAwareThreadLocalConnection) impl;
			previous.incUseCount(); // cf case 176500
		}
		return ret;
	}
	
	
	
	private boolean isInJtaTransaction() {
		CompositeTransaction ct = null;
		CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
		if (ctm != null) {
			ct = ctm.getCompositeTransaction();
			if (ct != null && TransactionManagerImp.isJtaTransaction(ct)) {
				return true;
			} 
		}
		return false;
	}

	Connection getConnection() 
	{
		return connection;
	}

	protected void testUnderlyingConnection() throws CreateConnectionException {
		String testQuery = getTestQuery();
		if ( isErroneous() ) {
		    throw new CreateConnectionException ( this + ": connection is erroneous" );
		}
		if ( maxLifetimeExceeded()) {
		    throw new CreateConnectionException ( this + ": connection too old - will be replaced"  );
		}

		int queryTimeout = Math.max(0, getBorrowConnectionTimeout());
		if (testQuery != null) {
			if ( LOGGER.isTraceEnabled() ) {
			    LOGGER.logTrace ( this + ": testing connection with query [" + testQuery + "]" );
			}
			Statement stmt = null;
			try {
				stmt = connection.createStatement();
				stmt.setQueryTimeout(queryTimeout);
				//use execute instead of executeQuery - cf case 58830
				stmt.execute(testQuery);
				stmt.close();
			} catch ( Exception e) {
				//catch any Exception - cf case 22198
				throw new CreateConnectionException ( "Error executing testQuery" ,  e );
			}
			if ( LOGGER.isTraceEnabled() ) { 
			    LOGGER.logTrace ( this + ": connection tested OK" );
			}
		} else if (isJdbc4Compatible) {
            if ( LOGGER.isTraceEnabled() ) {
                LOGGER.logTrace ( this + ": testing connection with connection.isValid()" );
            }
            try {
                if (!connection.isValid(queryTimeout)) {
                    throw new CreateConnectionException ("Connection no longer valid");
                }
            } catch (CreateConnectionException e) {
                throw e;
            } catch (Throwable e) {
                // happens if driver was compiled with old JDBC API version
                LOGGER.logWarning("JDBC connection validation not supported by DBMS driver - please set a testQuery if validation is desired...", e);
                isJdbc4Compatible = false;
            }       
        }
	}

	public boolean isAvailable() {
		boolean ret = true;

		Object handle = getCurrentConnectionProxy();
		if ( handle != null ) {
		    NonXaConnectionProxy previous = (NonXaConnectionProxy) Proxy.getInvocationHandler(handle);
			ret = previous.isAvailableForReuseByPool();
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
		Object handle = getCurrentConnectionProxy();
		if ( handle != null && !props.getIgnoreJtaTransactions()) {
			 CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager ();
			 CompositeTransaction ct = null;
			 if ( ctm != null ) {
                 ct = ctm.getCompositeTransaction ();
             }
             if ( ct != null && TransactionManagerImp.isJtaTransaction(ct) ) {
                 Object previous = Proxy.getInvocationHandler(handle);
                 if (previous instanceof JtaAwareThreadLocalConnection) { 
                     // instance check needed: see case 179070
                     // don't recycle pooled connections that were created in localTransactionMode before the JTA transaction was there
                     JtaAwareThreadLocalConnection previousConnection = (JtaAwareThreadLocalConnection) previous;
                     ret = previousConnection.isInTransaction(ct);
                 }
             }
		}
		return ret;
	}
}
