/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Enumeration;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.recovery.TxState;
import com.atomikos.thread.InterruptedExceptionHelper;

/**
 * A state handler for the heuristic hazard coordinator state.
 */

class HeurHazardStateHandler extends CoordinatorStateHandler
{
	private Vector<Participant> hazards_;

    HeurHazardStateHandler ( CoordinatorStateHandler previous ,
            Set<Participant> hazards )
    {
        super ( previous );
        hazards_ = new Vector<Participant>(hazards);

    }

    protected TxState getState ()
    {
        return TxState.HEUR_HAZARD;
    }

    protected void onTimeout ()
    {
        // this state can only be reached through COMMITTING or ABORTING
        // so getCommitted can not be null
        // or it can be: cf case 72990
    	Boolean commitDecided = getCommitted();
        boolean committed = false;
        
        addAllForReplay ( hazards_ ); 

        // get Stack to avoid overwriting effects of
        // intermediate recovery calls
        Stack<Participant> replayStack = getReplayStack ();
        boolean replay = false;
        if ( !replayStack.empty ()  && commitDecided != null ) {
        	committed = commitDecided.booleanValue ();
        	replay = true;
            int count = replayStack.size ();
            TerminationResult result = new TerminationResult ( count );

            while ( !replayStack.empty () ) {
                Participant part = replayStack.pop ();
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
                Stack<Reply> replies = result.getReplies ();
                Enumeration<Reply> enumm = replies.elements ();
                while ( enumm.hasMoreElements () ) {
                    Reply reply = enumm.nextElement ();

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
            	// cf bug 67457
    			InterruptedExceptionHelper.handleInterruptedException ( inter );
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

        throw new HeurHazardException();
    }

    protected void commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        throw new HeurHazardException();
    }

    protected void rollback () throws HeurCommitException,
            HeurMixedException, SysException, HeurHazardException,
            java.lang.IllegalStateException
    {

        throw new HeurHazardException();
    }

}
