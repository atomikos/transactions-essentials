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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.icatch.DataSerializable;
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
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.ClassLoadingHelper;

/**
 * Application of the state pattern to the transaction coordinator: each
 * important state has a handler and this class is the superclass that holds
 * common logic.
 *
 * <b>Note: this class and it subclasses should not use synchronized blocks;
 * the coordinator (owner) class is responsible for synchronizing access to
 * this class.</b>
 */

abstract class CoordinatorStateHandler implements Serializable, Cloneable,DataSerializable
{
	
	private static final long serialVersionUID = 5510459174124363958L;

	private static final Logger LOGGER = LoggerFactory.createLogger(CoordinatorStateHandler.class);

    private transient CoordinatorImp coordinator_;
    // the coordinator instance whose state we represent

    private Hashtable<Participant,Boolean> readOnlyTable_;
    // a hash table that keeps track of which participants are readonly
    // needed on prepare, commit and rollback

    private transient Propagator propagator_;
    // The propagator for propagation of messages

    private transient Stack<Participant> replayStack_;
    // where replay requests are queued

    private Boolean committed_;
    // True iff commit, False iff rollback, otherwise null

    private transient Dictionary<Participant,Integer> cascadeList_;
    // The participants to cascade prepare to

    private Hashtable heuristicMap_;
    // Where heuristic states are mapped to participants in that state

    
    public CoordinatorStateHandler() {
    	this((CoordinatorImp)null);
	}
    /**
     * Creates a new instance.
     *
     * @param coordinator
     *            The coordinator to represent.
     *
     */

    protected CoordinatorStateHandler ( CoordinatorImp coordinator )
    {
        coordinator_ = coordinator;
        replayStack_ = new Stack<Participant>();
        readOnlyTable_ = new Hashtable<Participant,Boolean> ();
        committed_ = null;

        heuristicMap_ = new Hashtable ();
        heuristicMap_.put ( TxState.HEUR_HAZARD, new Stack () );
        heuristicMap_.put ( TxState.HEUR_MIXED, new Stack () );
        heuristicMap_.put ( TxState.HEUR_ABORTED, new Stack () );
        heuristicMap_.put ( TxState.HEUR_COMMITTED, new Stack () );
        heuristicMap_.put ( TxState.TERMINATED, new Stack () );
    }

    /**
     * For use in this class or subclasses only. This constructor creates a new
     * instance based on a previous state handler's attributes. In this case,
     * activate or recover should NOT be called!
     *
     * @param other
     *            The previous instance whose attributes should be used.
     */

    protected CoordinatorStateHandler ( CoordinatorStateHandler other )
    {
        coordinator_ = other.coordinator_;
        propagator_ = other.propagator_;
        replayStack_ = other.replayStack_;
        readOnlyTable_ = other.readOnlyTable_;
        committed_ = other.committed_;
        cascadeList_ = other.cascadeList_;

        heuristicMap_ = other.heuristicMap_;

    }

    /**
     * For testing only.
     */

    void setCommitted ()
    {
        committed_ = new Boolean ( true );
    }

    /**
     * Performs a deep clone of the state handler, needed for logging the state
     * information in this handler.
     *
     * @return Object The deep clone.
     */

    public Object clone ()
    {
        CoordinatorStateHandler clone = null;
        try {
            clone = (CoordinatorStateHandler) super.clone ();
            clone.readOnlyTable_ = (Hashtable) readOnlyTable_.clone ();

            clone.heuristicMap_ = new Hashtable ();

            Stack hazStack = (Stack) heuristicMap_.get ( TxState.HEUR_HAZARD );
            Stack mixStack = (Stack) heuristicMap_.get ( TxState.HEUR_MIXED );
            Stack comStack = (Stack) heuristicMap_.get ( TxState.HEUR_COMMITTED );
            Stack abStack = (Stack) heuristicMap_.get ( TxState.HEUR_ABORTED );
            Stack termStack = (Stack) heuristicMap_.get ( TxState.TERMINATED );

            clone.heuristicMap_.put ( TxState.HEUR_HAZARD, hazStack.clone () );
            clone.heuristicMap_.put ( TxState.HEUR_MIXED, mixStack.clone () );
            clone.heuristicMap_.put ( TxState.HEUR_COMMITTED, comStack.clone () );
            clone.heuristicMap_.put ( TxState.HEUR_ABORTED, abStack.clone () );
            clone.heuristicMap_.put ( TxState.TERMINATED, termStack.clone () );
        } catch ( CloneNotSupportedException e ) {
            throw new RuntimeException ("CoordinatorStateHandler: clone failure :" + e.getMessage () );
        }

        return clone;
    }

