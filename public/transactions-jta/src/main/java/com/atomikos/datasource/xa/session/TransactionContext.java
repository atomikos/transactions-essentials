/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

 /**
  * This class represents a particular branch association of
  * the session handle. There is one instance for each
  * such association of a session handle. 
  */
class TransactionContext {
	
	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionContext.class);

	private TransactionContextStateHandler state; // never null
	
	TransactionContext ( XATransactionalResource resource , XAResource xaResource ) {
		// we're not transactional until we are actually used
		setState ( new NotInBranchStateHandler ( resource , xaResource ) );
	}
	
	private synchronized void setState ( TransactionContextStateHandler state ) {
		if ( state != null ) {  // null if no change
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": changing to state " + state );
			this.state = state;	
		}
	}

	/**
	 * Checks if the session handle state is terminated (i.e., if the session handle 
	 * can be discarded and the underlying connection can be closed).
	 * @return True if terminated.
	 */
	synchronized boolean isTerminated() {
		return state instanceof TerminatedStateHandler;
	}
	
	/**
	 * Checks and enlists with the current transaction if appropriate.
	 * @param ct The current transaction.
	 * @param hmsg The heuristic message.
	 * @throws InvalidSessionHandleStateException If the current handle is being used in an unexpected (wrong) context.
	 * @throws UnexpectedTransactionContextException If the current transaction context is not what would be expected.
	 */
	synchronized void checkEnlistBeforeUse ( CompositeTransaction ct ) throws InvalidSessionHandleStateException, UnexpectedTransactionContextException {
		TransactionContextStateHandler nextState = state.checkEnlistBeforeUse ( ct );
		setState ( nextState );
	}
	
	/**
	 * Notification that the session handle was closed.
	 *
	 */
	synchronized void sessionClosed() {
		TransactionContextStateHandler nextState = state.sessionClosed();
		setState ( nextState );
	}
	
	/**
	 * Notification that the branch has been suspended.
	 * @throws InvalidSessionHandleStateException 
	 *
	 */
	
	synchronized void transactionSuspended() throws InvalidSessionHandleStateException {
		TransactionContextStateHandler nextState = state.transactionSuspended();
		setState ( nextState );
	}
	
	/**
	 * Notification that the branch has been resumed.
	 * @throws InvalidSessionHandleStateException 
	 *
	 */
	
	synchronized void transactionResumed() throws InvalidSessionHandleStateException {
		TransactionContextStateHandler nextState = state.transactionResumed();
		setState ( nextState );
	}

	/** 
	 * Notification of transaction termination.
	 * @param ct The transaction. Irrelevant transactions should be ignored.
	 */
	
	synchronized void transactionTerminated ( CompositeTransaction ct ) {
		TransactionContextStateHandler nextState = state.transactionTerminated ( ct );
		setState ( nextState );
	}
	
	/**
	 * Checks if this branch is suspended in the given transaction. 
	 * @param ct The transaction. 
	 * @return True iff this branch is suspended for the ct.
	 */
	synchronized boolean isSuspendedInTransaction ( CompositeTransaction ct ) {
		return state.isSuspendedInTransaction ( ct );
	}
	
	public String toString() {
		return "a TransactionContext in state " + state;
	}
	
	boolean isInTransaction ( CompositeTransaction tx ) {
		return state.isInTransaction ( tx );
	}

	boolean isInactiveInTransaction(CompositeTransaction tx) {
		return state.isInactiveInTransaction ( tx );
	}
	
}
