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
