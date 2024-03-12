/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import com.atomikos.recovery.TxState;


public interface StateMutable extends  Stateful{
	
	/**
	*@exception IllegalStateException if the new state transition to 
	*the new state is not allowed.
	*/

	public void setState(TxState s) throws IllegalStateException;
}

