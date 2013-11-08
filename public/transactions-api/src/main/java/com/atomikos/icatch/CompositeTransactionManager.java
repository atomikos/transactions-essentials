/**
 * Copyright (C) 2000-2011 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch;


/**
 * This interface outlines the API for managing composite transactions
 * in the local VM.
 */

public interface CompositeTransactionManager
{
	/**
	 * Starts a new (sub)transaction (not an activity) for the current thread.
	 * Associates the current thread with that instance.
	 * <br>
	 * <b>NOTE:</b> subtransactions should not be mixed: either each subtransaction is
	 * an activity, or not (default). Use suspend/resume if mixed models are necessary:
	 * for instance, if you want to create a normal transaction within an activity, then
	 * suspend the activity first before starting the transaction. Afterwards, resume the
	 * activity.
	 *
	 * @param Timeout (in millis) for the transaction.
	 * 
	 * @return CompositeTransaction The new instance.
	 * @exception SysException Unexpected error.
	 * @exception IllegalStateException If there is an existing transaction that is 
	 * an activity instead of a classical transaction.
	 */

	public CompositeTransaction createCompositeTransaction ( long timeout ) 
	throws SysException, IllegalStateException;

	/**
	 * @return CompositeTransaction The instance for the current thread, null if none.
	 *
	 * @exception SysException On unexpected failure.
	 */

	public CompositeTransaction getCompositeTransaction () throws SysException;

	/**
	 * Gets the composite transaction with the given id.
	 * This method is useful e.g. for retrieving a suspended 
	 * transaction by its id.
	 *
	 * @param tid The id of the transaction.
	 * @return CompositeTransaction The transaction with the given id,
	 * or null if not found.
	 * @exception SysException Unexpected failure.
	 */

	public CompositeTransaction getCompositeTransaction ( String tid )
	throws SysException;

	/**
	 * Re-maps the calling thread to the given transaction.
	 *
	 * @param compositeTransaction
	 * @exception IllegalStateException If this thread has a transaction context already.
	 * @exception SysException 
	 */

	public void resume ( CompositeTransaction compositeTransaction )
	throws IllegalStateException, SysException;

	/**
	 * Suspends the transaction context for the current thread.
	 *
	 * @return CompositeTransaction The transaction for the current thread.
	 *
	 * @exception SysException 
	 */

	public CompositeTransaction suspend() throws SysException ;

    /**
     * Recreate a composite transaction based on an imported context. Needed by
     * the application's communication layer.
     *
     * @param context
     *            The propagationcontext.
     * @param orphancheck
     *            If true, real composite txs are done. If false, OTS like
     *            behavior applies.
     * @param heur_commit
     *            True for heuristic commit, false for heuristic rollback.
     *
     * @return CompositeTransaction The recreated local instance.
     * @exception SysException
     *                Failure.
     */

	public CompositeTransaction recreateCompositeTransaction(
			Propagation propagation, boolean orphancheck, boolean heur_commit);



}
