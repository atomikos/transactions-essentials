package com.atomikos.finitestates;

import java.util.EventListener;

/**
*
*
*A listener on FSMEnterEvent occurrences.
*
*/

public interface FSMEnterListener extends EventListener
{
	
	/**
	*Called when the FSM has entered a new state.
	*
	*/

	public void entered(FSMEnterEvent e);
}
