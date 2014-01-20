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

import com.atomikos.icatch.CompositeTransaction;
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
 * A transaction terminated state handler.
 */

class TxTerminatedStateHandler extends TransactionStateHandler
{
	private static Logger LOGGER = LoggerFactory.createLogger(TxTerminatedStateHandler.class);

    private boolean transactionCommitted;

    protected TxTerminatedStateHandler ( CompositeTransactionImp ct ,
            TransactionStateHandler handler , boolean transactionCommitted )
    {
        super ( ct , handler );
        this.transactionCommitted = transactionCommitted;
    }

    protected CompositeTransaction createSubTransaction () throws SysException,
            IllegalStateException
    {
        throw new IllegalStateException ( "Transaction no longer active" );
    }

    protected RecoveryCoordinator addParticipant ( Participant participant )
            throws SysException, java.lang.IllegalStateException
    {

        if ( !transactionCommitted ) {
        	// can happen after resuming a timedout transaction;
            // accept the participant, but call rollback immediately
        	// cf JBoss
            try {
                participant.rollback();
            } catch ( Exception ignore ) {
            	LOGGER.logDebug("Ignoring error on participant rollback",ignore);
            }
        } else {
            // transaction already committed, possibly with 2PC
            // so adding more work is unacceptable
            throw new IllegalStateException ( "Transaction no longer active" );
        }

        return getCT().getCoordinatorImp();
    }

    protected void registerSynchronization ( Synchronization sync )
            throws IllegalStateException, UnsupportedOperationException, SysException
    {
        throw new IllegalStateException ( "Transaction no longer active" );
    }

    protected void addSubTxAwareParticipant ( SubTxAwareParticipant subtxaware )
            throws SysException, java.lang.IllegalStateException
    {

        if ( transactionCommitted )
            throw new IllegalStateException ( "Transaction no longer active" );
        else {
            // accept the participant, but call rollback immediately
            // needed to allow JBoss integration for marked aborts
            subtxaware.rolledback ( getCT() );
        }
    }

    protected void rollbackWithStateCheck () throws java.lang.IllegalStateException, SysException

    {
        if (transactionCommitted) throw new IllegalStateException ( "Transaction no longer active" );
    }

    protected void commit () throws SysException,
            java.lang.IllegalStateException, RollbackException
    {
        if (!transactionCommitted) throw new IllegalStateException ( "Transaction no longer active" );
    }

    protected TxState getState()
    {
        if ( transactionCommitted ) return getCT().getCoordinatorImp().getStateWithTwoPhaseCommitDecision();
        else {
        	// Because we have no rolled back state, we return marked abort.
            // This should be indistinguishable for the client: a later rollback
            // will fail, but that will seem like an intermediate timeout rollback
            // of the transaction service
        	return TxState.MARKED_ABORT;
        }
        
    }
}
