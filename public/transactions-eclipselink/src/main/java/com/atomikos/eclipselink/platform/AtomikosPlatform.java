/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.eclipselink.platform;

import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

import com.atomikos.util.Atomikos;

public class AtomikosPlatform extends ServerPlatformBase {

	public AtomikosPlatform(DatabaseSession newDatabaseSession) {
		super(newDatabaseSession);
		disableRuntimeServices();
	}

	@Override
	public Class<?> getExternalTransactionControllerClass() {

		return AtomikosTransactionController.class;
	}

	@Override
	protected void initializeServerNameAndVersion() {
		this.serverNameAndVersion="Atomikos: "+Atomikos.VERSION;
	}
}
