//$Id: TransactionImp.java,v 1.2 2006/09/19 08:03:55 guy Exp $

//$Log: TransactionImp.java,v $
//Revision 1.2  2006/09/19 08:03:55  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:23:59  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.15  2005/08/05 15:03:40  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.14  2005/05/10 08:44:04  guy
//Merged-in changes from Transactions_2_03 branch.
//
//Revision 1.13  2004/12/10 05:58:50  guy
//Added synchronization to xaResTxMap (MM Bug).
//

//Revision 1.12.2.1  2004/12/13 19:41:48  guy
//Updated bug fix: synchronized XAResTx map for MM.
//

//Revision 1.12  2004/10/12 13:03:38  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.11  2004/10/11 13:39:37  guy
//Fixed javadoc and EOL delimiters.
//
//Revision 1.10  2004/10/01 12:53:54  guy
//Updated todos.
//
//Revision 1.9  2004/09/18 12:09:39  guy
//Added automatic resource registration mode.
//
//Revision 1.8  2004/09/17 16:41:53  guy
//Improved log methods in Configuration.
//
//Revision 1.7  2004/09/17 16:13:40  guy
//Added dynamic registration of TemporaryXATransactionalResource for
//unknown XAResource enlists.
//Changed UserTransactionImp to init TM if not done yet.
//Added an easy UserTransactionManager class for zero-setup usage.
//
//Revision 1.6  2004/09/03 10:00:22  guy
//*** empty log message ***
//
//Revision 1.5  2004/09/02 08:21:04  guy
//Corrected to avoid double calls in afterCompletion.
//Added tolerance for non-delisting appservers.
//
//Revision 1.4  2004/03/22 15:37:39  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.3.2.5  2003/09/10 14:04:17  guy
//Moved setRollbackOnly to native kernel. Added: enlistResource throws RollbackException after setRollbackOnly is called.
//
//Revision 1.3.2.4  2003/07/09 09:14:36  guy
//Added todo.
//
//Revision 1.3.2.3  2003/06/20 16:31:39  guy
//*** empty log message ***
//
//Revision 1.3.2.2  2003/05/07 12:05:09  guy
//Renamed COMPLETING to PREPARING.
//
//Revision 1.3.2.1  2003/05/07 11:35:37  guy
//Adapted to new state names.
//

//
//Revision 1.2.4.3  2002/11/14 15:01:55  guy
//Adapted to new (redesigned) paradigm: getTx based in tid and suspend/resume should not work with a stack.
//
//Revision 1.2.4.2  2002/09/09 14:02:52  guy
//Added support for setRollbackOnly and delistResource with TMFAIL.
//
//Revision 1.2.4.1  2002/08/29 07:22:31  guy
//Added support for XAResource timeout: we no longer use the
//XAResourceConfiguration class because the XAResources may be
//invalidated without notice.
//
//Revision 1.2  2002/02/22 17:28:43  guy
//Updated: no RollbackException in addParticipant and registerSynch.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

package com.atomikos.icatch.jta;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.TemporaryXATransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * An adaptor for the javax transaction interface.
 */

class TransactionImp implements Transaction
{

    //
    protected CompositeTransaction ct_;

    protected Map xaresToTxMap_;

    // maps xaresources to restxs, needed for xasuspend/xaresume
    // INVARIANT: this map contains only restxs that are isActive() or
    // isXaSuspended!!!

    protected Stack suspendstack_;

    // after suspend, resume must be able to
    // supply the proper stack of local parent txs

    protected boolean autoRegistration_;

    // true if an unknown XAResource should be accepted
    // and recovered

    TransactionImp ( CompositeTransaction ct , boolean autoRegistration )
    {
        // System.err.println ( "TransactionImp: autoRegistration = " +
        // autoRegistration );
        ct_ = ct;
        autoRegistration_ = autoRegistration;
        xaresToTxMap_ = new HashMap ();
    }

    void setSuspendedStack ( Stack suspendstack )
    {
        suspendstack_ = suspendstack;
    }

    CompositeTransaction getCT ()
    {
        return ct_;
    }

    Stack getSuspendedStack ()
    {
        return suspendstack_;
    }

