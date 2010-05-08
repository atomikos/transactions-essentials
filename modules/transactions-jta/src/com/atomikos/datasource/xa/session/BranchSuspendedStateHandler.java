package com.atomikos.datasource.xa.session;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;

class BranchSuspendedStateHandler extends TransactionContextStateHandler 
{
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
