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

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;

/**
 * 
 * 
 * A transaction terminated state handler.
 */

class TxTerminatedStateHandler extends TransactionStateHandler
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(TxTerminatedStateHandler.class);

    private boolean commit_;

    protected TxTerminatedStateHandler ( CompositeTransactionImp ct ,
            TransactionStateHandler handler , boolean commit )
    {
        super ( ct , handler );
        commit_ = commit;
    }

    protected CompositeTransaction createSubTransaction () throws SysException,
            IllegalStateException
    {
        throw new IllegalStateException ( "Transaction no longer active" );
    }

    protected RecoveryCoordinator addParticipant ( Participant participant )
            throws SysException, java.lang.IllegalStateException
    {
        // CHANGED TO ALLOW RESUME OF TIMEDOUT TXS IN JBOSS
        // IF ROLLBACK THEN
        // JUST ACCEPT THE OPERATION, BUT CALL ROLLBACK
        // IMMEDIATELY

        if ( !commit_ ) {
            // accept the participant, but call rollback
            // immediately
            try {
                participant.rollback ();
            } catch ( Exception ignore ) {
            }
        } else {
            // transaction already committed, possibly with 2PC
            // so adding more work is unacceptable
            throw new IllegalStateException ( "Transaction no longer active" );
        }

        return getCT ().getCoordinatorImp ();
    }

    protected void registerSynchronization ( Synchronization sync )
            throws IllegalStateException, UnsupportedOperationException, SysException
    {
        throw new IllegalStateException ( "Transaction no longer active" );
    }

    protected void addSubTxAwareParticipant ( SubTxAwareParticipant subtxaware )
            throws SysException, java.lang.IllegalStateException
    {

        if ( commit_ )
            throw new IllegalStateException ( "Transaction no longer active" );
        else {
            // accept the participant, but call rollback immediately
            // needed to allow JBoss integration for marked aborts
            subtxaware.rolledback ( getCT () );
        }
    }

    protected void rollbackWithStateCheck () throws java.lang.IllegalStateException,
            SysException

    {
        throw new IllegalStateException ( "Transaction no longer active" );
    }

    protected void commit () throws SysException,
            java.lang.IllegalStateException, RollbackException
    {
        throw new IllegalStateException ( "Transaction no longer active" );
    }

    protected Object getState ()
    {
        if ( commit_ )
            return getCT ().getCoordinatorImp ().getState ();
        else
            return TxState.MARKED_ABORT;

        // note: we do NOT return coordinator state for rollback, since
        // a SUBtransaction's rollback does not necessarily mean that the
        // coordinator (global) work will rollback, so the coordinator
        // may still be ACTIVE or even COMMITTED!!!
        // Because we have no rolled back state, we return marked abort.
        // This should be indistinguishible for the client: a later rollback
        // will fail, but that will seem like an intermediate timeout rollback
        // of the
        // transaction service
    }
}
