/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.util.Assert;

/**
 *
 *
 * A default XATransactionalResource implementation for JDBC.
 */

public class JdbcTransactionalResource extends XATransactionalResource
{
    private XADataSource xaDataSource;
    private XAConnection xaConnection;
    private String user; // null if not set
    private String password; // null if not set
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
        super(serverName);
        Assert.notNull("XADataSource must not be null", xads);
        this.xaDataSource = xads;
        this.xaConnection = null;
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
        this.xaDataSource = xads;
        this.xaConnection = null;
    }

    /**
     * Get the user
     *
     * @return String The user, or empty string.
     */
    private String getUser ()
    {
        String ret = "";
        if ( this.user != null )
            ret = this.user;

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
        if ( this.password != null )
            ret = this.password;
        return ret;
    }

    /**
     * Implements the functionality to get an XAResource handle.
     *
     * @return XAResource The XAResource instance.
     */

    @Override
	protected synchronized XAResource refreshXAConnection ()
            throws ResourceException
    {
        XAResource res = null;

        if ( this.xaConnection != null ) {
            try {
                this.xaConnection.close ();
            } catch ( Exception err ) {
                // happens if connection has timed out
                // which is probably normal, otherwise
                // refresh would not be called in the first place
            }
        }

        try {
            this.xaConnection = createXAConnection();
            if ( this.xaConnection != null )
                res = this.xaConnection.getXAResource ();
            // null if db down during recovery
        } catch ( SQLException sql ) {
            throw new ResourceException ( "Error in getting XA resource",sql );
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
        this.user = user;
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
        this.password = password;
    }

    /**
     * Overrides default close to include closing any open connections to the
     * XADataSource.
     */

    @Override
	public void close () throws ResourceException
    {
        super.close ();
        try {
            if ( this.xaConnection != null )
                this.xaConnection.close ();
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
                conn = this.xaDataSource.getXAConnection ();
            else
                conn = this.xaDataSource.getXAConnection ( getUser (), getPassword () );
        } catch ( SQLException noConnection ) {
            // ignore and return null: happens if
            // db is down at this time (during
            // recovery for instance)
            conn = null;
        }
        return conn;
    }



}
