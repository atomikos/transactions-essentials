package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;

/**
 * 
 * 
 * A state handler for the heuristic abort coordinator state.
 */

class HeurAbortedStateHandler extends CoordinatorStateHandler
{
    HeurAbortedStateHandler ( CoordinatorImp coordinator )
    {
        super ( coordinator );
    }

    HeurAbortedStateHandler ( CoordinatorStateHandler previous )
    {
        super ( previous );
    }

    protected Object getState ()
    {
        return TxState.HEUR_ABORTED;
    }

    protected void onTimeout ()
    {
        // nothing to do here
    }

    protected void setGlobalSiblingCount ( int count )
    {
        // nothing to do here
    }

    protected int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException
    {

        throw new HeurHazardException ( getHeuristicMessages () );
    }

    protected HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        throw new HeurMixedException ( getHeuristicMessages () );
    }

    protected HeuristicMessage[] rollback ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {

        // if global rollback coincides with heuristic outcome
        // -> terminated

        TerminatedStateHandler termStateHandler = new TerminatedStateHandler (
                this );
        getCoordinator ().setStateHandler ( termStateHandler );
        return getHeuristicMessages ();
    }

}
