/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

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
 * A state handler for the heuristic mixed coordinator state.
 */

class HeurMixedStateHandler extends CoordinatorStateHandler
{

    private Set<Participant> hazards_;
    private long timeoutTicks = 0;

    HeurMixedStateHandler ( CoordinatorStateHandler previous , Set<Participant> hazards )
    {
        super ( previous );
        hazards_ = new HashSet<Participant>(hazards);
    }

    protected TxState getState ()
    {
        return TxState.HEUR_MIXED;
    }

    protected void onTimeout ()
    {
        timeoutTicks++;
        // this state can only be reached through COMMITTING or ABORTING
        // so getCommitted can not be null
    	Boolean commitDecided = getCommitted();
        
        //replay does remove -> re-add hazards each time
        addAllForReplay ( hazards_ );

        Stack<Participant> replayStack = getReplayStack ();
        if ( !replayStack.empty ()  && commitDecided != null ) {
        	boolean committed = commitDecided.booleanValue ();
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

                // remove OK replies from hazards_ list and change state if
                // hazard_ is empty.

                Stack<Reply> replies = result.getReplies ();

                Enumeration<Reply> enumm = replies.elements ();
                while ( enumm.hasMoreElements () ) {
                    Reply reply = enumm.nextElement ();

                    if ( !reply.hasFailed () ) {
                        hazards_.remove ( reply.getParticipant () );
                    }
                }

                if ( hazards_.isEmpty () ) {
                    TerminatedStateHandler termStateHandler = new TerminatedStateHandler (
                            this );
                    getCoordinator().setStateHandler(termStateHandler);
                } 

            } catch ( InterruptedException inter ) {
            	// cf bug 67457
    			InterruptedExceptionHelper.handleInterruptedException ( inter );
                // return silently;
                // worst case is some remaining indoubt participants
            }

        }
        if (timeoutTicks > getCoordinator().getMaxIndoubtTicks()) { //we give up after a while
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

        // check heuristic state: during prepare, there can be no global commit
        // decision yet.
        // therefore, this prepare call is NOT allowed to return anything else
        // then a
        // heuristic hazard exception.
        // thus, no matter what the heuristic really is, report it as hazard.

        throw new HeurHazardException();
    }

    protected void commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        throw new HeurMixedException();
    }

    protected void rollback () throws HeurCommitException,
            HeurMixedException, SysException, HeurHazardException,
            java.lang.IllegalStateException
    {

        throw new HeurMixedException();
    }
}
