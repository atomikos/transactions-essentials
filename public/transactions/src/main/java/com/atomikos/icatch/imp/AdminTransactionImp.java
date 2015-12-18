/**
 * Copyright (C) 2000-2015 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.imp;

import java.util.Vector;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.AdminTransaction;

/**
 * A default implementation of the AdminTransaction. For local VM use only.
 */

class AdminTransactionImp implements AdminTransaction
{

    private CoordinatorImp coord_;
    // the wrapped coordinator

    /**
     * Create a new instance.
     *
     * @param coord
     *            The coordinator to use.
     */

    AdminTransactionImp ( CoordinatorImp coord )
    {
        coord_ = coord;
    }

    /**
     * @see AdminTransaction
     */

    public String getTid ()
    {
        return coord_.getCoordinatorId ();
    }

    /**
     * @see AdminTransaction
     */

    public boolean wasCommitted ()
    {
        return coord_.isCommitted ();
    }

    /**
     * @see AdminTransaction
     */

    public TxState getState ()
    {
    	return coord_.getState ();
    }
  

    /**
     * @see AdminTransaction
     */

    public void forceCommit () throws HeurRollbackException,
            HeurHazardException, HeurMixedException, SysException
    {
        try {
            coord_.commitHeuristically ();
        } catch ( RollbackException rb ) {
            // impossible since this happens for 1PC only,
            // and 1PC txs are not in the log?!
            throw new SysException ( "Error in forced commit: " + rb.getMessage (), rb );
        }
    }

    /**
     * @see AdminTransaction
     */

    public void forceRollback () throws HeurCommitException,
            HeurMixedException, HeurHazardException, SysException
    {
        coord_.rollbackHeuristically ();
    }

    /**
     * @see AdminTransaction
     */

    public void forceForget ()
    {
        coord_.forget ();
    }

	@Override
	public String[] getParticipantDetails() {
		Vector participants = coord_.getParticipants();
		String[] ret = new String[participants.size()];
		for (int i = 0 ; i < ret.length ; i++) {
			ret[i] = participants.get(i).toString();
		}
		return ret;
	}

	@Override
	public boolean hasExpired() {
		// TODO Auto-generated method stub
		return false;
	}

}
