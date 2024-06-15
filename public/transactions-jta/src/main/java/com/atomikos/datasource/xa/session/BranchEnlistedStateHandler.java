/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
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
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

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
	
	// Fix by Martin Aubele. Without this fix we need one new connection per call when no 2PC is used (no propagation header).
    // this leads to a connection shortage if the service is called multiple times. 
	
	boolean isInactiveInTransaction ( CompositeTransaction tx ) 
	{
		// code is separated in special if statements to allow setting a breakpoint
		if (ct.getCompositeCoordinator() != tx.getCompositeCoordinator())
			return false;
		if (ct.isRoot() != tx.isRoot())
			return false;
		
		// we need to check if the branch is active because of this scenario:
		// ServiceA on Server 1 uses a jdbc connection and then calls ServiceB on Server 2. ServiceB calls
		// back to Service A2 on Server 1. In this case, we cannot use the still ACTIVE jdbc connection.
		if (branch.isActive())
			return false;
		return true;
	}
}
