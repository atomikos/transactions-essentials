/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool.event;

import com.atomikos.icatch.event.Event;

abstract class ConnectionPoolEvent extends Event {

	private static final long serialVersionUID = 1L;
	
	public String uniqueResourceName;

	protected ConnectionPoolEvent(String uniqueResourceName) {
		this.uniqueResourceName = uniqueResourceName;
	}
}
