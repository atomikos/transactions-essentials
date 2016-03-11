/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import java.util.EventListener;


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
