/**
 * Copyright (C) 2000-2011 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.jta;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

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
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * Implementation of the javax.transaction.Transaction interface.
 */

class TransactionImp implements Transaction
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionImp.class);

	static void rethrowAsJtaRollbackException ( String msg , Throwable cause ) throws javax.transaction.RollbackException
	{
		javax.transaction.RollbackException ret = new javax.transaction.RollbackException ( msg );
		ret.initCause ( cause );
		throw ret;
	}

	static void rethrowAsJtaHeuristicMixedException ( String msg , Throwable cause ) throws javax.transaction.HeuristicMixedException
	{
		javax.transaction.HeuristicMixedException ret = new javax.transaction.HeuristicMixedException ( msg );
		ret.initCause ( cause );
		throw ret;
	}

	static void rethrowAsJtaHeuristicRollbackException ( String msg , Throwable cause ) throws javax.transaction.HeuristicRollbackException
	{
		javax.transaction.HeuristicRollbackException ret = new javax.transaction.HeuristicRollbackException ( msg );
		ret.initCause ( cause );
		throw ret;
	}

    protected CompositeTransaction ct_;

    protected Map<XAResourceKey,XAResourceTransaction> xaResourceToResourceTransactionMap_;

    protected boolean autoRegistration_;


    TransactionImp ( CompositeTransaction ct , boolean autoRegistration )
    {
        this.ct_ = ct;
        autoRegistration_ = autoRegistration;
        xaResourceToResourceTransactionMap_ = new HashMap<XAResourceKey,XAResourceTransaction>();
    }

    CompositeTransaction getCT ()
    {
        return ct_;
    }

    private synchronized void addXAResourceTransaction (
            XAResourceTransaction restx , XAResource xares )
    {   	
        xaResourceToResourceTransactionMap_.put ( new XAResourceKey ( xares ), restx );
    }

    private void assertActiveOrSuspended(XAResourceTransaction restx) {
		if (!(restx.isActive()||restx.isXaSuspended())) {
			LOGGER.logWarning("Unexpected resource transaction state for " + restx );
		}
	}

	private synchronized XAResourceTransaction findXAResourceTransaction (
            XAResource xares )
    {
        XAResourceTransaction ret = null;
        ret = xaResourceToResourceTransactionMap_.get ( new XAResourceKey ( xares ) );
        assertActiveOrSuspended(ret);
        return ret;
    }

    private synchronized void removeXAResourceTransaction ( XAResource xares )
    {
        xaResourceToResourceTransactionMap_.remove ( new XAResourceKey ( xares ) );
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
        	LOGGER.logWarning ( msg , se );
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



    /**
     * @see javax.transaction.Transaction.
     */

    public void commit () throws javax.transaction.RollbackException,
            javax.transaction.HeuristicMixedException,
            javax.transaction.HeuristicRollbackException,
            javax.transaction.SystemException, java.lang.SecurityException
    {


        try {
            ct_.commit ();
        } catch ( HeurHazardException hh ) {
            rethrowAsJtaHeuristicMixedException ( hh.getMessage () , hh );
        } catch ( HeurRollbackException hr ) {
        	rethrowAsJtaHeuristicRollbackException ( hr.getMessage () , hr );
        } catch ( HeurMixedException hm ) {
        	rethrowAsJtaHeuristicMixedException ( hm.getMessage () , hm );
        } catch ( SysException se ) {
        	LOGGER.logWarning ( se.getMessage() , se );
            throw new ExtendedSystemException ( se.getMessage (), se
                    .getErrors () );
        } catch ( com.atomikos.icatch.RollbackException rb ) {
        	//see case 29708: all statements have been closed
        	String msg = rb.getMessage ();
        	rethrowAsJtaRollbackException ( msg , rb );        }
    }

    /**
     * @see javax.transaction.Transaction.
     */

    public void rollback () throws IllegalStateException, SystemException
    {
        try {
            ct_.rollback ();
        } catch ( SysException se ) {
        	LOGGER.logWarning ( se.getMessage() , se );
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
        ct_.setRollbackOnly ();
    }

    /**
     * @see javax.transaction.Transaction.
     */

    public boolean enlistResource ( XAResource xares )
            throws javax.transaction.RollbackException,
            javax.transaction.SystemException, IllegalStateException
    {
        TransactionalResource res = null;
        XAResourceTransaction restx = null;
        Stack errors = new Stack ();

        if ( getStatus () == Status.STATUS_MARKED_ROLLBACK ) {
        	String msg =  "Transaction is already marked for rollback - enlisting more resources is useless.";
        	LOGGER.logWarning ( msg );
            throw new javax.transaction.RollbackException ( msg );
        }

        XAResourceTransaction suspendedXAResourceTransaction = findXAResourceTransaction ( xares );

        if ( suspendedXAResourceTransaction != null ) {

            if ( !suspendedXAResourceTransaction.isXaSuspended () ) {
            	String msg = "The given XAResource instance is being enlisted a second time without delist in between?";
            	LOGGER.logWarning ( msg );
                throw new IllegalStateException ( msg );
            }

            // note: for suspended XAResources, the lookup MUST SUCCEED
            // since the TMRESUME must be called on the SAME XAResource
            // INSTANCE, and lookup also works on the instance level
            try {
                suspendedXAResourceTransaction.setXAResource ( xares );
                suspendedXAResourceTransaction.xaResume ();
            } catch ( XAException xaerr ) {
                if ( (XAException.XA_RBBASE <= xaerr.errorCode)
                        && (xaerr.errorCode <= XAException.XA_RBEND) )
                	rethrowAsJtaRollbackException (
                            "Transaction was already rolled back inside the back-end resource. Further enlists are useless." , xaerr );

                errors.push ( xaerr );
                throw new ExtendedSystemException ( "Unexpected error during enlist", errors );
            }

        } else {

            res = findRecoverableResourceForXaResource(xares);

            if(LOGGER.isInfoEnabled()){
            	LOGGER.logInfo("enlistResource ( " + xares + " ) with transaction " + toString ());
            }

            if ( res == null ) {
                	String msg = "There is no registered resource that can recover the given XAResource instance. " + "\n" +
                    "Either enable automatic resource registration, or register a corresponding resource.";
                	LOGGER.logWarning ( msg );
                    throw new javax.transaction.SystemException ( msg );
            }

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
                Stack nested = re.getErrors ();
                if ( !nested.empty ()
                        && (nested.peek () instanceof XAException) ) {
                    XAException xaerr = (XAException) nested.peek ();
                    if ( (XAException.XA_RBBASE <= xaerr.errorCode)
                            && (xaerr.errorCode <= XAException.XA_RBEND) )
                    	rethrowAsJtaRollbackException (
                                "The transaction was rolled back in the back-end resource. Further enlists are useless." , re );
                }

                errors.push ( re );
                throw new ExtendedSystemException ( "Unexpected error during enlist", errors );
            } catch ( RuntimeException e ) {
                throw e;
            }

            addXAResourceTransaction ( restx, xares );
        }

        return true;
    }

	private TransactionalResource findRecoverableResourceForXaResource(
			XAResource xares ) {
		TransactionalResource ret = null;
		XATransactionalResource xatxres;
		
		Enumeration enumm = Configuration.getResources ();
        while ( enumm.hasMoreElements () ) {
            RecoverableResource rres = (RecoverableResource) enumm.nextElement ();
            if ( rres instanceof XATransactionalResource ) {
                xatxres = (XATransactionalResource) rres;
                if ( xatxres.usesXAResource ( xares ) ) ret = xatxres;
            }

        }

        if ( ret == null && autoRegistration_ ) {

            synchronized ( Configuration.class ) {
            	// synchronized to avoid case 61740

            	ret = new TemporaryXATransactionalResource(xares);
            	// cf case 61740: check for concurrent additions before this synch block was entered
            	if ( Configuration.getResource ( ret.getName() ) == null ) {
            		if(LOGGER.isDebugEnabled()){
            			LOGGER.logDebug("constructing new temporary resource "
        						+ "for unknown XAResource: " + xares);
            		}
            		Configuration.addResource ( ret );
            	}
			}

        }

		return ret;
	}

    /**
     * @see javax.transaction.Transaction.
     */

    public boolean delistResource ( XAResource xares , int flag )
            throws java.lang.IllegalStateException,
            javax.transaction.SystemException
    {
        Stack errors = new Stack ();

        if(LOGGER.isInfoEnabled()){
        	LOGGER.logInfo( "delistResource ( " + xares + " ) with transaction "
                    + toString ());
        }

        XAResourceTransaction active = findXAResourceTransaction ( xares );
        // NOTE: the lookup MUST have succeeded since the delist must be
        // done by the same XAResource INSTANCE as the enlist before,
        // and lookup also uses instance comparison.
        if ( active == null ) {
        	String msg = "Illegal attempt to delist an XAResource instance that was not previously enlisted.";
        	LOGGER.logWarning ( msg );
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
            if ( flag == XAResource.TMFAIL ) setRollbackOnly ();

        } else if ( flag == XAResource.TMSUSPEND ) {
            try {
                active.xaSuspend ();
            } catch ( XAException xaerr ) {
                errors.push ( xaerr );
                throw new ExtendedSystemException ( "Error in delisting the given XAResource", errors );
            }

        } else {
        	String msg = "Unknown delist flag: " + flag;
        	LOGGER.logWarning ( msg );
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
        if ( o == null || !(o instanceof TransactionImp) ) return false;
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
		Iterator xaResourceTransactions = xaResourceToResourceTransactionMap_.values().iterator();
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
