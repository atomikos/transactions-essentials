/**
 * Copyright (C) 2000-2012 Atomikos <info@atomikos.com>
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
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.thread.InterruptedExceptionHelper;

/**
 * A state handler for the active coordinator state.
 */

public class ActiveStateHandler extends CoordinatorStateHandler
{

	private static final long serialVersionUID = -80097456886481668L;

	private static final Logger LOGGER = LoggerFactory.createLogger(ActiveStateHandler.class);

    private long rollbackTicks_;
    // how many timeout events have happened?
    // if max allowed -> rollback on timeout

    private int globalSiblingCount_;

    
    public ActiveStateHandler() {
	
	}

    ActiveStateHandler ( CoordinatorImp coordinator )
    {
        super ( coordinator );
        rollbackTicks_ = 0;
    }


    protected long getRollbackTicks ()
    {
        return rollbackTicks_;
    }

    protected TxState getState ()
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
                	if ( getCoordinator().prefersSingleThreaded2PC() ) {
                		//cf case 71748
                		LOGGER.logWarning ( "Timeout/setRollbackOnly of ACTIVE coordinator !" );
                		getCoordinator().setRollbackOnly();
                	} else {
                		LOGGER.logWarning ( "Rollback of timedout ACTIVE coordinator !" );
                		final boolean indoubt = getCoordinator().isRecoverableWhileActive().booleanValue();
                		//treat activities (recoverable) as indoubts to make sure that anomalies
                		//with early prepare etc. are treated as heuristics
                		rollbackWithAfterCompletionNotification(new RollbackCallback() {
							public HeuristicMessage[] doRollback()
									throws HeurCommitException,
									HeurMixedException, SysException,
									HeurHazardException, IllegalStateException {
								return rollbackFromWithinCallback(indoubt,false);
							}});
                	}
                }
            }
        } catch ( Exception e ) {
            LOGGER.logInfo( "Error in timeout of ACTIVE state: " + e.getMessage ()
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

        int count = 0; // number of participants
        PrepareResult result = null; // synchronization
        boolean allReadOnly = true; // if still true at end-> readonly vote
        int ret = 0; // return value
        Vector participants = getCoordinator ().getParticipants ();
        CoordinatorStateHandler nextStateHandler = null;

        if ( orphansExist() ) {
            try {
                if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Orphans detected: "
                        + getCoordinator ().getLocalSiblingCount () + " vs "
                        + globalSiblingCount_ + " - forcing rollback." );
                rollbackWithAfterCompletionNotification(new RollbackCallback() {
					public HeuristicMessage[] doRollback()
							throws HeurCommitException,
							HeurMixedException, SysException,
							HeurHazardException, IllegalStateException {
						return rollbackFromWithinCallback(getCoordinator().isRecoverableWhileActive().booleanValue(),false);
					}});

            } catch ( HeurCommitException hc ) {
                throw new HeurMixedException ( hc.getHeuristicMessages() );
            }

            throw new RollbackException ( "Orphans detected." );
        }

        try {
        	try {
        		getCoordinator().setState ( TxState.PREPARING );
        	} catch ( RuntimeException error ) {
        		//See case 23334
        		String msg = "Error in preparing: " + error.getMessage() + " - rolling back instead";
        		LOGGER.logWarning ( msg , error );
        		try {
					rollbackWithAfterCompletionNotification(new RollbackCallback() {
						public HeuristicMessage[] doRollback()
								throws HeurCommitException,
								HeurMixedException, SysException,
								HeurHazardException, IllegalStateException {
							return rollbackFromWithinCallback(getCoordinator().isRecoverableWhileActive().booleanValue(),false);
						}});
					throw new RollbackException ( msg , error);
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
                if ( getCascadeList () != null && p.getURI () != null ) { //null for OTS
                    Integer sibnum = (Integer) getCascadeList ().get ( p.getURI () );
                    if ( sibnum != null ) { // null for local participant!
                        p.setGlobalSiblingCount ( sibnum.intValue () );
                    }
                    p.setCascadeList ( getCascadeList () );
                }

                getPropagator ().submitPropagationMessage ( pm );
            } // while

            result.waitForReplies ();

            boolean voteOK = result.allYes ();
            setReadOnlyTable ( result.getReadOnlyTable () );
            allReadOnly = result.allReadOnly ();

            if ( !voteOK ) {

                int res = result.getResult ();
               
                try {
                    rollbackWithAfterCompletionNotification(new RollbackCallback() {
						public HeuristicMessage[] doRollback()
								throws HeurCommitException,
								HeurMixedException, SysException,
								HeurHazardException, IllegalStateException {
							return rollbackFromWithinCallback(true,false);
						}});
                } catch ( HeurCommitException hc ) {
                    // should not happen:
                    // means that ALL subordinate work committed heuristically.
                    // this is impossible since it assumes that ALL
                    // participants voted YES in the first place,
                    // which contradicts the fact that we are dealing with
                    // !voteOK
                    throw new SysException ( "Unexpected heuristic: "
                            + hc.getMessage (), hc );
                }
                throw new RollbackException ( "Prepare: " + "NO vote" );
             
            }
        } catch ( RuntimeException runerr ) {
            throw new SysException ( "Error in prepare: " + runerr.getMessage (), runerr );
        } catch ( InterruptedException err ) {
        	// cf bug 67457
			InterruptedExceptionHelper.handleInterruptedException ( err );
            throw new SysException ( "Error in prepare: " + err.getMessage (), err );
        }
        // here we are if all yes.
        if ( allReadOnly ) {
            nextStateHandler = new TerminatedStateHandler ( this );
            getCoordinator ().setStateHandler ( nextStateHandler );
            ret = Participant.READ_ONLY;
        } else {
            nextStateHandler = new IndoubtStateHandler ( this );
            getCoordinator ().setStateHandler ( nextStateHandler );
            ret = Participant.READ_ONLY + 1;
        }

        return ret;
    }

    private boolean orphansExist() {
		return getCoordinator().checkSiblings() && globalSiblingCount_ != getCoordinator ().getLocalSiblingCount();
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
            if ( prepareResult == Participant.READ_ONLY ) result = getHeuristicMessages ();
            else {
            	result = commitWithAfterCompletionNotification ( new CommitCallback() {
            		public HeuristicMessage[] doCommit()
            				throws HeurRollbackException, HeurMixedException,
            				HeurHazardException, IllegalStateException,
            				RollbackException, SysException {
            			return commitFromWithinCallback ( false, false );
            		}          	
            	});
            }
        } else {
        	result = commitWithAfterCompletionNotification ( new CommitCallback() {
        		public HeuristicMessage[] doCommit()
        				throws HeurRollbackException, HeurMixedException,
        				HeurHazardException, IllegalStateException,
        				RollbackException, SysException {
        			return commitFromWithinCallback ( false, true );
        		}          	
        	});
        }
        return result;

    }

    protected HeuristicMessage[] rollback ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {

        return rollbackWithAfterCompletionNotification(new RollbackCallback() {
			public HeuristicMessage[] doRollback()
					throws HeurCommitException,
					HeurMixedException, SysException,
					HeurHazardException, IllegalStateException {
				return rollbackFromWithinCallback(getCoordinator().isRecoverableWhileActive().booleanValue(),false);
			}});
    }

    protected Boolean replayCompletion ( Participant participant )
            throws IllegalStateException
    {

        throw new IllegalStateException ( "No prepares sent yet." );
    }

}