    /**
     * Adds a participant with a given heuristic state to the map.
     *
     * @param p
     *            The participant.
     * @param state
     *            The (heuristic) state. Should be one of the four heuristic
     *            states, or the terminated state.
     */

    protected void addToHeuristicMap ( Participant p , Object state )
    {
        Stack stack = (Stack) heuristicMap_.get ( state );
        stack.push ( p );

    }

    /**
     * Adds a map of participants -> heuristic states to the map of heuristic
     * states -> participants. This method is called after heuristics on
     * commit/rollback, and allows to retrieve the exact state of each single
     * participant in case of heuristic terminations.
     *
     * @param participants
     *            The participant to heuristic state map.
     */

    protected void addToHeuristicMap ( Hashtable participants )
    {
        Enumeration parts = participants.keys ();
        while ( parts.hasMoreElements () ) {
            Participant next = (Participant) parts.nextElement ();
            Object state = participants.get ( next );
            addToHeuristicMap ( next, state );
        }
    }

    /**
     * Gets the heuristic messages for all participants that are in the given
     * heuristic state
     *
     * @param heuristicState
     *            The heuristic state, or the terminated state.
     * @return HeuristicMessage[] The heuristic messages of all participants in
     *         the given state, or an empty array if none.
     */

    protected HeuristicMessage[] getHeuristicMessages (
            Object heuristicState )
    {
        Vector msgs = new Vector ();

        Stack parts = (Stack) heuristicMap_.get ( heuristicState );
        if ( parts == null ) {
            throw new RuntimeException ( "Error: getHeuristicMessages "
                    + "for non-mapped heuristic state: " + heuristicState );
        }
        Enumeration enumm = parts.elements ();
        while ( enumm.hasMoreElements () ) {
            Participant p = (Participant) enumm.nextElement ();
            HeuristicMessage[] errs = p.getHeuristicMessages ();
            if ( errs != null ) {
                for ( int i = 0; i < errs.length; i++ ) {
                    msgs.addElement ( errs[i] );
                }
            }
        }
        HeuristicMessage[] template = new HeuristicMessage[0];
        return (HeuristicMessage[]) msgs.toArray ( template );
    }

    /**
     * Get the heuristic info for the message round.
     *
     * @return HeuristicMessages[] The heuristic messages, or an empty array if
     *         none.
     */

    protected HeuristicMessage[] getHeuristicMessages ()
    {
        // this method should NOT be synchronized to make rollback
        // recursion-safe.
        Vector msgs = new Vector ();
        Enumeration enumm = coordinator_.getParticipants ().elements ();
        while ( enumm.hasMoreElements () ) {
            Participant p = (Participant) enumm.nextElement ();
            HeuristicMessage[] errs = p.getHeuristicMessages ();
            if ( errs != null ) {
                for ( int i = 0; i < errs.length; i++ ) {
                    msgs.addElement ( errs[i] );
                }
            }
        }

        HeuristicMessage[] template = new HeuristicMessage[0];
        return (HeuristicMessage[]) msgs.toArray ( template );
    }

    /**
     * Get the coordinator whose state we handle.
     *
     * @return CoordinatorImp The coordinator.
     */

    protected CoordinatorImp getCoordinator ()
    {
        return coordinator_;
    }

    protected long getRollbackTicks ()
    {
        return 0;
    }

