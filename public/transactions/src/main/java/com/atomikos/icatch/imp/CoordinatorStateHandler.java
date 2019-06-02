/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;
import com.atomikos.thread.InterruptedExceptionHelper;

/**
 * Application of the state pattern to the transaction coordinator: each
 * important state has a handler and this class is the superclass that holds
 * common logic.
 *
 * <b>Note: this class and it subclasses should not use synchronized blocks;
 * the coordinator (owner) class is responsible for synchronizing access to
 * this class.</b>
 */

abstract class CoordinatorStateHandler
{
	
	private static final Logger LOGGER = LoggerFactory.createLogger(CoordinatorStateHandler.class);

    private transient CoordinatorImp coordinator_;
    // the coordinator instance whose state we represent

    private Set<Participant> readOnlyTable_;
    // a hash table that keeps track of which participants are readonly
    // needed on prepare, commit and rollback

    private Propagator propagator_;
    // The propagator for propagation of messages

    private Stack<Participant> replayStack_;
    // where replay requests are queued

    private Boolean committed_;
    // True iff commit, False iff rollback, otherwise null

    private Map<String, Integer> cascadeList_;
    // The participants to cascade prepare to


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
        readOnlyTable_ = new HashSet<Participant> ();
        committed_ = null;
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
    }

    /**
     * For testing only.
     */

    void setCommitted ()
    {
        committed_ = Boolean.TRUE;
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

    protected Stack<Participant> getReplayStack ()
    {
        return replayStack_;
    }

    /**
     * Get the cascade list.
     *
     * @return Map The cascade list.
     */

    protected Map<String,Integer> getCascadeList ()
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

    protected void setReadOnlyTable ( Set<Participant> table )
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

    protected void setCascadeList ( Map<String, Integer> allParticipants )
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

    protected abstract void commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException;

    /**
     * The corresponding 2PC method is delegated hereto. Subclasses should
     * override this, and may use the auxiliary rollback method provided by this
     * class (in addition to their state-specific preconditions).
     */

    protected abstract void rollback ()
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

    protected void commitFromWithinCallback ( boolean heuristic ,
            boolean onePhase ) throws HeurRollbackException,
            HeurMixedException, HeurHazardException,
            java.lang.IllegalStateException, RollbackException, SysException
    {
        CoordinatorStateHandler nextStateHandler = null;

        try {

            Vector<Participant> participants = coordinator_.getParticipants();
            int count = (participants.size () - readOnlyTable_.size ());
            TerminationResult commitresult = new TerminationResult ( count );

            // cf bug 64546: avoid committed_ being null upon recovery!
            committed_ = Boolean.TRUE;
            // for replaying completion: commit decision was reached
            // otherwise, replay requests might only see TERMINATED!

            try {
            	coordinator_.setState ( TxState.COMMITTING );
            } catch ( RuntimeException error ) {
            	//happens if interleaving recovery has done rollback, or if disk is full and log cannot be written
        		String msg = "Error in committing: " + error.getMessage() + " - recovery will clean up in the background";
        		LOGGER.logWarning ( msg , error );
        		throw new RollbackException ( msg , error );
        	}


            // start messages
            Enumeration<Participant> enumm = participants.elements ();
            while ( enumm.hasMoreElements () ) {
                Participant p = enumm.nextElement ();
                if ( !readOnlyTable_.contains ( p ) ) {
                    CommitMessage cm = new CommitMessage ( p, commitresult,
                            onePhase );

                    // if onephase: set cascadelist anyway, because if the
                    // participant is a REMOTE one, then it might have
                    // multiple participants that are not visible here!

                    if ( onePhase && cascadeList_ != null ) { 
                        Integer sibnum = cascadeList_.get ( p.getURI() );
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
                	Set<Participant> hazards = commitresult.getPossiblyIndoubts ();
                    nextStateHandler = new HeurMixedStateHandler ( this, hazards );
                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new HeurMixedException();
                }

                else if ( res == TerminationResult.ROLLBACK ) {
                    // 1PC and rolled back before commit arrived.
                    nextStateHandler = new TerminatedStateHandler ( this );
                    coordinator_.setStateHandler ( nextStateHandler );
                    Exception cause = commitresult.findFirstOriginalException();
                    if (cause != null) {
                        throw new RollbackException ( "Rolled back already.", cause);
                    } else {
                        throw new RollbackException ( "Rolled back already." );
                    }
                } else if ( res == TerminationResult.HEUR_ROLLBACK ) {
                    nextStateHandler = new HeurAbortedStateHandler ( this );
                    coordinator_.setStateHandler ( nextStateHandler );
                    // Here, we do NOT need to add extra information, since ALL
                    // participants agreed to rollback. 
                    // Therefore, we need not worry about who aborted and who committed.
                    throw new HeurRollbackException();

                }

                else if ( res == TerminationResult.HEUR_HAZARD ) {
                    Set<Participant> hazards = commitresult.getPossiblyIndoubts ();
                    nextStateHandler = new HeurHazardStateHandler ( this, hazards );
                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new HeurHazardException();
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
            throw new SysException ( "Error in commit: " + runerr.getMessage (), runerr );
        }

        catch ( InterruptedException intr ) {
        	// cf bug 67457
			InterruptedExceptionHelper.handleInterruptedException ( intr );
            throw new SysException ( "Error in commit" + intr.getMessage (), intr );
        }
    }


	/**
     * Auxiliary method for rollback. This method can be reused in subclasses in
     * order to process rollback.
     *
     * @param indoubt
     *            True iff some participants may already have voted YES.
     * @param heuristic
     *            True iff a heuristic rollback should be done.
     */

    protected void rollbackFromWithinCallback ( boolean indoubt ,
            boolean heuristic ) throws HeurCommitException, HeurMixedException,
            SysException, HeurHazardException, java.lang.IllegalStateException
    {
       
        CoordinatorStateHandler nextStateHandler = null;
        try {

            coordinator_.setState ( TxState.ABORTING );

            // mark decision for replay requests; since these might only
            // see TERMINATED state!
            committed_ = new Boolean ( false );

            Vector<Participant> participants = coordinator_.getParticipants ();
            int count = (participants.size () - readOnlyTable_.size ());

            TerminationResult rollbackresult = new TerminationResult ( count );

            Enumeration<Participant> enumm = participants.elements ();
            while ( enumm.hasMoreElements () ) {
                Participant p = enumm.nextElement ();
                if ( !readOnlyTable_.contains ( p ) ) {
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
                    Set<Participant> hazards = rollbackresult.getPossiblyIndoubts ();
                    nextStateHandler = new HeurMixedStateHandler ( this, hazards );
                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new HeurMixedException();
                } else if ( res == TerminationResult.HEUR_COMMIT ) {
                    nextStateHandler = new HeurCommittedStateHandler ( this );
                    coordinator_.setStateHandler ( nextStateHandler );
                    // NO extra per-participant state mappings, since ALL
                    // participants are heuristically committed.
                    throw new HeurCommitException();
                } else if ( res == TerminationResult.HEUR_HAZARD ) {
                    Set<Participant> hazards = rollbackresult.getPossiblyIndoubts ();
                    nextStateHandler = new HeurHazardStateHandler ( this, hazards );
                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new HeurHazardException();
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
            throw new SysException ( "Error in rollback: " + runerr.getMessage (), runerr );
        }

        catch ( InterruptedException e ) {
        	// cf bug 67457
			InterruptedExceptionHelper.handleInterruptedException ( e );
            throw new SysException ( "Error in rollback: " + e.getMessage (), e );
        }
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

        Vector<Participant> participants = coordinator_.getParticipants ();
        int count = (participants.size () - readOnlyTable_.size ());
        Enumeration<Participant> enumm = participants.elements ();
        ForgetResult result = new ForgetResult ( count );
        while ( enumm.hasMoreElements () ) {
            Participant p = (Participant) enumm.nextElement ();
            if ( !readOnlyTable_.contains ( p ) ) {
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
    
    public void rollbackWithAfterCompletionNotification(RollbackCallback cb) throws HeurCommitException,
    HeurMixedException, SysException, HeurHazardException,
    java.lang.IllegalStateException {
		try {
        	cb.doRollback();
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
	}
    
    void commitWithAfterCompletionNotification(CommitCallback cb) throws HeurRollbackException, HeurMixedException,
    HeurHazardException, java.lang.IllegalStateException,
    RollbackException, SysException {
		try {
			cb.doCommit();
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
    }
    
    protected void notifySynchronizationsAfterCompletion(TxState... successiveStates) {
    	coordinator_.notifySynchronizationsAfterCompletion(successiveStates);
    }
    
    public void rollbackHeuristically ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {
    	 rollbackWithAfterCompletionNotification(new RollbackCallback() {		
			public void doRollback() throws HeurCommitException,
					HeurMixedException, SysException, HeurHazardException,
					IllegalStateException {
				 rollbackFromWithinCallback(true, true);
			}
		});
    }

    public void commitHeuristically () throws HeurMixedException,
    SysException, HeurRollbackException, HeurHazardException,
    java.lang.IllegalStateException, RollbackException
    {
    	 commitWithAfterCompletionNotification(new CommitCallback() {		
			public void doCommit() throws HeurRollbackException,
					HeurMixedException, HeurHazardException, IllegalStateException,
					RollbackException, SysException {
				commitFromWithinCallback(true,false);
			}
		});
    }
    
    protected void removePendingOltpCoordinatorFromTransactionService() {
    	LOGGER.logDebug("Abandoning "+getCoordinator().getCoordinatorId()+" in state "+getState()+" after timeout - recovery will cleanup in the background");
    	getCoordinator().setState(TxState.ABANDONED);
    	dispose(); //needed to stop all threads
	}
}
