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
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.datasource.xa.jdbc.JdbcTransactionalResource;

/**
 * 
 * 
 * A default connection factory for XA connections that work with the Atomikos
 * connection pools.
 */

// @todo TEST NEW USER AND PASSWD SETS ON TXRESOURCE!!!
public class XAConnectionFactory implements ConnectionFactory
{
    private String userName_;

    private String passwd_;

    private String resourceName_;

    private XADataSource ds_;

    private JdbcTransactionalResource res_;

    private boolean exclusive_;

    // true iff XAResource is Oracle or SQLServer style
    // where 2PC must be on same instance as start&end

    /**
     * Constructor for subclasses with their own transactional resource.
     */

    protected XAConnectionFactory ( String resourceName , String userName ,
            String passwd , XADataSource xaDataSource ,
            JdbcTransactionalResource res )
    {
        resourceName_ = resourceName;
        userName_ = userName;
        passwd_ = passwd;
        ds_ = xaDataSource;
        res_ = res;

        // ONLY set the user and password if it is not
        // empty; in order to avoid that SimpleDataSourceBeans
        // will override user settings of XADataSource!!!
        // BUG IN INITIAL 2.0 BETA
        if ( userName != null && !userName.equals ( "" ) ) {
            res_.setUser ( userName );
            res_.setPassword ( passwd );
        }

        exclusive_ = false;
    }

    /**
     * Creates a new instance with a given resource name, user name and password
     * for the XADataSource.
     * 
     * @param resourceName
     *            The unique resource name for the corresponding
     *            JdbcTransactionalResource (which will be created
     *            automatically).
     * @param userName
     *            The user name to use for getting XA connections. Null if no
     *            authentication is supported by the supplied XADataSource.
     * @param passwd
     *            The password for the user, or null if no authentication is
     *            supported by the supplied XADataSource.
     * @param xaDataSource
     *            The XADataSource to use.
     */

    public XAConnectionFactory ( String resourceName , String userName ,
            String passwd , XADataSource xaDataSource )
    {
        resourceName_ = resourceName;
        userName_ = userName;
        passwd_ = passwd;
        ds_ = xaDataSource;
        res_ = new JdbcTransactionalResource ( resourceName, ds_ );
        res_.setUser ( userName );
        res_.setPassword ( passwd );
        exclusive_ = false;
    }

    /**
     * Creates a new instance with a given resource name, user name and password
     * for the XADataSource, and a custom XidFactory.
     * 
     * @param resourceName
     *            The unique resource name for the corresponding
     *            JdbcTransactionalResource (which will be created
     *            automatically).
     * @param userName
     *            The user name to use for getting XA connections. Null if no
     *            authentication is supported by the supplied XADataSource.
     * @param passwd
     *            The password for the user, or null if no authentication is
     *            supported by the supplied XADataSource.
     * @param xaDataSource
     *            The XADataSource to use.
     * @param xidFactory
     *            The custom XidFactory.
     */

    public XAConnectionFactory ( String resourceName , String userName ,
            String passwd , XADataSource xaDataSource , XidFactory xidFactory )
    {
        resourceName_ = resourceName;
        userName_ = userName;
        passwd_ = passwd;
        ds_ = xaDataSource;
        res_ = new JdbcTransactionalResource ( resourceName, ds_, xidFactory );
        res_.setUser ( userName );
        res_.setPassword ( passwd );
        exclusive_ = false;
    }

    /**
     * Used by getPooledConnection() to get a connection. If the drivers do not
     * support authentication, then an empty string should be returned here.
     * 
     * @return String The user name, or null if not applicable.
     */

    protected String getUserName ()
    {
        return userName_;
    }

    /**
     * Used by getPooledConnection() to get a connection.
     * 
     * 
     * @return String The password, or null if not applicable.
     */

    protected String getPassword ()
    {
        return passwd_;
    }

    /**
     * This method gets the underlying data source. It is called by
     * getPooledConnection() to get connections.
     * 
     * @return XADataSource The data source to get XAConnections from.
     */

    public XADataSource getXADataSource ()
    {
        return ds_;
    }

    /**
     * Sets the connections generated to exclusive for 2PC. This mode is needed
     * for certain databases that do not conform to XA entirely.
     * 
     * @param exclusive
     *            If true, then connections will be kept until after
     *            commit/rollback, resulting in lower reuse.
     */

    public void setExclusive ( boolean exclusive )
    {
        exclusive_ = exclusive;
    }

    /**
     * Tests if the connections that are generated will be exclusive or not.
     * 
     * @return boolean True iff connections are exclusive.
     */

    public boolean isExclusive ()
    {
        return exclusive_;
    }

    /**
     * Get the transactional resource.
     * 
     * @return TransactionalResource The transactional resource.
     */

    public TransactionalResource getTransactionalResource ()
    {
        return res_;
    }

    /**
     * The main method: gets a new DTPPooledConnection instance.
     * 
     * @return XPooledConnection An instance of DTPPooledConnection for use with
     *         DTPConnectionPool.
     * 
     * @exception IllegalStateException
     *                If not initialized.
     * @exception SQLException
     *                On sql errors.
     */

    public XPooledConnection getPooledConnection ()
            throws IllegalStateException, SQLException
    {
        XAConnection conn = null;
        XPooledConnection ret = null;

        if ( getTransactionalResource () == null )
            throw new IllegalStateException (
                    "XAConnectionFactory: no tx resource" );
        if ( getUserName () == null || getUserName ().equals ( "" ) ) {
            conn = getXADataSource ().getXAConnection ();
        } else {
            conn = getXADataSource ().getXAConnection ( getUserName (),
                    getPassword () );
        }

        if ( !exclusive_ ) {
            ret = new ExternalXAPooledConnectionImp ( conn,
                    getTransactionalResource (), getLogWriter () );
        } else {
            ret = new ExclusiveExternalXAPooledConnectionImp ( conn,
                    getTransactionalResource (), getLogWriter () );
        }

        return ret;
    }

    public PrintWriter getLogWriter () throws SQLException
    {
        return getXADataSource ().getLogWriter ();
    }

    public void setLogWriter ( PrintWriter pw ) throws SQLException
    {
        getXADataSource ().setLogWriter ( pw );
    }

    public int getLoginTimeout () throws SQLException
    {
        return getXADataSource ().getLoginTimeout ();
    }

    public void setLoginTimeout ( int secs ) throws SQLException
    {
        getXADataSource ().setLoginTimeout ( secs );
    }
}
