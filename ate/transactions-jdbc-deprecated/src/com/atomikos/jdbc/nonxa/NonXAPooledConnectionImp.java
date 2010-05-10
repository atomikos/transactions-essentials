package com.atomikos.jdbc.nonxa;

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
