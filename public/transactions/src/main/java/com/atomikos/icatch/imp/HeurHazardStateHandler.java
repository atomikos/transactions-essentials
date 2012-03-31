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
import com.atomikos.icatch.imp.thread.InterruptedExceptionHelper;

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
        // or it can be: cf case 72990
    	Boolean commitDecided = getCommitted();
        boolean committed = false;
        
        addAllForReplay ( hazards_ ); 

        // get Stack to avoid overwriting effects of
        // intermediate recovery calls
        Stack replayStack = getReplayStack ();
        boolean replay = false;
        if ( !replayStack.empty ()  && commitDecided != null ) {
        	committed = commitDecided.booleanValue ();
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
