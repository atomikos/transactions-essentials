/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import com.atomikos.recovery.TxState;

import java.io.Serializable;

public interface Stateful extends Serializable
{
	/**
	 *   @return The object representing the state.
	 */
	public TxState getState();
}

