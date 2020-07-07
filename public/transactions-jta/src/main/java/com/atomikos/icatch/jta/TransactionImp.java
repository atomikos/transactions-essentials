/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.jta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;

/**
 * Implementation of the javax.transaction.Transaction interface.
 */

class TransactionImp implements Transaction {
	private static final Logger LOGGER = LoggerFactory
			.createLogger(TransactionImp.class);

	private static void rethrowAsJtaRollbackException(String msg, Throwable cause)
			throws javax.transaction.RollbackException {
		javax.transaction.RollbackException ret = new javax.transaction.RollbackException(
				msg);
		ret.initCause(cause);
		throw ret;
	}

	private static void rethrowAsJtaHeuristicMixedException(String msg, Throwable cause)
			throws javax.transaction.HeuristicMixedException {
		javax.transaction.HeuristicMixedException ret = new javax.transaction.HeuristicMixedException(
				msg);
		ret.initCause(cause);
		throw ret;
	}

	private static void rethrowAsJtaHeuristicRollbackException(String msg,
			Throwable cause)
			throws javax.transaction.HeuristicRollbackException {
		javax.transaction.HeuristicRollbackException ret = new javax.transaction.HeuristicRollbackException(
				msg);
		ret.initCause(cause);
		throw ret;
	}

	private CompositeTransaction compositeTransaction;

	private Map<XAResourceKey, XAResourceTransaction> xaResourceToResourceTransactionMap_;


	TransactionImp(CompositeTransaction ct) {
		this.compositeTransaction = ct;
		this.xaResourceToResourceTransactionMap_ = new HashMap<XAResourceKey, XAResourceTransaction>();
	}

	CompositeTransaction getCT() {
		return this.compositeTransaction;
	}

	private synchronized void addXAResourceTransaction(
			XAResourceTransaction restx, XAResource xares) {
		this.xaResourceToResourceTransactionMap_.put(new XAResourceKey(xares),
				restx);
	}

	private void assertActiveOrSuspended(XAResourceTransaction restx) {
		if (!(restx.isActive() || restx.isXaSuspended())) {
			LOGGER.logWarning("Unexpected resource transaction state for " + restx);
		}
	}

	private synchronized XAResourceTransaction findXAResourceTransaction(
			XAResource xares) {
		XAResourceTransaction ret = null;
		ret = this.xaResourceToResourceTransactionMap_.get(new XAResourceKey(
				xares));
		if (ret != null)
			assertActiveOrSuspended(ret);
		return ret;
	}

	private synchronized void removeXAResourceTransaction(XAResource xares) {
		this.xaResourceToResourceTransactionMap_
				.remove(new XAResourceKey(xares));
	}

	/**
	 * @see javax.transaction.Transaction
	 */

	@Override
	public void registerSynchronization(javax.transaction.Synchronization s)
			throws java.lang.IllegalStateException,
			javax.transaction.SystemException {
		try {
			Sync2Sync adaptor = new Sync2Sync(s);
			this.compositeTransaction.registerSynchronization(adaptor);
		} catch (SysException se) {
			String msg = "Unexpected error during registerSynchronization";
			LOGGER.logWarning(msg, se);
			throw new ExtendedSystemException(msg, se);
		}

	}

	/**
	 * @see javax.transaction.Transaction
	 */

	@Override
	public int getStatus() {
		TxState state = this.compositeTransaction.getState();
		switch (state) {
			case IN_DOUBT:
				return Status.STATUS_PREPARED;
			case PREPARING:
				return Status.STATUS_PREPARING;
			case ACTIVE:
				return Status.STATUS_ACTIVE;
			case MARKED_ABORT:
				return Status.STATUS_MARKED_ROLLBACK;
			case COMMITTING:
				return Status.STATUS_COMMITTING;
			case ABORTING:
				return Status.STATUS_ROLLING_BACK;
			case COMMITTED:
				return Status.STATUS_COMMITTED;
			case ABORTED:
				return Status.STATUS_ROLLEDBACK;
			default:
				return Status.STATUS_UNKNOWN;
		}
	}

	/**
	 * @see javax.transaction.Transaction.
	 */

