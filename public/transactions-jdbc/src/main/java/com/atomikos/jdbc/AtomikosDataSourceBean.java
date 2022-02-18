/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.pool.ConnectionFactory;
import com.atomikos.datasource.pool.ConnectionPoolProperties;
import com.atomikos.datasource.pool.CreateConnectionException;
import com.atomikos.datasource.pool.XPooledConnection;
import com.atomikos.datasource.xa.jdbc.JdbcTransactionalResource;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.jdbc.internal.AbstractDataSourceBean;
import com.atomikos.jdbc.internal.AtomikosSQLException;
import com.atomikos.jdbc.internal.AtomikosXAPooledConnection;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.ClassLoadingHelper;

 /**
 * The preferred class for using Atomikos connection pooling. Use an instance of
 * this class if you want to use Atomikos JTA-enabled connection pooling. All
 * you need to do is construct an instance and set the required properties as
 * outlined below. The resulting bean will automatically register with the
 * transaction service (for recovery) and take part in active transactions.
 * All SQL done over connections (gotten from this class) will participate in JTA transactions.
 */

public class AtomikosDataSourceBean 
extends AbstractDataSourceBean
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosDataSourceBean.class);
	
	
	private static final long serialVersionUID = 1L;
	
	private Properties xaProperties = new Properties();
	private String xaDataSourceClassName;
	private transient XADataSource xaDataSource;
	private boolean localTransactionMode = true;
	
	public AtomikosDataSourceBean() {
	}
	
	/**
	 * Gets the properties used to
	 * configure the XADataSource.  
	 */
	
	public Properties getXaProperties()
	{
		return xaProperties;
	}

	/**
	 * Sets the properties (name,value pairs) used to
	 * configure the XADataSource. Required, unless you call setXaDataSource directly.
	 * 
	 * @param xaProperties 
	 * 
	 *
	 */
	public void setXaProperties ( Properties xaProperties ) 
	{
		this.xaProperties = xaProperties;
	}

	/**
	 * Get the XADataSource class name.
	 */
	public String getXaDataSourceClassName() 
	{
		return xaDataSourceClassName;
	}

	/**
	 * Sets the fully qualified underlying XADataSource class name. Required, unless you 
	 * call setXaDataSource directly.
	 * 
	 * @param xaDataSourceClassName
	 */
	public void setXaDataSourceClassName ( String xaDataSourceClassName ) 
	{
		this.xaDataSourceClassName = xaDataSourceClassName;
	}
	
	/**
	 * Gets the configured XADataSource (if any).
	 * @return The instance, or null if none.
	 */
	
	public XADataSource getXaDataSource()
	{
		return xaDataSource;
	}
	
	/**
	 * Sets the XADataSource directly - instead of providing the xaDataSourceClassName and xaProperties.
	 * @param xaDataSource
	 */
	public void setXaDataSource(XADataSource xaDataSource)
	{
		this.xaDataSource = xaDataSource;
	}
	
	@Override
	public boolean getLocalTransactionMode() {
	    return localTransactionMode;
	}
	
	/**
	 * Sets localTransactionMode. Optional, defaults to true.
	 * 
	 * @param localTransactionMode If true, then (for historical reasons) this
	 * datasource supports "hybrid" behaviour: if a JTA transaction is present then
	 * XA will be used, if not then a regular JDBC connection with connection-level
	 * commit/rollback will be returned. 
	 * 
	 * For safety, this property is best left to false: that way, there is no 
	 * doubt about the transactional nature of your JDBC work.
	 */
	public void setLocalTransactionMode(boolean localTransactionMode) {
	    this.localTransactionMode = localTransactionMode;
	}
	
	protected com.atomikos.datasource.pool.ConnectionFactory<Connection> doInit() throws Exception 
	{
		if (xaDataSource == null)
		{
			if (xaDataSourceClassName == null)
				throwAtomikosSQLException("Property 'xaDataSourceClassName' cannot be null");
			if (xaProperties == null)
				throwAtomikosSQLException("Property 'xaProperties' cannot be null");
		}
		
		
		if ( LOGGER.isDebugEnabled() ) LOGGER.logInfo(
				this + ": initializing with [" +
				" xaDataSourceClassName=" + xaDataSourceClassName + "," +
				" uniqueResourceName=" + getUniqueResourceName() + "," +
				" maxPoolSize=" + getMaxPoolSize() + "," +
				" minPoolSize=" + getMinPoolSize() + "," +
				" borrowConnectionTimeout=" + getBorrowConnectionTimeout() + "," +
				" maxIdleTime=" + getMaxIdleTime() + "," +
				" reapTimeout=" + getReapTimeout() + "," +
				" maintenanceInterval=" + getMaintenanceInterval() + "," +
				" testQuery=" + getTestQuery() + "," +
				" xaProperties=" + PropertyUtils.toString(xaProperties) + "," +
				" loginTimeout=" + getLoginTimeout() + "," + 
				" maxLifetime=" + getMaxLifetime() + "," + 
				" localTransactionMode=" + getLocalTransactionMode() +
				"]"
				);
		
		
			if (xaDataSource == null)
			{
				try {
					Class<XADataSource> xadsClass = ClassLoadingHelper.loadClass ( getXaDataSourceClassName() );
					xaDataSource =  xadsClass.newInstance();
					
				} catch ( ClassNotFoundException nf ) {
					AtomikosSQLException.throwAtomikosSQLException ( "The class '" + getXaDataSourceClassName() +
							"' specified by property 'xaDataSourceClassName' could not be found in the classpath. Please make sure the spelling is correct, and that the required jar(s) are in the classpath." , nf );
				} catch (ClassCastException cce) {
					AtomikosSQLException.throwAtomikosSQLException (
							 "The class '" + getXaDataSourceClassName() +
								"' specified by property 'xaDataSourceClassName' does not implement the required interface javax.jdbc.XADataSource. Please make sure the spelling is correct, and check your JDBC driver vendor's documentation.");
				}
				xaDataSource.setLoginTimeout ( getLoginTimeout() );
				xaDataSource.setLogWriter ( getLogWriter() );
				PropertyUtils.setProperties(xaDataSource, xaProperties );
				
			}
			
			JdbcTransactionalResource tr = new JdbcTransactionalResource(getUniqueResourceName() , xaDataSource);
			ConnectionFactory<Connection> cf = new AtomikosXAConnectionFactory(xaDataSource, tr, this);
			Configuration.addResource ( tr );
			
			return cf;
	}
	
	protected void doClose() 
	{
		RecoverableResource res = Configuration.getResource ( getUniqueResourceName() );
		if ( res != null ) {
			Configuration.removeResource ( getUniqueResourceName() );
			//fix for case 26005
			res.close();
		}
	}	

	protected boolean isAssignableFromWrappedVendorClass(Class<?> iface) {
		boolean ret = false;
		 if (xaDataSource != null ) { 
			 ret = iface.isAssignableFrom(xaDataSource.getClass());
		 }
		 return ret;
	}

	@Override
	protected Object unwrapVendorInstance() {
		return xaDataSource;
	}

	private static class  AtomikosXAConnectionFactory implements ConnectionFactory<Connection> 
	{
		private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosXAConnectionFactory.class);

		private final JdbcTransactionalResource jdbcTransactionalResource;
		private final XADataSource xaDataSource;
		private final ConnectionPoolProperties props;
		
		
		private AtomikosXAConnectionFactory ( XADataSource xaDataSource, JdbcTransactionalResource jdbcTransactionalResource, ConnectionPoolProperties props ) 
		{
			this.xaDataSource = xaDataSource;
			this.jdbcTransactionalResource = jdbcTransactionalResource;
			this.props = props;
		}

		public XPooledConnection<Connection> createPooledConnection() throws CreateConnectionException
		{
			try {
				XAConnection xaConnection = xaDataSource.getXAConnection();
				return new AtomikosXAPooledConnection ( xaConnection, jdbcTransactionalResource, props );
			} catch ( SQLException e ) {
				String msg = "XAConnectionFactory: failed to create pooled connection - DBMS down or unreachable?";
				LOGGER.logWarning ( msg , e );
				throw new CreateConnectionException ( msg , e );
			}
		}

	}

	@Override
	public boolean getIgnoreJtaTransactions() {
		return false;
	}


}
