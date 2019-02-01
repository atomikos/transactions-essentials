/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool.event;

abstract class PooledConnectionEvent extends ConnectionPoolEvent {

	private static final long serialVersionUID = 1L;
	
	public PooledConnectionEvent(String uniqueResourceName) {
		super(uniqueResourceName);
	}

}