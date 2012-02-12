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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.atomikos.jdbc.ConnectionFactory;
import com.atomikos.jdbc.XPooledConnection;

/**
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class NonXAConnectionFactory implements ConnectionFactory
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(NonXAConnectionFactory.class);

    private String userName;

    private String password;

    private DataSource dataSource;

    /**
     * Create a new instance.
     * 
     * @param dataSource
     *            The underlying (non-XA) datasource to use. For instance, a
     *            MySQL datasource.
     * @param userName
     *            The user name, or empty string if not applicable.
     * @param password
     *            The password for the given user, or empty if the user is
     *            empty.
     */

    public NonXAConnectionFactory ( DataSource dataSource , String userName ,
            String password )
    {
        this.userName = userName;
        this.password = password;
        this.dataSource = dataSource;
    }

    protected String getUserName ()
    {
        return userName;
    }

    protected String getPassword ()
    {
        return password;
    }

    protected DataSource getDataSource ()
    {
        return dataSource;
    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#getPooledConnection()
     */

    public XPooledConnection getPooledConnection () throws SQLException
    {
        NonXAPooledConnectionImp ret = null;
        Connection c = null;

        if ( getUserName () == null || getUserName ().equals ( "" ) )
            c = getDataSource ().getConnection ();
        else
            c = getDataSource ()
                    .getConnection ( getUserName (), getPassword () );
        ret = new NonXAPooledConnectionImp ( c );
        return ret;
    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#getLogWriter()
     */
    public PrintWriter getLogWriter () throws SQLException
    {
        return getDataSource ().getLogWriter ();
    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#setLogWriter(java.io.PrintWriter)
     */
    public void setLogWriter ( PrintWriter pw ) throws SQLException
    {
        getDataSource ().setLogWriter ( pw );

    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#getLoginTimeout()
     */
    public int getLoginTimeout () throws SQLException
    {
        return getDataSource ().getLoginTimeout ();
    }

    /**
     * @see com.atomikos.jdbc.ConnectionFactory#setLoginTimeout(int)
     */
    public void setLoginTimeout ( int secs ) throws SQLException
    {
        getDataSource ().setLoginTimeout ( secs );

    }

}
