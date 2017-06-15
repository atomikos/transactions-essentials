/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import java.util.EventListener;



public interface FSMEnterListener extends EventListener {
	
	/**
	 *  Called when the FSM has entered a new state.
	 *
	 */
	public void entered(FSMEnterEvent e);
}
