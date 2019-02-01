/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.tcc.rest;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.icatch.RecoveryService;

public class TccRecoverableResource implements RecoverableResource {

	static final String NAME = "TccRecoverableResource";
	private boolean closed = false;
	
	@Override
	public void setRecoveryService(RecoveryService recoveryService)
			throws ResourceException {
	}

	@Override
	public void close() throws ResourceException {
		this.closed = true;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}


	@Override
	public boolean isSameRM(RecoverableResource res) throws ResourceException {
		return getName().equals(res.getName());
	}

	@Override
	public boolean recover(long startOfRecoveryScan) {
		return false;
	}


}