    private void printMsg ( String msg , int level )
    {
        if ( level == Console.WARN )
            Configuration.logWarning ( msg );
        else if ( level == Console.INFO )
            Configuration.logInfo ( msg );
        else
            Configuration.logDebug ( msg );
    }

    // MM patch: synchronized to allow threaded access
    private synchronized void addXAResourceTransaction (
            XAResourceTransaction restx , XAResource xares )
    {
        xaresToTxMap_.put ( new XAResourceKey ( xares ), restx );
    }

    // MM patch: synchronized to allow threaded access
    private synchronized XAResourceTransaction findXAResourceTransaction (
            XAResource xares )
    {
        XAResourceTransaction ret = null;
        ret = (XAResourceTransaction) xaresToTxMap_.get ( new XAResourceKey (
                xares ) );

        return ret;
    }

    // MM patch: synchronized to allow threaded access
    private synchronized void removeXAResourceTransaction ( XAResource xares )
    {
        xaresToTxMap_.remove ( new XAResourceKey ( xares ) );
    }

    /**
     * @see javax.transaction.Transaction
     */

    public void registerSynchronization ( javax.transaction.Synchronization s )
            throws java.lang.IllegalStateException,
            javax.transaction.SystemException
    {
        try {
            Sync2Sync adaptor = new Sync2Sync ( s );
            ct_.registerSynchronization ( adaptor );
        } catch ( SysException se ) {
        	String msg = "Unexpected error during registerSynchronization";
        	Configuration.logWarning ( msg , se );
            throw new ExtendedSystemException ( msg , se
                    .getErrors () );
        }

    }

    /**
     * @see javax.transaction.Transaction
     */

    public int getStatus ()
    {
        TxState state = (TxState) ct_.getState ();

        if ( state.equals ( TxState.IN_DOUBT ) )
            return Status.STATUS_PREPARED;
        else if ( state.equals ( TxState.PREPARING ) )
            return Status.STATUS_PREPARING;
        else if ( state.equals ( TxState.ACTIVE ) )
            return Status.STATUS_ACTIVE;
        else if ( state.equals ( TxState.MARKED_ABORT ) )
            return Status.STATUS_MARKED_ROLLBACK;
        else
            // other cases are either very short or irrelevant to user?
            return Status.STATUS_UNKNOWN;
    }
    
    private CompositeTerminator getTerminator() 
    {
    	TransactionControl control =  ct_.getTransactionControl ();
        if ( control == null ) {
        	String msg = "No TransactionControl object found?";
        	Configuration.logWarning ( msg );
        	throw new SecurityException ( msg );
        }
            
        CompositeTerminator term = control.getTerminator ();
        if ( term == null ) {
        	String msg = "No TransactionTerminator object found?";
        	Configuration.logWarning ( msg );
        	throw new SecurityException ( msg );
        }
        return term;
    }

    /**
     * @see javax.transaction.Transaction.
     */

    public void commit () throws javax.transaction.RollbackException,
            javax.transaction.HeuristicMixedException,
            javax.transaction.HeuristicRollbackException,
            javax.transaction.SystemException, java.lang.SecurityException
    {

        CompositeTerminator term = getTerminator();
        try {
            term.commit ();
        } catch ( HeurHazardException hh ) {
            throw new HeuristicMixedException ( hh.getMessage () );
        } catch ( HeurRollbackException hr ) {
            throw new HeuristicRollbackException ( hr.getMessage () );
        } catch ( HeurMixedException hm ) {
            throw new HeuristicMixedException ( hm.getMessage () );
        } catch ( SysException se ) {
        	Configuration.logWarning ( se.getMessage() , se );
            throw new ExtendedSystemException ( se.getMessage (), se
                    .getErrors () );
        } catch ( com.atomikos.icatch.RollbackException rb ) {
        	//see case 29708: all statements have been closed
        	String msg = rb.getMessage ();
            throw new javax.transaction.RollbackException ( msg );
        }
    }

    /**
     * @see javax.transaction.Transaction.
     */

    public void rollback () throws IllegalStateException, SystemException
    {

    	CompositeTerminator term = getTerminator();
        try {
            term.rollback ();
        } catch ( SysException se ) {
        	Configuration.logWarning ( se.getMessage() , se );
            throw new ExtendedSystemException ( se.getMessage (), se
                    .getErrors () );
        }

    }

