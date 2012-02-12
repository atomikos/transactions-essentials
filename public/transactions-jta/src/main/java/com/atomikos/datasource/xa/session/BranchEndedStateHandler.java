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

import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.system.Configuration;

/**
 *
 *
 * State handler for when delisted in an XA branch.
 *
 */

class BranchEndedStateHandler
extends TransactionContextStateHandler
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(BranchEndedStateHandler.class);

	private CompositeTransaction ct;

	BranchEndedStateHandler ( XATransactionalResource resource , XAResourceTransaction branch , CompositeTransaction ct )
	{
		super ( resource , null );
		this.ct = ct;
		branch.suspend();
	}

	TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction ct , HeuristicMessage hmsg )
			throws InvalidSessionHandleStateException
	{
		String msg = "Detected illegal attempt to use a closed XA session";
		LOGGER.logWarning ( msg );
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

}
