/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import com.atomikos.recovery.TxState;


public interface FSMPreTransitionEventSource extends Stateful
{
    
    public void addFSMPreTransitionListener(FSMPreTransitionListener l,
    		TxState from, TxState to);

}
