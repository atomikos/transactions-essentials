package com.atomikos.finitestates;

import java.util.EventObject;

/**
*
*
*Events signalling a transition of a FSM.
*/
public class FSMTransitionEvent extends EventObject{
	protected Object from,to;

	public FSMTransitionEvent(Object source,Object fromState,Object toState){
		super(source);
		from=fromState;
		to=toState;
	}
	/**
	*The state that was left.
	*/
	public Object fromState(){
		return from;
	}
	
	/**
	*The state that was moved to.
	*/
	public Object toState(){
		return to;
	}
}
