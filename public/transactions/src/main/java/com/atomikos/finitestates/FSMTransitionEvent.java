/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
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

	protected final Transition transition;

	public FSMTransitionEvent(Object source,Transition transition){
		super(source);
		this.transition = transition;
	}
	
	public TxState fromState(){
		return transition.from;
	}

	public TxState toState(){
		return transition.to;
	}
}
