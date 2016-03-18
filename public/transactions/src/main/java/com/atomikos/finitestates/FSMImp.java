/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import java.util.EventListener;
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
    //the current state

    private Hashtable<TxState,Set<EventListener>>  enterlisteners_ = null;
    //the enter listeners

    private Hashtable<TxState,Set<EventListener>> preenterlisteners_ = null;
    //pre enter listeners

    private Hashtable<TxState,Hashtable<TxState,Set<EventListener>>> transitionlisteners_ = null;
    //transition listeners

    private Hashtable<TxState,Hashtable<TxState,Set<EventListener>>> pretransitionlisteners_ = null;
    //pretransition listeners

    
    private Object eventsource_ = null;

    private Object stateLatch_;


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
        enterlisteners_ = new Hashtable<TxState,Set<EventListener>>();
        preenterlisteners_ = new Hashtable<TxState,Set<EventListener>>();
        transitionlisteners_ = new Hashtable<TxState,Hashtable<TxState,Set<EventListener>>>();
        pretransitionlisteners_ = new Hashtable<TxState,Hashtable<TxState,Set<EventListener>>>();
        eventsource_ = eventsource;
        stateLatch_ = new Object();
    }

    /**
     *Help function for adding enter listeners.
     *
     *@param listeners One of the listener tables.
     *@param lstnr The listener to add.
     *@param state The state for which the listener wants to be notified.
     */

    protected synchronized  void addEnterListener(Hashtable<TxState,Set<EventListener>> listeners,
    		EventListener lstnr,
    		TxState state)
    {
        Set<EventListener> lstnrs = listeners.get(state);
        if ( lstnrs == null )
        	lstnrs = new HashSet<EventListener>();
        if ( !lstnrs.contains(lstnr) )
        	lstnrs.add( lstnr );
        listeners.put( state , lstnrs );
    }

    /**
     *Help function for adding transition listeners.
     *
     *@param listeners One of the transition listener tables.
     *@param lstnr The listener to add.
     *@param from The start state of the transition.
     *@param to The end state of the transition.
     */

    protected synchronized void addTransitionListener(Hashtable<TxState,Hashtable<TxState, Set<EventListener>>> listeners,
    			EventListener lstnr,
				 TxState from,
				 TxState to)
    {
    	Hashtable<TxState, Set<EventListener>> lstnrs =   listeners.get(from);
        if (lstnrs == null)
        	lstnrs = new Hashtable<TxState,Set<EventListener>>();
        Set<EventListener> tolstnrs = lstnrs.get(to);
        if (tolstnrs == null)
        	tolstnrs = new HashSet<EventListener>();
        if (!tolstnrs.contains(lstnr))
        	tolstnrs.add(lstnr);
        lstnrs.put(to,tolstnrs);
        listeners.put(from,lstnrs);
    }

    /**
     *Notify the enter listeners.
     *
     *@param listeners One of the enter listener tables.
     *@param state The state about to enter (or entered).
     *@param pre True iff before entering.
     */

    protected void notifyListeners(Hashtable<TxState,Set<EventListener>> listeners, TxState state,
			     boolean pre)
    {
        Set<EventListener> lstnrs = null;
        FSMEnterEvent event = new FSMEnterEvent (eventsource_, state);
        synchronized ( this ) {
            lstnrs= listeners.get ( state );
            if ( lstnrs == null )
                	return;
            //clone to avoid concurrency effects outside synch block
            //during iteration hereafter
            lstnrs = new HashSet<EventListener>(lstnrs);
        }
      //notify OUTSIDE SYNCH to minimize deadlocks
        for (EventListener listener : lstnrs) {
        	if ( pre && ( listener instanceof FSMPreEnterListener ))
        	    ((FSMPreEnterListener) listener).preEnter (event);
        	else if (!pre && (listener instanceof FSMEnterListener))
        	    ((FSMEnterListener) listener).entered ( event );
		}
            	
        
    }

    /**
     *Notify transition listeners.
     *
     *@param listeners One of the transition listener tables.
     *@param from The initial state.
     *@param to The end state.
     *@param pre True iff before transition.
     */

    protected void notifyListeners ( Hashtable<TxState,Hashtable<TxState,Set<EventListener>>> listeners, TxState from ,
    		TxState to , boolean pre )
    {
        FSMTransitionEvent event = new FSMTransitionEvent (eventsource_, from, to );
        Hashtable<TxState,Set<EventListener>> lstnrs = null;
        Set<EventListener>  tolstnrs = null;
        synchronized ( this ) {
            lstnrs =  listeners.get( from );
            if ( lstnrs == null )
                return;
            tolstnrs =  lstnrs.get( to );
            if ( tolstnrs == null )
                return;

            //clone to avoid concurrency effects
            //during iteration outside synch block
            lstnrs = new  Hashtable<TxState,Set<EventListener>> (lstnrs);
            tolstnrs = new HashSet<EventListener>(tolstnrs);
        }

        //iterator outside synch to avoid deadlocks
        for (EventListener listener : tolstnrs) {
        	 if ( pre && ( listener instanceof FSMPreTransitionListener )) {
                 ((FSMPreTransitionListener)listener).beforeTransition(event);
             }
             else if (!pre && (listener instanceof FSMTransitionListener)) {
                 ((FSMTransitionListener) listener).transitionPerformed(event);
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
        synchronized ( this ) {
            if (!state_.transitionAllowedTo(state))
                	throw new IllegalStateException("Transition not allowed: "+state_ +" to "+state);
               
               oldstate = state_;
        	   notifyListeners(preenterlisteners_ , state , true);
        	   notifyListeners(pretransitionlisteners_ , oldstate , state , true);
        	   setStateObject ( state );
        }
        //ENTER EVENTS ARE OUTSIDE SYNCH BLOCK TO MINIMIZE DEADLOCKS!!!
        notifyListeners(enterlisteners_ , state , false);
        notifyListeners(transitionlisteners_ , oldstate, state, false);
    }


    /**
     *@see com.atomikos.finitestates.FSMEnterEventSource
     */

    public void addFSMEnterListener(FSMEnterListener lstnr, TxState state)
    {
        addEnterListener(enterlisteners_ , lstnr , state);

    }


    /**
     *@see com.atomikos.finitestates.FSMPreEnterEventSource
     */

    public void addFSMPreEnterListener(FSMPreEnterListener lstnr,
    		TxState state)
    {
        addEnterListener(preenterlisteners_ , lstnr , state);
    }

    /**
     *@see com.atomikos.finitestates.FSMTransitionEventSource
     */


    public void addFSMTransitionListener(FSMTransitionListener lstnr,
    		TxState from, TxState to)
    {
        addTransitionListener ( transitionlisteners_ , lstnr , from , to );
    }

    /**
     *@see com.atomikos.finitestates.FSMPreTransitionEventSource
     */

    public void addFSMPreTransitionListener(FSMPreTransitionListener lstnr,
    		TxState from, TxState to)
    {
        addTransitionListener( pretransitionlisteners_ , lstnr , from , to );
    }





}

