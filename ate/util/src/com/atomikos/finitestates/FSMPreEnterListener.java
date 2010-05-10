package com.atomikos.finitestates;

import java.util.EventListener;

/**
*
*
*A listener that wants to be notified BEFORE the new state is entered.
*
*/

public interface FSMPreEnterListener extends EventListener
{
	/**
	*Called BEFORE the FSM enters the new state, so that 
	*the callee is sure that nobody has seen the new state yet.
	*
	*@exception IllegalStateException on failure.
	*The callee can use this to prevent the state change from
	*happening.
	*/

	public void preEnter(FSMEnterEvent e) throws IllegalStateException;
}
