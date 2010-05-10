package com.atomikos.finitestates;

import java.util.EventObject;

/**
*
*
*Events signalling the transition of the FSM to a new state.
*/
public class FSMEnterEvent extends EventObject{
	protected Object newState;
	
	public FSMEnterEvent(Object source, Object state){
		super(source);
		newState=state;
	}
	/**
	*The new state that was entered.
	*/
	public Object getState(){
		return newState;
	}
}
