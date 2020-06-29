/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event;

/**
  * Significant core events that are communicated to plugged-in listeners.
  */
public abstract class Event {
	
	public final long eventCreationTimestamp;
	
	protected Event() {
		this.eventCreationTimestamp = System.currentTimeMillis();
	}

}
