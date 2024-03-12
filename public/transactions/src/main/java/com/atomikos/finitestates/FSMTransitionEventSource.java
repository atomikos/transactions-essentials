/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import com.atomikos.recovery.TxState;



public interface FSMTransitionEventSource extends Stateful
{   
    public void addFSMTransitionListener(FSMTransitionListener l,
				 TxState from, TxState to);
	
}
