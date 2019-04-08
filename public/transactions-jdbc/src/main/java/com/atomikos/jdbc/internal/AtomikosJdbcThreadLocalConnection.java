/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.SQLException;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.Proxied;

public class AtomikosJdbcThreadLocalConnection extends AbstractJdbcConnectionProxy implements JtaAwareNonXaConnection {

    private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJdbcThreadLocalConnection.class);

    private int useCount = 0;

    private CompositeTransaction transaction;

    private boolean stale;

    private final AtomikosNonXAPooledConnection pooledConnection;

    private boolean originalAutoCommitState;

    private AtomikosNonXAParticipant participant; // null for non-JTA use

    private final boolean readOnly;

    private final String resourceName;
    
    private final boolean ignoreJtaTransactions;

    public AtomikosJdbcThreadLocalConnection(AtomikosNonXAPooledConnection pooledConnection, String resourceName) {
        this(pooledConnection, resourceName, false);
    }


    public AtomikosJdbcThreadLocalConnection(AtomikosNonXAPooledConnection pooledConnection,
            String uniqueResourceName, boolean ignoreJtaTransactions) {
        super(pooledConnection.getConnection());
        this.pooledConnection = pooledConnection;
        this.readOnly = pooledConnection.getReadOnly();
        this.resourceName = uniqueResourceName;
        this.ignoreJtaTransactions = ignoreJtaTransactions;
    }


    @Override
    protected void updateTransactionContext() throws SQLException {
        CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
        if (ctm == null || ignoreJtaTransactions) {
            return;
        }

        CompositeTransaction ct = ctm.getCompositeTransaction();
        if (ct != null && ct.getProperty(TransactionManagerImp.JTA_PROPERTY_NAME) != null) {

            // if we are already in another (parent) tx then reject this,
            // because nested tx rollback can not be supported!!!
            if (isEnlistedInGlobalTransaction() && !isInTransaction(ct))
                AtomikosSQLException.throwAtomikosSQLException("Connection accessed by transaction " + ct.getTid()
                        + " is already in use in another transaction: " + transaction.getTid()
                        + " Non-XA connections are not compatible with nested transaction use.");

            setTransaction(ct);
            if (participant == null) {
                // make sure we add a participant for commit/rollback
                // notifications
                participant = new AtomikosNonXAParticipant(this, resourceName);
                participant.setReadOnly(readOnly);
                ct.addParticipant(participant);
                originalAutoCommitState = this.delegate.getAutoCommit();
                this.delegate.setAutoCommit(false);

            }
        } else {
            // the current thread has NO tx, which means none was started (OK)
            // or
            // the previous one was terminated by the application. In that case,
            // check that there was no ACTIVE rollback on timeout (meaning that
            // transactionTerminated was never called!!!)
            if (isEnlistedInGlobalTransaction()) {
                transactionTerminated(false);
            }
        }
    }

    public void transactionTerminated(boolean commit) throws SQLException {

        // delegate commit or rollback to the underlying connection
        try {
            if (commit) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.logDebug(this + ": committing on connection...");
                }
                this.delegate.commit();

            } else {
                // see case 84252
                forceCloseAllPendingStatements(false);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.logDebug(this + ": transaction aborting - "
                            + "pessimistically closing all pending statements to avoid autoCommit after timeout");
                    LOGGER.logDebug(this + ": rolling back on connection...");
                }
                this.delegate.rollback();

            }
        } catch (SQLException e) {
            // make sure that reuse in pool is not possible
            pooledConnection.setErroneous();
            String msg = "Error in commit on vendor connection";
            if (!commit) {
                msg = "Error in rollback on vendor connection";
            }
            AtomikosSQLException.throwAtomikosSQLException(msg, e);
        } finally {
            // reset attributes for next tx
            resetForNextTransaction();
            // put connection in pool if no longer used
            // note: if erroneous then the pool will destroy the connection (cf
            // case 30752)
            // which seems desirable to avoid pool exhaustion
            markForReuseIfPossible();
            // see case 30752: resetting autoCommit should not be done here:
            // connection may have been reused already!
        }

    }

    private void resetForNextTransaction() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.logDebug(this + ": resetting autoCommit to " + originalAutoCommitState);
            }
            // see case 24567
            this.delegate.setAutoCommit(originalAutoCommitState);
        } catch (Exception ex) {
            LOGGER.logError("Failed to reset original autoCommit state: " + ex.getMessage(), ex);
        }
        setTransaction(null);
        participant = null;

    }

    private void setTransaction(CompositeTransaction tx) {
        this.transaction = tx;
    }

    /**
     * Checks if the connection is being used on behalf of the given
     * transaction.
     *
     * @param ct
     * @return
     */

    boolean isInTransaction(CompositeTransaction ct) {
        boolean ret = false;
        // See case 29060 and 28683 :COPY attribute to avoid race conditions
        // with NPE results
        CompositeTransaction tx = transaction;
        if (tx != null && ct != null) {
            ret = tx.isSameTransaction(ct);
        }
        return ret;
    }

    @Proxied
    public void close() throws SQLException {
        decUseCount();
    }

    public void incUseCount() {
        useCount++;
    }

    private void decUseCount() {
        useCount--;
        markForReuseIfPossible();
    }

    boolean isStale() {
        return stale;
    }

    private void markForReuseIfPossible() {

        if (isAvailableForReuseByPool()) {
            LOGGER.logTrace("ThreadLocalConnection: detected reusability");
            setStale();
            forceCloseAllPendingStatements(false);
            pooledConnection.fireOnXPooledConnectionTerminated();
        } else {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace("ThreadLocalConnection: not reusable yet");
            }
        }

    }

    private void setStale() {
        this.stale = true;
    }

    boolean isAvailableForReuseByPool() {
        return useCount <= 0 && !isEnlistedInGlobalTransaction();
    }

    /**
     * Checks if the connection is being used on behalf the a transaction.
     *
     * @return
     */
    @Override
    protected boolean isEnlistedInGlobalTransaction() {
        return transaction != null;
    }


    @Override
    protected void handleInvocationException(Throwable e) throws Throwable {
        pooledConnection.setErroneous();
        throw e;
    }
    
    @Override
    public String toString() {
        return "atomikosJdbcThreadLocalConnection (isAvailable = " + isAvailableForReuseByPool() + ")  for vendor instance " + delegate; 
    }

}
