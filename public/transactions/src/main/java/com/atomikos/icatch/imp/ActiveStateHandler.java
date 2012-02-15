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
import com.atomikos.icatch.system.Configuration;

/**
 *
 *
 * A state handler for the active coordinator state.
 */

class ActiveStateHandler extends CoordinatorStateHandler
{
	/**
	 *
	 */
	private static final long serialVersionUID = -80097456886481668L;

	private static final Logger LOGGER = LoggerFactory.createLogger(ActiveStateHandler.class);

    private long rollbackTicks_;
    // how many timeout events have happened?
    // if max allowed -> rollback on timeout

    private int globalSiblingCount_;


    ActiveStateHandler ( CoordinatorImp coordinator )
    {
        super ( coordinator );
        rollbackTicks_ = 0;
    }

//    ActiveStateHandler ( CoordinatorStateHandler previous )
//    {
//        super ( previous );
//        rollbackTicks_ = 0;
//    }

    protected long getRollbackTicks ()
    {
        return rollbackTicks_;
    }

    protected Object getState ()
    {
        return TxState.ACTIVE;
    }

    protected void onTimeout ()
    {

        try {
            if ( rollbackTicks_ < getCoordinator ().getMaxRollbackTicks () )
                rollbackTicks_++;
            else {
                // first check if we are still the current state!
                // otherwise, a COMMITTING tx could be rolled back
                // in case of 1PC!!!
                if ( getCoordinator ().getState ().equals ( getState () ) ) {

                    printMsg ( "Rollback of timedout ACTIVE coordinator !" );
                    boolean indoubt = getCoordinator().isRecoverableWhileActive().booleanValue();
                    //treat activities (recoverable) as indoubts to make sure that anomalies
                    //with early prepare etc. are treated as heuristics
                    super.rollback ( indoubt , false );
                }
            }
        } catch ( Exception e ) {
            printMsg ( "Error in timeout of ACTIVE state: " + e.getMessage ()
                    + " for coordinator " + getCoordinator ().getCoordinatorId () );
        }
    }

    protected void setGlobalSiblingCount ( int count )
    {
        globalSiblingCount_ = count;
    }

    protected int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException
    {

        Stack errors = new Stack (); // error propagation
        int count = 0; // number of participants
        PrepareResult result = null; // synchronization
        boolean allReadOnly = true; // if still true at end-> readonly vote
        int ret = 0; // return value
        Vector participants = getCoordinator ().getParticipants ();
        CoordinatorStateHandler nextStateHandler = null;

        if ( getCoordinator ().checkSiblings ()
                && globalSiblingCount_ != getCoordinator ()
                        .getLocalSiblingCount () ) {
            // NO COMMIT ALLOWED: ORPHANS!!!!
            try {
                if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Orphans detected: "
                        + getCoordinator ().getLocalSiblingCount () + " vs "
                        + globalSiblingCount_ + " - forcing rollback." );
                super.rollback ( getCoordinator().isRecoverableWhileActive().booleanValue() , false );

            } catch ( HeurCommitException hc ) {
                throw new HeurMixedException ( hc.getHeuristicMessages() );
            }

            throw new RollbackException ( "Orphans detected." );
        }