    /**
     * Get the replay stack for replay completion requests.
     *
     * @return Stack The stack with replay requests, or an empty stack if none
     *         are present.
     */

    protected Stack getReplayStack ()
    {
        return replayStack_;
    }

    /**
     * Get the readonly table.
     *
     * @return The table.
     */

    protected Hashtable getReadOnlyTable ()
    {
        return readOnlyTable_;
    }

    /**
     * Get the cascade list.
     *
     * @return Dictionary The cascade list.
     */

    protected Dictionary getCascadeList ()
    {
        return cascadeList_;
    }

    /**
     * Get the propagator for sending messages in the subclasses.
     *
     * @return Propagator The propagator.
     */

    protected Propagator getPropagator ()
    {
        return propagator_;
    }

    /**
     * Test if the result was commit.
     *
     * @return Boolean Null if not known yet, True if commit, False if rollback.
     */

    protected Boolean getCommitted ()
    {
        return committed_;
    }

    /**
     * Tests if commit has happened.
     *
     * @return boolean True iff commit happened.
     */

    protected boolean isCommitted ()
    {
        if ( committed_ == null )
            return false;
        else
            return committed_.booleanValue ();
    }



    /**
     * Sets the table of readonly participants.
     *
     * @param table
     *            The table.
     */

    protected void setReadOnlyTable ( Hashtable table )
    {
        readOnlyTable_ = table;
    }

    /**
     * Start the threads. This method should be called when the state handler
     * should start being active, as the first method for recovered instances or
     * when the constructor without a propagator argument is called.
     */

    protected void activate ()
    {
    	boolean threaded = !coordinator_.prefersSingleThreaded2PC();
        if ( propagator_ == null )
            propagator_ = new Propagator ( threaded );
    }

    /**
     * Recover the state handler after restart. For safety, this method should
     * be called AFTER activate has been called, or recovery may not work fine!
     *
     * @param coordinator
     *            The (transient) coordinator to use.
     */

    protected void recover ( CoordinatorImp coordinator )
    {
        coordinator_ = coordinator;
        replayStack_ = new Stack ();
    }

    /**
     * Notification of shutdown; this method triggers the stopping of all active
     * threads for propagation.
     */

    protected void dispose ()
    {
        propagator_ = null;
    }

    /**
     * Handle a replay request for a participant. This method makes the
     * participant eligible for replay on the next timer event, but does nothing
     * else. Subclasses should take care of checking preconditions!
     *
     * @return Boolean Indication of the termination decision, null if not known yet.
     */

    protected Boolean replayCompletion ( Participant participant )
            throws IllegalStateException
    {
        if ( !replayStack_.contains ( participant ) ) {
        	// check needed to be idempotent
            replayStack_.push ( participant );
        }
        return committed_;
    }

    /**
     * Utility method for subclasses.
     *
     * @param participants
     */
    protected void addAllForReplay ( Collection<Participant> participants )
    {
    	Iterator<Participant> it = participants.iterator();
    	while ( it.hasNext() ) {
    		Participant p = it.next();
    		replayCompletion ( p );
    	}
    }

    /**
     * The corresponding 2PC method is delegated hereto.
     */

    protected void setCascadeList ( Dictionary<Participant, Integer> allParticipants )
    {
        cascadeList_ = allParticipants;
    }

    /**
     * Callback method on timeout event of the coordinator. The interpretation
     * of timeout will typically be different for each state handler; some may
     * rollback while others need to inquire about completion. This method
     * should also check any replay requests.
     */

    protected abstract void onTimeout ();

    /**
     * Get the (non-pseudo) coordinator state to which this handler belongs.
     *
     * @return Object The object that represents the corresponding coordinator
     *         state.
     */

    abstract TxState getState ();

    /**
     * The corresponding 2PC method is delegated hereto.
     */

    abstract void setGlobalSiblingCount ( int count );

    /**
     * The corresponding 2PC method is delegated hereto.
     */

    protected abstract int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException;

