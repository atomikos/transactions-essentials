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
public class AtomikosPlatform implements JtaPlatform {

	private static final long serialVersionUID = 1L;

	private final TransactionManager txMgr;

	private final UserTransaction userTx;

	public AtomikosPlatform() {
		super();
		this.txMgr = new UserTransactionManager();
		this.userTx = new UserTransactionImp();
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
			throw new TransactionException( "Could not determine transaction status", se );
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
