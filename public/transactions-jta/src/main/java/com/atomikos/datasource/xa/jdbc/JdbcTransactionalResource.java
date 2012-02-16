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

package com.atomikos.datasource.xa.jdbc;

import java.sql.SQLException;
import java.util.Stack;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.XidFactory;

/**
 *
 *
 * A default XATransactionalResource implementation for JDBC.
 */

public class JdbcTransactionalResource extends XATransactionalResource
{

    private XADataSource xads_;

    // where to get new connections

    private XAConnection conn_;

    // the last connection used.

    private String user_;

    // the user; empty string if not set

    private String password_;

    // the password, empty string if not set
    /**
     * Constructs a new instance with a given name and XADataSource.
     *
     * @param serverName
     *            The unique name.
     * @param xads
     *            The data source.
     */

    public JdbcTransactionalResource ( String serverName , XADataSource xads )
    {
        super ( serverName );
        xads_ = xads;
        if ( xads_ == null )
            throw new RuntimeException ( "Null XADataSource argument" );
        conn_ = null;
    }

    /**
     * Constructs a new instance with a given name and XADataSource, and an Xid
     * factory to use. The custom Xid factory is needed for data servers that do
     * not accept arbitrary Xid formats.
     *
     * @param serverName
     *            The unique name.
     * @param xads
     *            The data source.
     * @param factory
     *            The custom Xid factory.
     */

    public JdbcTransactionalResource ( String serverName , XADataSource xads ,
            XidFactory factory )
    {
        super ( serverName , factory );
        xads_ = xads;
        conn_ = null;
    }

    /**
     * Get the user
     *
     * @return String The user, or empty string.
     */
    private String getUser ()
    {
        String ret = "";
        if ( user_ != null )
            ret = user_;

        return ret;
    }

    /**
     * Get the passwd
     *
     * @return String the password, or empty string
     */
    private String getPassword ()
    {
        String ret = "";
        if ( password_ != null )
            ret = password_;
        return ret;
    }

    /**
     * Implements the functionality to get an XAResource handle.
     *
     * @return XAResource The XAResource instance.
     */

    protected synchronized XAResource refreshXAConnection ()
            throws ResourceException
    {
        XAResource res = null;

        if ( conn_ != null ) {
            try {
                conn_.close ();
            } catch ( Exception err ) {
                // happens if connection has timed out
                // which is probably normal, otherwise
                // refresh would not be called in the first place
            }
        }

        try {
            conn_ = createXAConnection();
            if ( conn_ != null )
                res = conn_.getXAResource ();
            // null if db down during recovery
        } catch ( SQLException sql ) {
            Stack errors = new Stack ();
            errors.push ( sql );
            throw new ResourceException ( "Error in getting XA resource",
                    errors );
        }

        return res;

    }

    /**
     * Optionally set the user name with which to get connections for recovery.
     *
     * If not set, then the right user name should be configured on the
     * XADataSource directly.
     *
     * @param user
     *            The user name.
     */

    public void setUser ( String user )
    {
        user_ = user;
    }

    /**
     * Optionally set the password with which to get connections for recovery.
     *
     * If not set, then the right password should be configured on the
     * XADataSource directly.
     *
     * @param password
     *            The password.
     */

    public void setPassword ( String password )
    {
        password_ = password;
    }

    /**
     * Overrides default close to include closing any open connections to the
     * XADataSource.
     */

    public void close () throws ResourceException
    {
        super.close ();
        try {
            if ( conn_ != null )
                conn_.close ();
        } catch ( SQLException err ) {
            // throw new ResourceException ( err.getMessage() );
            // exception REMOVED because it close clashes
            // with shutdown hooks (in which the order of TS and
            // DS shutdown is unpredictable)
        }
    }

    private XAConnection createXAConnection()
    {
    		XAConnection conn = null;
    		try {
            if ( "".equals ( getUser () ) )
                conn = xads_.getXAConnection ();
            else
                conn = xads_.getXAConnection ( getUser (), getPassword () );
        } catch ( SQLException noConnection ) {
            // ignore and return null: happens if
            // db is down at this time (during
            // recovery for instance)
            conn = null;
        }
        return conn;
    }



}