    /**
     * The corresponding 2PC method is delegated hereto. Subclasses should
     * override this, and may use the auxiliary commit method provided by this
     * class (in addition to their state-specific preconditions).
     *
     */

    protected abstract HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException;

    /**
     * The corresponding 2PC method is delegated hereto. Subclasses should
     * override this, and may use the auxiliary rollback method provided by this
     * class (in addition to their state-specific preconditions).
     */

    protected abstract HeuristicMessage[] rollback ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException;

    /**
     * Auxiliary method for committing. This method can be reused in subclasses
     * in order to process commit.
     *
     * @param heuristic
     *            True iff a heuristic commit should be done.
     * @param onePhase
     *            True iff one-phase commit.
     */

    protected HeuristicMessage[] commitFromWithinCallback ( boolean heuristic ,
            boolean onePhase ) throws HeurRollbackException,
            HeurMixedException, HeurHazardException,
            java.lang.IllegalStateException, RollbackException, SysException
    {
        Stack<Exception> errors = new Stack<Exception> ();
        CoordinatorStateHandler nextStateHandler = null;

        try {

            Vector<Participant> participants = coordinator_.getParticipants();
            int count = (participants.size () - readOnlyTable_.size ());
            TerminationResult commitresult = new TerminationResult ( count );

            // cf bug 64546: avoid committed_ being null upon recovery!
            committed_ = new Boolean ( true );
            // for replaying completion: commit decision was reached
            // otherwise, replay requests might only see TERMINATED!

            try {
            	coordinator_.setState ( TxState.COMMITTING );
            } catch ( RuntimeException error ) {
        		//See case 23334
        		String msg = "Error in committing: " + error.getMessage() + " - rolling back instead";
        		LOGGER.logWarning ( msg , error );
        		try {
					rollbackFromWithinCallback(getCoordinator().isRecoverableWhileActive().booleanValue(),false);
					throw new RollbackException ( msg , error );
        		} catch ( HeurCommitException e ) {
					LOGGER.logWarning ( "Illegal heuristic commit during rollback:" + e );
					throw new HeurMixedException ( e.getHeuristicMessages() );
				}
        	}


            // start messages
            Enumeration<Participant> enumm = participants.elements ();
            while ( enumm.hasMoreElements () ) {
                Participant p = enumm.nextElement ();
                if ( !readOnlyTable_.containsKey ( p ) ) {
                    CommitMessage cm = new CommitMessage ( p, commitresult,
                            onePhase );

                    // if onephase: set cascadelist anyway, because if the
                    // participant is a REMOTE one, then it might have
                    // multiple participants that are not visible here!

                    if ( onePhase && cascadeList_ != null ) { // null for OTS
                        Integer sibnum = (Integer) cascadeList_.get ( p );
                        if ( sibnum != null ) // null for local participant!
                            p.setGlobalSiblingCount ( sibnum.intValue () );
                        p.setCascadeList ( cascadeList_ );
                    }
                    propagator_.submitPropagationMessage ( cm );
                }
            } // while

            commitresult.waitForReplies ();
            int res = commitresult.getResult ();

            if ( res != TerminationResult.ALL_OK ) {

                if ( res == TerminationResult.HEUR_MIXED ) {
                	Hashtable<Participant,TxState> hazards = commitresult.getPossiblyIndoubts ();
                    Hashtable heuristics = commitresult
                            .getHeuristicParticipants ();
                    addToHeuristicMap ( heuristics );
                    enumm = participants.elements ();
                    while ( enumm.hasMoreElements () ) {
                        Participant p = (Participant) enumm.nextElement ();
                        if ( !heuristics.containsKey ( p ) )
                            addToHeuristicMap ( p, TxState.TERMINATED );
                    }
                    nextStateHandler = new HeurMixedStateHandler ( this,
                            hazards );

                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new HeurMixedException ( getHeuristicMessages () );
                }

                else if ( res == TerminationResult.ROLLBACK ) {
                    // 1PC and rolled back before commit arrived.
                    nextStateHandler = new TerminatedStateHandler ( this );
                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new RollbackException ( "Rolled back already." );
                } else if ( res == TerminationResult.HEUR_ROLLBACK ) {
                    nextStateHandler = new HeurAbortedStateHandler ( this );
                    coordinator_.setStateHandler ( nextStateHandler );
                    // Here, we do NOT need to add extra information, since ALL
                    // participants agreed to rollback. 
                    // Therefore, we need not worry about who aborted and who committed.
                    throw new HeurRollbackException ( getHeuristicMessages () );

                }

                else if ( res == TerminationResult.HEUR_HAZARD ) {
                    Hashtable hazards = commitresult.getPossiblyIndoubts ();
                    Hashtable heuristics = commitresult
                            .getHeuristicParticipants ();
                    addToHeuristicMap ( heuristics );
                    enumm = participants.elements ();
                    while ( enumm.hasMoreElements () ) {
                        Participant p = (Participant) enumm.nextElement ();
                        if ( !heuristics.containsKey ( p ) )
                            addToHeuristicMap ( p, TxState.TERMINATED );
                    }
                    nextStateHandler = new HeurHazardStateHandler ( this,
                            hazards );
                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new HeurHazardException ( getHeuristicMessages () );
                }

            } else {
                // all OK
                if ( heuristic ) {
                    nextStateHandler = new HeurCommittedStateHandler ( this );
                    // again, here we do NOT need to preserve extra per-participant
                    // state mappings, since ALL participants were heur. committed.
                } else
                    nextStateHandler = new TerminatedStateHandler ( this );

                coordinator_.setStateHandler ( nextStateHandler );
            }
        } catch ( RuntimeException runerr ) {
            errors.push ( runerr );
            throw new SysException ( "Error in commit: " + runerr.getMessage (), errors );
        }

        catch ( InterruptedException intr ) {
        	// cf bug 67457
			InterruptedExceptionHelper.handleInterruptedException ( intr );
            errors.push ( intr );
            throw new SysException ( "Error in commit" + intr.getMessage (), errors );
        }

        return getHeuristicMessages ();

    }

