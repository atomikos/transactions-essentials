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
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( msg );
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
}
