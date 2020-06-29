/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
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
import com.atomikos.icatch.config.Configuration;
import com.atomikos.recovery.TxState;

/**
 * A state handler for the heuristic abort coordinator state.
 */

class HeurAbortedStateHandler extends CoordinatorStateHandler
{
    
    private long timeoutTicks = 0;
    private long maxTimeoutTicks = 0;

    HeurAbortedStateHandler ( CoordinatorStateHandler previous )
    {
        super ( previous );
        this.maxTimeoutTicks = Configuration.getConfigProperties().getMaxTimeout() / CoordinatorImp.DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS + 1;
    }

    protected TxState getState ()
    {
        return TxState.HEUR_ABORTED;
    }

    protected void onTimeout ()
    {
        if (timeoutTicks < maxTimeoutTicks) {
            //stay around for a while so incoming commit requests find out about heuristic abort
            timeoutTicks++;
        } else {
            removePendingOltpCoordinatorFromTransactionService();
        }
    }

    protected void setGlobalSiblingCount ( int count )
    {
        // nothing to do here
    }

    protected int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException
    {

        throw new HeurHazardException();
    }

    protected void commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        throw new HeurRollbackException();
    }

    protected void rollback ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {

        // if global rollback coincides with heuristic outcome -> terminated
        TerminatedStateHandler termStateHandler = new TerminatedStateHandler (
                this );
        getCoordinator ().setStateHandler ( termStateHandler );
    }

}
