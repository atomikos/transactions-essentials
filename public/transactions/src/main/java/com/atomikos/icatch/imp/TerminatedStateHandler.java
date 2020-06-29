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
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.recovery.TxState;

/**
 *
 *
 * A state handler for the terminated coordinator state.
 */

class TerminatedStateHandler extends CoordinatorStateHandler
{

	TerminatedStateHandler ( CoordinatorStateHandler previous )
    {
        super ( previous );
        // VERY important: stop all active threads
        dispose ();
    }

    protected TxState getState ()
    {
        return TxState.TERMINATED;
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

        // prepare in terminated state happens for JOIN cases where a second
        // client TM propagates prepare. In that case, reply readonly
        // to avoid double commits later. This only works if prepares
        // themselves are NEVER retried on failure!

        return Participant.READ_ONLY;
    }

    protected void commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        if ( !onePhase ) {
            // respond consistently on multiple commits
            // but only if NOT 1PC: in that case, terminated
            // means that we have rolled back!
            return;
        } else {
            // 1PC -> commit causes rolled back exception,
            // and this ONLY works if at most 1 commit message is
            // sent (otherwise, a second commit will see
            // terminated state and assume tx was rolled back)
            // ! => propagator thread should NOT retry
            // commit for 1PC!
            // The implication is that, if the rolled back
            // exception does not arrive due to masking
            // errors, then the client has no certainty about
            // the outcome.
            // In order to have certainty, a client participant
            // wrapper should be used to force 2PC and
            // logging.

            throw new RollbackException ( "Transaction was rolled back." );
        }

    }

    protected void rollback () throws HeurCommitException,
            HeurMixedException, SysException, HeurHazardException,
            java.lang.IllegalStateException
    {
    }

    protected void forget ()
    {
        // OVERRIDE TO DO NOTHING SINCE PROPAGATOR THREADS NO LONGER RUN
    }
}
