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

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.atomikos.diagnostics.Console;
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
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.imp.thread.TaskManager;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.persistence.ObjectImage;
import com.atomikos.persistence.StateRecoverable;
import com.atomikos.timing.AlarmTimer;
import com.atomikos.timing.AlarmTimerListener;
import com.atomikos.timing.PooledAlarmTimer;

/**
 * 
 * 
 * Implementation of termination logic.
 */

public class CoordinatorImp implements CompositeCoordinator, Participant,
        RecoveryCoordinator, StateRecoverable, AlarmTimerListener, Stateful,
        FSMPreEnterListener
{
	private static final Logger LOGGER = LoggerFactory.createLogger(CoordinatorImp.class);

    static long DEFAULT_TIMEOUT = 150;
    // how many millisec until timer thread wakes up
    // SHOULD NOT BE BIG, otherwise lots of sleeping
    // threads -> OUT OF MEMORY!

    private static final int MAX_INDOUBT_TICKS = 30;
    // max number of timeout ticks for indoubts.

    private static final int MAX_ROLLBACK_TICKS = 30;
    // max number of timer 'ticks' before rollback of
    // active txs
    // NOTE : a timer tick equals one wakeup of timer thread.

    private Console console_ = null;
    // diagnostics

    // private RecoveryManager recmgr_ = null;
    // for recoverability

    private int localSiblingCount_ = 0;
    // no of siblings seen by resource.

    // private int actives_ = 0;
    // for detecting locally done state.

    private AlarmTimer timer_ = null;
    // timer to wait on

    private boolean checkSiblings_ = true;
    // false for OTS txs

    //
    // BELOW ARE NON-TRANSIENT INSTANCE VARS
    //

    private long maxIndoubtTicks_ = MAX_INDOUBT_TICKS;
    // max no of indoubt timeout ticks before heuristic

    private long maxRollbackTicks_ = MAX_ROLLBACK_TICKS;
    // max no of rollback ticks before rollback

    private String root_ = null;

    private FSM fsm_ = null;
    // for safe state changes; ONLY STATE must be logged!
    
    private boolean recoverableWhileActive_;

    private boolean heuristicCommit_ = true;
    // what to do on timeout of indoubt: commit or not

    private Vector participants_ = new Vector ();
    // all participants known for this coordinator.

    private RecoveryCoordinator coordinator_ = null;
    // the recovery coordinator; null if root.

    private Vector tags_ = new Vector ();
    // the tags of all incoming txs
    // does NOT have to be logged: the contents are
    // retrieved BEFORE prepare (in Participant proxy),
    // at return time of the call.

    private CoordinatorStateHandler stateHandler_;
    // The state handler object to delegate 2PC methods to
    
    private boolean single_threaded_2pc_;
    // should two-phase commit happen in the same thread?
    // if false then commit will be done in parallel for # resources
    // if true then commit/rollback will be in the same thread
    // as the one that started the tx
    // see BugzID 20653

    /**
     * Constructor for testing only.
     */

    protected CoordinatorImp ( String root , boolean heuristic_commit ,
            Console console , boolean checkorphans )
    {
        root_ = root;
        fsm_ = new FSMImp ( this, new TransactionTransitionTable (),
                TxState.ACTIVE );
        heuristicCommit_ = heuristic_commit;
        console_ = console;
        setStateHandler ( new ActiveStateHandler ( this ) );
        startThreads ( DEFAULT_TIMEOUT, console );
        // actives_ = 0;
        // timeout_ = DEFAULT_TIMEOUT;
        checkSiblings_ = checkorphans;
        fsm_.addFSMPreEnterListener ( this, TxState.TERMINATED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_COMMITTED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_ABORTED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_MIXED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_HAZARD );
        single_threaded_2pc_ = false;
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
     *            The timeout in milliseconds for indoubts. before a heuristic
     *            decision is made.
     * @param checkorphans
     *            If true, orphan checks are made on prepare. For OTS, this is
     *            false.
     * @param single_threaded_2pc
     * 			 If true then commit is done in the same thread as the one that
     *            started the tx.
     */

    protected CoordinatorImp ( String root , RecoveryCoordinator coord ,
            Console console , boolean heuristic_commit ,
            long timeout , boolean checkorphans , boolean single_threaded_2pc )
    {
        root_ = root;
        single_threaded_2pc_ = single_threaded_2pc;
        // recmgr_ = recmgr;
        // recmgr_.register(this);
        fsm_ = new FSMImp ( this, new TransactionTransitionTable (),
                TxState.ACTIVE );
        heuristicCommit_ = heuristic_commit;
        console_ = console;
        recoverableWhileActive_ = false;
        coordinator_ = coord;
        if ( timeout > DEFAULT_TIMEOUT ) {
            // If timeout is smaller than the default timeout, then
            // there is no need to re-adjust the next two fields
            // since the defaults will be used.
            maxIndoubtTicks_ = timeout / DEFAULT_TIMEOUT;
            maxRollbackTicks_ = maxIndoubtTicks_;
            //System.out.println ( "Coordinator with timeout: " + timeout + " vs default value of " + DEFAULT_TIMEOUT );
        }

        setStateHandler ( new ActiveStateHandler ( this ) );
        // actives_ = 0;
        startThreads ( DEFAULT_TIMEOUT, console );
        checkSiblings_ = checkorphans;

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
            Console console , boolean heuristic_commit , boolean checkorphans )
    {
        this ( root , coord , console , heuristic_commit ,
                DEFAULT_TIMEOUT , checkorphans , false );
    }

    /**
     * No argument constructor as required by Recoverable interface.
     */

    public CoordinatorImp ()
    {

        fsm_ = new FSMImp ( this, new TransactionTransitionTable (),
                TxState.ACTIVE );
        heuristicCommit_ = false;

        // actives_ = 0;
        checkSiblings_ = true;
        recoverableWhileActive_ = false;
        single_threaded_2pc_ = false;
        
        fsm_.addFSMPreEnterListener ( this, TxState.TERMINATED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_COMMITTED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_ABORTED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_MIXED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_HAZARD );

    }

    /**
     * Tries to print a msg to console.
     * 
     * @param msg
     *            The message.
     */

    private void printMsg ( String msg )
    {
        if ( console_ != null ) {
            try {
                console_.println ( msg );
            } catch ( IOException ioerr ) {

            }
        } else {
        	//null on recovery
        	LOGGER.logWarning ( msg );
        }
    }

    private void printMsg ( String msg , int level )
    {
        if ( console_ != null ) {
            try {
                console_.println ( msg, level );
            } catch ( IOException ioerr ) {

            }
        } else {
        	//null on recovery
        	switch ( level ) {
        		case Console.DEBUG: 
        			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( msg ); 
        			break;
        		case Console.INFO: 
        			if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( msg ); 
        			break;
        		default: LOGGER.logWarning ( msg );
        	}
        }
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
        // If this method is synchronized then deadlock happens on heuristic
        // mixed?!
        Object state = stateHandler.getState ();
        stateHandler_ = stateHandler;
        setState ( state );
    }

    //
    // METHODS NEEDED BY STATE HANDLERS
    //
    //

    RecoveryCoordinator getSuperiorRecoveryCoordinator ()
    {
        return coordinator_;
    }

    Vector getParticipants ()
    {
        return participants_;
    }
    

    Console getConsole ()
    {
        return console_;
    }

    boolean prefersHeuristicCommit ()
    {
        return heuristicCommit_;
    }

    int getLocalSiblingCount ()
    {
        return localSiblingCount_;
    }

    long getMaxIndoubtTicks ()
    {
        return maxIndoubtTicks_;
    }

    long getMaxRollbackTicks ()
    {
        return maxRollbackTicks_;
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
    	//Note: don't synchronize, to avoid blocks in recursive 2PC
        return stateHandler_.getHeuristicMessages ( heuristicState );
    }

    /**
     * Test if the transaction was committed or not.
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
    	//Note: don't synchronize, to avoid blocks in recursive 2PC
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

    protected void startThreads ( long timeout ,
            Console console )
    {
    	   //System.out.println ( "Starting thread for coordinator " + getCoordinatorId() + " with timeout " + timeout );

    	synchronized ( fsm_ ) {
    		if ( timer_ != null ) {
    			// CHANGED FOR NEW RECOVERY:
    			// this should happen only for second or
    			// third recovery request from a resource
    			// so do nothing

    		} else {

    			stateHandler_.activate ();
    			timer_ = new PooledAlarmTimer(timeout);
    			timer_.addAlarmTimerListener(this);
    			submitTimer(timer_);
    		}
    	}

    }

    /**
     * Submit timer to system thread executor.
     * 
     * @param timer Timer to execute, must not be null
     */
    private void submitTimer(AlarmTimer timer) {
    		TaskManager.getInstance().executeTask (timer);
	}

	protected long getTimeOut ()
    {
        return (maxRollbackTicks_ - stateHandler_.getRollbackTicks ())
                * DEFAULT_TIMEOUT;
    }

    //
    //
    // IMPLEMENTATION OF STATEFUL
    //
    //

    void setState ( Object state ) throws IllegalStateException
    {
        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Coordinator " + getCoordinatorId ()
                + " entering state: " + state.toString () );
        fsm_.setState ( state );
 //       printMsg ( "Coordinator " + getCoordinatorId () + " entered state: "
 //               + state.toString (), Console.DEBUG );
        // System.out.println ( "Coordinator state set to " + state.toString()
        // );

    }

    /**
     * @see Stateful
     */

    public Object getState ()
    {
        // this method should NOT be synchronized to avoid
        // recursive 2PC deadlocks!
        return fsm_.getState ();
    }

    //
    //
    // IMPLEMENTATION OF FSMENTEREVENTSOURCE
    //
    //

    /**
     * @see FSMEnterEventSource.
     */

    public void addFSMEnterListener ( FSMEnterListener l ,
            Object state )
    {
        fsm_.addFSMEnterListener ( l, state );

    }

    //
    //
    // IMPLEMENTATION OF FSMPREENTEREVENTSOURCE
    //
    //

    /*
     * @see FSMPreEnterEventSource.
     */

    public void addFSMPreEnterListener ( FSMPreEnterListener l ,
            Object state )
    {
        fsm_.addFSMPreEnterListener ( l, state );

    }

    //
    //
    // IMPLEMENTATION OF COMPOSITECOORDINATOR
    //
    //

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

//  		next, make sure that aftercompletion notification is done.
    		setState ( TxState.ACTIVE );
    	}
    	
        // incActiveSiblings();

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
    		// if (getState().equals(TxState.MARKED_ABORT))
    		// throw new RollbackException();

    		if ( !getState ().equals ( TxState.ACTIVE ) )
    			throw new IllegalStateException ( "wrong state: " + getState () );

    		//register readonly participant to force 2PC: fixes bug 10035
    		ReadOnlyParticipant rop = new ReadOnlyParticipant ( this );
    		addParticipant ( rop );

    		SynchToFSM wrapper = new SynchToFSM ( sync );
    		addFSMEnterListener ( wrapper, TxState.COMMITTING );
    		addFSMEnterListener ( wrapper, TxState.ABORTING );

    		// addFSMEnterListener(wrapper ,TxState.PREPARING);
    		// VOTING no longer needed: beforeCompletion belongs in
    		// subtx commit, to execute with TX context for thread!

    		// addFSMEnterListener ( wrapper , TxState.PREPARING ); //SYNCH
    		addFSMEnterListener ( wrapper, TxState.TERMINATED );
    		// otherwise, readonly participants do not trigger notification!

    		// next, listen on all heur states as well, to make sure that
    		// connections get notified at end of 2pc (Oracle!)
    		addFSMEnterListener ( wrapper, TxState.HEUR_MIXED );
    		addFSMEnterListener ( wrapper, TxState.HEUR_ABORTED );
    		addFSMEnterListener ( wrapper, TxState.HEUR_HAZARD );
    		addFSMEnterListener ( wrapper, TxState.HEUR_COMMITTED );
    	}
    }

    //
    //
    // IMPLEMENTATION OF FSMPREENTERLISTENER
    //
    //

    /**
     * @see FSMPreEnterListener.
     */

    public void preEnter ( FSMEnterEvent event ) throws IllegalStateException
    {
        Object state = event.getState ();

        if ( state.equals ( TxState.TERMINATED )
                || state.equals ( TxState.HEUR_ABORTED )
                || state.equals ( TxState.HEUR_COMMITTED )
                || state.equals ( TxState.HEUR_HAZARD )
                || state.equals ( TxState.HEUR_MIXED ) ) {

            if ( !state.equals ( TxState.TERMINATED ) )
                printMsg ( "Local heuristic termination of coordinator "
                        + root_ + " with state " + getState () );
            else
                dispose ();
        }

    }

    //
    //
    // IMPLEMENTATION OF PARTICIPANT
    //
    //

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
    	
    	 if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (  "starting recover() for coordinator: " + getCoordinatorId () );
     //   printMsg ( "starting recover() for coordinator: " + getCoordinatorId (),
     //           Console.DEBUG );
        boolean allOK = true;      
        
        // return false only if NOT allOK and indoubt
        boolean ret;
        
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
		//			printMsg("coordinator: " + getCoordinatorId()
		//					+ "recovered participant: " + next, Console.DEBUG);
				} catch (Exception e) {
					// happens if XA connection could not be gotten
					// or other problems
					printMsg("Error in recovering participant");
					StackTraceElement[] infos = e.getStackTrace();
					for (int i = 0; i < infos.length; i++) {
						printMsg(infos[i].toString());
					}
					// do NOT throw any exception: tolerate this to
					// let the coordinator do the rest?
					// probably this will become a hazard?
				}
				allOK = allOK && recoveredParticipant;
			}
			// trigger recover procedure of state handler
			stateHandler_.recover(this);
			ret = (!allOK && getState().equals(TxState.IN_DOUBT));
		} // synchronized

        // ONLY NOW start threads and so on
        startThreads ( DEFAULT_TIMEOUT, console_ );

        
        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (   "recover() done for coordinator: " + getCoordinatorId () );
      //  printMsg ( "recover() done for coordinator: " + getCoordinatorId (),
      //          Console.DEBUG );

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

        // Recursive prepare-calls should be avoided for not deadlocking
        // rollback/commit methods
        // If a recursive prepare re-enters, then it will see a voting
        // state -> reject.
        // Note that this may also avoid some legal prepares,
        // but only rarely
        if ( getState ().equals ( TxState.PREPARING ) )
            throw new RollbackException ( "Recursion detected" );

        int ret = Participant.READ_ONLY + 1;
        synchronized ( fsm_ ) {
        	ret = stateHandler_.prepare ();
        	if ( ret == Participant.READ_ONLY ) {
        		
        		 if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (  "prepare() of Coordinator  " + getCoordinatorId ()
         				+ " returning READONLY" );
        	//	printMsg ( "prepare() of Coordinator  " + getCoordinatorId ()
        	//			+ " returning READONLY", Console.DEBUG );
        	} else {
        		
        		 if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "prepare() of Coordinator  " + getCoordinatorId ()
         				+ " returning YES vote");
 //       		printMsg ( "prepare() of Coordinator  " + getCoordinatorId ()
 //       				+ " returning YES vote", Console.DEBUG );
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
    		ret = stateHandler_.commit ( onePhase );
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

        if ( getState ().equals ( TxState.ABORTING ) ) {
            // this method is ONLY called for EXTERNAL events
            // ->by remote coordinators
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
        	return stateHandler_.rollback ();
        }
    }

    //
    // METHODS NEEDED BY ADMINISTRATION
    //
    //

    public HeuristicMessage[] rollbackHeuristically ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {
    	HeuristicMessage[] ret = null;
        synchronized ( fsm_ ) {
        	ret = stateHandler_.rollback ( true, true );
        }
        return ret;
    }

    public HeuristicMessage[] commitHeuristically () throws HeurMixedException,
            SysException, HeurRollbackException, HeurHazardException,
            java.lang.IllegalStateException, RollbackException
    {
    	HeuristicMessage[] ret = null;    	
    	synchronized ( fsm_ ) {
    		ret = stateHandler_.commit ( true, false );
    	}
    	return ret;
    }

    //
    //
    // IMPLEMENTATION OF RECOVERYCOORDINATOR
    //
    //

    /**
     * @see RecoveryCoordinator.
     */

    public Boolean replayCompletion ( Participant participant )
            throws IllegalStateException
    {
        printMsg ( "replayCompletion ( " + participant
                + " ) received by coordinator " + getCoordinatorId ()
                + " for participant " + participant.toString (), Console.INFO );
        Boolean ret = null;
        synchronized ( fsm_ ) {
        	ret = stateHandler_.replayCompletion ( participant );
        }
        return ret;
    }

    //
    //
    // IMPLEMENTATION OF STATERECOVERABLE
    //
    //

    /**
     * Help function for restoration.
     */

    protected void restore ( ObjectImage image )
    {
        CoordinatorLogImage img = (CoordinatorLogImage) image;

        root_ = img.root_;
        
        participants_ = img.participants_;
        coordinator_ = img.coordinator_;
        heuristicCommit_ = img.heuristicCommit_;
        maxIndoubtTicks_ = img.maxInquiries_;
        maxRollbackTicks_ = img.maxInquiries_;
        // timeout_ = img.timeout_;
        recoverableWhileActive_ = img.activity_;
        if ( recoverableWhileActive_ ) {
            checkSiblings_ = img.checkSiblings_;
            localSiblingCount_ = img.localSiblingCount_;
        }

        fsm_ = new FSMImp ( this, new TransactionTransitionTable (), img.state_ );
        fsm_.addFSMPreEnterListener ( this, TxState.TERMINATED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_COMMITTED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_ABORTED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_MIXED );
        fsm_.addFSMPreEnterListener ( this, TxState.HEUR_HAZARD );

        stateHandler_ = img.stateHandler_;
        if ( img.state_.equals ( TxState.COMMITTING )
                && stateHandler_.getState ().equals ( TxState.ACTIVE ) ) {
            // this is a recovered coordinator that was committing
            // ONE-PHASE; make this a heuristic hazard so it can
            // be terminated manually if desired (LogAdministrator)
            CoordinatorStateHandler stateHandler = new HeurHazardStateHandler (
                    stateHandler_, img.participants_ );

            // call recover to initialize the handler
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

    public ObjectImage getObjectImage ( Object state )
    {
        // IF VOTING: RETURN LIST OF ALL PARTICIPANTS
        // IF INDOUBT: RETURN LIST OF INDOUBTS AND NOT READONLY
        // IF COMMIT/ABORT: RETURN LIST OF REMAINING ACK

    	CoordinatorLogImage ret = null;
    	synchronized ( fsm_ ) {
    		
    		if ( !recoverableWhileActive_ &&
    				( state.equals ( TxState.ACTIVE ) ||
    			      ( coordinator_ == null && state.equals ( TxState.IN_DOUBT ) )     
    				    //see case 23693: don't log prepared state for roots
    			    ) 
    		    ) {
    				//merely return null to avoid logging overhead
    				ret = null;
    			
    		}
    		else {
    			TxState imgstate = (TxState) state;

    			// System.err.println ( "Getting object image for state: " +
    			// imgstate );

    			if ( recoverableWhileActive_ ) {
    				ret = new CoordinatorLogImage ( root_, imgstate, participants_,
    						coordinator_, heuristicCommit_, maxIndoubtTicks_,
    						stateHandler_, localSiblingCount_, checkSiblings_ , single_threaded_2pc_);
    			} else {
    				ret = new CoordinatorLogImage ( root_, imgstate, participants_,
    						coordinator_, heuristicCommit_, maxIndoubtTicks_,
    						stateHandler_ , single_threaded_2pc_ );
    			}
    		}
    	}
    	
        return ret;
    }

    /**
     * @see com.atomikos.persistence.StateRecoverable
     */

    public Object[] getRecoverableStates ()
    {
        // NOTE: make sure COMMITTING is recoverable as well,
        // in order to be able to recover the commit decision!
        // This also prevents anomalous notification of participants
        // when immediate shutdown leaves active coordinator threads
        // behind, because the forced log write on COMMIT will prevent
        // pending coordinators' commit decisions if the log service is down!
        
        Object[] ret = { TxState.ACTIVE , TxState.IN_DOUBT, TxState.COMMITTING,
                TxState.HEUR_COMMITTED, TxState.HEUR_ABORTED,
                TxState.HEUR_HAZARD, TxState.HEUR_MIXED };
        
        //note: active state is recoverable, but if feature is disabled then 
        //a null image will be returned to avoid log overhead
        

        return ret;

    }

    /**
     * @see com.atomikos.persistence.StateRecoverable
     */

    public Object[] getFinalStates ()
    {
        Object[] ret = { TxState.TERMINATED };
        return ret;
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
        	    //System.out.println ( "Timeout in state: " + stateHandler_.getState() );
            stateHandler_.onTimeout ();
        } catch ( Exception e ) {
            printMsg ( "Exception on timeout of coordinator " + root_ + ": "
                    + e.getMessage () );
        }
    }

    /**
     * For cleaning up properly.
     */

    protected void dispose ()
    {
        // System.err.println ( "CoordinatorImp: starting dispose" );
        // System.err.println ( "Stopping timer..." );
    	synchronized ( fsm_ ) {
    		if ( timer_ != null ) {
    			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Coordinator " + getCoordinatorId ()
    					+ " : stopping timer..." );
    			timer_.stop ();
    		}
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Coordinator " + getCoordinatorId ()
    				+ " : disposing statehandler " + stateHandler_.getState ()
    				+ "..." );
    		stateHandler_.dispose ();
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Coordinator " + getCoordinatorId ()
    				+ " : disposed." );
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
        // FOLLOWING IN COMMENTS: REMOVED FOR COMPENSATION!
        // if ( coordinator_ != null )
        // throw new java.lang.SecurityException ("Not root");

    	synchronized ( fsm_ ) {
    		if ( commit ) {
    			if ( participants_.size () <= 1 ) {
    				// System.err.println ( "CoordinatorImp: only one participant!"
    				// );
    				commit ( true );
    			} else {
    				int prepareResult = prepare ();
    				// make sure to only do commit if NOT read only
    				if ( prepareResult != Participant.READ_ONLY )
    					commit ( false );

    				// FOLLOWING WAS REPLACED BECAUSE IT CAUSES READ-ONLY
    				// TRANSACTIONS TO ROLLBACK!
    				// prepare();
    				// commit ( false );
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


}
