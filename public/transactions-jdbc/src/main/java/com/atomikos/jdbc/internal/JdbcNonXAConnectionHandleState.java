/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.util.HashSet;
import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;

/**
  *  State management for Non-XA JDBC connections borrowed from the pool.
  */

class JdbcNonXAConnectionHandleState {
    

    private CompositeTransaction currentTransaction;
    private boolean readOnly;
    private HashSet<CompositeTransaction> history;
    
    public JdbcNonXAConnectionHandleState() {
        this(false);
    }
    
    public JdbcNonXAConnectionHandleState(boolean readOnly) {
        this.readOnly = readOnly;
        this.history = new HashSet<CompositeTransaction>();
    }

    private void registerTransaction(CompositeTransaction ct) {
        currentTransaction = ct;
        history.add(ct);
    }

    private boolean registeredBefore(CompositeTransaction ct) {
        return ct != null && history.contains(ct);
    }

    /**
     * Updates the state to register the fact that we are going to use this connection
     * as part of the given transaction. If a new transaction is detected, then a 
     * suitable exception will be throw for the caller to register participants.
     * 
     * @param ct The transaction detected for the calling thread (assumed to be not null). 
     * Acceptable values are:
     * <ol>
     * <li>A new transaction (where currently none is registered)</li>
     * <li>The same transaction as currently registered</li>
     * <li>A subtransaction of the current transaction</li>
     * </ol>
     * 
     * All other cases throw InvalidTransactionContextException
     * 
     * 
     * @throws TransactionContextException Catch-all exception
     * @throws ParticipantRegistrationRequiredException If the caller should register a participant at the level of the local root transaction.
     * @throws ReadOnlyParticipantRegistrationRequiredException If the caller should register a readOnly participant at the level of the local root transaction.
     * @throws SubTxAwareParticipantRegistrationRequiredException If the caller should register a SubTxAwareParticipant for rollback to savepoint.
     * @throws InvalidTransactionContextException If the supplied transaction cannot be accepted for data integrity reasons.
     */
    public void notifyBeforeUse(CompositeTransaction ct) throws TransactionContextException {
       
        if (ct.isSameTransaction(currentTransaction)) {
            if (registeredBefore(ct)) {
                return; //do nothing: we're already doing work for this transaction
            } else {
                CompositeTransaction localRoot = findLocalRoot(ct);
                if (localRoot != ct) { //subtx but not registered before
                    registerTransaction(ct);
                    throw new SubTxAwareParticipantRegistrationRequiredException();
                }
                
            }
        } else if (ct.isDescendantOf(currentTransaction)) {
            currentTransaction = ct;
            registerTransaction(ct);
            throw new SubTxAwareParticipantRegistrationRequiredException();
        } else if (currentTransaction == null){
            currentTransaction = ct;
            registerTransaction(ct);
            if (readOnly) {
                throw new ReadOnlyParticipantRegistrationRequiredException();
            } else {
                throw new ParticipantRegistrationRequiredException();                
            }   
        } else {
            throw new InvalidTransactionContextException("Connection accessed by transaction " + ct.getTid() + 
                    "is already in use by another transaction: " + currentTransaction.getTid());
        }
        
    }

    /**
     * Updates the state to register the termination of the current transaction. 
     * If the current transaction is a subtransaction, then the parent transaction will become the new
     * current transaction as far as this state object is concerned.
     */
    
    public void subTransactionTerminated() {
        Stack<CompositeTransaction> parentTransactions = currentTransaction.getLineage();
        currentTransaction = null;
        if (parentTransactions != null && !parentTransactions.isEmpty()) {
            CompositeTransaction parent = parentTransactions.peek();
            currentTransaction = parent; 
        }
    }

    public boolean isEnlistedInGlobalTransaction() {
        return currentTransaction != null;
    }
    
    public boolean isEnlistedInGlobalTransaction(CompositeTransaction ct) {
        boolean ret = false;
        // See case 29060 and 28683:
        // COPY attribute to avoid race conditions
        // with NPE results
        CompositeTransaction tx = currentTransaction;
        if (tx != null && ct != null) {
            ret = tx.isSameTransaction(ct);
        }
        return ret;
    }
    
    public void reset() {
        this.currentTransaction = null;
        this.history.clear();
    }
    
    public CompositeTransaction findLocalRoot(CompositeTransaction ct) {
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

    
    static class TransactionContextException extends Exception {
        
        public TransactionContextException() {}

        public TransactionContextException(String msg) {
            super(msg);
        }

        private static final long serialVersionUID = 1L;
        
    }
    
    /**
     * Exception indicating that the caller should register an AtomikosNonXAParticipant instance
     * to take part in the commit phase.
     */
    static class ParticipantRegistrationRequiredException extends TransactionContextException {

        private static final long serialVersionUID = 1L;
        
    }
    
    /**
     * Exception indicating that the caller should register a read-only participant so the transaction
     * termination will be detected, but without taking part in two-phase commit.
     */
    static class ReadOnlyParticipantRegistrationRequiredException extends TransactionContextException {

        private static final long serialVersionUID = 1L;
        
    }
    
    /**
     * Exception indicating that the connection is now in use for a subtransaction, meaning that 
     * the caller should register a SubTxAwareParticipant for subtransaction rollback to a savepoint,
     * as well as an instance of AtomikosNonXAParticipant with the local root for two-phase commit.
     */
    static class SubTxAwareParticipantRegistrationRequiredException extends TransactionContextException {

        private static final long serialVersionUID = 1L;
        
    }

    /**
     * Exception indicating that the given transaction context is invalid for reuse of the connection.
     * This usually means that the connection is currently in use by a different transaction.
     */
    static class InvalidTransactionContextException extends TransactionContextException {

        public InvalidTransactionContextException(String msg) {
            super(msg);
        }

        private static final long serialVersionUID = 1L;
        
    }
}

