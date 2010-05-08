//$Id: CoordinatorStateHandler.java,v 1.2 2006/09/19 08:03:51 guy Exp $
//$Log: CoordinatorStateHandler.java,v $
//Revision 1.2  2006/09/19 08:03:51  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.9  2005/08/15 12:55:54  guy
//Corrected BUG: deadlock on immediate shutdown with interleaving
//terminating coordinator (statehandler). The entered() callback
//in the TS deadlocked with the shutdown (trying to synch in turn
//on the blocked statehandler.
//
//Revision 1.8  2005/08/15 10:43:27  guy
//Discovered (but not fixed) BUG: deadlock in state handler of
//immediate shutdown interleaves with entered() notification of TS.
//
//Revision 1.7  2004/11/24 10:20:15  guy
//Updated error messages.
//
//Revision 1.6  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.5  2004/09/06 09:26:37  guy
//Redesigned recovery: can now be done at any time.
//Resources can now be added after init() and will be
//recovered immediately rather than on the next restart.
//
//Revision 1.4  2004/09/03 10:00:18  guy
//Added check in replayCompletion: to make it idempotent.
//
//Revision 1.3  2004/09/01 13:39:02  guy
//Merged changes from TransactionsRMI 1.22.
//Corrected bug in SysException.printStackTrace.
//Added log method to Configuration.
//
//Revision 1.2  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.5  2004/01/14 10:38:25  guy
//Corrected forget to not block.
//
//Revision 1.1.2.4  2003/11/17 19:02:16  guy
//Corrected BUG: CoordinatorStateHandler blocked on forget() with readonly
//participant. Extended test for UserTransactionService.
//
//Revision 1.1.2.3  2003/06/20 16:31:32  guy
//*** empty log message ***
//
//Revision 1.1.2.2  2003/05/12 07:00:07  guy
//Redesigned Coordinator with STATE PATTERN.
//

package com.atomikos.icatch.imp;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * Application of the state pattern to the transaction coordinator: each
 * important state has a handler and this class is the superclass that holds
 * common logic.
 * 
 * <b>Note: this class and it subclasses should not use synchronized blocks; 
 * the coordinator (owner) class is responsible for synchronizing access to 
 * this class.</b>
 */

abstract class CoordinatorStateHandler implements Serializable, Cloneable
{
    private static final int THREADS = 1;
    // how many propagator threads do we need?

    private transient CoordinatorImp coordinator_;
    // the coordinator instance whose state we represent

    private Hashtable readOnlyTable_;
    // a hash table that keeps track of which participants are readonly
    // needed on prepare, commit and rollback

    private transient Propagator propagator_;
    // The propagator for propagation of messages

    private transient Stack replayStack_;
    // where replay requests are queued

    private Boolean committed_;
    // True iff commit, False iff rollback, otherwise null

    private transient Dictionary cascadeList_;
    // The participants to cascade prepare to

    private Hashtable heuristicMap_;

    // Where heuristic states are mapped to participants in that state

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
        replayStack_ = new Stack ();
        readOnlyTable_ = new Hashtable ();
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
            Stack comStack = (Stack) heuristicMap_
                    .get ( TxState.HEUR_COMMITTED );
            Stack abStack = (Stack) heuristicMap_.get ( TxState.HEUR_ABORTED );
            Stack termStack = (Stack) heuristicMap_.get ( TxState.TERMINATED );

