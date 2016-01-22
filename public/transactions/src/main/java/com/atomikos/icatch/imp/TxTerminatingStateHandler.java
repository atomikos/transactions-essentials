/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.imp;

import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.TxState;


/**
 *
 *
 * A transaction terminating state handler. The purpose of this
 * state is to detect and prevent concurrency effects when
 * a timout/rollback thread interleaves with an application/commit
 * thread. Due to the testAndSet functionality, the first such
 * interleaving thread will be allowed to proceed, whereas the
 * second thread will see this state and its safety checks.
 */

class TxTerminatingStateHandler extends TransactionStateHandler
{


	private boolean committing;

	public TxTerminatingStateHandler ( boolean committing , CompositeTransactionImp ct,
			TransactionStateHandler handler )
	{
		super ( ct , handler );
		this.committing = committing;
	}

	private void reject() throws IllegalStateException
	{
		if ( committing )
			throw new IllegalStateException ( "Transaction is committing - adding a new participant is not allowed" );
		else
			throw new IllegalStateException ( "Transaction is rolling back - adding a new participant is not allowed" );
	}

	/**
	 * @return ACTIVE or JPA implementations like EclipseJPA will not attempt to flush changes before commit!
	 */
	protected TxState getState()
	{
		return TxState.ACTIVE;
	}

	protected RecoveryCoordinator addParticipant ( Participant p )
	{
		if ( ! committing ) reject();

		return super.addParticipant ( p );
	}

	protected void addSubTxAwareParticipant ( SubTxAwareParticipant p )
	{
		if ( ! committing ) reject();

		super.addSubTxAwareParticipant ( p );
	}

	protected void addSynchronizations ( Stack s )
	{
		reject();
	}

	protected void commit()
	{
		//reject in all cases, even if committing: the application thread should commit, and only once
		reject();
	}

	protected CompositeTransaction createSubTransaction()
	{
		reject();
		return null;
	}

	protected void registerSynchronization()
	{
		reject();
	}

	protected void rollbackWithStateCheck()
	{
		if ( committing ) reject();

		//return silently if rolling back already: rollback twice should be the same as once
	}

	protected void setRollbackOnly()
	{

		if ( committing ) {
			//happens legally if synchronizations call this method!
			super.setRollbackOnly();
		} //else ignore: already rolling back; this is consistent with what is asked
	}

}
