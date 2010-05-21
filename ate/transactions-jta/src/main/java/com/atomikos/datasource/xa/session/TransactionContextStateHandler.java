/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

package com.atomikos.datasource.xa.session;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;

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
	 * @param HeuristicMessage hmsg The heuristic message.
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
	
	abstract TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction ct , HeuristicMessage hmsg )
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
	
	
}
