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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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
import com.atomikos.icatch.imp.thread.InterruptedExceptionHelper;

/**
 * 
 * 
 * A state handler for the heuristic mixed coordinator state.
 */

class HeurMixedStateHandler extends CoordinatorStateHandler
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(HeurMixedStateHandler.class);

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
            	// cf bug 67457
    			InterruptedExceptionHelper.handleInterruptedException ( inter );
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