    /**
     * Auxiliary method for rollback. This method can be reused in subclasses in
     * order to process rollback.
     *
     * @param indoubt
     *            True iff some participants may already have voted YES.
     * @param heuristic
     *            True iff a heuristic commit should be done.
     */

    protected HeuristicMessage[] rollbackFromWithinCallback ( boolean indoubt ,
            boolean heuristic ) throws HeurCommitException, HeurMixedException,
            SysException, HeurHazardException, java.lang.IllegalStateException
    {
       
        Stack errors = new Stack ();
        CoordinatorStateHandler nextStateHandler = null;
        try {

            coordinator_.setState ( TxState.ABORTING );

            // mark decision for replay requests; since these might only
            // see TERMINATED state!
            committed_ = new Boolean ( false );

            Vector participants = coordinator_.getParticipants ();
            int count = (participants.size () - readOnlyTable_.size ());

            TerminationResult rollbackresult = new TerminationResult ( count );

            Enumeration enumm = participants.elements ();
            while ( enumm.hasMoreElements () ) {
                Participant p = (Participant) enumm.nextElement ();
                if ( !readOnlyTable_.containsKey ( p ) ) {
                    RollbackMessage rm = new RollbackMessage ( p,
                            rollbackresult, indoubt );
                    propagator_.submitPropagationMessage ( rm );
                }
            } 

            rollbackresult.waitForReplies ();
            int res = rollbackresult.getResult ();

            // check results, but we only care if we are indoubt.
            // otherwise, we don't mind any remaining indoubts.
            if ( indoubt && res != TerminationResult.ALL_OK ) {

                if ( res == TerminationResult.HEUR_MIXED ) {
                    Hashtable hazards = rollbackresult.getPossiblyIndoubts ();
                    Hashtable heuristics = rollbackresult
                            .getHeuristicParticipants ();
                    addToHeuristicMap ( heuristics );
                    enumm = participants.elements ();
                    while ( enumm.hasMoreElements () ) {
                        Participant p = (Participant) enumm.nextElement ();
                        if ( !heuristics.containsKey ( p ) )
                            addToHeuristicMap ( p, TxState.TERMINATED );
                    }
                    nextStateHandler = new HeurMixedStateHandler ( this,
                            hazards );
                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new HeurMixedException ( getHeuristicMessages () );
                }

                else if ( res == TerminationResult.HEUR_COMMIT ) {
                    nextStateHandler = new HeurCommittedStateHandler ( this );
                    coordinator_.setStateHandler ( nextStateHandler );
                    // NO extra per-participant state mappings, since ALL
                    // participants are heuristically committed.
                    throw new HeurCommitException ( getHeuristicMessages () );

                }

                else if ( res == TerminationResult.HEUR_HAZARD ) {
                    Hashtable hazards = rollbackresult.getPossiblyIndoubts ();
                    Hashtable heuristics = rollbackresult
                            .getHeuristicParticipants ();
                    // will trigger logging of indoubts and messages
                    addToHeuristicMap ( heuristics );
                    enumm = participants.elements ();
                    while ( enumm.hasMoreElements () ) {
                        Participant p = (Participant) enumm.nextElement ();
                        if ( !heuristics.containsKey ( p ) ) {
                            addToHeuristicMap ( p, TxState.TERMINATED );
                        }
                    }
                    nextStateHandler = new HeurHazardStateHandler ( this, hazards );
                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new HeurHazardException ( getHeuristicMessages () );
                }
            }

            else {
                // all answers OK
                if ( heuristic ) {
                    nextStateHandler = new HeurAbortedStateHandler ( this );
                    // NO per-participant state mapping needed, since ALL agree
                    // on same heuristic outcome.
                } else
                    nextStateHandler = new TerminatedStateHandler ( this );

                coordinator_.setStateHandler ( nextStateHandler );
            }

        }

        catch ( RuntimeException runerr ) {
            errors.push ( runerr );
            throw new SysException ( "Error in rollback: " + runerr.getMessage (), errors );
        }

        catch ( InterruptedException e ) {
        	// cf bug 67457
			InterruptedExceptionHelper.handleInterruptedException ( e );
            errors.push ( e );
            throw new SysException ( "Error in rollback: " + e.getMessage (), errors );
        }

        return getHeuristicMessages ();

    }

