/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
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
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;

/**
 * A rollback only state handler.
 */

class TxRollbackOnlyStateHandler extends TransactionStateHandler
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TxRollbackOnlyStateHandler.class);

    protected TxRollbackOnlyStateHandler ( CompositeTransactionImp ct ,
            TransactionStateHandler handler )
    {
        super ( ct , handler );
    }

    protected RecoveryCoordinator addParticipant ( Participant participant )
    throws SysException, java.lang.IllegalStateException
    {
    	// see case 28843: accept the participant, but call rollback immediately
    	try {
    		participant.rollback();
    	} catch ( Exception ignore ) {
    		LOGGER.logTrace("Ignoring exception on participant rollback",ignore);
    	}

    	return getCT().getCoordinatorImp();
    }

    protected CompositeTransaction createSubTransaction () throws SysException,
            IllegalStateException
    {
        // creating a new subtx is not allowed to avoid that people keep adding work to that one.
        throw new IllegalStateException ( "Transaction is marked for rollback" );
    }


    protected void commit () throws SysException,
            java.lang.IllegalStateException, RollbackException
    {
        rollbackWithStateCheck();
        throw new RollbackException ( "Transaction set to rollback only" );
    }

    protected TxState getState()
    {
        return TxState.MARKED_ABORT;
    }
}
