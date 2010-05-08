//$Id: FSMImp.java,v 1.2 2006/09/19 08:03:57 guy Exp $
//$Log: FSMImp.java,v $
//Revision 1.2  2006/09/19 08:03:57  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:15  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:41  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:03  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:43  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:24:57  guy
//Updated javadoc.
//
//Revision 1.3  2004/10/12 13:04:22  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2002/01/29 11:29:58  guy
//Updated to latest state: repository seemed outdated?
//
//Revision 1.1  2001/03/08 18:18:50  pardon
//Made FSM a real state machine.
//


package com.atomikos.finitestates;
import java.util.Enumeration;
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

public class FSMImp implements FSM 
{

    private Object state_ = null;
    //the current state
    
    private Hashtable enterlisteners_ = null;
    //the enter listeners

    private Hashtable preenterlisteners_ = null;
    //pre enter listeners

    private Hashtable transitionlisteners_ = null;
    //transition listeners

    private Hashtable pretransitionlisteners_ = null;
    //pretransition listeners

    private TransitionTable transitiontable_ = null;
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

    public FSMImp ( TransitionTable transitiontable , Object initialstate ) 
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
     
    public FSMImp ( Object eventsource, TransitionTable transitiontable, 
                    Object initialstate )
    {
        transitiontable_ = transitiontable;
        state_ = initialstate;
        enterlisteners_ = new Hashtable();
        preenterlisteners_ = new Hashtable();
        transitionlisteners_ = new Hashtable();
        pretransitionlisteners_ = new Hashtable();
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

    protected synchronized  void addEnterListener(Hashtable listeners, 
			      Object lstnr,
			      Object state)
    {
        Hashtable lstnrs = ( Hashtable ) listeners.get(state);
        if ( lstnrs == null )
	  lstnrs = new Hashtable();
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
				 Object from,
				 Object to)
    {
        Hashtable lstnrs = ( Hashtable ) listeners.get(from);
        if (lstnrs == null) 
	  lstnrs = new Hashtable();
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

    protected void notifyListeners(Hashtable listeners, Object state, 
			     boolean pre)
    {
        Hashtable lstnrs = null;
        FSMEnterEvent event = new FSMEnterEvent (eventsource_, state);
        synchronized ( this ) {
            
            lstnrs= (Hashtable) listeners.get ( state );
            if ( lstnrs == null ) 
                	return;
            //clone to avoid concurrency effects outside synch block
            //during iteration hereafter
            lstnrs = ( Hashtable ) lstnrs.clone();
        }
        
        //notify OUTSIDE SYNCH to minimize deadlocks
        Enumeration enumm=lstnrs.keys();
        while (enumm.hasMoreElements()) {
            	Object listener=enumm.nextElement();
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

    protected void notifyListeners ( Hashtable listeners, Object from ,
			       Object to , boolean pre )
    {
        FSMTransitionEvent event = new FSMTransitionEvent (eventsource_, from, to );
        Hashtable lstnrs = null;
        Hashtable  tolstnrs = null;
        synchronized ( this ) {
            lstnrs = ( Hashtable ) listeners.get( from );
            if ( lstnrs == null ) 
                return;
            tolstnrs = ( Hashtable ) lstnrs.get( to );
            if ( tolstnrs == null )
                return;
            
            //clone to avoid concurrency effects 
            //during iteration outside synch block
            lstnrs = ( Hashtable ) lstnrs.clone();
            tolstnrs = ( Hashtable ) tolstnrs.clone();
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

    public Object getState()
    {
    	//Note: this method should NOT be synchronized on the FSM itself, to avoid deadlocks
    	//in re-entrant 2PC calls!
    	Object ret = null;
    	//don't synch on FSM -> use latch object instead
    	synchronized ( stateLatch_ ) {
    		ret = state_;
    	}
        return ret;
    }
    
    private void setStateObject ( Object state )
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
    
    public void setState(Object state) 
        throws IllegalStateException
    {
    		Object oldstate = null;
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

    public void addFSMEnterListener(FSMEnterListener lstnr, Object state) 
    {
        addEnterListener(enterlisteners_ , lstnr , state);
       
    }

     
    /**
     *@see com.atomikos.finitestates.FSMPreEnterEventSource
     */

    public void addFSMPreEnterListener(FSMPreEnterListener lstnr, 
			         Object state)
    {
        addEnterListener(preenterlisteners_ , lstnr , state);
    }
 
    /**
     *@see com.atomikos.finitestates.FSMTransitionEventSource
     */
    

    public void addFSMTransitionListener(FSMTransitionListener lstnr,
				 Object from, Object to)
    { 
        addTransitionListener ( transitionlisteners_ , lstnr , from , to );
    }
 
    /**
     *@see com.atomikos.finitestates.FSMPreTransitionEventSource
     */
    
    public void addFSMPreTransitionListener(FSMPreTransitionListener lstnr,
				    Object from, Object to)
    {
        addTransitionListener( pretransitionlisteners_ , lstnr , from , to );
    }


   
  

}

