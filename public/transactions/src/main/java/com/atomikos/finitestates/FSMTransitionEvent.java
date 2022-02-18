/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import java.util.EventObject;

import com.atomikos.recovery.TxState;

public class FSMTransitionEvent extends EventObject{

	private static final long serialVersionUID = 7629493293234798149L;

	protected TxState from,to;

	public FSMTransitionEvent(Object source,TxState fromState,TxState toState){
		super(source);
		from=fromState;
		to=toState;
	}
	
	public TxState fromState(){
		return from;
	}

	public TxState toState(){
		return to;
	}
}
