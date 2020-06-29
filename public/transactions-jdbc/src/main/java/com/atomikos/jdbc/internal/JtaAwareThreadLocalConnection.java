/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.jdbc.internal.JdbcNonXAConnectionHandleState.ParticipantRegistrationRequiredException;
import com.atomikos.jdbc.internal.JdbcNonXAConnectionHandleState.ReadOnlyParticipantRegistrationRequiredException;
import com.atomikos.jdbc.internal.JdbcNonXAConnectionHandleState.SubTxAwareParticipantRegistrationRequiredException;
import com.atomikos.jdbc.internal.JdbcNonXAConnectionHandleState.TransactionContextException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.Proxied;

public class JtaAwareThreadLocalConnection extends AbstractJdbcConnectionProxy implements NonXaConnectionProxy {

    private static final Logger LOGGER = LoggerFactory.createLogger(JtaAwareThreadLocalConnection.class);

    private int useCount = 0;

    private boolean stale;

    private final AtomikosNonXAPooledConnection pooledConnection;

    private boolean originalAutoCommitState;

    private final String resourceName;
    
    private final JdbcNonXAConnectionHandleState state;
    
    public JtaAwareThreadLocalConnection(AtomikosNonXAPooledConnection pooledConnection, String resourceName) {
        super(pooledConnection.getConnection());
        this.pooledConnection = pooledConnection;
        this.resourceName =resourceName;
        this.state = new JdbcNonXAConnectionHandleState(pooledConnection.getReadOnly());
    }


