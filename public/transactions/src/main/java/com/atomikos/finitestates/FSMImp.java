/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import com.atomikos.recovery.TxState;


/**
 *
 *
 * Implementation of a finite state machine. The following
 * consistency is provided:
 * <ul>
 * <li>getState returns a snapshot (state may change continuously)</li>
 * <li>FSMPreEnterListeners have the guarantee that getState() returns the same
 * value each time during the <b>preEnter</b> notification.</li>
 * <li>FSMEnterListeners have <b>no guarantee</b> that the getState() method will
 * return the state that was entered - this state may have changed since.</li>
 * </ul>
 *
 */

public class FSMImp implements FSM
{

    private TxState state_ = null;

    private final Hashtable<TxState,Set<FSMEnterListener>>  enterlisteners_ = new Hashtable<>();

    private final Hashtable<Transition,Set<FSMTransitionListener>> transitionlisteners_ = new Hashtable<>();
    
    private Object eventsource_ = null;

    private final Object stateLatch_ = new Object();


    /**
     *Constructor.
     *
     *@param transitiontable The transitiontable with valid
     *transitions.
     *
     *@param initialstate The initial state of the FSM.
     */

    public FSMImp ( TxState initialstate )
    {
        this ( null, initialstate );
        eventsource_ = this;
    }

    /**
     *Creates a new instance with a given event source.
     *Useful for cases where finite state machine behaviour is modelled
     *by delegation to an instance of this class.
     *
     *@param eventsource The object to be used as source of events.
     *@param transitiontable The transitiontable for state changes.
     *@param initialstate The initial state of the FSM.
     */

    public FSMImp ( Object eventsource, TxState initialstate )
    {
        state_ = initialstate;
        eventsource_ = eventsource;
    }



    /**
     *Notify the enter listeners.
     *
     *@param state The state about to enter (or entered).
     *@param pre True iff before entering.
     */

    protected void notifyListeners(TxState state,
			     boolean pre)
    {
        Set<FSMEnterListener> lstnrs = null;
        FSMEnterEvent event = new FSMEnterEvent (eventsource_, state);
        synchronized ( this ) {
            lstnrs = enterlisteners_.get ( state );
            if ( lstnrs == null ) return;
            //clone to avoid concurrency effects outside synch block
            //during iteration hereafter
            lstnrs = new HashSet<FSMEnterListener>(lstnrs);
        }
        //notify OUTSIDE SYNCH to minimize deadlocks
        for (FSMEnterListener listener : lstnrs) {
        	if ( pre ) {
        	    listener.preEnter(event);
        	} else {
        	    listener.entered(event);
        	}
        }         	
    }

    /**
     *Notify transition listeners.
     *
     *@param transition
     *@param pre True iff before transition.
     */

    protected void notifyListeners(Transition transition, boolean pre) {
        FSMTransitionEvent event = new FSMTransitionEvent (eventsource_, transition);
        Set<FSMTransitionListener> lstnrs = null;
        synchronized ( this ) {
            lstnrs =  transitionlisteners_.get( transition );
            if ( lstnrs == null ) {
            	return;
            }
            //clone to avoid concurrency effects
            //during iteration outside synch block
            lstnrs = new  HashSet<FSMTransitionListener>(lstnrs);
        }

        //iterator outside synch to avoid deadlocks
        for (FSMTransitionListener listener : lstnrs) {
        	 if ( pre ) {
        		 listener.beforeTransition(event);
             }   else {
            	 listener.transitionPerformed(event);
             }
		}
    }

    /**
     *@see com.atomikos.finitestates.FSM
     */

    public TxState getState()
    {
    	//Note: this method should NOT be synchronized on the FSM itself, to avoid deadlocks
    	//in re-entrant 2PC calls!
    	TxState ret = null;
    	//don't synch on FSM -> use latch object instead
    	synchronized ( stateLatch_ ) {
    		ret = state_;
    	}
        return ret;
    }

    private void setStateObject ( TxState state )
    {
    	//synchronize on stateLatch ONLY to make sure that getState
    	//returns the latest (non-cached) value
    	synchronized ( stateLatch_ ) {
    		this.state_ = state;
    	}
    }


    /**
     *@see com.atomikos.finitestates.StateMutable
     */

    public void setState(TxState state)
        throws IllegalStateException
    {
    	TxState oldstate = null;
    	Transition transition = null;
        synchronized ( this ) {
            if (!state_.transitionAllowedTo(state)) {
                	throw new IllegalStateException("Transition not allowed: "+state_ +" to "+state);
            }
            oldstate = state_;
            transition = new Transition(oldstate, state);
            notifyListeners(state , true);
            notifyListeners(transition , true);
            setStateObject ( state );
        }
        //ENTER EVENTS ARE OUTSIDE SYNCH BLOCK TO MINIMIZE DEADLOCKS!!!
        notifyListeners(state , false);
        notifyListeners(transition, false);
    }


    /**
     *@see com.atomikos.finitestates.FSMEnterEventSource
     */

    public synchronized void addFSMEnterListener(FSMEnterListener lstnr, TxState state)
    {
        Set<FSMEnterListener> lstnrs = enterlisteners_.get(state);
        if ( lstnrs == null ) {
        	lstnrs = new HashSet<FSMEnterListener>();
        	enterlisteners_.put( state , lstnrs );
        }
        if ( !lstnrs.contains(lstnr) ) {
        	lstnrs.add(lstnr);
        }
    }

    /**
     *@see com.atomikos.finitestates.FSMTransitionEventSource
     */


    public synchronized void addFSMTransitionListener(FSMTransitionListener listener,
    		TxState from, TxState to) {
    	Transition transition = new Transition(from, to);
    	Set<FSMTransitionListener> lstnrs = transitionlisteners_.get(transition);
        if (lstnrs == null) {
        	lstnrs = new HashSet<FSMTransitionListener>();
        	transitionlisteners_.put(transition, lstnrs);
        }
        if (!lstnrs.contains(listener)) {
        	lstnrs.add(listener);
        }
    }


}

