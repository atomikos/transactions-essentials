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

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.jta.TransactionManagerImp;

 /**
  *
  *
  * State handler dealing with the situation where there is no
  * current XA branch associated with the session.
  */

class NotInBranchStateHandler extends TransactionContextStateHandler
{

	NotInBranchStateHandler ( XATransactionalResource resource , XAResource xaResource )
	{
		super ( resource , xaResource );
	}

	TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction ct , HeuristicMessage hmsg ) throws InvalidSessionHandleStateException
	{
		TransactionContextStateHandler ret = null;
		if ( ct != null && ct.getProperty ( TransactionManagerImp.JTA_PROPERTY_NAME ) != null ) {

			if ( TxState.MARKED_ABORT.equals ( ct.getState() ) ) {
				//see case 27857
				throw new InvalidSessionHandleStateException (
					"Transaction is marked for rollback only or has timed out"
				);
			}

			//JTA transaction found for calling thread -> enlist
			//also see the state diagram documentation
			ret = new BranchEnlistedStateHandler ( getXATransactionalResource() , ct , getXAResource() , hmsg );

		}
		return ret;
	}

	TransactionContextStateHandler sessionClosed()
	{
		//see the state diagram documentation
		return new TerminatedStateHandler();
	}

	TransactionContextStateHandler transactionTerminated ( CompositeTransaction ct )
	{
		return null;
	}

}
