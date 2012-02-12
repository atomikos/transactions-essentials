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

package com.atomikos.icatch.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 *
 *
 * The state pattern applied to the CompositeTransaction classes.
 */

abstract class TransactionStateHandler implements SubTxAwareParticipant
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionStateHandler.class);

    private int subtxs_;

    // REMOVED: one coordinator per subtransaction
    // so coordinator can hold all participants
    // private Stack participants_;

    private Stack synchronizations_;

    private List subtxawares_;

    private CompositeTransactionImp ct_;

    protected TransactionStateHandler ( CompositeTransactionImp ct )
    {
        ct_ = ct;
        subtxs_ = 0;
        subtxawares_ = new ArrayList ();
        // participants_ = new Stack();
        synchronizations_ = new Stack ();
    }

    protected TransactionStateHandler ( CompositeTransactionImp ct ,
            TransactionStateHandler handler )
    {
        subtxs_ = handler.getSubTransactionCount ();
        // participants_ = handler.getParticipants();
        synchronizations_ = handler.getSynchronizations ();
        subtxawares_ = handler.getSubtxawares ();
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

    private synchronized void localAddSubTxAwareParticipant ( SubTxAwareParticipant p )
    {
    	subtxawares_.add ( p );
    }

    protected CompositeTransaction createSubTransaction ()
            throws SysException, IllegalStateException
    {
        // argument is not null on test!
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
                ct_.getCoordinatorImp ().registerSynchronization ( sync );
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
    	 Stack errors = new Stack ();

         for ( int i = 0; i < subtxawares_.size (); i++ ) {
         	SubTxAwareParticipant subtxaware = (SubTxAwareParticipant) subtxawares_
         	.get ( i );
         	subtxaware.rolledback ( ct_ );
         	// NOTE: this can NOT be done by coordinator imp.,
         	// since that one will not know which tx is locally done!
         }

         // Added (bug discovered by MM):
         // rollback extent participants too!
         Enumeration enumm = null;
         Extent extent = ct_.getExtent ();
         if ( extent != null ) {
         	enumm = extent.getParticipants ().elements ();
         	while ( enumm.hasMoreElements () ) {
         		Participant part = (Participant) enumm.nextElement ();
         		// participants_.push ( part );
         		addParticipant ( part );
         	}
         }



         ct_.localSetTransactionStateHandler ( new TxTerminatedStateHandler (
         		ct_, this, false ) );


         // rollback coordinator outside SYNCH block to avoid deadlocks
         try {
             ct_.getCoordinatorImp ().rollback ();
         } catch ( HeurCommitException e ) {
             errors.push ( e );
             throw new SysException ( "Unexpected error in rollback", errors );
         } catch ( HeurMixedException e ) {
             errors.push ( e );
             throw new SysException ( "Unexpected error in rollback", errors );
         } catch ( HeurHazardException e ) {
             errors.push ( e );
             throw new SysException ( "Unexpected error in rollback", errors );
         }
    }

    protected void rollbackWithStateCheck () throws java.lang.IllegalStateException,
            SysException
    {

        //prevent concurrent commits - relevant if this is a timeout thread
        ct_.localTestAndSetTransactionStateHandler ( this , new TxTerminatingStateHandler ( false , ct_ , this ) );

        rollback();

    }

    // REMEMBER: don't synchronize the commit method, because it causes
    // deadlocks
    // (since this method also indirectly locks the coordinator and the FSM)
    // This deadlock happens in particular when application commit interleaves
    // with
    // timeout-driven rollback (during preEnter, the rollback of this same
    // handler
    // is invoked)
    protected void commit () throws SysException,
            java.lang.IllegalStateException, RollbackException
    {
        Stack participants = null;
        Stack synchronizations = null;

        //prevent concurrent rollback due to timeout
        ct_.localTestAndSetTransactionStateHandler ( this , new TxTerminatingStateHandler ( true , ct_ , this ) );

        // first: check if local root; if so: add all local participants
        // of all COMMITTED local subtxs to the coordinator.
        // NOTE: this MUST be out of the synch block, since the coordinator
        // is accessed in synch mode and hence can cause deadlocks.
        // ALSO NOTE: this must be done BEFORE calling notifications
        // to make sure that active recovery works for early prepares
        if ( ct_.isLocalRoot () ) {

        	// add the tag as a summary heuristic to the coordinator.
        	// note: the same coordinator may have multiple tags
        	// from different local roots!
        	ct_.getCoordinatorImp ().addTag ( ct_.tag_ );

        	Enumeration enumm = ct_.getExtent ().getParticipants ().elements ();
        	while ( enumm.hasMoreElements () ) {
        		Participant part = (Participant) enumm.nextElement ();
        		addParticipant ( part );

        	}
        }



        // @todo move this to AFTER syncs were notified, to avoid pending
        // thread associations?
        if ( subtxs_ > 0 )
        	throw new IllegalStateException (
        			"active subtransactions exist" );

        // BEFORE calling SubTxAwares, make sure that synchronizations
        // are called. This is because the calling thread must still be
        // associated with the tx at beforeCompletion, and the
        // TM is listening as a SubTxAware!
        // NOTE: doing this at the very beginning of commit
        // also makes sure that the tx can still get new Participants
        // from beforeCompletion work being done! This is required.
        Synchronization sync = null;
        Enumeration enumm = synchronizations_.elements ();
        while ( enumm.hasMoreElements () ) {
        	sync = (Synchronization) enumm.nextElement ();
        	try {
        		sync.beforeCompletion ();
        	} catch ( RuntimeException error ) {
        		//see case 24246: rollback only
        		setRollbackOnly();
        		Configuration.logWarning ( "Unexpected error in beforeCompletion: " , error );
        	}
        }

        if ( ct_.getState().equals ( TxState.MARKED_ABORT ) ) {
        	//happens if synchronization has called setRollbackOnly
        	//-> rollback and throw error
        	rollback();
        	throw new RollbackException ( "The transaction was set to rollback only" );
        }

        // for loop to make sure that new registrations are possible
        // during callback
        for ( int i = 0; i < subtxawares_.size (); i++ ) {
        	SubTxAwareParticipant subtxaware = (SubTxAwareParticipant) subtxawares_
        	.get ( i );
        	subtxaware.committed ( ct_ );
        	// NOTE: this can NOT be done by coordinator imp.,
        	// since that one will not know which tx is locally done!
        }

        // change state handler to avoid other, concurrent modifications
        // after we
        // leave the synchronized block
        ct_.localSetTransactionStateHandler ( new TxTerminatedStateHandler (
        		ct_, this, true ) );



    }

    protected void setRollbackOnly ()
    {
        StringHeuristicMessage msg = new StringHeuristicMessage (
                "Transaction.setRollbackOnly was called." );
        RollbackOnlyParticipant p = new RollbackOnlyParticipant ( msg );

        try {
        	addParticipant ( p );
        } catch ( IllegalStateException alreadyTerminated ) {
            //happens in rollback after timeout - see case 27857
        	//ignore but log
        	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Error during setRollbackOnly" , alreadyTerminated );
        }
        synchronized ( this ) {
        	ct_.localSetTransactionStateHandler ( new TxRollbackOnlyStateHandler ( ct_,
                this ) );
        }

    }

    public void committed ( CompositeTransaction subtx )
    {
        CompositeTransactionImp ct = (CompositeTransactionImp) subtx;
        Extent toAdd = subtx.getTransactionControl ().getExtent ();
        Extent target = ct_.getExtent ();
        target.add ( toAdd );

        // next: all LOCAL participants of the child subtx must be added to
        // the 2PC set.
        // Stack participants = ct.getParticipants ();
        // addParticipants ( participants );

        SubTransactionCoordinatorParticipant part = new SubTransactionCoordinatorParticipant (
                ct.getCoordinatorImp () );
        addParticipant ( part );


        // decrement count of active subtxs
        localDecSubTxCount();
    }

    public void rolledback ( CompositeTransaction subtx )
    {
        localDecSubTxCount();
    }

    protected abstract Object getState ();


    /**
     * @return List The subtx awares
     */
    protected List getSubtxawares ()
    {
        return subtxawares_;
    }

    protected CompositeTransactionImp getCT ()
    {
        return ct_;
    }

    /**
     * @return int The subtx count
     */
    protected int getSubTransactionCount ()
    {
        return localGetSubTxCount();
    }

    /**
     * @return Stack The synchronizations
     */
    protected Stack getSynchronizations ()
    {
        return synchronizations_;
    }

    protected void addSynchronizations ( Stack synchronizations )
    {
        while ( !synchronizations.empty () ) {
            Synchronization next = (Synchronization) synchronizations.pop ();
            localPushSynchronization ( next );
        }
    }

}
