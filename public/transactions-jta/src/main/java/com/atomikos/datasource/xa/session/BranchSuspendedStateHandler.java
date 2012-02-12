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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;

class BranchSuspendedStateHandler extends TransactionContextStateHandler 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(BranchSuspendedStateHandler.class);

	private XAResourceTransaction branch;
	private CompositeTransaction ct;
	
	BranchSuspendedStateHandler ( XATransactionalResource resource, XAResourceTransaction branch , CompositeTransaction ct ,  XAResource xaResource ) 
	{
		super ( resource, xaResource );
		this.branch = branch;
		this.ct = ct;
	}

	TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction ct , HeuristicMessage hmsg )
			throws InvalidSessionHandleStateException,
			UnexpectedTransactionContextException 
	{
		String msg = "Detected illegal attempt to use a suspended XA session";
		Configuration.logWarning ( msg );
		throw new InvalidSessionHandleStateException ( msg );
	}

	TransactionContextStateHandler sessionClosed() 
	{
		return new BranchEndedStateHandler ( getXATransactionalResource() , branch , ct );
	}

	TransactionContextStateHandler transactionTerminated ( CompositeTransaction tx ) 
	{
		TransactionContextStateHandler ret = null;
		if ( ct.isSameTransaction ( tx ) ) ret = new NotInBranchStateHandler ( getXATransactionalResource() , getXAResource() );
		return ret;
	}

	boolean isSuspendedInTransaction ( CompositeTransaction tx )
	{
		boolean ret = false;
		if ( tx != null && ct.isSameTransaction ( tx ) ) ret = true;
		return ret;
	}
	
	public TransactionContextStateHandler transactionResumed() throws InvalidSessionHandleStateException 
	{
		return new BranchEnlistedStateHandler ( getXATransactionalResource() , ct , getXAResource() , branch );
	}
}
