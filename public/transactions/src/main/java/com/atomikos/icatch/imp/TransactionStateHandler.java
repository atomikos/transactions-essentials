/**
 * Copyright (C) 2000-2012 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.imp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * The state pattern applied to the CompositeTransaction classes.
 */

abstract class TransactionStateHandler implements SubTxAwareParticipant
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionStateHandler.class);

    private int subtxs_;
    private Stack<Synchronization> synchronizations_;
    private List<SubTxAwareParticipant> subtxawares_;
    private CompositeTransactionImp ct_;
    
    protected TransactionStateHandler ( CompositeTransactionImp ct )
    {
        ct_ = ct;
        subtxs_ = 0;
        subtxawares_ = new ArrayList<SubTxAwareParticipant>();
        synchronizations_ = new Stack<Synchronization>();
    }

    protected TransactionStateHandler ( CompositeTransactionImp ct ,
            TransactionStateHandler handler )
    {
        subtxs_ = handler.getSubTransactionCount();
        synchronizations_ = handler.getSynchronizations();
        subtxawares_ = handler.getSubtxawares();
        ct_ = ct;

    }
    
    private synchronized void localDecSubTxCount()
    {
    	subtxs_--;
    }
    
    private synchronized void localIncSubTxCount()
    {
    	subtxs_++;
    }
    
    private synchronized int localGetSubTxCount()
    {
    	return subtxs_;
    }
    
    private synchronized void localPushSynchronization ( Synchronization sync ) 
    {
    	synchronizations_.push ( sync );
    }
    
    /**
     * Should be called instead of iterator: commit can add more synchronizations
     * so an iterator would give ConcurrentModificationException!
     */
    private Synchronization localPopSynchronization() {
    	Synchronization ret = null;
    	if (!synchronizations_.isEmpty()) ret = synchronizations_.pop();
    	return ret;
    }
    
    private synchronized void localAddSubTxAwareParticipant ( SubTxAwareParticipant p )
    {
    	subtxawares_.add ( p );
    }

    protected CompositeTransaction createSubTransaction ()
            throws SysException, IllegalStateException
    {
        CompositeTransaction ct = null;
        ct = ct_.getTransactionService ().createSubTransaction ( ct_ );
        // we want to be notified of subtx commit for handling extents
        ct.addSubTxAwareParticipant ( this );
        localIncSubTxCount();
        return ct;
    }

    // this method should NOT be synchronized, to avoid deadlocks in JBoss
    // termination handling at remote servers!
    protected RecoveryCoordinator addParticipant ( Participant participant )
            throws SysException, java.lang.IllegalStateException
    {

        CoordinatorImp coord = ct_.getCoordinatorImp ();
        try {
            coord.addParticipant ( participant );

        } catch ( RollbackException e ) {
            throw new IllegalStateException ( "Transaction already rolled back" );
        }
        return coord;
    }

    protected void registerSynchronization ( Synchronization sync )
            throws IllegalStateException, UnsupportedOperationException, SysException
    {
        if ( sync != null ) {
            try {
                ct_.getCoordinatorImp().registerSynchronization(sync);
            } catch ( RollbackException e ) {
                throw new IllegalStateException (
                        "Transaction already rolled back" );
            }
            localPushSynchronization ( sync );
        }
    }

    protected void addSubTxAwareParticipant (
            SubTxAwareParticipant subtxaware ) throws SysException,
            java.lang.IllegalStateException
    {

        localAddSubTxAwareParticipant ( subtxaware );
    }
    
    private void rollback() throws IllegalStateException, SysException
    {
         for ( int i = 0; i < subtxawares_.size (); i++ ) {
         	SubTxAwareParticipant subtxaware = (SubTxAwareParticipant) subtxawares_.get ( i );
         	subtxaware.rolledback ( ct_ );
         }

         Extent extent = ct_.getExtent ();
         if ( extent != null ) {
        	 Enumeration<Participant> enumm = extent.getParticipants ().elements ();
         	while ( enumm.hasMoreElements () ) {
         		Participant part = (Participant) enumm.nextElement();
         		addParticipant ( part );
         	}
         }

         ct_.localSetTransactionStateHandler ( new TxTerminatedStateHandler ( ct_, this, false ) );

         try {
             ct_.getCoordinatorImp().rollback();
         } catch ( HeurCommitException e ) {
             throw new SysException ( "Unexpected error in rollback", e );
         } catch ( HeurMixedException e ) {
             throw new SysException ( "Unexpected error in rollback", e );
         } catch ( HeurHazardException e ) {
             throw new SysException ( "Unexpected error in rollback", e );
         }
    }

    protected void rollbackWithStateCheck () throws java.lang.IllegalStateException,
            SysException
    {
        //prevent concurrent commits - relevant if this is a timeout thread
        ct_.localTestAndSetTransactionStateHandler ( this , new TxTerminatingStateHandler ( false , ct_ , this ) );
        rollback();

    }

    // IMPORTANT: don't synchronize the commit method, because it causes deadlocks
    // (since this method also indirectly locks the coordinator and the FSM)
    // This deadlock happens in particular when application commit interleaves
    // with timeout-driven rollback (during preEnter, the rollback of this same
    // handler is invoked)
    protected void commit() throws SysException,
            java.lang.IllegalStateException, RollbackException
    {
        Stack participants = null;
        Stack synchronizations = null;
        
        //prevent concurrent rollback due to timeout
        ct_.localTestAndSetTransactionStateHandler(this , new TxTerminatingStateHandler(true , ct_ , this));

        // NOTE: this must be done BEFORE calling notifications
        // to make sure that active recovery works for early prepares 
        if ( ct_.isLocalRoot() ) {

        	ct_.getCoordinatorImp().addTag(ct_.tag_);

        	Enumeration enumm = ct_.getExtent().getParticipants().elements();
        	while ( enumm.hasMoreElements () ) {
        		Participant part = (Participant) enumm.nextElement();
        		addParticipant(part);

        	}
        }

        if ( subtxs_ > 0 ) throw new IllegalStateException ( "Active subtransactions exist" );

        // BEFORE calling SubTxAwares, make sure that synchronizations
        // are called. This is because the calling thread must still be
        // associated with the tx at beforeCompletion, and the
        // TM is listening as a SubTxAware!
        // NOTE: doing this at the very beginning of commit
        // also makes sure that the tx can still get new Participants
        // from beforeCompletion work being done! This is required.
        Synchronization sync = null;
        Throwable cause = notifyBeforeCompletion();

        if ( ct_.getState().equals ( TxState.MARKED_ABORT ) ) {
        	// happens if synchronization has called setRollbackOnly
        	rollback();
        	throw new RollbackException ( "The transaction was set to rollback only" , cause );
        }

        // for loop to make sure that new registrations are possible during callback
        for ( int i = 0; i < subtxawares_.size (); i++ ) {
        	SubTxAwareParticipant subtxaware = (SubTxAwareParticipant) subtxawares_.get ( i );
        	subtxaware.committed ( ct_ );
        }

        ct_.localSetTransactionStateHandler ( new TxTerminatedStateHandler ( ct_, this, true ) );



    }

	private Throwable notifyBeforeCompletion() {
		Throwable cause = null;
		Synchronization sync = localPopSynchronization();
        while ( sync != null ) {
        	try {
        		sync.beforeCompletion ();
        	} catch ( RuntimeException error ) {
        		// see case 24246: rollback only
        		setRollbackOnly();
        		// see case 115604
        		// transport the first exception here as return value
        		if (cause == null) {
        			cause = error;
        		} else {
        			// log the others which may still happen as error
        			LOGGER.logError("Unexpected error in beforeCompletion: ", error);
        		}       		
        	}
        	sync = localPopSynchronization();
        }
		return cause;
	}

    protected void setRollbackOnly ()
    {
    	ct_.getCoordinatorImp().setRollbackOnly();
        synchronized ( this ) {
        	ct_.localSetTransactionStateHandler ( new TxRollbackOnlyStateHandler ( ct_,this ) );
        }

    }

    public void committed ( CompositeTransaction subtx )
    {
        CompositeTransactionImp ct = (CompositeTransactionImp) subtx;
        Extent toAdd = subtx.getTransactionControl().getExtent();
        Extent target = ct_.getExtent();
        target.add ( toAdd );

        SubTransactionCoordinatorParticipant part = new SubTransactionCoordinatorParticipant ( ct.getCoordinatorImp() );
        addParticipant ( part );
      
        localDecSubTxCount();
    }

    public void rolledback ( CompositeTransaction subtx )
    {
        localDecSubTxCount();
    }

    protected abstract TxState getState();


    protected List<SubTxAwareParticipant> getSubtxawares()
    {
        return subtxawares_;
    }

    protected CompositeTransactionImp getCT()
    {
        return ct_;
    }


    protected int getSubTransactionCount()
    {
        return localGetSubTxCount();
    }

    protected Stack<Synchronization> getSynchronizations()
    {
        return synchronizations_;
    }

    protected void addSynchronizations ( Stack<Synchronization> synchronizations )
    {
        while ( !synchronizations.isEmpty() ) {
            Synchronization next = synchronizations.pop();
            localPushSynchronization ( next );
        }
    }

}
