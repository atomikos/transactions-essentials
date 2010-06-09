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

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;

/**
 * 
 * 
 * A state handler for the terminated coordinator state.
 */

class TerminatedStateHandler extends CoordinatorStateHandler
{
    TerminatedStateHandler ( CoordinatorImp coordinator )
    {
        super ( coordinator );
    }

    TerminatedStateHandler ( CoordinatorStateHandler previous )
    {
        super ( previous );
        // VERY important: stop all active threads
        dispose ();
    }

    protected Object getState ()
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

    protected HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        if ( !onePhase ) {
            // respond consistently on multiple commits
            // but only if NOT 1PC: in that case, terminated
            // means that we have rolled back!
            return getHeuristicMessages ();
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

    protected HeuristicMessage[] rollback () throws HeurCommitException,
            HeurMixedException, SysException, HeurHazardException,
            java.lang.IllegalStateException
    {

        return getHeuristicMessages ();
    }

    protected void forget ()
    {
        // OVERRIDE TO DO NOTHING SINCE PROPAGATOR THREADS NO LONGER RUN
    }
}
