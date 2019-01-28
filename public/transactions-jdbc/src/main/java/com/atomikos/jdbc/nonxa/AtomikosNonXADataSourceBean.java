/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.nonxa;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import com.atomikos.datasource.pool.ConnectionFactory;
import com.atomikos.jdbc.AbstractDataSourceBean;
import com.atomikos.jdbc.internal.AtomikosJdbcThreadLocalConnection;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

 /**
  * 
  * A Bean class for DataSource access to non-XA JDBC implementations. 
  * Instances are JTA transaction-aware and can rollback the work done 
  * over multiple connections (provided that all work was done in one and the same thread).
  * 
  *
  */
public class AtomikosNonXADataSourceBean extends AbstractDataSourceBean 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosNonXADataSourceBean.class);

	private static final long serialVersionUID = 1L;
	
	private String url;
	
	private String user;
	
	private String password;
	
	private String driverClassName;
	
	private boolean readOnly;
	
	/**
     * Sets the URL to use for getting connections. Required.
     * 
     * @param url
     */
	
	public void setUrl ( String url ) 
	{
		this.url = url;
	}
	
	/**
	 * Gets the URL to connect. 
	 */
	
	public String getUrl()
	{
		return url;
	}
	

	/**
	 * Marks this datasource as being used for read-only work. Optional.
	 * 
	 * Setting this to true will avoid warnings/errors upon recovery. ReadOnly mode
	 * is intended to avoid XA configuration of databases where no updates are
	 * being done.
	 * 
	 * @param readOnly Defaults to false.
	 */
	
	public void setReadOnly ( boolean readOnly ) 
	{
		this.readOnly = readOnly;
	}

	/**
	 * @return Whether or not this datasource is marked as readOnly.
	 */
	
	public boolean getReadOnly()
	{
		return readOnly;
	}

    /**
     * @return The password.
     */
	
	public String getPassword ()
	{
		return password;
	}

    /**
     * Sets the password to use.
     * 
     * @param string
     */
	
    public void setPassword ( String string )
    {
        password = string;
    }

    /**
     * Set the user name to get connections with.
     * 
     * @param string
     */
    
    public void setUser ( String string )
    {
        user = string;
    }

    /**
     * @return The URL to connect with.
     */
    
    public String getUser ()
    {
        return user;
    }
    
    /**
     * 
     * @return The DriverManager class name.
     */

    public String getDriverClassName ()
    {
        return driverClassName;
    }

    /**
     * Sets the driver class name to be used by the DriverManager. Required.
     * 
     * @param string
     */
    public void setDriverClassName ( String string )
    {
        driverClassName = string;
    }
    

	protected void doClose() 
	{
		//nothing to do
	}

	protected ConnectionFactory<Connection> doInit() throws Exception 
	{
		AtomikosNonXAConnectionFactory ret = null;
		if ( LOGGER.isDebugEnabled() ) LOGGER.logInfo(
				this + ": initializing with [" +
				" uniqueResourceName=" + getUniqueResourceName() + "," +
				" maxPoolSize=" + getMaxPoolSize() + "," +
				" minPoolSize=" + getMinPoolSize() + "," +
				" borrowConnectionTimeout=" + getBorrowConnectionTimeout() + "," +
				" maxIdleTime=" + getMaxIdleTime() + "," +
				" reapTimeout=" + getReapTimeout() + "," +
				" maintenanceInterval=" + getMaintenanceInterval() + "," +
				" testQuery=" + getTestQuery() + "," +
				" driverClassName=" + getDriverClassName() + "," +
				" user=" + getUser() + "," +
				" url=" + getUrl() + 
				" loginTimeout=" + getLoginTimeout() +
				"]"
				);
		
		
		ret = new com.atomikos.jdbc.nonxa.AtomikosNonXAConnectionFactory ( this , url , driverClassName , user , password , getLoginTimeout() , readOnly ) ;
		ret.init();
		return ret;
	}

	public synchronized Connection getConnection() throws SQLException
	{
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getConnection()..." );
		
		init();
		
		
		//let pool take care of reusing an existing handle
		Connection connection = super.getConnection();          

		AtomikosJdbcThreadLocalConnection previous = (AtomikosJdbcThreadLocalConnection) Proxy.getInvocationHandler(connection);

        previous.incUseCount();
        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": returning " + connection );
		return connection;
	}

	

	public String toString() 
	{
		String ret = "AtomikosNonXADataSourceBean";
		String name = getUniqueResourceName();
		if ( name != null ) {
			ret = ret + " '" + name + "'";
		}
		return ret;
	}

	@Override
	protected boolean isAssignableFromWrappedVendorClass(Class<?> iface) {
		//we don't really care
		return false;
	}

	@Override
	protected Object unwrapVendorInstance() {
		throw new UnsupportedOperationException();
	}

	
}
