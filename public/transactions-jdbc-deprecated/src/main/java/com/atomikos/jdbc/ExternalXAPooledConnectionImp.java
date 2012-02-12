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
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.LoggingXAResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.TxState;

/**
 * 
 * 
 * An implementation of a DTPPooledConnection that can be managed by internal as
 * well as external pools.
 */

public class ExternalXAPooledConnectionImp implements DTPPooledConnection,
        ConnectionEventListener
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(ExternalXAPooledConnectionImp.class);
	
	protected static void suspendResourceTransaction (
			ResourceTransaction restx ) {
		XAResourceTransaction xarestx = ( XAResourceTransaction ) restx;
		if ( xarestx != null && 
			!xarestx.getState().equals( TxState.TERMINATED )) {
			//check terminated to resolve ISSUE 10102
			xarestx.suspend();
		}
	}

    protected java.util.Date lastUse_ = new java.util.Date ();
    // the date of last successful Statement creation...
    protected XAConnection myConn_;
    protected Vector listeners_ = new Vector ();
    // the objects listening for events
    // NOTE: events of the wrapped instance are caught here
    // and transformed into a new event with this as a source.
    protected boolean invalidated_ = false;
    // if true: connectionpool should not reuse this connection
    protected java.util.Date creationTime_ = new java.util.Date ();
    // for checking in debugging whether 2 are the same
    protected boolean discarded_ = false;
    protected TransactionalResource resource_;
    protected XAResourceTransaction restx_;
    protected PrintWriter logWriter_;
    protected HeuristicMessage msg_;
    protected java.sql.Connection connectionProxy_;

    // to return the same connection on consecutive
    // getConnection() calls

    public String toString ()
    {
        return new String ( "ExternalXAPooledConnectionImp"
                + creationTime_.getTime () + super.toString () );
    }

    public ExternalXAPooledConnectionImp ( XAConnection c ,
            TransactionalResource resource )
    {
        myConn_ = c;
        myConn_.addConnectionEventListener ( this );
        // System.out.println ( this + " listening on " + myConn_ );
        resource_ = resource;
    }

    protected ExternalXAPooledConnectionImp ( XAConnection c ,
            TransactionalResource resource , PrintWriter logWriter )
    {
        this ( c , resource );
        logWriter_ = logWriter;
    }

//    public void setConnection ( XAConnection c )
//    {
//        if ( myConn_ != null )
//            myConn_.removeConnectionEventListener ( this );
//        myConn_ = c;
//        myConn_.addConnectionEventListener ( this );
//
//    }

    public TransactionalResource getTransactionalResource ()
    {
        return resource_;
    }

    public void setResourceTransaction ( ResourceTransaction restx )
            throws SQLException
    {
        if ( !(restx instanceof XAResourceTransaction) )
            throw new RuntimeException ( "Expected: XAResourceTransaction" );

        restx_ = (XAResourceTransaction) restx;

        // make sure that restx uses correct xa resource
        XAResource xares = myConn_.getXAResource ();

        if ( logWriter_ != null ) {
            xares = new LoggingXAResource ( xares, logWriter_ );
        }

        restx_.setXAResource ( xares );
        if ( msg_ != null ) {
            // System.out.println ( "Adding message: " + msg_ );
            restx_.addHeuristicMessage ( msg_ );
            // set msg to null to avoid repeated adding of same msg
            // for each SQL call on the connection
            msg_ = null;
        }

        // FOLLOWING NOT ALLOWED HERE: calling isClosed() or
        // other meta-methods after close() will re-associate
        // the connection with the tx!!!!
        // discarded_ = false;
    }

    public ResourceTransaction unsetResourceTransaction ()
    {
        ResourceTransaction ret = restx_;
        restx_ = null;
        return ret;
    }

    /**
     * To get the connection to work with.
     * 
     * @return Connection The connection to work on. Any statement that is
     *         created will have a default timeout of 60 seconds. It is
     *         recommended that a timeout is used in any case, in order to avoid
     *         blocking rollbacks!
     */

    public Connection getConnection () throws SQLException
    {
        return getConnection ( null );
    }

    public void setInvalidated ()
    {
        invalidated_ = true;
    }

    public boolean getInvalidated ()
    {
        return invalidated_;
    }

    public void close () throws SQLException
    {
        myConn_.removeConnectionEventListener ( this );
        myConn_.close ();
    }

    public synchronized void addConnectionEventListener (
            ConnectionEventListener l )
    {
        if ( !listeners_.contains ( l ) )
            listeners_.addElement ( l );

    }

    public synchronized void removeConnectionEventListener (
            ConnectionEventListener l )
    {
        listeners_.removeElement ( l );
    }

    public void setLastUse ( java.util.Date date )
    {
        lastUse_ = date;
    }

    public java.util.Date getLastUse ()
    {
        return lastUse_;
    }

    public boolean isDiscarded ()
    {

        return discarded_;
    }

    protected void setDiscarded ()
    {
        discarded_ = true;
        connectionProxy_ = null;
        // before reusing the connection,
        // make sure that the restx association
        // is cleared; to avoid mixing the work
        // of different txs!
        unsetResourceTransaction ();

    }

    public void connectionClosed ( ConnectionEvent e )
    {

    		ResourceTransaction restx = unsetResourceTransaction ();
        suspendResourceTransaction ( restx );

        setDiscarded ();

        ConnectionEvent e2 = new ConnectionEvent ( this );
        Enumeration enumm = listeners_.elements ();
        while ( enumm.hasMoreElements () ) {
            ConnectionEventListener l = (ConnectionEventListener) enumm
                    .nextElement ();
            l.connectionClosed ( e2 );
        }
    }

    public void connectionErrorOccurred ( ConnectionEvent e )
    {
        ResourceTransaction restx = unsetResourceTransaction ();
        suspendResourceTransaction ( restx );
        setInvalidated ();
        ConnectionEvent e2 = new ConnectionEvent ( this, e.getSQLException () );
        Enumeration enumm = listeners_.elements ();
        while ( enumm.hasMoreElements () ) {
            ConnectionEventListener l = (ConnectionEventListener) enumm
                    .nextElement ();
            l.connectionErrorOccurred ( e2 );
        }
    }

    /**
     * @see com.atomikos.jdbc.DTPPooledConnection#getConnection(com.atomikos.icatch.HeuristicMessage)
     */
    public Connection getConnection ( HeuristicMessage msg )
            throws SQLException
    {
        msg_ = msg;
        discarded_ = false;
        if ( connectionProxy_ == null ) {
            connectionProxy_ = (java.sql.Connection) ConnectionProxy
                    .newInstance ( this, myConn_.getConnection (), resource_ );
        }
        return connectionProxy_;

    }

    /**
     * @see com.atomikos.jdbc.DTPPooledConnection#isInResourceTransaction()
     */
    public boolean isInResourceTransaction ()
    {
        return restx_ != null;
    }

}