            clone.heuristicMap_.put ( TxState.HEUR_HAZARD, hazStack.clone () );
            clone.heuristicMap_.put ( TxState.HEUR_MIXED, mixStack.clone () );
            clone.heuristicMap_
                    .put ( TxState.HEUR_COMMITTED, comStack.clone () );
            clone.heuristicMap_.put ( TxState.HEUR_ABORTED, abStack.clone () );
            clone.heuristicMap_.put ( TxState.TERMINATED, termStack.clone () );
        } catch ( CloneNotSupportedException e ) {
            throw new RuntimeException (
                    "CoordinatorStateHandler: clone failure :"
                            + e.getMessage () );
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
        // returns non-null since stack is always created at construction time,
        // and restored from log image as well
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
            // System.err.println ( "Adding to heuristic map: state " + state );
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
        // return 0 by default
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
     * Print a message to the console.
     * 
     * @param message
     *            The message to print.
     */

    protected void printMsg ( String message )
    {
        try {
            Console console = coordinator_.getConsole ();
            if ( console != null )
                console.println ( message );
        } catch ( IOException io ) {
        }
    }

    protected void printMsg ( String msg , int level )
    {
        Console console = coordinator_.getConsole ();
        if ( console != null ) {
            try {
                console.println ( msg, level );
            } catch ( IOException ioerr ) {

            }
        }
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
        // stopThreads removed to make activate idempotent
        // required for new recovery!
        // if ( propagator_ != null ) propagator_.stopThreads();
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
    	    //notifying threads is no longer required since 3.2
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
        // check added to make recovery idempotent
        // as from the official 2.0 release
        if ( !replayStack_.contains ( participant ) )
            replayStack_.push ( participant );

        return committed_;
    }

    /**
     * Utility method for subclasses.
     * 
     * @param participants
     */
    protected void addAllForReplay ( Collection participants ) 
    {
    	Iterator it = participants.iterator();
    	while ( it.hasNext() ) {
    		Participant p = ( Participant ) it.next();
    		replayCompletion ( p );
    	}
    }
    
    /**
     * The corresponding 2PC method is delegated hereto.
     */

    protected void setCascadeList ( Dictionary allParticipants )
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
     * @return Object The object that represents the corresponsding coordinator
     *         state.
     */

    abstract Object getState ();

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

    /*
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

    protected HeuristicMessage[] commit ( boolean heuristic ,
            boolean onePhase ) throws HeurRollbackException,
            HeurMixedException, HeurHazardException,
            java.lang.IllegalStateException, RollbackException, SysException
    {
        Stack errors = new Stack ();
        CoordinatorStateHandler nextStateHandler = null;

        try {

            Vector participants = coordinator_.getParticipants ();
            int count = (participants.size () - readOnlyTable_.size ());
            TerminationResult commitresult = new TerminationResult ( count );

            try {
            	coordinator_.setState ( TxState.COMMITTING );
            } catch ( RuntimeException error ) {
        		//See case 23334
        		String msg = "Error in committing: " + error.getMessage() + " - rolling back instead";
        		Configuration.logWarning ( msg , error );
        		try {
					rollback ( getCoordinator().isRecoverableWhileActive().booleanValue() , false );
					throw new RollbackException ( msg );
        		} catch ( HeurCommitException e ) {
					Configuration.logWarning ( "Illegal heuristic commit during rollback:" + e );
					throw new HeurMixedException ( e.getHeuristicMessages() );
				}
        	}
            committed_ = new Boolean ( true );
            // for replaying completion: commit decision was reached
            // otherwise, replay requests might only see TERMINATED!

            // start messages
            Enumeration enumm = participants.elements ();
            while ( enumm.hasMoreElements () ) {
                Participant p = (Participant) enumm.nextElement ();
                if ( !readOnlyTable_.containsKey ( p ) ) {
                    CommitMessage cm = new CommitMessage ( p, commitresult,
                            onePhase );

                    // if onephase: set cascadelist anyway, because if the
                    // participant is a REMOTE one, then it might have
                    // multiple participants that are not visible here!

                    if ( onePhase && cascadeList_ != null ) { // null for OTS
                                                                // case?
                        Integer sibnum = (Integer) cascadeList_.get ( p );
                        if ( sibnum != null ) // null for local participant!
                            p.setGlobalSiblingCount ( sibnum.intValue () );
                        p.setCascadeList ( cascadeList_ );
                    }
                    propagator_.submitPropagationMessage ( cm );
                    // this will trigger sending the message
                }
            } // while

            commitresult.waitForReplies ();
            int res = commitresult.getResult ();

            if ( res != TerminationResult.ALL_OK ) {

                if ( res == TerminationResult.HEUR_MIXED ) {
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
                    // participants
                    // agreed to rollback. Therefore, we need not worry about
                    // who aborted
                    // and who committed.
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
                    // again, here we do NOT need to preserve extra
                    // per-participant
                    // state mappings, since ALL participants were heur.
                    // committed.
                } else
                    nextStateHandler = new TerminatedStateHandler ( this );

                coordinator_.setStateHandler ( nextStateHandler );
            }
        } catch ( RuntimeException runerr ) {
            errors.push ( runerr );
            throw new SysException (
                    "Error in commit: " + runerr.getMessage (), errors );
        }

        catch ( InterruptedException intr ) {
            errors.push ( intr );
            throw new SysException ( "Error in commit" + intr.getMessage (),
                    errors );
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

    protected HeuristicMessage[] rollback ( boolean indoubt ,
            boolean heuristic ) throws HeurCommitException, HeurMixedException,
            SysException, HeurHazardException, java.lang.IllegalStateException
    {
        // propagate ONLY IF participants are NOT readonly!

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

            // start messages

            Enumeration enumm = participants.elements ();
            while ( enumm.hasMoreElements () ) {
                Participant p = (Participant) enumm.nextElement ();
                if ( !readOnlyTable_.containsKey ( p ) ) {
                    RollbackMessage rm = new RollbackMessage ( p,
                            rollbackresult, indoubt );
                    propagator_.submitPropagationMessage ( rm );
                    // this will trigger sending the message
                }
            } // while

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
                    // participants
                    // are heuristically committed.
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
                    nextStateHandler = new HeurHazardStateHandler ( this,
                            hazards );
                    coordinator_.setStateHandler ( nextStateHandler );
                    throw new HeurHazardException ( getHeuristicMessages () );
                }
            }

            else {
                // all answers OK
                if ( heuristic ) {
                    nextStateHandler = new HeurAbortedStateHandler ( this );
                    // NO per-participant state mapping needed, since ALL agree
                    // on same
                    // heuristic outcome.
                } else
                    nextStateHandler = new TerminatedStateHandler ( this );

                coordinator_.setStateHandler ( nextStateHandler );
            }

        }

        catch ( RuntimeException runerr ) {
            errors.push ( runerr );
            throw new SysException ( "Error in rollback: "
                    + runerr.getMessage (), errors );
        }

        catch ( InterruptedException e ) {
            errors.push ( e );
            throw new SysException ( "Error in rollback: " + e.getMessage (),
                    errors );
        } finally {
            // System.err.println ( "Exiting CoordinatorStateHandler.rollback"
            // );
        }

        return getHeuristicMessages ();

    }

    protected void forget ()
    {
        // NOTE: no need to add synchronized -> don't
        // do it, you never know if recursion happens here

        // start messages
        // NOTE: this is of secondary importance; failures are not
        // problematic since forget is mainly for log efficiency.
        // Therefore, this does not affect the final TERMINATED state
        // NOTE: remote participants are notified as well, but they
        // are themselves responsible for deciding whether or not
        // to react to the forget notification.

        CoordinatorStateHandler nextStateHandler = null;

        Vector participants = coordinator_.getParticipants ();
        // @todo CHECK IN TRMI RELEASE BRANCH if forget might block!
        int count = (participants.size () - readOnlyTable_.size ());
        Enumeration enumm = participants.elements ();
        ForgetResult result = new ForgetResult ( count );
        while ( enumm.hasMoreElements () ) {
            Participant p = (Participant) enumm.nextElement ();
            if ( !readOnlyTable_.containsKey ( p ) ) {
                ForgetMessage fm = new ForgetMessage ( p, result );
                propagator_.submitPropagationMessage ( fm );
                // this will trigger sending the message
            }

        }
        try {
            // System.out.println ( "Coordinator state handler: waiting for
            // replies...");
            result.waitForReplies ();
            // System.out.println ( "Coordinator state handler: replies
            // gotten");
        } catch ( InterruptedException inter ) {
            // some might be left in heuristic state -- that's OK.
        }
        nextStateHandler = new TerminatedStateHandler ( this );
        coordinator_.setStateHandler ( nextStateHandler );
    }
}
