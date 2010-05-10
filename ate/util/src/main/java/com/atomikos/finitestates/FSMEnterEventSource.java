package com.atomikos.finitestates;

/**
*
*
*Interface of an FSMEnterEventSource.
*/

public interface FSMEnterEventSource extends Stateful
{
	
	/**
	 *Add an enter event listener. 
	 *@param l The listener.
	 *@param state The state to listen on.
	 *
	 */
	 
	public void addFSMEnterListener(FSMEnterListener l, Object state);
	
}
