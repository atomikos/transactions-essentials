/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import java.util.EventObject;

import com.atomikos.recovery.TxState;

public class FSMEnterEvent extends EventObject{

	private static final long serialVersionUID = -7910459829127232977L;
	
	protected TxState newState;

	public FSMEnterEvent(Object source, TxState state){
		super(source);
		newState=state;
	}
	
	public TxState getState(){
		return newState;
	}
}
