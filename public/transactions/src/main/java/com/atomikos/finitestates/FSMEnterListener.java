/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import java.util.EventListener;



public interface FSMEnterListener extends EventListener
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
	
	/**
	*Called when the FSM has entered a new state.
	*
	*/

	public void entered(FSMEnterEvent e);
}
