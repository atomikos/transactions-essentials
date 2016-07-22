/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta.hibernate4;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.TransactionException;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformException;

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
public class AtomikosJ2eePlatform implements JtaPlatform {

	private static final long serialVersionUID = 1L;

	private final TransactionManager txMgr;

	private final UserTransaction userTx;

	public AtomikosJ2eePlatform() {
		super();
		this.txMgr = new J2eeTransactionManager();
		this.userTx = new J2eeUserTransaction();
	}

	@Override
	public Object getTransactionIdentifier(Transaction transaction) {
		// generally we use the transaction itself.
		return transaction;
	}

	@Override
	public void registerSynchronization(Synchronization synchronization) {
		try {
			this.txMgr.getTransaction()
					.registerSynchronization(synchronization);
		} catch (Exception e) {
			throw new JtaPlatformException(
					"Could not access JTA Transaction to register synchronization",
					e);
		}
	}

	@Override
	public boolean canRegisterSynchronization() {
		try {
			if (this.txMgr.getTransaction() != null) {
				return this.txMgr.getTransaction().getStatus() == Status.STATUS_ACTIVE;
			}
		} catch (SystemException se) {
			throw new TransactionException(
					"Could not determine transaction status", se);
		}
		return false;
	}

	@Override
	public int getCurrentStatus() throws SystemException {
		return retrieveTransactionManager().getStatus();
	}

	@Override
	public UserTransaction retrieveUserTransaction() {
		return this.userTx;
	}

	@Override
	public TransactionManager retrieveTransactionManager() {
		return this.txMgr;
	}
}