	@Override
	public void commit() throws javax.transaction.RollbackException,
			javax.transaction.HeuristicMixedException,
			javax.transaction.HeuristicRollbackException,
			javax.transaction.SystemException, java.lang.SecurityException {
		try {
			this.compositeTransaction.commit();
		} catch (HeurHazardException hh) {
			rethrowAsJtaHeuristicMixedException(hh.getMessage(), hh);
		} catch (HeurMixedException hm) {
			rethrowAsJtaHeuristicMixedException(hm.getMessage(), hm);
		} catch (SysException se) {
			LOGGER.logError(se.getMessage(), se);
			throw new ExtendedSystemException(se.getMessage(), se);
		} catch (com.atomikos.icatch.RollbackException rb) {
			// see case 29708: all statements have been closed
			String msg = rb.getMessage();
			Throwable cause = rb.getCause();
			if (cause == null)
				cause = rb;
			rethrowAsJtaRollbackException(msg, cause);
		}
	}

	/**
	 * @see javax.transaction.Transaction.
	 */

	@Override
	public void rollback() throws IllegalStateException, SystemException {
		try {
			this.compositeTransaction.rollback();
		} catch (SysException se) {
			LOGGER.logError(se.getMessage(), se);
			throw new ExtendedSystemException(se.getMessage(), se);
		}

	}

