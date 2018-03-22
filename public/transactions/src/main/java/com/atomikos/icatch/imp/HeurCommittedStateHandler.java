/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.recovery.TxState;

/**
 * A state handler for the heuristic committed coordinator state.
 */

class HeurCommittedStateHandler extends CoordinatorStateHandler
{

    HeurCommittedStateHandler ( CoordinatorStateHandler previous )
    {
        super ( previous );
    }

    protected TxState getState ()
    {
        return TxState.HEUR_COMMITTED;
    }

    protected void onTimeout ()
    {
        // nothing to do here
    }

    protected void setGlobalSiblingCount ( int count )
    {
        // nothing to do here
    }

    protected  int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException
    {

        throw new HeurHazardException ();
    }

    protected void commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        // heur outcome same as global outcome ->terminated state
        TerminatedStateHandler termStateHandler = new TerminatedStateHandler (
                this );
        getCoordinator ().setStateHandler ( termStateHandler );

    }

    protected void rollback ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {

        throw new HeurCommitException();
    }

}
