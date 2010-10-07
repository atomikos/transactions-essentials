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

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.jdbc.HeuristicDataSource;
import com.atomikos.util.SerializableObjectFactory;

/**
 * 
 * 
 * 
 * A Bean class for DataSource access to non-XA JDBC implementations. Instances
 * are transaction-aware and can rollback the work done over multiple
 * connections (provided that all work was done in one and the same thread).
 * 
 *  @deprecated As of release 3.3, the {@link AtomikosNonXADataSourceBean} should be used instead.
 * 
 */
public class NonXADataSourceBean implements HeuristicDataSource, Referenceable,
        Serializable
{

    private String validatingQuery;

    private String jndiName;

    private String url;

    private String user;

    private String password;

    private String driverClassName;

    private int poolSize;

    private int connectionTimeout;
    
    private boolean testOnBorrow;

    
    private transient NonXADataSourceImp delegate;

    public NonXADataSourceBean ()
    {
        jndiName = "some unique name";
        url = "";
        user = "";
        password = "";
        driverClassName = "";
        poolSize = 2;
        connectionTimeout = 15;
        testOnBorrow = false;
    }

    private synchronized void checkSetup ( boolean validation )
            throws SQLException
    {
        // try if there has been a previous bean setup; return the DS if so
        delegate = NonXADataSourceImp.getInstance ( jndiName );
        // return if found, but ONLY if validation is NOT true
        // since validation requires asserting settings and creation
        if ( delegate != null && !validation )
            return;

        if ( url == null || url.equals ( "" ) )
            throw new SQLException ( "NonXADataSourceBean: url not set." );
        if ( driverClassName == null || driverClassName.equals ( "" ) )
            throw new SQLException (
                    "NonXADataSourceBean: driverClassName not set." );

        DriverManagerDataSource driver = new DriverManagerDataSource ();
        driver.setDriverClassName ( driverClassName );
        driver.setUser ( user );
        driver.setPassword ( password );
        driver.setUrl ( url );

        delegate = new NonXADataSourceImp ( driver, jndiName, user, password,
                poolSize, connectionTimeout, validation, validatingQuery , testOnBorrow );

        // the application does not know the datasource and hence can not
        // shut it down. therefore, add a shutdown hook to do this job
        DataSourceShutdownHook hook = new DataSourceShutdownHook ( delegate );
        Configuration.addShutdownHook ( hook );
        
        StringBuffer sb = new StringBuffer();
        sb.append("NonXADataSourceBean configured with [");
        sb.append("uniqueResourceName=").append(jndiName).append(", ");
        sb.append("url=").append(url).append(", ");
        sb.append("user=").append(user).append(", ");
        sb.append("password=").append(password).append(", ");
        sb.append("driverClassName=").append(driverClassName).append(", ");
        sb.append("poolSize=").append(poolSize).append(", ");
        sb.append("connectionTimeout=").append(connectionTimeout).append(", ");
        sb.append("testOnBorrow=").append(testOnBorrow);
        sb.append("]");
        
        if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug(sb.toString());
        
        Configuration.logWarning ( "WARNING: class " + getClass().getName() + " is deprecated!" );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(com.atomikos.icatch.HeuristicMessage)
     */

    public Connection getConnection ( HeuristicMessage msg )
            throws SQLException
    {
        checkSetup ( false );
        return delegate.getConnection ( msg );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(java.lang.String,
     *      java.lang.String, com.atomikos.icatch.HeuristicMessage)
     */

    public Connection getConnection ( String user , String passwd ,
            HeuristicMessage msg ) throws SQLException
    {
        checkSetup ( false );
        return delegate.getConnection ( user, passwd, msg );
    }

    /**
     * @see javax.naming.Referenceable#getReference()
     */
    public Reference getReference () throws NamingException
    {
    		//FIXED ISSUE 10085
    		return SerializableObjectFactory.createReference ( this );
    }

    /**
     * @see javax.sql.DataSource#getConnection()
     */
    public Connection getConnection () throws SQLException
    {
        checkSetup ( false );
        return delegate.getConnection ();
    }

    /**
     * @see javax.sql.DataSource#getConnection(java.lang.String,
     *      java.lang.String)
     */
    public Connection getConnection ( String user , String pass )
            throws SQLException
    {
        checkSetup ( false );
        return delegate.getConnection ( user, pass );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(java.lang.String,
     *      java.lang.String, com.atomikos.icatch.HeuristicMessage)
     */

    public Connection getConnection ( String msg ) throws SQLException
    {
        checkSetup ( false );
        return delegate.getConnection ( msg );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource#getConnection(java.lang.String,
     *      java.lang.String, com.atomikos.icatch.HeuristicMessage)
     */

    public Connection getConnection ( String user , String passwd , String msg )
            throws SQLException
    {
        checkSetup ( false );
        return delegate.getConnection ( user, passwd, msg );
    }

    /**
     * @see javax.sql.DataSource#getLogWriter()
     */
    public PrintWriter getLogWriter () throws SQLException
    {
        checkSetup ( false );
        return delegate.getLogWriter ();
    }

    /**
     * @see javax.sql.DataSource#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter ( PrintWriter pw ) throws SQLException
    {
        checkSetup ( false );
        delegate.setLogWriter ( pw );

    }

    /**
     * @see javax.sql.DataSource#setLoginTimeout(int)
     */
    public void setLoginTimeout ( int val ) throws SQLException
    {

        checkSetup ( false );
        delegate.setLoginTimeout ( val );
    }

    /**
     * @see javax.sql.DataSource#getLoginTimeout()
     */
    public int getLoginTimeout () throws SQLException
    {
        checkSetup ( false );
        return delegate.getLoginTimeout ();
    }

    /**
     * @return The connection refresh timeout in secs.
     */
    public int getConnectionTimeout ()
    {
        return connectionTimeout;
    }

    /**
     * @return The driver class to be used by the DriverManager.
     */
    public String getDriverClassName ()
    {
        return driverClassName;
    }

    /**
     * @return The JNDI name to bind on.
     */
    public String getUniqueResourceName ()
    {

        return jndiName;
    }

    /**
     * @return The password.
     */
    public String getPassword ()
    {
        return password;
    }

    /**
     * @return The poolsize.
     */
    public int getPoolSize ()
    {
        return poolSize;
    }

    /**
     * @return The URL to connect with.
     */
    public String getUrl ()
    {
        return url;
    }

    /**
     * @return The user to connect with.
     */
    public String getUser ()
    {
        return user;
    }
    
    /**
     * Set whether connections should be tested when gotten.
     * 
     * @param value Whether to test connections when taken out of the pool.
     */
    
    public void setTestOnBorrow ( boolean value )
    {
    		this.testOnBorrow = value;
    }
    
    /**
     * 
     * @return whether to test connections when gotten.
     */
    public boolean getTestOnBorrow()
    {
    		return testOnBorrow;
    }

    /**
     * Set the refresh timeout interval for pool connections (optional).
     * 
     * @param secs
     *            The value in seconds.
     */
    public void setConnectionTimeout ( int secs )
    {
        connectionTimeout = secs;
    }

    /**
     * Set the name of the driver class that the DriverManager should use
     * (required).
     * 
     * @param name
     *            The name.
     */
    public void setDriverClassName ( String name )
    {
        driverClassName = name;
    }

    /**
     * Set the JNDI name to bind on (required). Should be unique.
     * 
     * @param name
     *            The JNDI name.
     */
    public void setUniqueResourceName ( String name )
    {
        jndiName = name;
    }

    /**
     * Set the password to use.
     * 
     * @param string
     */
    public void setPassword ( String string )
    {
        password = string;
    }

    /**
     * Set the minimum size of the pool (optional).
     * 
     * @param size
     */
    public void setPoolSize ( int size )
    {
        poolSize = size;
    }

    /**
     * Set the URL to use for getting connections (required).
     * 
     * @param url
     */
    public void setUrl ( String url )
    {
        this.url = url;
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
     * Set a validating query for easy verification of connectivity (optional).
     * 
     * @param query
     */

    public void setValidatingQuery ( String query )
    {
        validatingQuery = query;
    }

    /**
     * Get the validating query
     * 
     * @return String The query.
     */

    public String getValidatingQuery ()
    {
        return validatingQuery;
    }

    /**
     * Close the instance after use. Calling this method will clean up the
     * internal pool and housekeeping thread, so that the VM can exit normally.
     */
    public void close ()
    {
        if ( delegate != null )
            delegate.close ();
    }

    /**
     * Perform validation based on the validating query. This method does
     * nothing if no query was specified.
     * 
     * @throws SQLException
     *             If validation fails.
     */
    public void validate () throws SQLException
    {
        checkSetup ( true );
        String query = getValidatingQuery ();
        if ( query == null || query.equals ( "" ) )
            return;

        Connection c = null;
        Statement s = null;
        try {
            // don't use our own getConnection since it will
            // call checkSetup without validation flag!
            c = delegate.getConnection ();

            try {
                s = c.createStatement ();
                ResultSet rs = s.executeQuery ( query );
                s.close ();
            } finally {
                if ( s != null )
                    s.close ();
                // this will also close the resultsets if any
            }
        }

        finally {
            if ( c != null )
                c.close ();
        }
    }

}
