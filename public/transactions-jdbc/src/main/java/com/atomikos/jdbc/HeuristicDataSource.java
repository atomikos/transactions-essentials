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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.atomikos.icatch.HeuristicMessage;

/**
 * 
 * 
 * A data source that supports the addition of heuristic messages to SQL data
 * access.
 */

public interface HeuristicDataSource extends DataSource
{

    /**
     * Get a connection to the datasource for the given description of the work.
     * 
     * @param msg
     *            The heuristic message that best describes the work about to be
     *            done.
     * @return Connection The connection.
     * @exception SQLException
     *                On error.
     */
    public Connection getConnection ( String msg ) throws SQLException;

    /**
     * Get a connection to the datasource for the given description of the work.
     * 
     * @param user
     *            The user name to use.
     * @param passwd
     *            The password.
     * @param msg
     *            The heuristic message that best describes the work about to be
     *            done.
     * @return Connection The connection.
     * @exception SQLException
     *                On error.
     */

    public Connection getConnection ( String user , String passwd , String msg )
            throws SQLException;

    /**
     * Get a connection to the datasource for the given description of the work.
     * 
     * @param msg
     *            The heuristic message that best describes the work about to be
     *            done.
     * @return Connection The connection.
     * @exception SQLException
     *                On error.
     */

    public Connection getConnection ( HeuristicMessage msg )
            throws SQLException;

    /**
     * Get a connection to the datasource for the given description of the work.
     * 
     * @param user
     *            The user name to use.
     * @param passwd
     *            The password.
     * @param msg
     *            The heuristic message that best describes the work about to be
     *            done.
     * @return Connection The connection.
     * @exception SQLException
     *                On error.
     */

    public Connection getConnection ( String user , String passwd ,
            HeuristicMessage msg ) throws SQLException;
}
