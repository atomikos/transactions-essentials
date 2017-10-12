/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;

import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.recovery.TxState;

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

	TransactionContextStateHandler checkEnlistBeforeUse ( CompositeTransaction ct ) throws InvalidSessionHandleStateException
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
			ret = new BranchEnlistedStateHandler ( getXATransactionalResource() , ct , getXAResource() );

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