    @Override
    protected void updateTransactionContext() throws SQLException {
        CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
        if (ctm == null ) {
            return;
        }

        CompositeTransaction ct = ctm.getCompositeTransaction();
        if (ct != null && TransactionManagerImp.isJtaTransaction(ct)) {

            try {
                state.notifyBeforeUse(ct);
            } catch (ParticipantRegistrationRequiredException e) {
                registerAsParticipantFor(ct);
            } catch (ReadOnlyParticipantRegistrationRequiredException e) {
               registerAsReadOnlyParticipantFor(ct);
            } catch (SubTxAwareParticipantRegistrationRequiredException e) {
                registerAsSubTxAwareParticipantFor(ct);
            } catch (TransactionContextException e) {
                AtomikosSQLException.throwAtomikosSQLException(e.getMessage(), e);
            }

        } else {
            AtomikosSQLException.throwAtomikosSQLException("A JTA transaction is required but none was found - please start one first (or set localTransactionMode=true to allow JDBC transactions)");
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
        state.reset();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.logDebug(this + ": resetting autoCommit to " + originalAutoCommitState);
            }
            // see case 24567
            this.delegate.setAutoCommit(originalAutoCommitState);
        } catch (Exception ex) {
            LOGGER.logError(this + ": failed to reset original autoCommit state: " + ex.getMessage(), ex);
        }

    }


    /**
     * Checks if the connection is being used on behalf of the given
     * transaction.
     *
     * @param ct
     * @return
     */

    boolean isInTransaction(CompositeTransaction ct) {
        return state.isEnlistedInGlobalTransaction(ct);
    }

    @Proxied
    public void close() throws SQLException {
        decUseCount();
        //markClosed(); cf case 179080: don't mark as closed to allow reuse in same transaction
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
            LOGGER.logTrace(this + ": detected reusability");
            setStale();
            forceCloseAllPendingStatements(false);
            pooledConnection.fireOnXPooledConnectionTerminated();
        } else {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.logTrace(this + ": not reusable yet");
            }
        }

    }

    private void setStale() {
        this.stale = true;
    }

    public boolean isAvailableForReuseByPool() {
        return useCount <= 0 && !isEnlistedInGlobalTransaction();
    }

    /**
     * Checks if the connection is being used on behalf the a transaction.
     *
     * @return
     */
    @Override
    protected boolean isEnlistedInGlobalTransaction() {
        return state.isEnlistedInGlobalTransaction();
    }


    @Override
    protected void handleInvocationException(Throwable e) throws Throwable {
        pooledConnection.setErroneous();
        throw e;
    }
    
    @Override
    public String toString() {
        return "jtaAwareThreadLocalConnection (isAvailable = " + isAvailableForReuseByPool() + ")  for vendor instance " + delegate; 
    }
    
    private void registerAsParticipantFor(CompositeTransaction ct) throws SQLException {
        originalAutoCommitState = this.delegate.getAutoCommit();
        this.delegate.setAutoCommit(false);
        AtomikosNonXAParticipant participant = new AtomikosNonXAParticipant(this, resourceName);
        CompositeTransaction localRoot = findLocalRoot(ct);
        localRoot.addParticipant(participant);
        if (localRoot != ct) {
            //first registration happens for a subtransaction => also deal with subtx rollback
            registerAsSubTxAwareParticipantFor(ct);
        }
    }
    
    private void registerAsReadOnlyParticipantFor(CompositeTransaction ct) throws SQLException {
        originalAutoCommitState = this.delegate.getAutoCommit(); 
        this.delegate.setAutoCommit(false);
        CompositeTransaction localRoot = findLocalRoot(ct);
        ReadOnlyParticipant participant = new ReadOnlyParticipant(this);
        localRoot.addSubTxAwareParticipant(participant);
    }

    private void registerAsSubTxAwareParticipantFor(CompositeTransaction ct) throws SQLException {
        boolean supportsSavepoints = false;
        try {
            supportsSavepoints = delegate.getMetaData().supportsSavepoints();
        } catch (Error e) {
            LOGGER.logDebug("Failed to determine savepoint support in JDBC driver - see stacktrace for details", e);
            //ignore, continue without subtx support
        }
        if (!supportsSavepoints) {
            state.subTransactionTerminated(); 
            // no SubTxRollbackParticipant will be added so terminate here already to keep the transaction state clean
            // note: next updateTransactionContext call will try and fail again but that is ok
            AtomikosSQLException.throwAtomikosSQLException("You're trying to use nested transactions but the underlying JDBC driver does not support savepoints - which is required for this to work...");
        }
        SubTxParticipant participant = new SubTxParticipant(ct.getTid(), this);
        ct.addSubTxAwareParticipant(participant);
    }

    private CompositeTransaction findLocalRoot(CompositeTransaction ct) {
        CompositeTransaction ret = ct;
        Stack<CompositeTransaction> parents = ct.getLineage();
        if (parents != null) {
            Stack<CompositeTransaction> parentsClone = (Stack<CompositeTransaction>) parents.clone();
            while (!parentsClone.isEmpty()) {
                CompositeTransaction parent = parentsClone.pop();
                if (parent.isLocal()) {
                    ret = parent;
                }
            }
        }
        return ret;
    }
    
    private static class SubTxParticipant implements SubTxAwareParticipant {
        private Savepoint savepoint;
        private JtaAwareThreadLocalConnection owner;
        
        SubTxParticipant(String tid, JtaAwareThreadLocalConnection owner) throws SQLException {
            this.owner = owner;
            this.savepoint = owner.delegate.setSavepoint(tid);
        }

        @Override
        public void committed(CompositeTransaction transaction) {
            owner.state.subTransactionTerminated();
        }

        @Override
        public void rolledback(CompositeTransaction transaction) {
            owner.state.subTransactionTerminated();
            try {
                owner.delegate.rollback(savepoint);
            } catch (SQLException e) {
                LOGGER.logWarning("Failed to rollback to savepoint", e);
                owner.pooledConnection.setErroneous();
            }
        }
        
        @Override
        public boolean equals(Object o) {
            boolean ret = false;
            if (o instanceof SubTxParticipant) {
                SubTxParticipant other = (SubTxParticipant) o;
                ret = owner == other.owner;
            }
            return ret;
        }
        
        @Override
        public int hashCode() {
            return owner.hashCode();
        }
    }
    
    private static class ReadOnlyParticipant implements SubTxAwareParticipant {
        
        JtaAwareThreadLocalConnection owner;
        
        ReadOnlyParticipant(JtaAwareThreadLocalConnection owner) {
            this.owner = owner;
        }

        @Override
        public void rolledback(CompositeTransaction transaction) {
            try {
                owner.transactionTerminated(false);
            } catch (SQLException e) {
                LOGGER.logWarning("Unexpected error during rollback", e);
            }
        }

        @Override
        public void committed(CompositeTransaction transaction) {
            try {
                owner.transactionTerminated(true);
            } catch (SQLException e) {
                LOGGER.logWarning("Unexpected error during commit", e);
            }

        }

        @Override
        public boolean equals(Object o) {
            boolean ret = false;
            if (o instanceof ReadOnlyParticipant) {
                ReadOnlyParticipant other = (ReadOnlyParticipant) o;
                ret = owner == other.owner;
            }
            return ret;
        }
        
        @Override
        public int hashCode() {
            return owner.hashCode();
        }

    }

	@Override
	protected Class<Connection> getRequiredInterfaceType() {
		return Connection.class;
	}

}
