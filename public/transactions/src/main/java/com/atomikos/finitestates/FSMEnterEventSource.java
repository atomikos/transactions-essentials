/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import com.atomikos.recovery.TxState;


public interface FSMEnterEventSource extends Stateful
{
	
	/**
	 *Add an enter event listener. 
	 *@param l The listener.
	 *@param state The state to listen on.
	 *
	 */
	 
	public void addFSMEnterListener(FSMEnterListener l, TxState state);
	
}
