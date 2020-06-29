/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;

 /**
  *
  *
  * The common logic for the branch state handlers.
  * Methods that can lead to a state change return
  * a state handler object, or null if no state change
  * should occur. In general, the methods here
  * correspond to all relevant events; it is up to
  * each subclass to ignore those events that are
  * irrelevant to it.
  *
  */

abstract class TransactionContextStateHandler
{

	private XATransactionalResource resource;
	private XAResource xaResource;

	TransactionContextStateHandler ( XATransactionalResource resource , XAResource xaResource )
	{
		this.resource = resource;
		this.xaResource = xaResource;
	}

	XATransactionalResource getXATransactionalResource()
	{
		return resource;
	}

	XAResource getXAResource()
	{
		return xaResource;
	}

	/**
	 * Checks and performs an XA enlist if needed.
	 * @param ct The transaction to enlist with, null if none.
	 *
	 * @return The next state, or null if no change.
	 *
	 * @throws InvalidSessionHandleStateException If the state does not allow
	 * enlisting for the given transaction.
	 *
	 * @throws UnexpectedTransactionContextException If the transaction context is not
	 * what was expected by this state.
	 *
	 */

	abstract TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction ct )
	throws InvalidSessionHandleStateException, UnexpectedTransactionContextException;

	/**
	 * Notification that the session has been
	 * closed by the application.
	 *
	 * @return The next state, or null if no change.
	 */

	abstract TransactionContextStateHandler sessionClosed();

	/**
	 * Notification that the transaction has been terminated.
	 * @param ct The transaction. Irrelevant transactions should be ignored.
	 * @return The next state, or null if no change.
	 */

	abstract TransactionContextStateHandler transactionTerminated ( CompositeTransaction ct );

	/**
	 * Checks if the branch is suspended for this tx.
	 * @param ct The transaction
	 * @return True iff suspended in this transaction.
	 */

	boolean isSuspendedInTransaction ( CompositeTransaction ct )
	{
		return false;
	}

	/**
	 * Notification that the current branch is being suspended.
	 * @return The next state, or null if no change.
	 * @throws InvalidSessionHandleStateException
	 */

	TransactionContextStateHandler transactionSuspended() throws InvalidSessionHandleStateException
	{
		throw new InvalidSessionHandleStateException ( "Could not suspend in state: " + this );
	}

	/**
	 * Notification that the current branch is being resumed.
	 * @return The next state, or null if no change.
	 * @throws InvalidSessionHandleStateException
	 */

	TransactionContextStateHandler transactionResumed() throws InvalidSessionHandleStateException
	{
		throw new InvalidSessionHandleStateException ( "Could not resume in state: " + this );
	}

	/**
	 * Tests if the state is active in the given transaction.
	 * @param tx
	 * @return
	 */

	boolean isInTransaction ( CompositeTransaction tx )
	{
		return false;
	}

	/**
	 * Tests if the state is inactive in the given transaction.
	 * @param tx
	 * @return
	 */
	boolean isInactiveInTransaction ( CompositeTransaction tx )
	{

		return false;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}


}
