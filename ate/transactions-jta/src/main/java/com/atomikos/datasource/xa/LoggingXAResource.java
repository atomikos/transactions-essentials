//$Id: LoggingXAResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: LoggingXAResource.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:36  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:52  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:30  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:06  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2004/10/12 13:04:52  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/10/11 13:40:12  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: LoggingXAResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.2  2003/03/11 06:42:57  guy
//$Id: LoggingXAResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: LoggingXAResource.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//Revision 1.1.2.1  2003/02/26 21:46:43  guy
//Added Logging Features.
//

package com.atomikos.datasource.xa;

import java.io.PrintWriter;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * 
 * 
 * A wrapper for an XAResource that enables logging to a PrintWriter.
 */

public class LoggingXAResource implements XAResource
{
    private PrintWriter writer_;
    // where to log to

    private XAResource xares_;

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
        xares_ = xares;
        writer_ = writer;
    }

    private void log ( String msg , XAException error )
    {
        if ( writer_ != null ) {
            writer_.println ( "XAResource: " + msg + ": " + error.toString ()
                    + " errorCode " + error.errorCode );
        }
    }

    private void log ( String msg )
    {
        if ( writer_ != null ) {
            writer_.println ( "XAResource: " + msg );
        }
    }

    /**
     * @see XAResource
     */

    public Xid[] recover ( int flag ) throws XAException
    {
        Xid[] ret = null;

        try {
            log ( "Enter recover" );
            ret = xares_.recover ( flag );
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

    public boolean setTransactionTimeout ( int secs ) throws XAException
    {
        boolean ret = false;

        try {
            log ( "Enter setTransactionTimeout" );
            ret = xares_.setTransactionTimeout ( secs );
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

    public int getTransactionTimeout () throws XAException
    {
        int ret = -1;

        try {
            log ( "Enter getTransactionTimeout" );
            ret = xares_.getTransactionTimeout ();
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

    public boolean isSameRM ( XAResource xares ) throws XAException
    {
        boolean ret = false;

        try {
            log ( "Enter isSameRM" );
            ret = xares_.isSameRM ( xares );
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

    public void start ( Xid xid , int flags ) throws XAException
    {
        try {
            log ( "Enter start for xid " + xid.toString () );
            xares_.start ( xid, flags );
            log ( "Exit start" );
        } catch ( XAException e ) {
            log ( "Error in start", e );
            throw e;
        }

    }

    /**
     * @see XAResource
     */

    public void end ( Xid xid , int flags ) throws XAException
    {
        try {
            log ( "Enter end for xid " + xid.toString () );
            xares_.end ( xid, flags );
            log ( "Exit end" );
        } catch ( XAException e ) {
            log ( "Error in end", e );
            throw e;
        }

    }

    /**
     * @see XAResource
     */

    public int prepare ( Xid xid ) throws XAException
    {
        int ret = -1;
        try {
            log ( "Enter prepare for xid " + xid.toString () );
            ret = xares_.prepare ( xid );
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

    public void rollback ( Xid xid ) throws XAException
    {
        try {
            log ( "Enter rollback for xid " + xid.toString () );
            xares_.rollback ( xid );
            log ( "Exit rollback" );
        } catch ( XAException e ) {
            log ( "Error in rollback", e );
            throw e;
        }

    }

    /**
     * @see XAResource
     */

    public void commit ( Xid xid , boolean onephase ) throws XAException
    {
        try {
            log ( "Enter commit for xid " + xid.toString () );
            xares_.commit ( xid, onephase );
            log ( "Exit commit" );
        } catch ( XAException e ) {
            log ( "Error in commit", e );
            throw e;
        }

    }

    /**
     * @see XAResource
     */

    public void forget ( Xid xid ) throws XAException
    {
        try {
            log ( "Enter forget for xid " + xid.toString () );
            xares_.forget ( xid );
            log ( "Exit forget" );
        } catch ( XAException e ) {
            log ( "Error in forget", e );
            throw e;
        }

    }
}
