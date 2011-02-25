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
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

import com.atomikos.beans.BeanInspector;
import com.atomikos.beans.Property;
import com.atomikos.beans.PropertyException;
import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.DefaultXidFactory;
import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.ClassLoadingHelper;
import com.atomikos.util.SerializableObjectFactory;

/**
 * 
 * 
 * An Atomikos DataSource implementation.
 * 
 * @deprecated As of release 3.3, the {@link AtomikosDataSourceBean} should be used instead.
 * 
 */

public class SimpleDataSourceBean implements HeuristicDataSource,
        ConnectionPoolDataSource, Serializable, Referenceable
{

	private static final long serialVersionUID = 6413560960979946477L;

	private transient JtaDataSourceImp ds_;
    // the data source to delegate to

    private transient XADataSource xads_;
    // the hook to the JDBC database, needed to
    // reconstruct the transient datasource
    // NOTE: this is transient to make sure that
    // serialization works even in the presence
    // of non-compliant XADataSource classes

    private String resourceName_;
    // the unique name for the resource associated with us

    private String xidFactory_;
    // the name of the XID factory, needed to reconstruct ds_

    private int poolSize_;
    // the pool size for the ds_

    private int connectionTimeout_;
    // needed for the pool

    private boolean exclusive_;
    // true if not reusable before 2PC

    private String validatingQuery_;
    // an optional query for validating all settings

    private String xaProperties_;
    // the specific properties for XADataSource,
    // as a semi-colon-separated list

    private String xaDataSourceClassName_;
    // the name of the XADataSource class to use
    
    private boolean testOnBorrow_;
    //should connections be tested upon get or not?

    public SimpleDataSourceBean ()
    {
        ds_ = null;
        xads_ = null;
        resourceName_ = "someUniqueName";
        xidFactory_ = "Default";
        poolSize_ = 1;
        connectionTimeout_ = 30;
        exclusive_ = true;
        validatingQuery_ = "";
        xaProperties_ = "";
        xaDataSourceClassName_ = "";
        testOnBorrow_ = false;
    }

    private Properties parseProperties ()
    {
        Properties ret = new Properties ();
        StringTokenizer t = new StringTokenizer ( xaProperties_, ";" );
        if ( xaProperties_.indexOf ( "," ) >= 0 ) {
        		//case 20167: generate warning
        		Configuration.logWarning ( "xaDataSourceProperties: found comma(s) - please make sure to use ';' to separate properties: " + xaProperties_ );
        }
        else if ( t.countTokens() <= 1 ) {
        		//case 20167: generate warning
        		Configuration.logWarning ( "xaDataSourceProperties: only one property found - please check format: " + xaProperties_ );
        }
        while ( t.hasMoreTokens () ) {
            String next = t.nextToken ();
            int index = next.indexOf ( "=" );
            if ( index < 0 )
                throw new RuntimeException ( "Invalid xaDataSourceProperties format" );
            String name = next.substring ( 0, index );
            String val = next.substring ( index + 1 );
            ret.setProperty ( name, val );
        }
        return ret;
    }

    private synchronized void checkSetup ( boolean validation )
            throws SQLException
    {
        // try if there has been a previous bean setup; return the DS if so
        ds_ = JtaDataSourceImp.getInstance ( getUniqueResourceName () );
        // return if found, but ONLY if validation is NOT true
        // since validation requires asserting XA settings and creation
        if ( ds_ != null && !validation )
            return;

        if ( xads_ == null && ( getXaDataSourceClassName () == null || getXaDataSourceClassName ().length() == 0 ) )
            throw new SQLException (
                    "SimpleDataSourceBean: xaDataSourceClassName not set." );
        if ( xidFactory_ == null )
            throw new SQLException ( "SimpleDataSourceBean: xidFormat not set." );

        if ( xads_ == null ) {
        		//check added for case 20112
	        try {
	        	Class xadsClass = ClassLoadingHelper.loadClass ( getXaDataSourceClassName() );
	            xads_ = (XADataSource) xadsClass.newInstance ();
	            BeanInspector inspector = new BeanInspector ( xads_ );
	            Properties p = parseProperties ();
	            Enumeration names = p.propertyNames ();
	            while ( names.hasMoreElements () ) {
	                String name = (String) names.nextElement ();
	                String val = p.getProperty ( name );
	                inspector.setPropertyValue ( name, val );
	            }
	
	        } catch ( Exception e ) {
	            Configuration.logWarning (
	                    "SimpleDataSourceBean: could not configure XADataSource of class "
	                            + getXaDataSourceClassName (), e );
	            throw new SQLException ( "Could not configure XADataSource: "
	                    + e.getMessage () + " " + e.getClass ().getName () );
	        }
        }
        XidFactory xidFactory = null;

        xidFactory = new DefaultXidFactory ();

        XAConnectionFactory factory = new XAConnectionFactory ( resourceName_,
                "", "", xads_, xidFactory );
        factory.setExclusive ( exclusive_ );

        ds_ = new JtaDataSourceImp ( factory, poolSize_, connectionTimeout_,
                validation, !validation, validatingQuery_ , testOnBorrow_ );

        // MOVED TO JTADATASOURCEIMP
        // if ( ! validation )
        // Configuration.addResource ( ds_.getTransactionalResource() );

        // the application does not know the Jta datasource and hence can not
        // shut it down. therefore, add a shutdown hook to do this job
        DataSourceShutdownHook hook = new DataSourceShutdownHook ( ds_ );
        Configuration.addShutdownHook ( hook );
        
        
        StringBuffer sb = new StringBuffer();
        sb.append("SimpleDataSourceBean configured with [");
        sb.append("resourceName=").append(resourceName_).append(", ");
        sb.append("xidFactory=").append(xidFactory_).append(", ");
        sb.append("poolSize=").append(poolSize_).append(", ");
        sb.append("connectionTimeout=").append(connectionTimeout_).append(", ");
        sb.append("exclusive=").append(exclusive_).append(", ");
        sb.append("validatingQuery=").append(validatingQuery_).append(", ");
        sb.append("xaProperties=").append(xaProperties_).append(", ");
        sb.append("xaDataSourceClassName=").append(xaDataSourceClassName_).append(", ");
        sb.append("testOnBorrow=").append(testOnBorrow_);
        sb.append("]");
        
        if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug(sb.toString());
        
        Configuration.logWarning ( "WARNING: class " + getClass().getName() + " is deprecated!" );
        
    }

    private synchronized void checkSetup () throws SQLException
    {
        checkSetup ( false );
    }
    
    /**
     * Init method to setup the bean for recovery
     * and other init stuff.
     * @throws SQLException 
     *
     */
    public void init() throws SQLException
    {
    		checkSetup ( false );
    }


    /**
     * Set the identifying name for this data source (required). Used by the
     * transaction logging mechanism. NOTE: the value should not exceed
     * 45 bytes in length.
     * 
     * @param resourceName
     */
    public void setUniqueResourceName ( String resourceName )
    {
        resourceName_ = resourceName;
    }

    /**
     * Get the unique resource name for this instance.
     * 
     * @return String The name.
     */

    public String getUniqueResourceName ()
    {
        return resourceName_;
    }

    /**
     * Set the size of the internal connection pool (optional). Note: this class
     * also implements ConnectionPoolDataSource, meaning that it can be used for
     * third-party connection pools. In that case, the internal connection pool
     * size can still be configured to have pooling at two levels.
     * 
     * @param poolSize
     *            The size of the pool.
     */

    public void setConnectionPoolSize ( int poolSize )
    {
        poolSize_ = poolSize;
    }

    /**
     * Get the size of the connection pool.
     * 
     * @return int The size.
     */
    
    public int getConnectionPoolSize ()
    {
        return poolSize_;
    }
    
    /**
     * Set whether connections should be tested when gotten (optional). Default is false.
     * 
     * @param value True if connections should be tested when gotten.
     */
    
    public void setTestOnBorrow ( boolean value ) 
    {
    		testOnBorrow_ = value;
    }

    /**
     * Get whether connections should be tested when gotten.
     * @return
     */
    
    public boolean getTestOnBorrow() 
    {
    		return testOnBorrow_;
    }
    
    /**
     * Set the timeout after which connections should be checked for liveness
     * (optional).
     * 
     * @param timeout
     *            The timeout in seconds.
     */

    public void setConnectionTimeout ( int timeout )
    {
        connectionTimeout_ = timeout;
    }

    /**
     * Get the timeout after which connections are checked for liveness.
     * 
     * @return int The timeout.
     */
    public int getConnectionTimeout ()
    {
        return connectionTimeout_;
    }

    /**
     * Set the sharing preferences (optional).
     * 
     * @param mode
     *            The mode. If true, then connections are not shared when they
     *            are in a transaction. This is the safest setting and allows to
     *            avoid problems with databases that don't implement XA
     *            correctly (such as Oracle). Setting this to true will slightly
     *            affect performance of connection pooling in a negative way.
     *            Default is true.
     */

    public void setExclusiveConnectionMode ( boolean mode )
    {
        exclusive_ = mode;
    }

    /**
     * Get the sharing preference.
     * 
     * @return boolean true if exclusive, false if not.
     */

    public boolean getExclusiveConnectionMode ()
    {
        return exclusive_;
    }

    /**
     * Get the transactional resource.
     * 
     * @return TransactionalResource The resource as it is used by the
     *         transaction service during recovery.
     */

    protected TransactionalResource getTransactionalResource ()
    {
        try {
            checkSetup ();
        } catch ( SQLException err ) {
            err.printStackTrace ();
            throw new RuntimeException ( err.getMessage () );
        }
        return ds_.getTransactionalResource ();
    }

    /**
     * Set the validating query for this datasource (optional). This optional
     * property allows you to give a test query to see if the configuration
     * works.
     * 
     * @param query
     *            The SQL query that should work if the connectivity is made.
     */

    public void setValidatingQuery ( String query )
    {
        validatingQuery_ = query;
    }

    /**
     * Get the validating query.
     * 
     * @return String The query.
     */
    public String getValidatingQuery ()
    {
        return validatingQuery_;
    }

    /**
     * Set the fully qualified name of the XADataSource class to use (required).
     * 
     * @param name
     *            The vendor-specific XADataSource class to use. Ignored if the XADataSource instance is set directly.
     */

    public void setXaDataSourceClassName ( String name )
    {
        xaDataSourceClassName_ = name;
    }

    // /**
    // * Check if the setup is valid.
    // *
    // * @return boolean False if the connectivity fails,
    // * true if the validating query could be executed,
    // * or if no validating query was specified.
    // */
    //    
    // public boolean isValidatingQueryOK()
    // {
    // boolean ret = false;
    // try
    // {
    // validate();
    // ret = true;
    // }
    // catch (SQLException e)
    // {
    //            
    // }
    // return ret ;
    // }
    //    
    /**
     * Get the full name of the vendor-specific XADataSource class.
     * 
     * @return String The fully qualified name.
     */

    public String getXaDataSourceClassName ()
    {
    		String ret = xaDataSourceClassName_;
    		if ( ret == null && xads_ != null ) {
    			//if xads is set directly -> use the actual classname
    			//since xaDataSourceClassName_ may be null!
    			ret = xads_.getClass().getName();
    		}
         return ret;
    }

    /**
     * Set the XADataSource-specific properties as a semicolon-separated list of
     * string values (required unless the XADataSource instance is set directly).
     * 
     * @param properties
     *            The properties expressed as a semi-colon separated list. For
     *            example: port=8000;user=demo;password=sa
     * Ignored if the XADataSource instance is set directly.
     */

    public void setXaDataSourceProperties ( String properties )
    {
        xaProperties_ = properties;
    }

    /**
     * Get the XADataSource properties as one large string.
     * 
     * @return String The poperty list as set previously by setXaDataSourceProperties.
     */

    public String getXaDataSourceProperties () 
    {
    	    StringBuffer ret = new StringBuffer();
    	    if ( xaProperties_ != null ) ret.append ( xaProperties_ );
    	    if ( ret.length() == 0 && xads_ != null ) { 	    
    	    	    try {
	    	    		BeanInspector inspector = new BeanInspector ( xads_ );
	    	    		Property[] props = inspector.getProperties();
	    	    		for ( int i = 0 ; i < props.length ; i++ ) {
	    	    			String name = props[i].getName();
	    	    			String value = inspector.getPropertyValue ( name );
	    	    			ret.append ( name ); ret.append( "=" ); ret.append ( value );
	    	    			if ( i < props.length - 1 ) ret.append ( ";" );
	    	    		}
    	    	    }
    	    	    catch ( PropertyException e ) {
    	    	    		Configuration.logWarning ( "Error in getXaDataSourceProperties" , e );
    	    	    		throw new UndeclaredThrowableException ( e );
    	    	    }
    	    }
        return ret.toString();
    }
    
    /**
     * Sets the (preconfigured) XADataSource instance.
     * 
     * @param xads The instance.
     */
    public void setXaDataSource ( XADataSource xads ) 
    {
    		this.xads_ = xads;
    }
    
    /**
     * Gets the configured XADataSource instance (if set). 
     * @return The XADataSource - or null if not set.
     */
    public XADataSource getXaDataSource()
    {
    		return xads_;
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
            c = ds_.getConnection ();

            try {
                s = c.createStatement ();
                ResultSet rs = s.executeQuery ( query );
                s.close ();
            } finally {
                if ( s != null )
                    s.close ();
                // this will also close the resultsets if any
            }
        } catch ( SQLException e ) {
            Configuration.logWarning (
                    "Error in validating query for resource "
                            + getUniqueResourceName (), e );
        } finally {
            if ( c != null )
                c.close ();
        }
    }

    //
    //
    // IMPLEMENTATION OF HEURISTICDATASOURCE
    //
    //

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */
    public Connection getConnection () throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ();
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public Connection getConnection ( String user , String pw )
            throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( user, pw );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public Connection getConnection ( HeuristicMessage msg )
            throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( msg );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public Connection getConnection ( String user , String pw ,
            HeuristicMessage msg ) throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( user, pw, msg );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public Connection getConnection ( String msg ) throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( msg );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public Connection getConnection ( String user , String passwd , String msg )
            throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( user, passwd, msg );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public int getLoginTimeout () throws SQLException
    {
        checkSetup ();
        return ds_.getLoginTimeout ();
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public PrintWriter getLogWriter () throws SQLException
    {
        checkSetup ();
        return ds_.getLogWriter ();
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public void setLoginTimeout ( int seconds ) throws SQLException
    {
        checkSetup ();
        ds_.setLoginTimeout ( seconds );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public void setLogWriter ( PrintWriter out ) throws SQLException
    {
        checkSetup ();
        ds_.setLogWriter ( out );
    }

    /**
     * @see com.atomikos.jdbc.HeuristicDataSource
     */

    public void close () throws SQLException
    {
    	    //DON'T setup: see issue 10098
        //checkSetup ();
    	
    		if ( ds_ != null  ) {
    			//issue 10098: don't call setup -> added null check
    			ds_.close ();
    		}
    }

    //
    //
    // IMPLEMENTATION OF REFERENCEABLE
    //
    //

    /**
     * @see javax.naming.Referenceable
     */
    public Reference getReference () throws NamingException
    {
        return SerializableObjectFactory.createReference ( this );
    }

    /**
     * @see javax.sql.ConnectionPoolDataSource#getPooledConnection()
     */

    public PooledConnection getPooledConnection () throws SQLException
    {
        checkSetup ();
        PooledConnection ret = null;
        XAConnection xaconn = xads_.getXAConnection ();
        ret = new ExternalXAPooledConnectionImp ( xaconn,
                getTransactionalResource (), getLogWriter () );
        return ret;
    }

    /**
     * @see javax.sql.ConnectionPoolDataSource#getPooledConnection(java.lang.String,
     *      java.lang.String)
     */

    public PooledConnection getPooledConnection ( String user , String pw )
            throws SQLException
    {
        PooledConnection ret = null;
        checkSetup ();
        XAConnection xaconn = xads_.getXAConnection ( user, pw );
        ret = new ExternalXAPooledConnectionImp ( xaconn,
                getTransactionalResource (), getLogWriter () );
        return ret;
    }

}