        try {
        	try {
        		getCoordinator ().setState ( TxState.PREPARING );
        	} catch ( RuntimeException error ) {
        		//See case 23334
        		String msg = "Error in preparing: " + error.getMessage() + " - rolling back instead";
        		LOGGER.logWarning ( msg , error );
        		try {
					super.rollback ( getCoordinator().isRecoverableWhileActive().booleanValue() , false );
					throw new RollbackException ( msg );
        		} catch ( HeurCommitException e ) {
					LOGGER.logWarning ( "Illegal heuristic commit during rollback before prepare:" + e );
					throw new HeurMixedException ( e.getHeuristicMessages() );
				}
        	}
            count = participants.size ();
            result = new PrepareResult ( count );
            Enumeration enumm = participants.elements ();
            while ( enumm.hasMoreElements () ) {
                Participant p = (Participant) enumm.nextElement ();
                PrepareMessage pm = new PrepareMessage ( p, result );
                if ( getCascadeList () != null && p.getURI () != null ) { // null
                                                                            // for
                                                                            // OTS
                                                                            // case?
                    Integer sibnum = (Integer) getCascadeList ().get (
                            p.getURI () );
                    if ( sibnum != null ) // null for local participant!
                        p.setGlobalSiblingCount ( sibnum.intValue () );
                    p.setCascadeList ( getCascadeList () );
                }

                getPropagator ().submitPropagationMessage ( pm );
                // this will trigger sending the message
            } // while

            // now wait for all replies and act accordingly
            result.waitForReplies ();

            boolean voteOK = result.allYes ();
            setReadOnlyTable ( result.getReadOnlyTable () );
            // indoubttable_ = result.getIndoubtTable();
            allReadOnly = result.allReadOnly ();

            if ( !voteOK ) {
                // addErrorMessages(result.getMessages());

                int res = result.getResult ();
                // FOLLOWING CODE REMOVED:
                // DURING PREPARE, NO CONTRACTS ARE BROKEN
                // IF PREPARE FAILS. the only danger is that
                // some servers vote yes and remain indoubt,
                // but that is a heuristic case at these
                // remote sites, but not locally.
                // indeed, locally there is no uncertainty
                // about the outcome, because we will
                // roll back and vote no!
                // THEREFORE, WE CAN FORGET ABOUT ANY
                // INDOUBT SERVERS; IF THEY INQUIRE, WE WILL
                // SAY ABORTED.

                // if (res == Result.HEUR_MIXED) {
                // rollback ( true );
                // throw new HeurMixedException(getHeuristicMessages());
                // }
                // else if (res == Result.HEUR_COMMIT) {
                // commit ( true, false );
                // throw new HeurCommitException(getHeuristicMessages());
                // }
                // else if (res == Result.HEUR_HAZARD) {
                // rollback ( true );
                // throw new HeurMixedException(getHeuristicMessages());
                // }
                // else {

                // resolve indoubt situations and return NO vote.
                try {
                    rollback ( true, false );
                } catch ( HeurCommitException hc ) {
                    // can not happen:
                    // heuristic commit means that ALL subordinate work
                    // as been committed heuristically.
                    // this is impossible since it assumes that ALL
                    // participants voted YES in the first place,
                    // which contradicts the fact that we are dealing with
                    // !voteOK
                    errors.push ( hc );
                    throw new SysException ( "Unexpected heuristic: "
                            + hc.getMessage (), errors );
                }
                throw new RollbackException ( "Prepare: " + "NO vote" );

                // } //else
            } // if (!voteOK)
        } catch ( RuntimeException runerr ) {
            errors.push ( runerr );
            throw new SysException ( "Error in prepare: "
                    + runerr.getMessage (), errors );
        } catch ( InterruptedException err ) {
        	// cf bug 67457
			InterruptedExceptionHelper.handleInterruptedException ( err );
            errors.push ( err );
            throw new SysException ( "Error in prepare: " + err.getMessage (),
                    errors );
        }
        // here we are if all yes.
        if ( allReadOnly ) {
            nextStateHandler = new TerminatedStateHandler ( this );
            getCoordinator ().setStateHandler ( nextStateHandler );
            ret = Participant.READ_ONLY;
            // System.err.println ( "Prepare: returning readonly" );
        } else {
            nextStateHandler = new IndoubtStateHandler ( this );
            getCoordinator ().setStateHandler ( nextStateHandler );
            ret = Participant.READ_ONLY + 1;
            // System.err.println ( "Prepare: returning not readonly" );
        }

        return ret;
    }

    protected HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {
        HeuristicMessage[] result = new HeuristicMessage[0];
        if ( !onePhase )
            throw new IllegalStateException (
                    "Illegal state for commit: ACTIVE!" );

        if ( getCoordinator ().getParticipants ().size () > 1 ) {
            int prepareResult = Participant.READ_ONLY + 1;

            // happens if client has one remote participant
            // and hence decides for 1PC, but the remote
            // instance (this one) has more participants registered
            // in that case: first prepare and then do normal
            // 2PC commit

            setGlobalSiblingCount ( 1 );
            prepareResult = prepare ();
            // make sure to only do 2PC commit if NOT read only
            if ( prepareResult == Participant.READ_ONLY )
                result = getHeuristicMessages ();
            else
                result = commit ( false, false );
        } else {
            result = commit ( false, true );
        }
        return result;

    }

    protected HeuristicMessage[] rollback ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {

        return rollback ( getCoordinator().isRecoverableWhileActive().booleanValue() , false );
    }

    protected Boolean replayCompletion ( Participant participant )
            throws IllegalStateException
    {

        throw new IllegalStateException ( "No prepares sent yet." );
    }

}