	/**
	 * @see javax.transaction.Transaction.
	 */

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		this.compositeTransaction.setRollbackOnly();
	}

	/**
	 * @see javax.transaction.Transaction.
	 */

	@Override
	public boolean enlistResource(XAResource xares)
			throws javax.transaction.RollbackException,
			javax.transaction.SystemException, IllegalStateException {
		TransactionalResource res = null;
		XAResourceTransaction restx = null;
		int status = getStatus();
		switch (status) {
		case Status.STATUS_MARKED_ROLLBACK:
		case Status.STATUS_ROLLEDBACK:
		case Status.STATUS_ROLLING_BACK:
			String msg = "Transaction rollback - enlisting more resources is useless.";
			LOGGER.logWarning(msg);
			throw new javax.transaction.RollbackException(msg);
		case Status.STATUS_COMMITTED:
		case Status.STATUS_PREPARED:
		case Status.STATUS_UNKNOWN:
			msg = "Enlisting more resources is no longer permitted: transaction is in state "
					+ this.compositeTransaction.getState();
			LOGGER.logWarning(msg);
			throw new IllegalStateException(msg);
		}

		XAResourceTransaction suspendedXAResourceTransaction = findXAResourceTransaction(xares);

		if (suspendedXAResourceTransaction != null) {

			if (!suspendedXAResourceTransaction.isXaSuspended()) {
				String msg = "The given XAResource instance is being enlisted a second time without delist in between?";
				LOGGER.logWarning(msg);
				throw new IllegalStateException(msg);
			}

			// note: for suspended XAResources, the lookup MUST SUCCEED
			// since the TMRESUME must be called on the SAME XAResource
			// INSTANCE, and lookup also works on the instance level
			try {
				suspendedXAResourceTransaction.setXAResource(xares);
				suspendedXAResourceTransaction.xaResume();
			} catch (XAException xaerr) {
				if (XAException.XA_RBBASE <= xaerr.errorCode
						&& xaerr.errorCode <= XAException.XA_RBEND)
					rethrowAsJtaRollbackException(
							"Transaction was already rolled back inside the back-end resource. Further enlists are useless.",
							xaerr);
				throw new ExtendedSystemException(
						"Unexpected error during enlist", xaerr);
			}

		} else {

			res = findRecoverableResourceForXaResource(xares);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.logDebug("enlistResource ( " + xares
						+ " ) with transaction " + toString());
			}

			if (res == null) {
				String msg = "There is no registered resource that can recover the given XAResource instance. "
						+ "\n"
						+ "Please register a corresponding resource first.";
				LOGGER.logWarning(msg);
				throw new javax.transaction.SystemException(msg);
			}

			try {
				restx = (XAResourceTransaction) res
						.getResourceTransaction(this.compositeTransaction);

				// next, we MUST set the xa resource again,
				// because ONLY the instance we got as argument
				// is available for use now !
				// older instances (set in restx from previous sibling)
				// have connections that may be in reuse already
				// ->old xares not valid except for 2pc operations

				restx.setXAResource(xares);
				restx.resume();
			} catch (ResourceException re) {
				throw new ExtendedSystemException(
						"Unexpected error during enlist", re);
			} catch (RuntimeException e) {
				throw e;
			}

			addXAResourceTransaction(restx, xares);
		}

		return true;
	}

	private TransactionalResource findRecoverableResourceForXaResource(
			XAResource xares) {
		TransactionalResource ret = null;
		XATransactionalResource xatxres;

		synchronized (Configuration.class) {
			// synchronized to avoid case 61740 and 142795
			
			Collection<RecoverableResource> resources = Configuration.getResources();
			for (RecoverableResource rres : resources) {
				if (rres instanceof XATransactionalResource) {
					xatxres = (XATransactionalResource) rres;
					if (xatxres.usesXAResource(xares))
						ret = xatxres;
				}
			}

		}

		return ret;
	}

	/**
	 * @see javax.transaction.Transaction.
	 */

	@Override
	public boolean delistResource(XAResource xares, int flag)
			throws java.lang.IllegalStateException,
			javax.transaction.SystemException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("delistResource ( " + xares + " ) with transaction "
					+ toString());
		}

		XAResourceTransaction active = findXAResourceTransaction(xares);
		// NOTE: the lookup MUST have succeeded since the delist must be
		// done by the same XAResource INSTANCE as the enlist before,
		// and lookup also uses instance comparison.
		if (active == null) {
			String msg = "Illegal attempt to delist an XAResource instance that was not previously enlisted.";
			LOGGER.logWarning(msg);
			throw new IllegalStateException(msg);
		}

		if (flag == XAResource.TMSUCCESS || flag == XAResource.TMFAIL) {

			try {
				active.suspend();
			} catch (ResourceException re) {
				throw new ExtendedSystemException(
						"Error in delisting the given XAResource", re);
			}
			removeXAResourceTransaction(xares);
			if (flag == XAResource.TMFAIL)
				setRollbackOnly();

		} else if (flag == XAResource.TMSUSPEND) {
			try {
				active.xaSuspend();
			} catch (XAException xaerr) {
				throw new ExtendedSystemException(
						"Error in delisting the given XAResource", xaerr);
			}

		} else {
			String msg = "Unknown delist flag: " + flag;
			LOGGER.logWarning(msg);
			throw new javax.transaction.SystemException(msg);
		}
		return true;
	}

	/**
	 * Compares to another object.
	 * 
	 * @param o
	 *            The other object to compare to.
	 * 
	 * @return boolean True iff the underlying tx is the same.
	 */

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TransactionImp))
			return false;
		TransactionImp other = (TransactionImp) o;
		return this.compositeTransaction.isSameTransaction(other.compositeTransaction);
	}

	/**
	 * Computes a hash value for the object.
	 * 
	 * @return int The hash value.
	 */

	@Override
	public int hashCode() {
		return this.compositeTransaction.hashCode();
	}

	@Override
	public String toString() {
		return this.compositeTransaction.getTid().toString();
	}

	void suspendEnlistedXaResources() throws ExtendedSystemException {
		// cf case 61305
		Iterator<XAResourceTransaction> xaResourceTransactions = this.xaResourceToResourceTransactionMap_
				.values().iterator();
		while (xaResourceTransactions.hasNext()) {
			XAResourceTransaction resTx = xaResourceTransactions.next();
			try {
				resTx.xaSuspend();
			} catch (XAException e) {
				throw new ExtendedSystemException(
						"Error in suspending the given XAResource", e);
			}
		}
	}

	void resumeEnlistedXaReources() throws ExtendedSystemException {
		Iterator<XAResourceTransaction> xaResourceTransactions = this.xaResourceToResourceTransactionMap_
				.values().iterator();
		while (xaResourceTransactions.hasNext()) {
			XAResourceTransaction resTx = xaResourceTransactions.next();
			try {
				resTx.xaResume();
				xaResourceTransactions.remove();
			} catch (XAException e) {
				throw new ExtendedSystemException(
						"Error in resuming the given XAResource", e);
			}
		}
	}
}
