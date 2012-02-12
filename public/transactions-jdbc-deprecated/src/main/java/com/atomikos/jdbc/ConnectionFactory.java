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
import java.sql.SQLException;


/**
 * 
 * 
 * A wrapper for masking the different JDBC pooled connection factory
 * interfaces, (discrepancy between ConnectionPoolDataSource and XADataSource)
 * so that one pool can be used for both. Instances should have a public no-arg
 * constructor.
 */

public interface ConnectionFactory
{

    public XPooledConnection getPooledConnection () throws SQLException;

    /**
     * Gets the log writer for debugging.
     * 
     * @return PrintWriter The log writer; null if none or if not supported.
     * @exception SQLException
     *                On error.
     */

    public PrintWriter getLogWriter () throws SQLException;

    /**
     * Sets the log writer for debugging.
     * 
     * @param pw
     *            The print writer to log to.
     * @exception SQLException
     *                On error.
     */

    public void setLogWriter ( PrintWriter pw ) throws SQLException;

    /**
     * Get the login timeout in seconds
     * 
     * @return int The no of secs before login times out.
     * @exception SQLException
     *                On error.
     */

    public int getLoginTimeout () throws SQLException;

    /**
     * Sets the login timeout.
     * 
     * @param secs
     *            The no of seconds.
     * @exception SQLException
     *                On error.
     */

    public void setLoginTimeout ( int secs ) throws SQLException;

}
