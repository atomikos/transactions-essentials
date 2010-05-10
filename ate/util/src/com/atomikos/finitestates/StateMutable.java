package com.atomikos.finitestates;

/**
*
*
*Abstract representation of an object that has a state and this state 
*can be set.
*A state can be anything.
*/
public interface StateMutable extends  Stateful{
	
	/**
	*To set a new state.
	*
	*@param s The new state.
	*@exception IllegalStateException if the new state transition to 
	*the new state is not allowed.
	*/

	public void setState(Object s) throws IllegalStateException;
}

