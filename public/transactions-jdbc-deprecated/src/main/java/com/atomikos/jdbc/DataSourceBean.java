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

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.XADataSource;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.DefaultXidFactory;
import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * @deprecated As of release 3.3, the {@link AtomikosDataSourceBean} should be used instead.
 * 
 */

public class DataSourceBean implements HeuristicDataSource, Serializable,
        Referenceable
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(DataSourceBean.class);

    private transient JtaDataSourceImp ds_;
    // the data source to delegate to

    private XADataSource xads_;
    // the hook to the JDBC database, needed to
    // reconstruct the transient datasource

    private String xadsJndiName_;
    // optionally allow the xads to be specified through the
    // JNDI name on which it is intalled

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
    
    private boolean testOnBorrow_;
    //should connections be tested when gotten?

    public DataSourceBean ()
    {
        ds_ = null;
        xads_ = null;
        resourceName_ = "someUniqueName";
        xidFactory_ = "Default";
        poolSize_ = 2;
        connectionTimeout_ = 30;
        exclusive_ = true;
        xadsJndiName_ = "";
        validatingQuery_ = "";
        testOnBorrow_ = false;
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

        if ( xads_ == null && getDataSourceName ().equals ( "" ) )
            throw new SQLException ( "DataSourceBean: xaDataSource not set." );
        if ( xidFactory_ == null )
            throw new SQLException ( "DataSourceBean: xidFormat not set." );

        if ( !validation && !getDataSourceName ().equals ( "" ) ) {
            // XADataSource is specified through its JNDI name -> look it up
            try {
                Context ctx = new InitialContext ();
                Context envCtx = (Context) ctx.lookup ( "java:comp/env" );
                xads_ = (XADataSource) envCtx.lookup ( getDataSourceName () );
            } catch ( Exception e ) {
                // e.printStackTrace();
                throw new SQLException ( "DataSourceBean: setup error: "
                        + e.getClass ().getName () + " " + e.getMessage () );
            }
        }

        XidFactory xidFactory = null;

        xidFactory = new DefaultXidFactory ();

        XAConnectionFactory factory = new XAConnectionFactory ( resourceName_,
                "", "", xads_, xidFactory );
        factory.setExclusive ( exclusive_ );
        ds_ = new JtaDataSourceImp ( factory, poolSize_, connectionTimeout_,
                validation, !validation, validatingQuery_ , testOnBorrow_ );

        // the application does not know the Jta datasource and hence can not
        // shut it down. therefore, add a shutdown hook to do this job
        DataSourceShutdownHook hook = new DataSourceShutdownHook ( ds_ );
        Configuration.addShutdownHook ( hook );
    }

    private synchronized void checkSetup () throws SQLException
    {
        checkSetup ( false );
    }
    
    public void setTestOnBorrow ( String value )
    {
    		testOnBorrow_ = "true".equals ( value );
    }
    
    public String getTestOnBorrow()
    {
    		return testOnBorrow_ + "";
    }

    public void setDataSourceName ( String name )
    {
        xadsJndiName_ = name;
    }

    public String getDataSourceName ()
    {
        return xadsJndiName_;
    }

    public void setXaDataSource ( XADataSource xads )
    {
        xads_ = xads;
    }

    public XADataSource getXaDataSource ()
    {
        return xads_;
    }

    public void setUniqueResourceName ( String resourceName )
    {
        resourceName_ = resourceName;
    }

    public String getUniqueResourceName ()
    {
        return resourceName_;
    }

    public void setXidFormat ( String factory )
    {
        xidFactory_ = factory;
    }

    public String getXidFormat ()
    {
        return xidFactory_;
    }

    public void setConnectionPoolSize ( String poolSize )
    {
        poolSize_ = Integer.parseInt ( poolSize );
    }

    public String getConnectionPoolSize ()
    {
        return "" + poolSize_;
    }

    public void setConnectionTimeout ( String timeout )
    {
        connectionTimeout_ = Integer.parseInt ( timeout );
    }

    public String getConnectionTimeout ()
    {
        return "" + connectionTimeout_;
    }

    public void setExclusiveConnectionMode ( String mode )
    {
        exclusive_ = "true".equals ( mode );
    }

    public String isExclusiveConnectionMode ()
    {
        return "" + exclusive_;
    }

    public TransactionalResource getTransactionalResource ()
    {
        try {
            checkSetup ();
        } catch ( SQLException err ) {
            err.printStackTrace ();
            throw new RuntimeException ( err.getMessage () );
        }
        return ds_.getTransactionalResource ();
    }

    public void setValidatingQuery ( String query )
    {
        validatingQuery_ = query;
    }

    public String getValidatingQuery ()
    {
        return validatingQuery_;
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
        }

        finally {
            if ( c != null )
                c.close ();
        }
    }

    //
    //
    // IMPLEMENTATION OF HEURISTICDATASOURCE
    //
    //

    public Connection getConnection () throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ();
    }

    public Connection getConnection ( String user , String pw )
            throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( user, pw );
    }

    public Connection getConnection ( HeuristicMessage msg )
            throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( msg );
    }

    public Connection getConnection ( String msg ) throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( msg );
    }

    public Connection getConnection ( String user , String passwd , String msg )
            throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( user, passwd, msg );
    }

    public Connection getConnection ( String user , String pw ,
            HeuristicMessage msg ) throws SQLException
    {
        checkSetup ();
        return ds_.getConnection ( user, pw, msg );
    }

    public int getLoginTimeout () throws SQLException
    {
        checkSetup ();
        return ds_.getLoginTimeout ();
    }

    public PrintWriter getLogWriter () throws SQLException
    {
        checkSetup ();
        return ds_.getLogWriter ();
    }

    public void setLoginTimeout ( int seconds ) throws SQLException
    {
        checkSetup ();
        ds_.setLoginTimeout ( seconds );
    }

    public void setLogWriter ( PrintWriter out ) throws SQLException
    {
        checkSetup ();
        ds_.setLogWriter ( out );
    }

    // close the datasources
    public void close () throws SQLException
    {
    	    //see case 21666: don't call setup
        //checkSetup ();
        if ( ds_ != null ) ds_.close ();
    }

    //
    //
    // IMPLEMENTATION OF REFERENCEABLE
    //
    //

    public Reference getReference () throws NamingException
    {
        return JtaDataSourceImp.createReference ( getUniqueResourceName () );
    }

	
}
