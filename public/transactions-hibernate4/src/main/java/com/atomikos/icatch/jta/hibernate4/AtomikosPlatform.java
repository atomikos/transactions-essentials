/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate4;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;


/**
 * 
 * Hibernate4 JTA Platform which is using the standard UserTransactionManager and the standard UserTransactionImp object.
 * 
 * If you are looking for J2EE container managed transactions or using an external infrastructure like Spring to setup
 * them, use the {@link AtomikosJ2eePlatform} to integrate Hibernate4.
 * 
 * @author tkrah
 *
 */
public class AtomikosPlatform extends AbstractJtaPlatform {
  
  /**
   * SUID.
   */
  private static final long serialVersionUID = 1L;
  
  
  private final TransactionManager txMgr;
  
  private final UserTransaction userTx;
  
  public AtomikosPlatform() {
    super();
    this.txMgr = new UserTransactionManager();
    this.userTx = new UserTransactionImp();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected TransactionManager locateTransactionManager() {
    return this.txMgr;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  protected UserTransaction locateUserTransaction() {
    return this.userTx;
  }
  
}
