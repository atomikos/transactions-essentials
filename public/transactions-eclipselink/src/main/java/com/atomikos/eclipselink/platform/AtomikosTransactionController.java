/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.eclipselink.platform;

import javax.transaction.TransactionManager;

import org.eclipse.persistence.transaction.JTATransactionController;

import com.atomikos.icatch.jta.UserTransactionManager;

public class AtomikosTransactionController extends JTATransactionController {

	private UserTransactionManager utm;

	public AtomikosTransactionController() {
		utm = new UserTransactionManager();
	}
	/**
	 * INTERNAL: Obtain and return the JTA TransactionManager on this platform
	 */
	protected TransactionManager acquireTransactionManager() throws Exception {
		return utm;
	}

	@Override
	public TransactionManager getTransactionManager() {

		return utm;
	}

}
