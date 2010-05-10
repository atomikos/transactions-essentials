//$Id: TerminatedStateHandler.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: TerminatedStateHandler.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:06  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:40  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:09  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.3  2003/06/20 16:31:32  guy
//*** empty log message ***
//
//Revision 1.1.2.2  2003/05/12 07:00:08  guy
//Redesigned Coordinator with STATE PATTERN.
//

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