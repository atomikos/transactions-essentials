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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

import javax.sql.PooledConnection;

import com.atomikos.icatch.system.Configuration;

/**
 *
 *
 * <p>
 * A connection pooling implementation. This class is NOT meant to be used by
 * the application directly. Rather, a DataSource object should wrap this class.
 *
 *  @deprecated As of release 3.3, the newer pool should be used instead.
 *
 */

public class ConnectionPool implements Runnable
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(ConnectionPool.class);


    // timeout_ after which connections are tested for liveness and removed if
    // not ok
    private long timeout_;

    private Vector pool_;
    // contains the available objects
    private int maxSize_;
    // the maximum INITIAL size of the pool_
    private ConnectionFactory source_;
    // where to get a new connection from.
    private boolean timerNeeded_ = true;
    // keeps timer thread running
	private String testQuery_;
	//test SQL to validate connection
	private boolean testOnBorrow_;
	//test connections before use?


    /**
     * Constructor.
     *
     * @param size
     *            The size of the pool.
     * @param connSource
     *            The ConnectionFactory to use.
     * @param seconds
     *            The timeout for old connections in the pool.
     * @param testQuery
     * 			 A test query to validate connection liveness.
     * @param testOnBorrow
     * 			 Should connections be tested when borrowed or not?
     * 			 If true then connections will be tested when gotten:<br>
     *            <ul>
     *            <li>for a connection from the pool, a failing test will retry with another connection</li>
     *            <li>for new connections (i.e. when pool is empty), a failing test will be thrown to the application</li>
     *            </ul>
     */

    public ConnectionPool ( int size , ConnectionFactory connSource ,
            int connectionTimeout , String testQuery , boolean testOnBorrow ) throws SQLException
    {
        testQuery_ = testQuery;
        timeout_ = connectionTimeout * 1000;
        source_ = connSource;
        pool_ = new Vector ();
        maxSize_ = size;
        testOnBorrow_ = testOnBorrow;
        for ( int i = 0; i < maxSize_; i++ ) {
            XPooledConnection pc = source_.getPooledConnection ();
            if ( pc != null ) {
                pool_.addElement ( pc );
            } else
                throw new SQLException (
                        "ConnectionPool constructor: null PooledConnection" );
        }// for

        Thread timer = new Thread ( this );
        timer.setDaemon ( true );
        timer.start ();

    }

    /**
     * Get the current size of the available pool.
     *
     * @return int The current size. Does NOT include connections that are
     *         currently being used!
     */

    public int getSize ()
    {
        return pool_.size ();
    }

    /**
     * Get a PooledConnection instance from the pool.
     *
     * @return PooledConnection The instance.
     * @exception SQLException
     *                On error.
     */

    public synchronized PooledConnection getPooledConnection ()
            throws SQLException
    {
        PooledConnection retVal;

        if ( pool_.isEmpty () ) {
            retVal = ( XPooledConnection ) source_.getPooledConnection ();
            Configuration
                    .logWarning ( "JDBC ConnectionPool exhausted - allocated new connection: "
                            + retVal.toString () );

            if ( testOnBorrow_ ) {
            		//test, but DON'T CLOSE handle since the application will still need it!
            		test ( retVal , false );
            }
            //this is a NEW connection -> failure of test is DBMS problem -> propagate to application
        }
        else {

	        // here we are if pool not empty
	        retVal = (XPooledConnection) pool_.firstElement ();

	        if ( !pool_.removeElement ( retVal ) )
	            throw new SQLException ( "Unable to take connection out of pool?" );

	        if ( testOnBorrow_ ) {
		        try {
		        		//test, but DON'T CLOSE handle since the application will still need it!
		        		test ( retVal , false );
		        }
		        catch ( SQLException retry ) {
		        		retVal = getPooledConnection();
		        }
	        }
        }

        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "JDBC ConnectionPool: using connection: "
                + retVal );
        return retVal;
    }

    protected synchronized void putInPool ( PooledConnection pc )
    {
        pool_.addElement ( pc );
        notifyAll ();
    }

    /**
     * Puts back a connection after usage. This method should be called by
     * DataSource objects.
     *
     * @param conn
     *            The connection. If the connection has been invalidated, or if
     *            the pool is large enough, then this method will actually close
     *            the connection.
     */

    public synchronized void putBack ( XPooledConnection conn )
    {
        if ( !conn.getInvalidated () && getSize () < maxSize_ ) {
            if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Putting connection back in pool: "
                    + conn.toString () );
            putInPool ( conn );
        } else {

            try {
                if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Pool: closing connection: "
                        + conn.toString () );
                conn.close ();
                // closes underlying connection
            } catch ( SQLException err ) {
            }
        }
    }

    protected synchronized Enumeration getOldConnections ()
    {
        Vector returnVals = new Vector ();
        java.util.Date now = new java.util.Date ();
        // select those candidates that have not been used during the timeout_
        // interval
        Enumeration enumm = pool_.elements ();
        while ( enumm.hasMoreElements () ) {
            XPooledConnection pc = (XPooledConnection) enumm.nextElement ();
            if ( (now.getTime () - pc.getLastUse ().getTime ()) > timeout_ )
                returnVals.addElement ( pc );
        }// while

        // remove candidates from available pool_, for cleanup inspection
        enumm = returnVals.elements ();
        while ( enumm.hasMoreElements () ) {
            pool_.removeElement ( enumm.nextElement () );
        }// while
        return returnVals.elements ();
    }

    public void run ()
    {
        Enumeration enumm;

        try {
            while ( timerNeeded_ ) {
                Thread.sleep ( timeout_ );
                enumm = getOldConnections ();
                // re-check if not shutdown, or the pool will
                // restore connections
                while ( timerNeeded_ && enumm.hasMoreElements () ) {
                    XPooledConnection pc = (XPooledConnection) enumm
                            .nextElement ();

                    try {
                        if ( !pc.getInvalidated () ) {
                        	   test ( pc , true );
                        	   if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "ConnectionPool: connection is fine, keeping it in pool: " + pc);
                            // here we are if connection still OK
                            putInPool ( pc );

                        }// if
                        else if ( getSize () < maxSize_ ) {
                            // replace old connection, but only if pool is
                            // shrinking
                            PooledConnection newConn = source_
                                    .getPooledConnection ();
                            putInPool ( newConn );
                            Configuration
                                    .logDebug ( "ConnectionPool: replacing invalidated connection "
                                            + pc.toString () );
                        }
                    }// try
                    catch ( SQLException sqlErr ) {
                        // here we are if connection has a problem
                        // discard it and put new one in pool_
                        if ( getSize () < maxSize_ ) {
                            PooledConnection newConn = source_
                                    .getPooledConnection ();
                            putInPool ( newConn );
                            Configuration
                                    .logDebug ( "ConnectionPool: connection invalid, replacing it: "
                                            + pc.toString (), sqlErr );

                        }
                    }

                }// while
            }// while
        }// try
        catch ( Exception e ) {
        }
        // System.out.println ( "Connectionpool: thread exiting");
    }

    /**
     * Tests the pc for (re)usability.
     * @param pc The pooled connection to test.
     * @param closeConnectionAfterTest Whether to close the connection handle or not.
     * @throws SQLException On error - in that case, the connection will have been closed and should not be used.
     */
    private void test ( PooledConnection pc , boolean closeConnectionAfterTest ) throws SQLException
    {
    		Connection connection = null;
    		boolean cleanup = false;
    		try {
				connection = pc.getConnection();
				if ( testQuery_ == null || "".equals ( testQuery_ ) ) {
					if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "ConnectionPool: no query to test connection, trying getMetaData(): " + connection );
					connection.getMetaData();
					return;
				}
				if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "ConnectionPool: trying query '" + testQuery_ + "' on connection " + connection );
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery ( testQuery_ );
				rs.close();
				stmt.close();
		} catch ( Exception e ) {
			//catch any Exception - cf case 22198
			LOGGER.logWarning ( "ConnectionPool: error testing connection" , e );
			cleanup = true;
			//rethrow to fail test!
			SQLException sqlErr = new SQLException ( e.getMessage() );
			sqlErr.initCause ( e );
			throw sqlErr;
		}
		finally {
			try {
				if ( connection != null &&  ( closeConnectionAfterTest || cleanup ) ) connection.close();
				if ( cleanup ) pc.close();
			}
			catch ( SQLException error ) {
				LOGGER.logWarning ( "ConnectionPool: error closing connection during test" , error );
				//ignore; let original SQLException be thrown if any
			}
		}
	}

	/**
     * To be called when the pool of connections is no longer needed. This
     * method will close all connections in the pool.
     */

    public synchronized void cleanup ()
    {

        timerNeeded_ = false;
        if ( pool_ == null )
            return; // null if called twice

        Enumeration enumm = pool_.elements ();
        try {
            while ( enumm.hasMoreElements () ) {
                PooledConnection pc = (PooledConnection) enumm.nextElement ();
                pc.close ();
            }// while
        }// try
        catch ( SQLException e ) {
        }
        pool_ = null;
    }

    public void finalize () throws Throwable
    {
        try {
            cleanup ();
        } finally {
            super.finalize ();
        }
    }

    public PrintWriter getLogWriter () throws SQLException
    {
        return source_.getLogWriter ();
    }

    public void setLogWriter ( PrintWriter pw ) throws SQLException
    {
        source_.setLogWriter ( pw );
    }

    public void setLoginTimeout ( int secs ) throws SQLException
    {
        source_.setLoginTimeout ( secs );
    }

    public int getLoginTimeout () throws SQLException
    {
        return source_.getLoginTimeout ();
    }

}
