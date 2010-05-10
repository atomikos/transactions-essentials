package com.atomikos.jdbc;

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
