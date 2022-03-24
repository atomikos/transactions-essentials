/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * 
 * 
 * State handler for when delisted in an XA branch.
 *
 */

class BranchEndedStateHandler 
extends TransactionContextStateHandler 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(BranchEndedStateHandler.class);

	private XAResourceTransaction branch;
	private CompositeTransaction ct;
	
	BranchEndedStateHandler(XATransactionalResource resource, XAResourceTransaction branch, CompositeTransaction ct, XAResource xaResource)
	{
		super ( resource , xaResource );
		this.branch = branch;
		this.ct = ct;
		branch.suspend();
	}

	TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction ct)
			throws InvalidSessionHandleStateException 
	{
		if (canTransactionBranchBeReused()) {
			return new BranchEnlistedStateHandler(getXATransactionalResource(), ct, getXAResource(), branch);
		}

		String msg = "Detected illegal attempt to use a closed XA session";
		LOGGER.logError ( msg );
		throw new InvalidSessionHandleStateException ( msg );
	}

	TransactionContextStateHandler sessionClosed() 
	{
		//close can happen several times -> should be idempotent
		return null;
	}

	
	TransactionContextStateHandler transactionTerminated ( CompositeTransaction tx ) 
	{
		TransactionContextStateHandler ret = null;
		if ( ct.isSameTransaction ( tx ) ) ret = new TerminatedStateHandler ();
		return ret;
	}
	
	boolean isInactiveInTransaction ( CompositeTransaction tx ) 
	{
		return ct.isSameTransaction ( tx );
	}

	boolean canTransactionBranchBeReused() {
		return branch.isAssociatedWithResource(getXAResource());
	}

}
