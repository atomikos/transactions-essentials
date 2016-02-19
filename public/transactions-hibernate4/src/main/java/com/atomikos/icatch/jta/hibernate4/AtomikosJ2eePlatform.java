/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

/**
 * 
 */
package com.atomikos.icatch.jta.hibernate4;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import com.atomikos.icatch.jta.J2eeTransactionManager;
import com.atomikos.icatch.jta.J2eeUserTransaction;


/**
 * 
 * Hibernate4 JTA Platform which is using the J2eeTransactionManager and the J2eeUserTransaction.
 * 
 * Use this one, if you want to integrate Hibernate4 and Atomikos is used in a J2EE container environment
 * or externally managed via Spring.  
 * 
 * @author tkrah
 *
 */
public class AtomikosJ2eePlatform extends AbstractJtaPlatform {
  
  /**
   * SUID.
   */
  private static final long serialVersionUID = 1L;
  
  
  private final TransactionManager txMgr;
  
  private final UserTransaction userTx;
  
  public AtomikosJ2eePlatform() {
    super();
    this.txMgr = new J2eeTransactionManager();
    this.userTx = new J2eeUserTransaction();
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
