/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate4;

import com.atomikos.icatch.jta.J2eeTransactionManager;
import com.atomikos.icatch.jta.J2eeUserTransaction;

/**
 * 
 * Hibernate4 JTA Platform which is using the J2eeTransactionManager and the
 * J2eeUserTransaction.
 * 
 * Use this one, if you want to integrate Hibernate4 and Atomikos is used in a
 * J2EE container environment or externally managed via Spring.
 * 
 */
public class AtomikosJ2eePlatform extends AbstractAtomikosPlatform {

	public AtomikosJ2eePlatform() {
		super();
		this.txMgr = new J2eeTransactionManager();
		this.userTx = new J2eeUserTransaction();
	}
}
