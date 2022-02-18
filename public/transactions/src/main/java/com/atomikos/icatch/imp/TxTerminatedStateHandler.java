/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;

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
            	LOGGER.logTrace("Ignoring error on participant rollback",ignore);
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
