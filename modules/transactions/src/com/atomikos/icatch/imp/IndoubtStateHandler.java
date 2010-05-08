//$Id: IndoubtStateHandler.java,v 1.2 2006/09/19 08:03:52 guy Exp $
//$Log: IndoubtStateHandler.java,v $
//Revision 1.2  2006/09/19 08:03:52  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
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
//Revision 1.3  2006/03/21 13:22:56  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:31:40  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:09  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.6  2005/09/23 10:30:15  guy
//Modified indoubt timeout: no replay request each time, but rather
//only after half of the timeout has passed. Needed to comply with
//WS-T, where replay requests during the VOTING phase will lead
//to rollback!
//
//Revision 1.5  2005/09/23 09:41:06  guy
//Modified to comply with WS-T: repeated prepare returns same vote.
//Also make recover synchronized to deal with threaded recovery.
//
//Revision 1.4  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/09/01 13:39:02  guy
//Merged changes from TransactionsRMI 1.22.
//Corrected bug in SysException.printStackTrace.
//Added log method to Configuration.
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

import java.util.Enumeration;
import java.util.Hashtable;

import com.atomikos.diagnostics.Console;
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
 * A state handler for the indoubt coordinator state.
 */

class IndoubtStateHandler extends CoordinatorStateHandler
{
    private int inquiries_;
    // how many timeout events have happened?
    // if max allowed -> take heuristic decision

    private boolean recovered_;

    // useful in the case of a non-recovered ROOT, which needs to
    // timeout to a heuristic state since the client terminator
    // will not receive the outcome!

    IndoubtStateHandler ( CoordinatorImp coordinator )
    {
        super ( coordinator );
        inquiries_ = 0;
        recovered_ = false;
    }

    IndoubtStateHandler ( CoordinatorStateHandler previous )
    {
        super ( previous );
        inquiries_ = 0;
        recovered_ = false;
    }

    protected void recover ( CoordinatorImp coordinator )
    {
        super.recover ( coordinator );

        if ( getCoordinator ().getState ().equals ( TxState.COMMITTING ) ) {
            // A coordinator that is still in this state might not have notified
            // all its
            // participants -> make sure replay happens

            Enumeration enumm = getCoordinator ().getParticipants ().elements ();
            Hashtable hazards = new Hashtable ();
            while ( enumm.hasMoreElements () ) {
                Participant p = (Participant) enumm.nextElement ();
                if ( !getReadOnlyTable ().containsKey ( p ) ) {
                    addToHeuristicMap ( p, TxState.HEUR_HAZARD );
                    hazards.put ( p, TxState.HEUR_HAZARD );
                }
            }
            HeurHazardStateHandler hazardStateHandler = new HeurHazardStateHandler (
                    this, hazards );
            // set state to hazard AFTER having added all heuristic info,
            // otherwise
            // this info will NOT be in the log image!
            getCoordinator ().setStateHandler ( hazardStateHandler );

            // propagate recover notification to next state
            hazardStateHandler.recover ( coordinator );

        }

        recovered_ = true;
    }

    protected Object getState ()
    {
        return TxState.IN_DOUBT;
    }

    protected void onTimeout ()
    {

        // first check if we are still the current state!
        // otherwise, a COMMITTING tx could be rolled back if it
        // times out in between (i.e. a commit can come in while
        // this state handler gets a timeout event and rolls back)
        if ( !getCoordinator ().getState ().equals ( getState () ) )
            return;

        try {

            if ( inquiries_ < getCoordinator ().getMaxIndoubtTicks () ) {
            	    
                inquiries_++;
                if ( inquiries_ >= getCoordinator ().getMaxIndoubtTicks () / 2 ) {
                    // only ask for replay if half of timeout ticks has passed,
                    // to avoid hitting the coordinator with replays from the
                    // start!
                    // this is needed for WS-T compliance (WS-T coordinator will
                    // abort
                    // if it receives a replay during preparing)
                    if ( getCoordinator ().getSuperiorRecoveryCoordinator () != null ) {
                        printMsg (
                                "Requesting replayCompletion on behalf of coordinator "
                                        + getCoordinator ().getCoordinatorId (),
                                Console.INFO );

                        getCoordinator ().getSuperiorRecoveryCoordinator ()
                                .replayCompletion ( getCoordinator () );
                    }
                }
            } else {
                if ( getCoordinator ().getSuperiorRecoveryCoordinator () == null ) {

                    // root tx, where terminator did not issue commit
                    // after prepare -> decide abort.
                    // NOTE: if not recovered then this is a heuristic decision
                    // because the decision needs to be detectable
                    // by the client terminator.
                    // otherwise, state would be TERMINATED, and a late
                    // commit from terminator would return OK -> BAD!

                    rollback ( true, !recovered_ );
                }
                // heuristic decision
                else if ( getCoordinator ().prefersHeuristicCommit () )
                    commit ( true, false );
                else
                    rollback ( true, true );
            } // else
        } catch ( Exception e ) {
            printMsg ( "Error in timeout of INDOUBT state: " + e.getMessage () );
        }
    }

    protected void setGlobalSiblingCount ( int count )
    {
    }

    protected int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException
    {
        // we need to reply the same to prepare (cf WS-T)!
        // -> change this to return NOT readonly
        // throw new IllegalStateException ( "Prepare received for INDOUBT" );

        // if we have a repeated prepare in this state, then the
        // first prepare must have been a YET vote, otherwise we
        // would not be Indoubt!!! -> repeat the same vote
        // (required for WS-T)
        return Participant.READ_ONLY + 1;
    }

    protected HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        return commit ( false, false );

    }

    protected HeuristicMessage[] rollback ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {

        return rollback ( true, false );
    }

}