    protected void forget ()
    {
        // NOTE: no need to add synchronized -> don't
        // do it, you never know if recursion happens here

        // NOTE: this is of secondary importance; failures are not
        // problematic since forget is mainly for log efficiency.
        // Therefore, this does not affect the final TERMINATED state
        // NOTE: remote participants are notified as well, but they
        // are themselves responsible for deciding whether or not
        // to react to the forget notification.

        CoordinatorStateHandler nextStateHandler = null;

        Vector participants = coordinator_.getParticipants ();
        int count = (participants.size () - readOnlyTable_.size ());
        Enumeration enumm = participants.elements ();
        ForgetResult result = new ForgetResult ( count );
        while ( enumm.hasMoreElements () ) {
            Participant p = (Participant) enumm.nextElement ();
            if ( !readOnlyTable_.containsKey ( p ) ) {
                ForgetMessage fm = new ForgetMessage ( p, result );
                propagator_.submitPropagationMessage ( fm );
            }

        }
        try {
            result.waitForReplies ();
        } catch ( InterruptedException inter ) {
        	// cf bug 67457
			InterruptedExceptionHelper.handleInterruptedException ( inter );
            // some might be left in heuristic state -- that's OK.
        }
        nextStateHandler = new TerminatedStateHandler ( this );
        coordinator_.setStateHandler ( nextStateHandler );
    }
    
