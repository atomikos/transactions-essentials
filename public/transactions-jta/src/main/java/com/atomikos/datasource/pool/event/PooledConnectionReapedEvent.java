/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool.event;

import com.atomikos.datasource.pool.XPooledConnection;

public class PooledConnectionReapedEvent extends PooledConnectionEvent {

	private static final long serialVersionUID = 1L;

	public PooledConnectionReapedEvent(String uniqueResourceName,
			XPooledConnection pc) {
		super(uniqueResourceName, pc);
	}


}
