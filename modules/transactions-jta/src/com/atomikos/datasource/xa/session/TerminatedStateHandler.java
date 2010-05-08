package com.atomikos.datasource.xa.session;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;

class TerminatedStateHandler 
extends TransactionContextStateHandler 
{

	TerminatedStateHandler() 
	{
		super ( null , null );
	}
	
	TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction ct , HeuristicMessage hmsg ) throws InvalidSessionHandleStateException 
	{
		String msg = "Detected illegal attempt to use a terminated XA session";
		Configuration.logWarning ( msg );
		throw new InvalidSessionHandleStateException ( msg );
	}

	TransactionContextStateHandler sessionClosed() 
	{
		return null;
	}

	TransactionContextStateHandler transactionTerminated ( CompositeTransaction ct ) 
	{
		return null;
	}
}
