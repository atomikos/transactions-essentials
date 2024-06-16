/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.atomikos.finitestates.FSM;
import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMEnterListener;
import com.atomikos.finitestates.FSMImp;
import com.atomikos.finitestates.FSMTransitionEvent;
import com.atomikos.finitestates.FSMTransitionListener;
import com.atomikos.finitestates.Stateful;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.event.transaction.TransactionHeuristicEvent;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.RecoverableCoordinator;
import com.atomikos.publish.EventPublisher;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.TxState;
import com.atomikos.thread.TaskManager;
import com.atomikos.timing.AlarmTimer;
import com.atomikos.timing.AlarmTimerListener;
import com.atomikos.timing.PooledAlarmTimer;

/**
 *
 * All things related to termination logic.
 * 
 */

public class CoordinatorImp implements CompositeCoordinator, Participant,
        RecoveryCoordinator, RecoverableCoordinator, AlarmTimerListener, Stateful,
        FSMEnterListener, FSMTransitionListener
{
	private static final Logger LOGGER = LoggerFactory.createLogger(CoordinatorImp.class);

    static long DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS = 150;
    // SHOULD NOT BE BIG, otherwise lots of sleeping threads -> OUT OF MEMORY!
    
    private static final int MAX_NUMBER_OF_TIMEOUT_TICKS_FOR_INDOUBTS = 30;
    private static final int MAX_NUMBER_OF_TIMEOUT_TICKS_BEFORE_ROLLBACK_OF_ACTIVES = 30;

    private int localSiblingsStarted = 0;
    private int localSiblingsTerminated = 0;
    private AlarmTimer timer_ = null;

    private long maxNumberOfTimeoutTicksBeforeHeuristicDecision_ = MAX_NUMBER_OF_TIMEOUT_TICKS_FOR_INDOUBTS;
    private long maxNumberOfTimeoutTicksBeforeRollback_ = MAX_NUMBER_OF_TIMEOUT_TICKS_BEFORE_ROLLBACK_OF_ACTIVES;

    private String root_ = null;
    private String coordinatorId = null;
    private FSM fsm_ = null;
    private Vector<Participant> participants_ = new Vector<Participant>();
    private RecoveryCoordinator superiorCoordinator_ = null; 

    private CoordinatorStateHandler stateHandler_;
    private boolean single_threaded_2pc_;
	private transient List<Synchronization> synchronizations;
	private boolean timedout = false;

    private String recoveryDomainName;

    /**
     * Constructor for testing only.
     */

    protected CoordinatorImp ( String root)
    {
        root_ = root;
        this.coordinatorId = root;
        initFsm(TxState.ACTIVE);
       
        setStateHandler ( new ActiveStateHandler ( this ) );
        startThreads ( DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS );
        single_threaded_2pc_ = false;
        synchronizations = new ArrayList<Synchronization>();
    }

	private void initFsm(TxState initialState) {
		fsm_ = new FSMImp ( this, initialState );
		fsm_.addFSMEnterListener(this, TxState.TERMINATED);
        fsm_.addFSMEnterListener(this, TxState.HEUR_COMMITTED );
        fsm_.addFSMEnterListener(this, TxState.HEUR_ABORTED );
        fsm_.addFSMEnterListener(this, TxState.HEUR_MIXED );
        fsm_.addFSMEnterListener(this, TxState.HEUR_HAZARD );
        fsm_.addFSMEnterListener(this, TxState.ABANDONED);
        fsm_.addFSMTransitionListener ( this, TxState.COMMITTING, TxState.TERMINATED );
        fsm_.addFSMTransitionListener ( this, TxState.ABORTING, TxState.TERMINATED );
        fsm_.addFSMTransitionListener ( this, TxState.PREPARING, TxState.TERMINATED);
	}

    /**
     * Constructor.
     *
     * @param recoveryDomainName
     *
     * @param coordinatorId
     * 
     * @param root
     *            The root tid.
     * @param coord
     *            The RecoverCoordinator, null if root.
     * @param console
     *            The console to log to, or null if none.
     * @param timeout
     *            The timeout in milliseconds for indoubts before a heuristic
     *            decision is made.
     * 
     * @param single_threaded_2pc
     * 			 If true then commit is done in the same thread as the one that
     *            started the tx.
     */

    protected CoordinatorImp ( String recoveryDomainName, String coordinatorId, String root , RecoveryCoordinator coord ,
            long timeout , boolean single_threaded_2pc )
    {
        root_ = root;
        this.coordinatorId = coordinatorId;
        this.recoveryDomainName = recoveryDomainName;
        single_threaded_2pc_ = single_threaded_2pc;
	    initFsm(TxState.ACTIVE );

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
        synchronizations = new ArrayList<Synchronization>();
    }

    /**
     * No argument constructor as required by Recoverable interface.
     */

    public CoordinatorImp ()
    {

    	initFsm(TxState.ACTIVE );

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

    public Vector<Participant> getParticipants ()
    {
        return participants_;
    }


    int getLocalSiblingCount ()
    {
        return localSiblingsStarted;
    }

    long getMaxIndoubtTicks ()
    {
        return maxNumberOfTimeoutTicksBeforeHeuristicDecision_;
    }

    long getMaxRollbackTicks ()
    {
        return maxNumberOfTimeoutTicksBeforeRollback_;
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
     * Start threads, propagator and timer logic. Needed on construction AND by
     * replay request events: timers have stopped by then!
     *
     * @param timeout
     *            The timeout for the thread wakeup interval.
     * @param console
     *            The console, null if none.
     */

    private void startThreads ( long timeout )
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
    		TaskManager.SINGLETON.executeTask (timer);
	}

	protected long getTimeOut ()
    {
        return (maxNumberOfTimeoutTicksBeforeRollback_ - stateHandler_.getRollbackTicks ())
                * DEFAULT_MILLIS_BETWEEN_TIMER_WAKEUPS;
    }

   
    void setState ( TxState state ) throws IllegalStateException
    {
        if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Coordinator " + getCoordinatorId ()
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

    public void addFSMEnterListener ( FSMEnterListener l, TxState state )
    {
        fsm_.addFSMEnterListener ( l, state );

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
        return coordinatorId;
    }

    RecoveryCoordinator addParticipant (
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

    protected void incLocalSiblingsStarted ()
    {
    	synchronized ( fsm_ ) {
    		localSiblingsStarted++;
    	}
    }
    
    protected void incLocalSiblingsTerminated() throws HeurRollbackException, HeurMixedException, SysException, SecurityException, HeurCommitException, HeurHazardException, IllegalStateException, RollbackException {
        synchronized ( fsm_ ) {
            localSiblingsTerminated++;
            if (hasTimedOut() && !hasActiveSiblings()) {
                terminate(false);
            }
        }
    }
    
    boolean hasTimedOut() {
        synchronized ( fsm_ ) {
            return timedout;
        }
    }

    public boolean hasActiveSiblings() {
        return localSiblingsStarted > localSiblingsTerminated;
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
	
	private List<Synchronization> cloneAndReverseSynchronizationsForAfterCompletion() {
		List<Synchronization> src = getSynchronizations();
		List<Synchronization> ret = new ArrayList<>(src.size());
		synchronized(fsm_) {
			ret.addAll(src);
			Collections.reverse(ret); // cf case 20711
		}
		return ret;
	}
	
	void notifySynchronizationsAfterCompletion(TxState... successiveStates) {
		for ( TxState state : successiveStates ) {
			for (Synchronization s : cloneAndReverseSynchronizationsForAfterCompletion()) {
				try {
					s.afterCompletion(state);
				} catch (Throwable t) {
					LOGGER.logWarning("Unexpected error in afterCompletion", t);
				}
			}
		}
	}

	/**
     * @see FSMEnterListener.
     */
    public void preEnter ( FSMEnterEvent event ) throws IllegalStateException
    {
    	TxState state = event.getState ();
    	if (state.isHeuristic() && requiresHeuristics()) {
    	    //if logcloud: recovery will take care of this so don't publish event
    	    TransactionHeuristicEvent the = new TransactionHeuristicEvent(getCoordinatorId(), superiorCoordinatorId(), state);
    	    EventPublisher.INSTANCE.publish(the);
    	}   	
    	if (state.isFinalStateForOltp()) {
    	    dispose ();
    	} 
        
    }
    
    boolean requiresHeuristics() {
        boolean ret = false;
        if (recoveryDomainName != null) { // can be null for testing            
            String recoveryDomainName = Configuration.getConfigProperties().getTmUniqueName();
            PendingTransactionRecord record = getPendingTransactionRecord(getState());
            if (record != null) {
                ret = record.allowsHeuristicTermination(recoveryDomainName);
            }
        }
        return ret;
    }

    /**
     * @see Participant
     */

    public String getURI ()
    {
        return getCoordinatorId ();
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

    public void setCascadeList ( Map<String,Integer> allParticipants )
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
        	return Participant.READ_ONLY;
            //throw new RollbackException ( "Recursion detected" );

        int ret = Participant.READ_ONLY + 1;
        synchronized ( fsm_ ) {
        	ret = stateHandler_.prepare ();
        	if ( ret == Participant.READ_ONLY ) {

        		 if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace (  "prepare() of Coordinator  " + getCoordinatorId ()
         				+ " returning READONLY" );
        	} else {

        		 if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "prepare() of Coordinator  " + getCoordinatorId ()
         				+ " returning YES vote");
        	}
        }
        return ret;

    }

    /**
     * @see Participant.
     */

    public void commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {
    	synchronized ( fsm_ ) {
    		 stateHandler_.commit(onePhase);
    	}
    }

    /**
     * @see Participant.
     */

    public void rollback () throws HeurCommitException,
            HeurMixedException, SysException, HeurHazardException,
            java.lang.IllegalStateException
    {
    	
        if ( getState ().equals ( TxState.ABORTING ) ) {
            // this method is ONLY called for EXTERNAL events -> by remote coordinators
            // therefore, state aborting means either a recursive
            // call or a concurrent rollback by two different coordinators.
            // Recursion can be detected by this state, because the
            // original call will still be in its propagation phase,
            // where the state is set to ABORTING.
            // Returning immediately will make sure no
            // deadlock happens during 2PC, especially for recursion!
            return;
        }

        // here, we are certain that no RECURSIVE call is going on,
        // so we can safely lock this instance.

        synchronized ( fsm_ ) {
        	stateHandler_.rollback();
        }
    }


    void rollbackHeuristically ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {
        synchronized ( fsm_ ) {
        	stateHandler_.rollbackHeuristically();
        } 
    }

    void commitHeuristically () throws HeurMixedException,
            SysException, HeurRollbackException, HeurHazardException,
            java.lang.IllegalStateException, RollbackException
    {
    	synchronized ( fsm_ ) {
    		stateHandler_.commitHeuristically();
    	}
    }


    /**
     * @see RecoveryCoordinator.
     */

    public Boolean replayCompletion ( Participant participant )
            throws IllegalStateException
    {
    	if(LOGGER.isDebugEnabled()){
    		LOGGER.logDebug("replayCompletion ( " + participant
                    + " ) received by coordinator " + getCoordinatorId ()
                    + " for participant " + participant.toString ());
    	}
        Boolean ret = null;
        synchronized ( fsm_ ) {
        	ret = stateHandler_.replayCompletion ( participant );
        }
        return ret;
    }


	private boolean excludedFromLogging(TxState state) {
		boolean ret = false;
		if (!state.isRecoverableState() ) {
				ret = true;
		} else if ( superiorCoordinator_ == null) {
			if ( state.equals( TxState.IN_DOUBT )) {
				ret = true; //see case 23693: don't log prepared state for roots 
			} else if ( participants_.isEmpty() ) {
				ret = true; //see case 84851: avoid logging overhead for empty transactions
			}					
		}
		
		if (state.isHeuristic()) {
			//new recovery: don't log heuristics - let recovery clean them up
			ret = true;
		}
		
		return ret;
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
    			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Coordinator " + getCoordinatorId() + " : stopping timer..." );
    			timer_.stopTimer ();
    		}
    		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Coordinator " + getCoordinatorId() + " : disposing statehandler " + stateHandler_.getState() + "..." );
    		stateHandler_.dispose ();
    		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Coordinator " + getCoordinatorId() + " : disposed." );
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

    void setRollbackOnly() { 	
    	
    	RollbackOnlyParticipant p = new RollbackOnlyParticipant ( );

    	try {
    		addParticipant ( p );
    	} catch ( IllegalStateException alreadyTerminated ) {
    		//happens in rollback after timeout - see case 27857; ignore but log
    		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Error during setRollbackOnly" , alreadyTerminated );
    	} catch ( RollbackException e ) {
    		//ignore: corresponds to desired outcome
    		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Error during setRollbackOnly" , e );
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



	@Override
	public void transitionPerformed(FSMTransitionEvent e) {
	    //nothing to do in open source
	}
	
	@Override
	public PendingTransactionRecord getPendingTransactionRecord(TxState state) {
		synchronized ( fsm_ ) {
    		if ( excludedFromLogging(state)) {
    				//merely return null to avoid logging overhead
    				return null;
    		}
    		else {
        		return new PendingTransactionRecord(this.getCoordinatorId(), state, this.getExpires(), recoveryDomainName, superiorCoordinatorId());	
    		}	
    	}
	}

    private String superiorCoordinatorId() {
		String ret = null;
		if (getSuperiorRecoveryCoordinator()!=null) {
			ret =  this.getSuperiorRecoveryCoordinator().getURI();
		}
		return ret;
	}



	private long getExpires() {
		return System.currentTimeMillis() + getTimeOut();
	}


	@Override
	public String getResourceName() {
		return null;
	}

	@Override
	public String getRootId() {
		return root_;
	}

    public void timedout(boolean rollbackOnly) {
        synchronized ( fsm_ ) {
            timedout = true;
            if (rollbackOnly) {
                setRollbackOnly();
            }
        }
        
    }

    public boolean isRoot() {
        return superiorCoordinator_ == null;
    }

    @Override
    public String getRecoveryDomainName() {
        return recoveryDomainName;
    }

	@Override
	public void beforeTransition(FSMTransitionEvent e) throws IllegalStateException {
	}

	@Override
	public void entered(FSMEnterEvent e) {
	}


}
