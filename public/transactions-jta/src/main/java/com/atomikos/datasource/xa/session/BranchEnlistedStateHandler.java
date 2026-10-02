/**
 * Copyright (C) 2000-2026 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.TxState;

 /**
  * 
  * 
  * State handler for when enlisted in an XA branch.
  *
  */

class BranchEnlistedStateHandler extends TransactionContextStateHandler 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(BranchEnlistedStateHandler.class);

	private CompositeTransaction ct;
	private XAResourceTransaction branch;
	
	BranchEnlistedStateHandler ( XATransactionalResource resource , CompositeTransaction ct , XAResource xaResource) 
    {
		super ( resource , xaResource );
		this.ct = ct;
		this.branch = ( XAResourceTransaction ) resource.getResourceTransaction ( ct );
		branch.setXAResource ( xaResource );
		branch.resume();
	}

	public BranchEnlistedStateHandler ( 
			XATransactionalResource resource, CompositeTransaction ct, 
			XAResource xaResource , XAResourceTransaction branch ) throws InvalidSessionHandleStateException 
	{
		super ( resource , xaResource  );
		this.ct = ct;
		this.branch = branch;
		branch.setXAResource ( xaResource );
		try {
			branch.xaResume();
		} catch ( XAException e ) {
			String msg = "Failed to resume branch: " + branch;
			throw new InvalidSessionHandleStateException ( msg );
		}
	}

	TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction currentTx)
			throws InvalidSessionHandleStateException, UnexpectedTransactionContextException 
	{
		
		if ( currentTx == null || !currentTx.isSameTransaction ( ct ) ) {
			//OOPS! we are being used a different tx context than the one expected...
			
			//TODO check: what if subtransaction? Possible solution: ignore if serial_jta mode, error otherwise.
			
			String msg = "The connection/session object is already enlisted in a (different) transaction.";
			if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( msg );
			throw new UnexpectedTransactionContextException();
		}

		// cf case 227307: repeated enlist on an already-associated branch never
		// re-checks doomed status otherwise -- resume() (and its guard) only
		// runs on the FIRST enlist, in the constructor above. Re-check here so
		// a transaction that becomes doomed between two statements on the
		// same connection is caught too, not just on the first statement.
		if ( Configuration.getConfigProperties().getFeatureFlag("227307")
				&& ( currentTx.getTimeout() <= 0 || TxState.MARKED_ABORT.equals ( currentTx.getState() ) ) ) {
			String msg = "Transaction " + currentTx.getTid() + " is doomed (timed out or rollback-only) - using resource "
					+ getXATransactionalResource().getName() + " is no longer allowed.";
			LOGGER.logWarning ( msg );
			throw new InvalidSessionHandleStateException ( msg );
		}

		//tx context is still the same -> no change in state required
		return null;
	}

	TransactionContextStateHandler sessionClosed() 
	{
		return new BranchEndedStateHandler ( getXATransactionalResource() , branch , ct );
	}

	
	TransactionContextStateHandler transactionTerminated ( CompositeTransaction tx ) 
	{
		TransactionContextStateHandler ret = null;
		if ( ct.isSameTransaction ( tx ) ) ret = new NotInBranchStateHandler  ( getXATransactionalResource() , getXAResource() );
		return ret;
		
	}
	
	public TransactionContextStateHandler transactionSuspended() throws InvalidSessionHandleStateException 
	{
		try {
			branch.xaSuspend();
		} catch ( XAException e ) {
			LOGGER.logWarning ( "Error in suspending transaction context for transaction: " + ct , e );
			String msg = "Failed to suspend branch: " + branch;
			throw new InvalidSessionHandleStateException ( msg , e );
		}
		return new BranchSuspendedStateHandler ( getXATransactionalResource() , branch , ct , getXAResource() );
	}

	boolean isInTransaction ( CompositeTransaction tx )
	{
		return ct.isSameTransaction ( tx );
	}
}
