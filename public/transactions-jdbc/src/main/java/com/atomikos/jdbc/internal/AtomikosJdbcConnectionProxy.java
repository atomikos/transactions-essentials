/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.Connection;
import java.sql.SQLException;

import com.atomikos.datasource.xa.session.InvalidSessionHandleStateException;
import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;
import com.atomikos.util.Proxied;

public class AtomikosJdbcConnectionProxy extends AbstractJdbcConnectionProxy {

	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJdbcConnectionProxy.class);

	private final SessionHandleState sessionHandleState;
	private final boolean localTransactionMode;

	public AtomikosJdbcConnectionProxy(Connection delegate, SessionHandleState sessionHandleState, boolean localTransactionMode) {
		super(delegate);
		this.sessionHandleState = sessionHandleState;
		sessionHandleState.notifySessionBorrowed();
		this.localTransactionMode = localTransactionMode;
	}

	private CompositeTransactionManager getCompositeTransactionManager() {
		CompositeTransactionManager ret = Configuration.getCompositeTransactionManager();
		if (ret == null) {
			LOGGER.logWarning(this + ": WARNING: transaction manager not running?");
		}
		return ret;
	}

	@Override
	protected boolean isEnlistedInGlobalTransaction() {
		CompositeTransactionManager compositeTransactionManager = getCompositeTransactionManager();
		if (compositeTransactionManager == null) {
			return false; // TM is not running, we can only be in local TX mode
		}
		CompositeTransaction ct = compositeTransactionManager.getCompositeTransaction();
		return sessionHandleState.isActiveInTransaction(ct);
	}

	@Override
    protected void updateTransactionContext() throws SQLException {
		try {
			enlist();
		} catch (Exception e) {
			// fix for bug 25678
			sessionHandleState.notifySessionErrorOccurred();
			LOGGER.logWarning("Error enlisting in transaction - connection might be broken? Please check the logs for more information...", e);
			throw e;
		}
	}

	/**
	 * Enlist if necessary
	 * 
	 * @return True if a JTA transaction was found, false otherwise.
	 *
	 * @throws AtomikosSQLException
	 */
	private boolean enlist() throws AtomikosSQLException {
		boolean ret = false;
		try {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.logTrace(this + ": notifyBeforeUse " + sessionHandleState);
			}
			CompositeTransaction ct = null;
			CompositeTransactionManager ctm = getCompositeTransactionManager();
			if (ctm != null) {
				ct = ctm.getCompositeTransaction();
				// first notify the session handle - see case 27857
				sessionHandleState.notifyBeforeUse(ct);
				if (ct != null && TransactionManagerImp.isJtaTransaction(ct)) {
					ret = true;
					if (LOGGER.isTraceEnabled()) {
						LOGGER.logTrace(this + ": detected transaction " + ct);
					}
					if (ct.getState().equals(TxState.ACTIVE)) {
						ct.registerSynchronization(new JdbcRequeueSynchronization(this, ct));
					} else {
						AtomikosSQLException.throwAtomikosSQLException(
								"The transaction has timed out - try increasing the timeout if needed");
					}
				} else {
				    if (!localTransactionMode) {
				        AtomikosSQLException.throwAtomikosSQLException("A JTA transaction is required but none was found - please start one first (or set localTransactionMode=true to allow JDBC transactions)");
				    }
				}
			}

		} catch (InvalidSessionHandleStateException ex) {
			AtomikosSQLException.throwAtomikosSQLException(ex.getMessage(), ex);
		}
		return ret;
	}

	@Proxied
	public void close() throws SQLException {
		forceCloseAllPendingStatements(false);
		markClosed();
		sessionHandleState.notifySessionClosed();
	}

	private class JdbcRequeueSynchronization implements Synchronization {

		private CompositeTransaction compositeTransaction;
		private AtomikosJdbcConnectionProxy proxy;
		private boolean afterCompletionDone;

		public JdbcRequeueSynchronization(AtomikosJdbcConnectionProxy proxy,
				CompositeTransaction compositeTransaction) {
			this.compositeTransaction = compositeTransaction;
			this.proxy = proxy;
			this.afterCompletionDone = false;
		}

		public void afterCompletion(TxState state) {

			if (afterCompletionDone) {
				return;
			}

			if (state == TxState.ABORTING) {
				// see bug 29708: close all pending statements to avoid reuse
				// outside timed-out tx scope
				forceCloseAllPendingStatements(true);
			}

			if (state == TxState.TERMINATED || state.isHeuristic()) {
				// connection is reusable!
				if (LOGGER.isTraceEnabled()) {
					LOGGER.logTrace(proxy + ": detected termination of transaction " + compositeTransaction);
				}
				sessionHandleState.notifyTransactionTerminated(compositeTransaction);
				afterCompletionDone = true;
				forceCloseAllPendingStatements(false); // see case 73007 and 84252
			}

		}

		public void beforeCompletion() {
		}

		@Override
		public boolean equals(Object other) {
			// override equals: synchronizations for the same tx are equal
			// to avoid receiving double notifications on termination!
			boolean ret = false;
			if (other instanceof JdbcRequeueSynchronization) {
				JdbcRequeueSynchronization o = (JdbcRequeueSynchronization) other;
				ret = this.compositeTransaction.isSameTransaction(o.compositeTransaction);
			}
			return ret;
		}

		public int hashCode() {
			return compositeTransaction.hashCode();
		}
	}

    @Override
    protected void handleInvocationException(Throwable e) throws Throwable {
        sessionHandleState.notifySessionErrorOccurred();
        throw e;
    }
    
    @Override
    public String toString() {
        return "atomikosJdbcConnectionProxy (state = " + sessionHandleState + ") for vendor instance " + delegate;
    }

}
