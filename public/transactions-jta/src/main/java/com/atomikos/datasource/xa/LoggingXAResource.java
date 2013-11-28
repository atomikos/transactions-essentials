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

package com.atomikos.datasource.xa;

import java.io.PrintWriter;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * A wrapper for an XAResource that enables logging to a PrintWriter.
 */

public class LoggingXAResource implements XAResource
{

    private PrintWriter writer;
    // where to log to

    private XAResource xares;
    // the wrapped resource

    /**
     * Create a new instance.
     *
     * @param xares
     *            The XAResource to wrap.
     * @param writer
     *            The writer to log to, null if none.
     */

    public LoggingXAResource ( XAResource xares , PrintWriter writer )
    {
        this.xares = xares;
        this.writer = writer;
    }

    private void log ( String msg , XAException error )
    {
        if ( this.writer != null ) {
            this.writer.println ( "XAResource: " + msg + ": " + error.toString ()
                    + " errorCode " + error.errorCode );
        }
    }

    private void log ( String msg )
    {
        if ( this.writer != null ) {
            this.writer.println ( "XAResource: " + msg );
        }
    }

    /**
     * @see XAResource
     */

    @Override
	public Xid[] recover ( int flag ) throws XAException
    {
        Xid[] ret = null;

        try {
            log ( "Enter recover" );
            ret = this.xares.recover ( flag );
            log ( "Exit recover" );
        } catch ( XAException e ) {
            log ( "Error in recover", e );
            throw e;
        }

        return ret;
    }

    /**
     * @see XAResource
     */

    @Override
	public boolean setTransactionTimeout ( int secs ) throws XAException
    {
        boolean ret = false;

        try {
            log ( "Enter setTransactionTimeout" );
            ret = this.xares.setTransactionTimeout ( secs );
            log ( "Exit setTransactionTimeout with return value " + ret );
        } catch ( XAException e ) {
            log ( "Error in setTransactionTimeout", e );
            throw e;
        }

        return ret;
    }

    /**
     * @see XAResource
     */

    @Override
	public int getTransactionTimeout () throws XAException
    {
        int ret = -1;

        try {
            log ( "Enter getTransactionTimeout" );
            ret = this.xares.getTransactionTimeout ();
            log ( "Exit getTransactionTimeout with return value " + ret );
        } catch ( XAException e ) {
            log ( "Error in getTransactionTimeout", e );
            throw e;
        }

        return ret;
    }

    /**
     * @see XAResource
     */

    @Override
	public boolean isSameRM ( XAResource xares ) throws XAException
    {
        boolean ret = false;

        try {
            log ( "Enter isSameRM" );
            ret = this.xares.isSameRM ( xares );
            log ( "Exit isSameRM with return value " + ret );
        } catch ( XAException e ) {
            log ( "Error in isSameRM", e );
            throw e;
        }

        return ret;
    }

    /**
     * @see XAResource
     */

    @Override
	public void start ( Xid xid , int flags ) throws XAException
    {
        try {
            log ( "Enter start for xid " + xid.toString () );
            this.xares.start ( xid, flags );
            log ( "Exit start" );
        } catch ( XAException e ) {
            log ( "Error in start", e );
            throw e;
        }

    }

    /**
     * @see XAResource
     */

    @Override
	public void end ( Xid xid , int flags ) throws XAException
    {
        try {
            log ( "Enter end for xid " + xid.toString () );
            this.xares.end ( xid, flags );
            log ( "Exit end" );
        } catch ( XAException e ) {
            log ( "Error in end", e );
            throw e;
        }

    }

    /**
     * @see XAResource
     */

    @Override
	public int prepare ( Xid xid ) throws XAException
    {
        int ret = -1;
        try {
            log ( "Enter prepare for xid " + xid.toString () );
            ret = this.xares.prepare ( xid );
            log ( "Exit prepare with return value " + ret );
        } catch ( XAException e ) {
            log ( "Error in prepare", e );
            throw e;
        }

        return ret;
    }

    /**
     * @see XAResource
     */

    @Override
	public void rollback ( Xid xid ) throws XAException
    {
        try {
            log ( "Enter rollback for xid " + xid.toString () );
            this.xares.rollback ( xid );
            log ( "Exit rollback" );
        } catch ( XAException e ) {
            log ( "Error in rollback", e );
            throw e;
        }

    }

    /**
     * @see XAResource
     */

    @Override
	public void commit ( Xid xid , boolean onephase ) throws XAException
    {
        try {
            log ( "Enter commit for xid " + xid.toString () );
            this.xares.commit ( xid, onephase );
            log ( "Exit commit" );
        } catch ( XAException e ) {
            log ( "Error in commit", e );
            throw e;
        }

    }

    /**
     * @see XAResource
     */

    @Override
	public void forget ( Xid xid ) throws XAException
    {
        try {
            log ( "Enter forget for xid " + xid.toString () );
            this.xares.forget ( xid );
            log ( "Exit forget" );
        } catch ( XAException e ) {
            log ( "Error in forget", e );
            throw e;
        }

    }
}
