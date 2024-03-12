/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.recovery.TxState;


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

	protected void addSynchronizations ( Stack<Synchronization> s )
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
