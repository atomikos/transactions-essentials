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

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;

/**
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
    	// see case 28843: accept the participant, but call rollback immediately
    	try {
    		participant.rollback();
    	} catch ( Exception ignore ) {
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

    protected Object getState()
    {
        return TxState.MARKED_ABORT;
    }
}