    /**
     * @see javax.transaction.Transaction.
     */

    public void setRollbackOnly () throws IllegalStateException,
            SystemException
    {
        // rollback();
        ct_.getTransactionControl ().setRollbackOnly ();
    }

    /**
     * @see javax.transaction.Transaction.
     */

    public boolean enlistResource ( XAResource xares )
            throws javax.transaction.RollbackException,
            javax.transaction.SystemException, IllegalStateException
    {
        TransactionalResource res = null;
        XATransactionalResource xatxres = null;
        XAResourceTransaction restx = null;
        Stack errors = new Stack ();

        if ( getStatus () == Status.STATUS_MARKED_ROLLBACK ) {
        	String msg =  "Transaction is already marked for rollback - enlisting more resources is useless.";
        	Configuration.logWarning ( msg );
            throw new javax.transaction.RollbackException ( msg );
        }

        Enumeration enumm = Configuration.getResources ();

        while ( enumm.hasMoreElements () ) {
            RecoverableResource rres = (RecoverableResource) enumm
                    .nextElement ();
            if ( rres instanceof XATransactionalResource ) {
                xatxres = (XATransactionalResource) rres;

                if ( xatxres.usesXAResource ( xares ) )
                    res = xatxres;

            }

        }

        printMsg ( "enlistResource ( " + xares + " ) with transaction "
                + toString (), Console.INFO );

        if ( res == null ) {

            if ( autoRegistration_ ) {

                synchronized ( Configuration.class ) {
                	// synchronized to avoid case 61740
                	               	
					// ADDED with new recovery: unknown resources can be tolerated
					// by adding a new TemporaryXATransactionalResource
                	res = new TemporaryXATransactionalResource(xares);
                	
                	// cf case 61740: check for concurrent additions before this synch block was entered
                	if ( Configuration.getResource ( res.getName() ) == null ) {
                		printMsg("constructing new temporary resource "
							+ "for unknown XAResource: " + xares, Console.DEBUG);
                		Configuration.addResource ( res );
                	}
				}

            } else {
            	String msg = "There is no registered resource that can recover the given XAResource instance. " + "\n" +
                "Either enable automatic resource registration, or register a corresponding resource.";
            	Configuration.logWarning ( msg );
                throw new javax.transaction.SystemException ( msg );
            }
        }

        
        // if this xares was suspended then it will still be in the map
        XAResourceTransaction active = findXAResourceTransaction ( xares );

        if ( active != null ) {

            // following violates XA state tables
            // and the invariant of the xaresToTxMap table
            if ( !active.isXaSuspended () ) {
            	String msg = "The given XAResource instance is being enlisted a second time without delist in between?";
            	Configuration.logWarning ( msg );
                throw new IllegalStateException ( msg );
            }

            // note: for suspended XAResources, the lookup MUST SUCCEED
            // since the TMRESUME must be called on the SAME XAResource
            // INSTANCE, and lookup also works on the instance level
            try {
                // ADDED: resume should also refresh the xaresource
                active.setXAResource ( xares );
                active.xaResume ();
            } catch ( XAException xaerr ) {
                if ( (XAException.XA_RBBASE <= xaerr.errorCode)
                        && (xaerr.errorCode <= XAException.XA_RBEND) )
                    throw new javax.transaction.RollbackException (
                            "Transaction was already rolled back inside the back-end resource. Further enlists are useless." );

                errors.push ( xaerr );
                throw new ExtendedSystemException ( "Unexpected error during enlist", errors );
            }

        } else {

            try {
                restx = (XAResourceTransaction) res
                        .getResourceTransaction ( ct_ );

                // next, we MUST set the xa resource again,
                // because ONLY the instance we got as argument
                // is available for use now !
                // older instances (set in restx from previous sibling)
                // have connections that may be in reuse already
                // ->old xares not valid except for 2pc operations

                restx.setXAResource ( xares );
                restx.resume ();
            } catch ( ResourceException re ) {
                // re.printStackTrace();
                Stack nested = re.getDetails ();
                if ( !nested.empty ()
                        && (nested.peek () instanceof XAException) ) {
                    XAException xaerr = (XAException) nested.peek ();
                    if ( (XAException.XA_RBBASE <= xaerr.errorCode)
                            && (xaerr.errorCode <= XAException.XA_RBEND) )
                        throw new javax.transaction.RollbackException (
                                "The transaction was rolled back in the back-end resource. Further enlists are useless." );
                }

                errors.push ( re );
                throw new ExtendedSystemException ( "Unexpected error during enlist", errors );
            } catch ( RuntimeException e ) {
                // e.printStackTrace();
                throw e;
            }

            addXAResourceTransaction ( restx, xares );
        }

        return true;
    }

