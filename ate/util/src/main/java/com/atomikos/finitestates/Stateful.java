package com.atomikos.finitestates;

/**
*
*
*Abstract representation of an object that has a state.
*A state can be anything.
*/
public interface Stateful{
	/**
	*@return The object representing the state.
	*/
	public Object getState();
}