    public HeuristicMessage[] rollbackWithAfterCompletionNotification(RollbackCallback cb) throws HeurCommitException,
    HeurMixedException, SysException, HeurHazardException,
    java.lang.IllegalStateException {
		HeuristicMessage[] ret = null;
		try {
        	ret = cb.doRollback();
        	coordinator_.notifySynchronizationsAfterCompletion(TxState.ABORTING, TxState.TERMINATED);
    	} catch (HeurCommitException hc) {
    		coordinator_.notifySynchronizationsAfterCompletion(TxState.COMMITTING, TxState.TERMINATED);
    		throw hc;
    	} catch (HeurMixedException hm) {
    		coordinator_.notifySynchronizationsAfterCompletion(TxState.ABORTING, TxState.TERMINATED);
    		throw hm;
    	} catch (HeurHazardException hh) {
    		coordinator_.notifySynchronizationsAfterCompletion(TxState.ABORTING,TxState.TERMINATED);
    		throw hh;
    	}
		return ret;
	}
    
    HeuristicMessage[] commitWithAfterCompletionNotification(CommitCallback cb) throws HeurRollbackException, HeurMixedException,
    HeurHazardException, java.lang.IllegalStateException,
    RollbackException, SysException {
		HeuristicMessage[] ret = null;
		try {
			ret = cb.doCommit();
			coordinator_.notifySynchronizationsAfterCompletion(TxState.COMMITTING,TxState.TERMINATED);
		} catch (RollbackException rb) {
			coordinator_.notifySynchronizationsAfterCompletion(TxState.ABORTING,TxState.TERMINATED);
			throw rb;
		} catch (HeurMixedException hm) {
			coordinator_.notifySynchronizationsAfterCompletion(TxState.COMMITTING,TxState.TERMINATED);
			throw hm;
		} catch (HeurHazardException hh) {
			coordinator_.notifySynchronizationsAfterCompletion(TxState.COMMITTING,TxState.TERMINATED);
			throw hh;
		} catch (HeurRollbackException hr) {
			coordinator_.notifySynchronizationsAfterCompletion(TxState.ABORTING,TxState.TERMINATED);
			throw hr;
		}
		return ret;
    }
    
    public HeuristicMessage[] rollbackHeuristically ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {
    	return rollbackWithAfterCompletionNotification(new RollbackCallback() {		
			public HeuristicMessage[] doRollback() throws HeurCommitException,
					HeurMixedException, SysException, HeurHazardException,
					IllegalStateException {
				return rollbackFromWithinCallback(true, true);
			}
		});
    }

    public HeuristicMessage[] commitHeuristically () throws HeurMixedException,
    SysException, HeurRollbackException, HeurHazardException,
    java.lang.IllegalStateException, RollbackException
    {
    	return commitWithAfterCompletionNotification(new CommitCallback() {		
			public HeuristicMessage[] doCommit() throws HeurRollbackException,
					HeurMixedException, HeurHazardException, IllegalStateException,
					RollbackException, SysException {
				return commitFromWithinCallback(true,false);
			}
		});
    }
    
    public void writeData(DataOutput out) throws IOException {
    	out.writeBoolean(committed_==null?false:committed_);
    	//readOnlyTable_
    	out.writeInt(readOnlyTable_.size());
    	 Set<Map.Entry<Participant,Boolean>> entries= readOnlyTable_.entrySet();
    	 for (Entry<Participant, Boolean> entry : entries) {
			out.writeUTF(entry.getKey().getClass().getName());
			((DataSerializable)entry.getKey() ).writeData(out);
			out.writeBoolean(entry.getValue());
		}

    	
    	 
    }
    
    public void readData(DataInput in) throws IOException {
    	committed_=in.readBoolean();
    	int size = in.readInt();
    	readOnlyTable_ = new Hashtable<Participant, Boolean>(size);
    	for (int i = 0; i < size; i++) {
			String participantClassName=in.readUTF();
			Participant participant=(Participant)ClassLoadingHelper.newInstance(participantClassName);
			((DataSerializable)participant).readData(in);
			boolean value=in.readBoolean();
			readOnlyTable_.put(participant, value);
			
		}
    	
    }
    
}
