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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.atomikos.finitestates.FSM;
import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMEnterListener;
import com.atomikos.finitestates.FSMImp;
import com.atomikos.finitestates.FSMPreEnterListener;
import com.atomikos.finitestates.Stateful;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.imp.thread.TaskManager;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.StateRecoverable;
import com.atomikos.timing.AlarmTimer;
import com.atomikos.timing.AlarmTimerListener;
import com.atomikos.timing.PooledAlarmTimer;

/**
 *
 * All things related to termination logic.
 * 
 */

public class CoordinatorImp implements CompositeCoordinator, Participant,
        RecoveryCoordinator, StateRecoverable<TxState>, AlarmTimerListener, Stateful<TxState>,
        FSMPreEnterListener<TxState>
{
	private static final Logger LOGGER = LoggerFactory.createLogger(CoordinatorImp.class);

    static long DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS = 150;
    // SHOULD NOT BE BIG, otherwise lots of sleeping threads -> OUT OF MEMORY!
    
    private static final int MAX_NUMBER_OF_TIMEOUT_TICKS_FOR_INDOUBTS = 30;
    private static final int MAX_NUMBER_OF_TIMEOUT_TICKS_BEFORE_ROLLBACK_OF_ACTIVES = 30;

    private int localSiblingCount_ = 0;
    private AlarmTimer timer_ = null;
    private boolean checkSiblings_ = true;

    private long maxNumberOfTimeoutTicksBeforeHeuristicDecision_ = MAX_NUMBER_OF_TIMEOUT_TICKS_FOR_INDOUBTS;
    private long maxNumberOfTimeoutTicksBeforeRollback_ = MAX_NUMBER_OF_TIMEOUT_TICKS_BEFORE_ROLLBACK_OF_ACTIVES;

    private String root_ = null;
    private FSM<TxState> fsm_ = null;
    private boolean recoverableWhileActive_;
    private boolean heuristicMeansCommit_ = true;
    private Vector<Participant> participants_ = new Vector<Participant>();
    private RecoveryCoordinator superiorCoordinator_ = null; 
    private Vector<HeuristicMessage> tags_ = new Vector<HeuristicMessage>();
    // the tags of all incoming txs
    // does NOT have to be logged: the contents are
    // retrieved BEFORE prepare (in Participant proxy),
    // at return time of the call.

    private CoordinatorStateHandler stateHandler_;
    private boolean single_threaded_2pc_;
	private transient List<Synchronization> synchronizations;

    /**
     * Constructor for testing only.
     */

    protected CoordinatorImp ( String root , boolean heuristic_commit ,
        boolean checkorphans )
    {
        root_ = root;
        
        initFsm(TxState.ACTIVE);
        
        heuristicMeansCommit_ = heuristic_commit;

        setStateHandler ( new ActiveStateHandler ( this ) );
        startThreads ( DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS );
        checkSiblings_ = checkorphans;
        single_threaded_2pc_ = false;
        synchronizations = new ArrayList<Synchronization>();
    }

	private void initFsm(TxState initialState) {
		fsm_ = new FSMImp<TxState> ( this, new TransactionTransitionTable (),
                initialState );
        fsm_.addFSMPreEnterListener ( this, TxState.TERMINATED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_COMMITTED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_ABORTED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_MIXED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_HAZARD );
	}

    /**
     * Constructor.
     *
     * @param root
     *            The root tid.
     * @param coord
     *            The RecoverCoordinator, null if root.
     * @param console
     *            The console to log to, or null if none.
     * @param heuristic_commit
     *            Whether to do commit on heuristic.
     * @param timeout
     *            The timeout in milliseconds for indoubts before a heuristic
     *            decision is made.
     * @param checkorphans
     *            If true, orphan checks are made on prepare. For OTS, this is
     *            false.
     * @param single_threaded_2pc
     * 			 If true then commit is done in the same thread as the one that
     *            started the tx.
     */

    protected CoordinatorImp ( String root , RecoveryCoordinator coord ,
             boolean heuristic_commit ,
            long timeout , boolean checkorphans , boolean single_threaded_2pc )
    {
        root_ = root;
        single_threaded_2pc_ = single_threaded_2pc;
	    initFsm(TxState.ACTIVE );
        heuristicMeansCommit_ = heuristic_commit;

        recoverableWhileActive_ = false;
        superiorCoordinator_ = coord;
        if ( timeout > DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS ) {
            // If timeout is smaller than the default timeout, then
            // there is no need to re-adjust the next two fields
            // since the defaults will be used.
            maxNumberOfTimeoutTicksBeforeHeuristicDecision_ = timeout / DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS;
            maxNumberOfTimeoutTicksBeforeRollback_ = maxNumberOfTimeoutTicksBeforeHeuristicDecision_;
        }

        setStateHandler ( new ActiveStateHandler ( this ) );
        startThreads ( DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS );
        checkSiblings_ = checkorphans;
        synchronizations = new ArrayList<Synchronization>();

    }

    /**
     * Constructor.
     *
     * @param root
     *            The root String for this one.
     * @param console
     *            The console to log to, or null if none.
     * @param coord
     *            The recovery coordinator for indoubt resolution.
     * @param heuristic_commit
     *            If true, heuristic decision is commit.
     * @param checkorphans
     *            If true, orphan checking is done at prepare.
     */

    public CoordinatorImp ( String root , RecoveryCoordinator coord ,
             boolean heuristic_commit , boolean checkorphans )
    {
        this ( root , coord ,  heuristic_commit ,
                DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS , checkorphans , false );
    }

    /**
     * No argument constructor as required by Recoverable interface.
     */

    public CoordinatorImp ()
    {

    	initFsm(TxState.ACTIVE );
        heuristicMeansCommit_ = false;

        checkSiblings_ = true;
        recoverableWhileActive_ = false;
        single_threaded_2pc_ = false;
        synchronizations = new ArrayList<Synchronization>();

    }



    boolean prefersSingleThreaded2PC()
    {
    		return single_threaded_2pc_;
    }

    /**
     * Mark the tx as committed. Needed for testing.
     */

    void setCommitted ()
    {
        stateHandler_.setCommitted ();
    }

    void addTag ( HeuristicMessage tag )
    {
    	synchronized ( fsm_ ) {
    		if ( tag != null )
    			tags_.addElement ( tag );
    	}
    }

    /**
     * Set the state handler. This method should always be preferred over
     * calling setState directly.
     *
     * @param stateHandler
     *            The next state handler.
     */

    void setStateHandler ( CoordinatorStateHandler stateHandler )
    {
        // NB: if this method is synchronized then deadlock happens on heuristic mixed!
        TxState state = stateHandler.getState ();
        stateHandler_ = stateHandler;
        setState ( state );
    }


    RecoveryCoordinator getSuperiorRecoveryCoordinator ()
    {
        return superiorCoordinator_;
    }

    Vector<Participant> getParticipants ()
    {
        return participants_;
    }


    boolean prefersHeuristicCommit ()
    {
        return heuristicMeansCommit_;
    }

    int getLocalSiblingCount ()
    {
        return localSiblingCount_;
    }

    long getMaxIndoubtTicks ()
    {
        return maxNumberOfTimeoutTicksBeforeHeuristicDecision_;
    }

    long getMaxRollbackTicks ()
    {
        return maxNumberOfTimeoutTicksBeforeRollback_;
    }

    boolean checkSiblings ()
    {
        return checkSiblings_;
    }

    public Boolean isRecoverableWhileActive()
    {
        return new Boolean ( recoverableWhileActive_ );
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

    public HeuristicMessage[] getHeuristicMessages (
            Object heuristicState )
    {
    	//NB: don't synchronize, to avoid blocks in recursive 2PC
        return stateHandler_.getHeuristicMessages ( heuristicState );
    }

    /**
     * Tests if the transaction was committed or not.
     *
     * @return boolean True iff committed.
     */

    public boolean isCommitted ()
    {
        return stateHandler_.isCommitted ();
    }

    /**
     * Get the heuristic info for the message round.
     *
     * @return HeuristicMessages[] The heuristic messages, or an empty array if
     *         none.
     */

    public HeuristicMessage[] getHeuristicMessages ()
    {
    	// NB: don't synchronize, to avoid blocks in recursive 2PC
        return stateHandler_.getHeuristicMessages ();
    }

    /**
     * Get the heuristic tags for this coordinator. This info is returned to
     * remote client TMs by a Participant proxy.
     */

    public HeuristicMessage[] getTags ()
    {
    	HeuristicMessage[] template = null;
    	synchronized ( fsm_ ) {
    		template = new HeuristicMessage[tags_.size ()];
    		for ( int i = 0; i < template.length; i++ ) {
    			template[i] = (HeuristicMessage) tags_.elementAt ( i );
    		}
    	}
        return template;
    }

    /**
     * Start threads, propagator and timer logic. Needed on construction AND by
     * replay request events: timers have stopped by then!
     *
     * @param timeout
     *            The timeout for the thread wakeup interval.
     * @param console
     *            The console, null if none.
     */

    protected void startThreads ( long timeout )
    {
    	  
    	synchronized ( fsm_ ) {
    		if ( timer_ == null ) { //not null for repeated recovery 
    			stateHandler_.activate ();
    			timer_ = new PooledAlarmTimer(timeout);
    			timer_.addAlarmTimerListener(this);
    			submitTimer(timer_);
    		}
    	}

    }

   
    private void submitTimer(AlarmTimer timer) {
    		TaskManager.getInstance().executeTask (timer);
	}

	protected long getTimeOut ()
    {
        return (maxNumberOfTimeoutTicksBeforeRollback_ - stateHandler_.getRollbackTicks ())
                * DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS;
    }

   
    void setState ( TxState state ) throws IllegalStateException
    {
        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Coordinator " + getCoordinatorId ()
                + " entering state: " + state.toString () );
        fsm_.setState ( state );

    }

    /**
     * @see Stateful
     */

    public TxState getState ()
    {
        // this method should NOT be synchronized to avoid
        // recursive 2PC deadlocks!
        return fsm_.getState ();
    }

   
    /**
     * @see FSMEnterEventSource.
     */

    public void addFSMEnterListener ( FSMEnterListener l ,
    		TxState state )
    {
        fsm_.addFSMEnterListener ( l, state );

    }

  
    /*
     * @see FSMPreEnterEventSource.
     */

    public void addFSMPreEnterListener ( FSMPreEnterListener l ,
    		TxState state )
    {
        fsm_.addFSMPreEnterListener ( l, (TxState)state );

    }


    /**
     * @see CompositeCoordinator.
     */

    public RecoveryCoordinator getRecoveryCoordinator ()
    {
        return this;
    }

    /**
     * @see CompositeCoordinator.
     */

    public Participant getParticipant () throws UnsupportedOperationException
    {
        return this;
    }

    /**
     * @see com.atomikos.icatch.CompositeCoordinator.
     */

    public String getCoordinatorId ()
    {
        return root_;
    }

    public RecoveryCoordinator addParticipant (
            Participant participant ) throws SysException,
            java.lang.IllegalStateException, RollbackException
    {
    	synchronized ( fsm_ ) {
    		if ( !getState ().equals ( TxState.ACTIVE ) )
    			throw new IllegalStateException (
    					getCoordinatorId() +
    					" is no longer active but in state " +
    					getState ().toString () );

    		//FIRST add participant, THEN set state to support active recovery
    		if ( !participants_.contains ( participant ) ) {
    			participants_.add ( participant );
    		}
    		//make sure that aftercompletion notification is done.
    		setState ( TxState.ACTIVE );
    	}


        return this;

    }

    /**
     * Called when a tx import is being done.
     */

    protected void incLocalSiblingCount ()
    {
    	synchronized ( fsm_ ) {
    		localSiblingCount_++;
    	}
    }

    void registerSynchronization ( Synchronization sync )
            throws RollbackException, IllegalStateException,
            UnsupportedOperationException, SysException

    {

    	synchronized ( fsm_ ) {
    		if ( !getState ().equals ( TxState.ACTIVE ) )
    			throw new IllegalStateException ( "wrong state: " + getState () );   		
    		rememberSychronizationForAfterCompletion(sync);
    	}
    }

 
    private void rememberSychronizationForAfterCompletion(Synchronization sync) {
		getSynchronizations().add(sync);		
	}

	private List<Synchronization> getSynchronizations() {
		synchronized(fsm_) {
			if (synchronizations == null) synchronizations = new ArrayList<Synchronization>();
			return synchronizations;
		}
	}
	
	void notifySynchronizationsAfterCompletion(TxState... successiveStates) {
		for ( TxState state : successiveStates ) {
			for (Synchronization s : getSynchronizations()) {
				try {
					s.afterCompletion(state);
				} catch (Throwable t) {
					LOGGER.logWarning("Unexpected error in afterCompletion", t);
				}
			}
		}
	}

	/**
     * @see FSMPreEnterListener.
     */

    public void preEnter ( FSMEnterEvent<TxState> event ) throws IllegalStateException
    {
    	TxState state = event.getState ();

        if ( state.equals ( TxState.TERMINATED )
                || state.equals ( TxState.HEUR_ABORTED )
                || state.equals ( TxState.HEUR_COMMITTED )
                || state.equals ( TxState.HEUR_HAZARD )
                || state.equals ( TxState.HEUR_MIXED ) ) {

            if ( !state.equals ( TxState.TERMINATED ) )
            	LOGGER.logWarning ( "Local heuristic termination of coordinator "
                        + root_ + " with state " + getState () );
            else
                dispose ();
        }

    }

    /**
     * @see Participant
     */

    public String getURI ()
    {
        return getCoordinatorId ();
    }

    /**
     * @see Participant
     */

    public boolean recover () throws SysException
    {
        boolean allOK = true;
        boolean ret;

    	 if ( LOGGER.isDebugEnabled() ){
    		 LOGGER.logDebug (  "starting recover() for coordinator: " + getCoordinatorId () );
    	 }

		synchronized ( fsm_ ) {
			// cf case 61686 and case 62217: avoid concurrent enlists while recovering
			Iterator parts = getParticipants().iterator();
			while (parts.hasNext()) {
				Participant next = (Participant) parts.next();
				boolean recoveredParticipant = false;
				try {
					recoveredParticipant = next.recover();
					 if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (  "coordinator: " + getCoordinatorId()
								+ "recovered participant: " + next );
				} catch (Exception e) {
					// happens if XA connection could not be gotten or other problems
					LOGGER.logWarning("Error in recovering participant");
					StackTraceElement[] infos = e.getStackTrace();
					for (int i = 0; i < infos.length; i++) {
						LOGGER.logWarning(infos[i].toString());
					}
					// do NOT throw any exception: tolerate this to let the coordinator do the rest
				}
				allOK = allOK && recoveredParticipant;
			}
			stateHandler_.recover(this);
			ret = !(!allOK && getState().equals(TxState.IN_DOUBT));
		} // synchronized

        // ONLY NOW start threads and so on
        startThreads ( DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS );


        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (   "recover() done for coordinator: " + getCoordinatorId () );
      

        return ret;
    }

    /**
     * @see Participant.
     */

    public void forget ()
    {
        stateHandler_.forget ();
    }

    /**
     * @see Participant.
     */

    public void setCascadeList ( java.util.Dictionary allParticipants )
            throws SysException
    {
        stateHandler_.setCascadeList ( allParticipants );
    }

    /**
     * @see Participant.
     */

    public void setGlobalSiblingCount ( int count )
    {
        stateHandler_.setGlobalSiblingCount ( count );
    }

    /**
     * @see Participant.
     */

    public int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException
    {
        // FIRST, TAKE CARE OF DUPLICATE PREPARES

        // Recursive prepare-calls should be avoided for not deadlocking rollback/commit methods
        // If a recursive prepare re-enters, then it will see a voting state -> reject.
        // Note that this may also avoid some legal prepares, but only rarely
        if ( getState ().equals ( TxState.PREPARING ) )
            throw new RollbackException ( "Recursion detected" );

        int ret = Participant.READ_ONLY + 1;
        synchronized ( fsm_ ) {
        	ret = stateHandler_.prepare ();
        	if ( ret == Participant.READ_ONLY ) {

        		 if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (  "prepare() of Coordinator  " + getCoordinatorId ()
         				+ " returning READONLY" );
        	} else {

        		 if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "prepare() of Coordinator  " + getCoordinatorId ()
         				+ " returning YES vote");
        	}
        }
        return ret;

    }

    /**
     * @see Participant.
     */

    public HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {
    	HeuristicMessage[] ret = null;
    	synchronized ( fsm_ ) {
    		ret = stateHandler_.commit(onePhase);
    	}
    	return ret;
    }

    /**
     * @see Participant.
     */

    public HeuristicMessage[] rollback () throws HeurCommitException,
            HeurMixedException, SysException, HeurHazardException,
            java.lang.IllegalStateException
    {

    	HeuristicMessage[] ret = null;
    	
        if ( getState ().equals ( TxState.ABORTING ) ) {
            // this method is ONLY called for EXTERNAL events -> by remote coordinators
            // therefore, state aborting means either a recursive
            // call or a concurrent rollback by two different coordinators.
            // Recursion can be detected by this state, because the
            // original call will still be in its propagation phase,
            // where the state is set to ABORTING.
            // Returning immediately will make sure no
            // deadlock happens during 2PC, especially for recursion!.
            // NOTE that if heuristic problems arise, the first call will always
            // return the heuristic info and set the heuristic state.
            return getHeuristicMessages ();
        }

        // here, we are certain that no RECURSIVE call is going on,
        // so we can safely lock this instance.

        synchronized ( fsm_ ) {
        	ret = stateHandler_.rollback();
        }
        return ret;
    }


    public HeuristicMessage[] rollbackHeuristically ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {
    	HeuristicMessage[] ret = null;
        synchronized ( fsm_ ) {
        	ret = stateHandler_.rollbackHeuristically();
        } 
        return ret;
    }

    public HeuristicMessage[] commitHeuristically () throws HeurMixedException,
            SysException, HeurRollbackException, HeurHazardException,
            java.lang.IllegalStateException, RollbackException
    {
    	HeuristicMessage[] ret = null;
    	synchronized ( fsm_ ) {
    		ret = stateHandler_.commitHeuristically();
    	}
    	return ret;
    }


    /**
     * @see RecoveryCoordinator.
     */

    public Boolean replayCompletion ( Participant participant )
            throws IllegalStateException
    {
    	if(LOGGER.isInfoEnabled()){
    		LOGGER.logInfo("replayCompletion ( " + participant
                    + " ) received by coordinator " + getCoordinatorId ()
                    + " for participant " + participant.toString ());
    	}
        Boolean ret = null;
        synchronized ( fsm_ ) {
        	ret = stateHandler_.replayCompletion ( participant );
        }
        return ret;
    }

    /**
     * Help function for restoration.
     */

    protected void restore ( ObjectImage image )
    {
        CoordinatorLogImage img = (CoordinatorLogImage) image;

        root_ = img.root_;

        participants_ = img.participants_;
        superiorCoordinator_ = img.coordinator_;
        heuristicMeansCommit_ = img.heuristicCommit_;
        maxNumberOfTimeoutTicksBeforeHeuristicDecision_ = img.maxInquiries_;
        maxNumberOfTimeoutTicksBeforeRollback_ = img.maxInquiries_;
        recoverableWhileActive_ = img.activity_;
        if ( recoverableWhileActive_ ) {
            checkSiblings_ = img.checkSiblings_;
            localSiblingCount_ = img.localSiblingCount_;
        }

	    initFsm(img.state_);

        stateHandler_ = img.stateHandler_;
        if ( img.state_.equals ( TxState.COMMITTING )
                && stateHandler_.getState ().equals ( TxState.ACTIVE ) ) {
            // this is a recovered coordinator that was committing
            // ONE-PHASE; make this a heuristic hazard so it can
            // be terminated manually if desired (LogAdministrator)
            CoordinatorStateHandler stateHandler = new HeurHazardStateHandler (
                    stateHandler_, img.participants_ );

            stateHandler.recover ( this );
            setStateHandler ( stateHandler );

        }
        single_threaded_2pc_ = img.single_threaded_2pc_;
    }

    /**
     * @see com.atomikos.persistence.Recoverable
     */

    public ObjectImage getObjectImage ()
    {
    	synchronized ( fsm_ ) {
    		return getObjectImage ( getState () );
    	}
    }

    /**
     * @see com.atomikos.persistence.StateRecoverable
     */

    public ObjectImage getObjectImage ( TxState state )
    {
        // IF VOTING: RETURN LIST OF ALL PARTICIPANTS
        // IF INDOUBT: RETURN LIST OF INDOUBTS AND NOT READONLY
        // IF COMMIT/ABORT: RETURN LIST OF REMAINING ACK

    	CoordinatorLogImage ret = null;
    	synchronized ( fsm_ ) {

    		if ( !recoverableWhileActive_ &&
    				( state.equals ( TxState.ACTIVE ) ||
    			      ( superiorCoordinator_ == null && state.equals ( TxState.IN_DOUBT ) )
    				    //see case 23693: don't log prepared state for roots
    			    )
    		    ) {
    				//merely return null to avoid logging overhead
    				ret = null;

    		}
    		else {
    			TxState imgstate = (TxState) state;

    			if ( recoverableWhileActive_ ) {
    				ret = new CoordinatorLogImage ( root_, imgstate, participants_,
    						superiorCoordinator_, heuristicMeansCommit_, maxNumberOfTimeoutTicksBeforeHeuristicDecision_,
    						stateHandler_, localSiblingCount_, checkSiblings_ , single_threaded_2pc_);
    			} else {
    				ret = new CoordinatorLogImage ( root_, imgstate, participants_,
    						superiorCoordinator_, heuristicMeansCommit_, maxNumberOfTimeoutTicksBeforeHeuristicDecision_,
    						stateHandler_ , single_threaded_2pc_ );
    			}
    		}
    	}

        return ret;
    }

    /**
     * @see com.atomikos.persistence.StateRecoverable
     */

    public TxState[] getRecoverableStates ()
    {
        // NOTE: make sure COMMITTING is recoverable as well,
        // in order to be able to recover the commit decision!
        // This also prevents anomalous notification of participants
        // when immediate shutdown leaves active coordinator threads
        // behind, because the forced log write on COMMIT will prevent
        // pending coordinators' commit decisions if the log service is down!
    	
        // NOTE:: active state is recoverable, but if feature is disabled then
        // a null image will be returned to avoid log overhead

        return new TxState[] { TxState.ACTIVE , TxState.IN_DOUBT, TxState.COMMITTING,
                TxState.HEUR_COMMITTED, TxState.HEUR_ABORTED,
                TxState.HEUR_HAZARD, TxState.HEUR_MIXED };


    }

    /**
     * @see com.atomikos.persistence.StateRecoverable
     */

    public TxState[] getFinalStates ()
    {
        return new TxState[] { TxState.TERMINATED };
    }

    /**
     * @see com.atomikos.persistence.Recoverable
     */

    public Object getId ()
    {
        return root_;
    }

    public void alarm ( AlarmTimer timer )
    {
        try {
            stateHandler_.onTimeout ();
        } catch ( Exception e ) {
            LOGGER.logWarning( "Exception on timeout of coordinator " + root_ , e );
        }
    }

    protected void dispose ()
    {
    	synchronized ( fsm_ ) {
    		if ( timer_ != null ) {
    			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Coordinator " + getCoordinatorId() + " : stopping timer..." );
    			timer_.stop ();
    		}
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Coordinator " + getCoordinatorId() + " : disposing statehandler " + stateHandler_.getState() + "..." );
    		stateHandler_.dispose ();
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Coordinator " + getCoordinatorId() + " : disposed." );
    	}
    }

    /**
     * Terminate the work, on behalf of Terminator.
     *
     * @param commit
     *            True iff commit termination is asked.
     */

    protected void terminate ( boolean commit ) throws HeurRollbackException,
            HeurMixedException, SysException, java.lang.SecurityException,
            HeurCommitException, HeurHazardException, RollbackException,
            IllegalStateException

    {    
    	synchronized ( fsm_ ) {
    		if ( commit ) {
    			if ( participants_.size () <= 1 ) {
    				commit ( true );
    			} else {
    				int prepareResult = prepare ();
    				// make sure to only do commit if NOT read only
    				if ( prepareResult != Participant.READ_ONLY )
    					commit ( false );
    			}
    		} else {
    			rollback ();
    		}
    	}
    }

    public void setRecoverableWhileActive () throws UnsupportedOperationException
    {
        recoverableWhileActive_ = true;
    }
    
    void setRollbackOnly() { 	
    	StringHeuristicMessage msg = new StringHeuristicMessage (
    	"setRollbackOnly" );
    	RollbackOnlyParticipant p = new RollbackOnlyParticipant ( msg );

    	try {
    		addParticipant ( p );
    	} catch ( IllegalStateException alreadyTerminated ) {
    		//happens in rollback after timeout - see case 27857; ignore but log
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Error during setRollbackOnly" , alreadyTerminated );
    	} catch ( RollbackException e ) {
    		//ignore: corresponds to desired outcome
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Error during setRollbackOnly" , e );
        }
    }

	public TxState getStateWithTwoPhaseCommitDecision() {
		TxState ret = getState();
		if (TxState.TERMINATED.equals(getState())) {
			if (isCommitted()) ret = TxState.COMMITTED;
			else ret = TxState.ABORTED;
		} else if (TxState.HEUR_ABORTED.equals(getState())) {
			ret = TxState.ABORTED;
		} else if (TxState.HEUR_COMMITTED.equals(getState())) {
			ret = TxState.COMMITTED;
		} else if (TxState.HEUR_HAZARD.equals(getState())) {
			if (isCommitted()) ret = TxState.COMMITTING;
			else ret = TxState.ABORTING;
		}
		return ret;
	}


}
