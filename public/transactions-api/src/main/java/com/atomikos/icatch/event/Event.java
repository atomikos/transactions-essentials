/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event;

import java.io.Serializable;

 /**
  * Significant core events that are communicated to the outside world.
  */
public abstract class Event implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final long eventCreationTimestamp;
	
	protected Event() {
		this.eventCreationTimestamp = System.currentTimeMillis();
	}

}
