//$Id: HeurMixedStateHandler.java,v 1.2 2006/09/19 08:03:51 guy Exp $
//$Log: HeurMixedStateHandler.java,v $
//Revision 1.2  2006/09/19 08:03:51  guy
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
//Revision 1.2  2006/03/15 10:31:40  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/09/06 09:26:37  guy
//Redesigned recovery: can now be done at any time.
//Resources can now be added after init() and will be
//recovered immediately rather than on the next restart.
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
import java.util.Stack;

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
 * A state handler for the heuristic mixed coordinator state.
 */

class HeurMixedStateHandler extends CoordinatorStateHandler
{
    private Hashtable hazards_;

    HeurMixedStateHandler ( CoordinatorImp coordinator )
    {
        super ( coordinator );
        hazards_ = new Hashtable ();
        
    }

    HeurMixedStateHandler ( CoordinatorStateHandler previous , Hashtable hazards )
    {
        super ( previous );
        hazards_ = (Hashtable) hazards.clone ();
    }

    protected Object getState ()
    {
        return TxState.HEUR_MIXED;
    }

    protected void onTimeout ()
    {

        // this state can only be reached through COMMITTING or ABORTING
        // so getCommitted can not be null
        boolean committed = getCommitted ().booleanValue ();
        
        //replay does remove -> re-add hazards each time
        addAllForReplay ( hazards_.keySet() ); 

        Stack replayStack = getReplayStack ();
        boolean replay = false;
        if ( !replayStack.empty () ) {
        	replay = true;
            int count = replayStack.size ();
            TerminationResult result = new TerminationResult ( count );
            
            while ( !replayStack.empty () ) {
                Participant part = (Participant) replayStack.pop ();
                if ( committed ) {
                    CommitMessage cm = new CommitMessage ( part, result, false );
                    getPropagator ().submitPropagationMessage ( cm );
                } else {
                    RollbackMessage rm = new RollbackMessage ( part, result,
                            true );
                    getPropagator ().submitPropagationMessage ( rm );
                }
            }
            try {
                result.waitForReplies ();

                // remove OK replies from hazards_ list and change state if
                // hazard_ is empty.

                Stack replies = result.getReplies ();

                Enumeration enumm = replies.elements ();
                while ( enumm.hasMoreElements () ) {
                    Reply reply = (Reply) enumm.nextElement ();

                    if ( !reply.hasFailed () ) {
                        hazards_.remove ( reply.getParticipant () );
                    }
                }

                if ( hazards_.isEmpty () ) {
                    TerminatedStateHandler termStateHandler = new TerminatedStateHandler (
                            this );
                    getCoordinator ().setStateHandler ( termStateHandler );
                } else if ( replay ) {
                    // set state to heuristic again, to
                    // notify logging of swapout.
                	// only do this if replay was true, i.e. if there could be changes

                    getCoordinator ().setStateHandler ( this );
                }

            } catch ( InterruptedException inter ) {
                // return silently;
                // worst case is some remaining indoubt participants
            }

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

        // check heuristic state: during prepare, there can be no global commit
        // decision yet.
        // therefore, this prepare call is NOT allowed to return anything else
        // then a
        // heuristic hazard exception.
        // thus, no matter what the heuristic really is, report it as hazard.

        throw new HeurHazardException ( getHeuristicMessages () );
    }

    protected HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        throw new HeurMixedException ( getHeuristicMessages () );
    }

    protected HeuristicMessage[] rollback () throws HeurCommitException,
            HeurMixedException, SysException, HeurHazardException,
            java.lang.IllegalStateException
    {

        throw new HeurMixedException ( getHeuristicMessages () );
    }

}