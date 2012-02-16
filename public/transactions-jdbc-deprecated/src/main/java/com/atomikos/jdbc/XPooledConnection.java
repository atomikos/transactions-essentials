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

import javax.sql.PooledConnection;

/**
 * 
 * 
 * An enhancement of a pooled connection, that works with the ConnectionPool
 * class.
 */

public interface XPooledConnection extends PooledConnection
{

    public String toString ();

    /**
     * To get the connection to work with.
     * 
     * @return Connection The connection to work on. Any statement that is
     *         created will have a default timeout of 60 seconds. It is
     *         recommended that a timeout is used in any case, in order to avoid
     *         blocking rollbacks!
     */
    public Connection getConnection () throws SQLException;

    /**
     * Closes the underlying connection for keeps.
     */
    public void close () throws SQLException;

    /**
     * For pool management reasons.
     */
    public void setLastUse ( java.util.Date date );

    /**
     * For pool management .
     */
    public java.util.Date getLastUse ();

    public void setInvalidated ();

    public boolean getInvalidated ();

}
