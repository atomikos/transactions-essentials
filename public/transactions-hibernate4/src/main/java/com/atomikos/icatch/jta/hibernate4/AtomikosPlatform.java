/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate4;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * 
 * Hibernate4 JTA Platform which is using the standard UserTransactionManager
 * and the standard UserTransactionImp object.
 * 
 * If you are looking for J2EE container managed transactions or using an
 * external infrastructure like Spring to setup them, use the
 * {@link AtomikosJ2eePlatform} to integrate Hibernate4.
 * 
 */
public class AtomikosPlatform extends AbstractAtomikosPlatform
{
  public AtomikosPlatform() {
		super();

    this.txMgr = new UserTransactionManager();
    this.userTx = new UserTransactionImp();
  }
}
