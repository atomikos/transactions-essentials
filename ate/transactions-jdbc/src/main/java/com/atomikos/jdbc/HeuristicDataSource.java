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
