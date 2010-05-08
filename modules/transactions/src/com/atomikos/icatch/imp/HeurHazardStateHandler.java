//$Id: HeurHazardStateHandler.java,v 1.2 2006/09/19 08:03:51 guy Exp $
//$Log: HeurHazardStateHandler.java,v $
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
import java.util.Vector;

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
 * A state handler for the heuristic hazard coordinator state.
 */

class HeurHazardStateHandler extends CoordinatorStateHandler
{
    private Vector hazards_;

    HeurHazardStateHandler ( CoordinatorImp coordinator )
    {
        super ( coordinator );
        hazards_ = new Vector ();
    }

    HeurHazardStateHandler ( CoordinatorStateHandler previous ,
            Vector hazards )
    {
        super ( previous );
        hazards_ = (Vector) hazards.clone ();

    }

    HeurHazardStateHandler ( CoordinatorStateHandler previous ,
            Hashtable hazards )
    {
        super ( previous );
        hazards_ = new Vector();
        hazards_.addAll ( hazards.keySet() );

    }
    
 
    protected void recover ( CoordinatorImp coordinator )
    {
        super.recover ( coordinator );

        // add all recovered participants to the replay stack
        // to resume where we left off before the crash,
        // and try to notify all hazards
        Enumeration enumm = getCoordinator ().getParticipants ().elements ();
        while ( enumm.hasMoreElements () ) {
            Participant p = (Participant) enumm.nextElement ();
            if ( !getReadOnlyTable ().containsKey ( p ) ) {
                replayCompletion ( p );
            }
        } // while
    }

    protected Object getState ()
    {
        return TxState.HEUR_HAZARD;
    }

    protected void onTimeout ()
    {
        // this state can only be reached through COMMITTING or ABORTING
        // so getCommitted can not be null
        boolean committed = getCommitted ().booleanValue ();
        
        addAllForReplay ( hazards_ ); 

        // get Stack to avoid overwriting effects of
        // intermediate recovery calls
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
                // TODO if overall result failed: check if heuristic state
                // should change?
                // for instance: if mixed replies -> change state to HEURMIXED
                // NOTE: this can happen on recovery with late registration
                // where the resource has ended in mixed mode


            } catch ( InterruptedException inter ) {
                // return silently;
                // worst case is some remaining indoubt participants
            }

        }

        if ( hazards_.isEmpty () ) {
            TerminatedStateHandler termStateHandler = new TerminatedStateHandler (
                    this );
            getCoordinator ().setStateHandler ( termStateHandler );
        } else if ( replay ) {
            // set state to heuristic again, to
            // notify logging of swapout.
        	// note: only do this if something could have changed such as in replay
            getCoordinator ().setStateHandler ( this );
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

        throw new HeurHazardException ( getHeuristicMessages () );
    }

    protected HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        throw new HeurHazardException ( getHeuristicMessages () );
    }

    protected HeuristicMessage[] rollback () throws HeurCommitException,
            HeurMixedException, SysException, HeurHazardException,
            java.lang.IllegalStateException
    {

        throw new HeurHazardException ( getHeuristicMessages () );
    }

}