    /**
     * @see javax.transaction.Transaction.
     */

    public boolean delistResource ( XAResource xares , int flag )
            throws java.lang.IllegalStateException,
            javax.transaction.SystemException
    {
        Stack errors = new Stack ();

        TransactionalResource res = null;
        Enumeration enumm = Configuration.getResources ();
        while ( enumm.hasMoreElements () ) {
            RecoverableResource rres = (RecoverableResource) enumm
                    .nextElement ();
            if ( rres instanceof XATransactionalResource ) {
                XATransactionalResource xatxres = (XATransactionalResource) rres;
                if ( xatxres.usesXAResource ( xares ) )
                    res = xatxres;
            }

        }

        printMsg ( "delistResource ( " + xares + " ) with transaction "
                + toString (), Console.INFO );

        if ( res == null ) {
        	String msg =  "There is no registered resource that can recover the given XAResource instance. " + "\n" +
    		"Either enable automatic resource registration, or register a corresponding resource.";
        	Configuration.logWarning ( msg );
            throw new javax.transaction.SystemException ( msg );
        }

    
        XAResourceTransaction active = findXAResourceTransaction ( xares );

        // NOTE: the lookup MUST have succeeded since the delist must be
        // done by the same XAResource INSTANCE as the enlist before,
        // and lookup also uses instance comparison.
        if ( active == null ) {
        	String msg = "Illegal attempt to delist an XAResource instance that was not previously enlisted.";
        	Configuration.logWarning ( msg );
            throw new IllegalStateException ( msg );
        }

        if ( flag == XAResource.TMSUCCESS || flag == XAResource.TMFAIL ) {

            try {
                active.suspend ();
            } catch ( ResourceException re ) {
                errors.push ( re );
                throw new ExtendedSystemException ( "Error in delisting the given XAResource", errors );
            }
            removeXAResourceTransaction ( xares );

            // NOTE: if failure, then make sure no rollback can happen
            if ( flag == XAResource.TMFAIL )
                setRollbackOnly ();
        } else if ( flag == XAResource.TMSUSPEND ) {
            // call suspend on active xaresource.

            try {
                active.xaSuspend ();
            } catch ( XAException xaerr ) {
                errors.push ( xaerr );
                throw new ExtendedSystemException ( "Error in delisting the given XAResource", errors );
            }

        } else {
        	String msg = "Unknown delist flag: " + flag;
        	Configuration.logWarning ( msg );
            throw new javax.transaction.SystemException ( msg );
        }
        return true;
    }



    /**
     * Compares to another object.
     * 
     * @param o
     *            The other object to compare to.
     * 
     * @return boolean True iff the underlying tx is the same.
     */

    public boolean equals ( Object o )
    {
        if ( o == null || !(o instanceof TransactionImp) )
            return false;
        TransactionImp other = (TransactionImp) o;
        return ct_.isSameTransaction ( other.ct_ );
    }

    /**
     * Computes a hash value for the object.
     *
     * @return int The hash value.
     */

    public int hashCode ()
    {
        return ct_.hashCode ();
    }

    public String toString ()
    {
        return ct_.getTid ().toString ();
    }

	void suspendEnlistedXaResources() throws ExtendedSystemException 
	{
		// cf case 61305
		Iterator xaResourceTransactions = xaresToTxMap_.values().iterator();
		while ( xaResourceTransactions.hasNext() ) {
			XAResourceTransaction resTx = (XAResourceTransaction) xaResourceTransactions.next();			
			try {
				resTx.xaSuspend();
			} catch (XAException e) {
				Stack errors = new Stack();
				errors.push ( e );
	            throw new ExtendedSystemException ( "Error in delisting the given XAResource", errors );
			}
		}
	}
}
