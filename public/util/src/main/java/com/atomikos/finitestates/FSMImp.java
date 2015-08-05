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

package com.atomikos.finitestates;

import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;


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

public class FSMImp<Status> implements FSM<Status>
{

    private Status state_ = null;
    //the current state

    private Hashtable<Status,Hashtable<EventListener,Object>>  enterlisteners_ = null;
    //the enter listeners

    private Hashtable<Status,Hashtable<EventListener,Object>> preenterlisteners_ = null;
    //pre enter listeners

    private Hashtable<Status,Hashtable<EventListener,Object>> transitionlisteners_ = null;
    //transition listeners

    private Hashtable<Status,Hashtable<EventListener,Object>> pretransitionlisteners_ = null;
    //pretransition listeners

    private TransitionTable<Status> transitiontable_ = null;
    //records the legal transitions

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

    public FSMImp ( TransitionTable<Status> transitiontable , Status initialstate )
    {
        this ( null, transitiontable, initialstate );
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

    public FSMImp ( Object eventsource, TransitionTable<Status> transitiontable,
                    Status initialstate )
    {
        transitiontable_ = transitiontable;
        state_ = initialstate;
        enterlisteners_ = new Hashtable<Status,Hashtable<EventListener,Object>>();
        preenterlisteners_ = new Hashtable<Status,Hashtable<EventListener,Object>>();
        transitionlisteners_ = new Hashtable<Status,Hashtable<EventListener,Object>>();
        pretransitionlisteners_ = new Hashtable<Status,Hashtable<EventListener,Object>>();
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

    protected synchronized  void addEnterListener(Hashtable<Status,Hashtable<EventListener,Object>> listeners,
    		EventListener lstnr,
			      Status state)
    {
        Hashtable<EventListener,Object> lstnrs = ( Hashtable<EventListener,Object> ) listeners.get(state);
        if ( lstnrs == null )
	  lstnrs = new Hashtable<EventListener,Object>();
        if ( !lstnrs.containsKey(lstnr) )
	  lstnrs.put( lstnr , new Object() );
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

    protected synchronized void addTransitionListener(Hashtable listeners,
				 Object lstnr,
				 Status from,
				 Status to)
    {
        Hashtable<Status,Hashtable> lstnrs = ( Hashtable<Status,Hashtable> ) listeners.get(from);
        if (lstnrs == null)
	  lstnrs = new Hashtable<Status,Hashtable>();
        Hashtable tolstnrs = (Hashtable ) lstnrs.get(to);
        if (tolstnrs == null)
	  tolstnrs = new Hashtable();
        if (!tolstnrs.containsKey(lstnr))
	  tolstnrs.put(lstnr,new Object());
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

    protected void notifyListeners(Hashtable<Status,Hashtable<EventListener,Object>> listeners, Status state,
			     boolean pre)
    {
        Hashtable<EventListener,Object> lstnrs = null;
        FSMEnterEvent<Status> event = new FSMEnterEvent<Status> (eventsource_, state);
        synchronized ( this ) {

            lstnrs= listeners.get ( state );
            if ( lstnrs == null )
                	return;
            //clone to avoid concurrency effects outside synch block
            //during iteration hereafter
            lstnrs = ( Hashtable<EventListener,Object> ) lstnrs.clone();
        }

        //notify OUTSIDE SYNCH to minimize deadlocks
        Enumeration<EventListener> enumm=lstnrs.keys();
        while (enumm.hasMoreElements()) {
        		EventListener listener=enumm.nextElement();
            	if ( pre && ( listener instanceof FSMPreEnterListener ))
            	    ((FSMPreEnterListener<Status>) listener).preEnter (event);
            	else if (!pre && (listener instanceof FSMEnterListener))
            	    ((FSMEnterListener<Status>) listener).entered ( event );
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

    protected void notifyListeners ( Hashtable<Status,Hashtable<EventListener,Object>> listeners, Status from ,
    		Status to , boolean pre )
    {
        FSMTransitionEvent<Status> event = new FSMTransitionEvent<Status> (eventsource_, from, to );
        Hashtable<EventListener,Object> lstnrs = null;
        Hashtable<EventListener,Object>  tolstnrs = null;
        synchronized ( this ) {
            lstnrs = ( Hashtable<EventListener,Object> ) listeners.get( from );
            if ( lstnrs == null )
                return;
            tolstnrs = ( Hashtable<EventListener,Object> ) lstnrs.get( to );
            if ( tolstnrs == null )
                return;

            //clone to avoid concurrency effects
            //during iteration outside synch block
            lstnrs = ( Hashtable<EventListener,Object> ) lstnrs.clone();
            tolstnrs = ( Hashtable<EventListener,Object> ) tolstnrs.clone();
        }

        //iterator outside synch to avoid deadlocks
        Enumeration enumm =  tolstnrs.keys();
        while ( enumm.hasMoreElements() ) {
            Object listener = enumm.nextElement();
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

    public Status getState()
    {
    	//Note: this method should NOT be synchronized on the FSM itself, to avoid deadlocks
    	//in re-entrant 2PC calls!
    	Status ret = null;
    	//don't synch on FSM -> use latch object instead
    	synchronized ( stateLatch_ ) {
    		ret = state_;
    	}
        return ret;
    }

    private void setStateObject ( Status state )
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

    public void setState(Status state)
        throws IllegalStateException
    {
    	Status oldstate = null;
        synchronized ( this ) {
            if (!transitiontable_.legalTransition(state_,state))
                	throw new IllegalStateException("Transition not allowed: "+
				    state_ +" to "+state);
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

    public void addFSMEnterListener(FSMEnterListener<Status> lstnr, Status state)
    {
        addEnterListener(enterlisteners_ , lstnr , state);

    }


    /**
     *@see com.atomikos.finitestates.FSMPreEnterEventSource
     */

    public void addFSMPreEnterListener(FSMPreEnterListener<Status> lstnr,
			         Status state)
    {
        addEnterListener(preenterlisteners_ , lstnr , state);
    }

    /**
     *@see com.atomikos.finitestates.FSMTransitionEventSource
     */


    public void addFSMTransitionListener(FSMTransitionListener<Status> lstnr,
    		Status from, Status to)
    {
        addTransitionListener ( transitionlisteners_ , lstnr , from , to );
    }

    /**
     *@see com.atomikos.finitestates.FSMPreTransitionEventSource
     */

    public void addFSMPreTransitionListener(FSMPreTransitionListener<Status> lstnr,
    		Status from, Status to)
    {
        addTransitionListener( pretransitionlisteners_ , lstnr , from , to );
    }





}

