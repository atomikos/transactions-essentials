/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

class TerminatedStateHandler 
extends TransactionContextStateHandler 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TerminatedStateHandler.class);

	TerminatedStateHandler() 
	{
		super ( null , null );
	}
	
	TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction ct) throws InvalidSessionHandleStateException 
	{
		String msg = "Detected illegal attempt to use a terminated XA session";
		LOGGER.logError ( msg );
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
