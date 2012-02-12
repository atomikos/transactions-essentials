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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

import com.atomikos.jdbc.XPooledConnection;

/**
 * 
 * 
 * 
 * 
 * 
 */

class NonXAPooledConnectionImp implements XPooledConnection
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(NonXAPooledConnectionImp.class);

    private Connection connection;
    // the actual JDBC connection (non-XA capable)

    private Date lastUsed;

    private boolean invalidated;

    private ArrayList listeners;

    NonXAPooledConnectionImp ( Connection wrapped )
    {
        connection = wrapped;
        invalidated = false;
        listeners = new ArrayList ();
    }

    /**
     * @see javax.sql.PooledConnection#getConnection()
     */

    public Connection getConnection () throws SQLException
    {
        return connection;
    }

    /**
     * @see javax.sql.PooledConnection#close()
     */

    public void close () throws SQLException
    {
        connection.close ();
    }

    /**
     * @see com.atomikos.jdbc.XPooledConnection#setLastUse(java.util.Date)
     */

    public void setLastUse ( Date date )
    {
        lastUsed = date;

    }

    /**
     * @see com.atomikos.jdbc.XPooledConnection#getLastUse()
     */

    public Date getLastUse ()
    {
        return lastUsed;
    }

    /**
     * @see com.atomikos.jdbc.XPooledConnection#setInvalidated()
     */

    public void setInvalidated ()
    {
        invalidated = true;

    }

    /**
     * @see com.atomikos.jdbc.XPooledConnection#getInvalidated()
     */

    public boolean getInvalidated ()
    {
        return invalidated;
    }

    /**
     * @see javax.sql.PooledConnection#addConnectionEventListener(javax.sql.ConnectionEventListener)
     */

    public void addConnectionEventListener ( ConnectionEventListener listener )
    {
        listeners.add ( listener );

    }

    /**
     * @see javax.sql.PooledConnection#removeConnectionEventListener(javax.sql.ConnectionEventListener)
     */

    public void removeConnectionEventListener ( ConnectionEventListener listener )
    {
        listeners.remove ( listener );

    }

    void notifyCloseListeners ()
    {
        // clone to avoid interference of concurrent removals
        List list = (List) listeners.clone ();
        Iterator all = list.iterator ();
        ConnectionEvent event = new ConnectionEvent ( this );
        while ( all.hasNext () ) {
            ConnectionEventListener l = (ConnectionEventListener) all.next ();
            l.connectionClosed ( event );
        }
    }

}
