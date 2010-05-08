package com.atomikos.icatch.imp;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;

/**
 * 
 * 
 * A rollback only state handler.
 */

class TxRollbackOnlyStateHandler extends TransactionStateHandler
{
    protected TxRollbackOnlyStateHandler ( CompositeTransactionImp ct ,
            TransactionStateHandler handler )
    {
        super ( ct , handler );
    }
    
    protected RecoveryCoordinator addParticipant ( Participant participant )
    throws SysException, java.lang.IllegalStateException
    {
    	// see case 28843
    	// accept the participant, but call rollback
    	// immediately
    	try {
    		participant.rollback ();
    	} catch ( Exception ignore ) {
    	}

    	return getCT ().getCoordinatorImp ();
    }

    protected CompositeTransaction createSubTransaction () throws SysException,
            IllegalStateException
    {
        // creating a new subtx is not allowed to avoid that people
        // keep adding work to that one.
        throw new IllegalStateException ( "Transaction is marked for rollback" );
    }

    // COMMENTED OUT TO MAKE SETROLLBACKONLY WORK HERE TOO
    // SHOULD BE NO PROBLEM SINCE ROLLBACK IS STILL GOING TO COME

    // protected RecoveryCoordinator addParticipant ( Participant participant )
    // throws SysException, java.lang.IllegalStateException
    // {
    // throw new IllegalStateException ( "Transaction is marked for rollback");
    // }

    protected void commit () throws SysException,
            java.lang.IllegalStateException, RollbackException
    {
        rollbackWithStateCheck ();
        throw new RollbackException ( "Transaction set to rollback only" );
    }

    protected Object getState ()
    {
        return TxState.MARKED_ABORT;
    }
}