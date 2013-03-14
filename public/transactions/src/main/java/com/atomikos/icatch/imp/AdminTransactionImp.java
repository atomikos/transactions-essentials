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

package com.atomikos.icatch.imp;

import java.util.Stack;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
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
     * Convert the given state object.
     *
     * @param state
     *            The object state.
     * @return int The corresponding int state.
     */

    static int convertState ( Object state )
    {
        int ret = STATE_UNKNOWN;

        if ( state.equals ( TxState.ACTIVE ) )
            ret = STATE_ACTIVE;
        else if ( state.equals ( TxState.PREPARING ) )
            ret = STATE_PREPARING;
        else if ( state.equals ( TxState.IN_DOUBT ) )
            ret = STATE_PREPARED;
        else if ( state.equals ( TxState.HEUR_MIXED ) )
            ret = STATE_HEUR_MIXED;
        else if ( state.equals ( TxState.HEUR_HAZARD ) )
            ret = STATE_HEUR_HAZARD;
        else if ( state.equals ( TxState.HEUR_COMMITTED ) )
            ret = STATE_HEUR_COMMITTED;
        else if ( state.equals ( TxState.HEUR_ABORTED ) )
            ret = STATE_HEUR_ABORTED;
        else if ( state.equals ( TxState.COMMITTING ) )
            ret = STATE_COMMITTING;
        else if ( state.equals ( TxState.ABORTING ) )
            ret = STATE_ABORTING;
        else if ( state.equals ( TxState.TERMINATED ) )
            ret = STATE_TERMINATED;

        return ret;
    }

    /**
     * Convert the given int state.
     *
     * @param state
     *            The given int state.
     * @return Object The object state, or null if not found.
     */

    static TxState convertState ( int state )
    {
    	TxState ret = null;

        switch ( state ) {
        case STATE_PREPARED:
            ret = TxState.IN_DOUBT;
            break;
        case STATE_HEUR_MIXED:
            ret = TxState.HEUR_MIXED;
            break;
        case STATE_HEUR_HAZARD:
            ret = TxState.HEUR_HAZARD;
            break;
        case STATE_HEUR_COMMITTED:
            ret = TxState.HEUR_COMMITTED;
            break;
        case STATE_HEUR_ABORTED:
            ret = TxState.HEUR_ABORTED;
            break;
        case STATE_COMMITTING:
            ret = TxState.COMMITTING;
            break;
        case STATE_ABORTING:
            ret = TxState.ABORTING;
            break;
        case STATE_TERMINATED:
            ret = TxState.TERMINATED;
            break;

        default:
            break;
        }

        return ret;
    }

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

    public int getState ()
    {
    	TxState state = coord_.getState ();

        return convertState ( state );
    }

    /**
     * @see AdminTransaction
     */

    public HeuristicMessage[] getTags ()
    {
        return coord_.getTags ();
    }

    /**
     * @see AdminTransaction
     */

    public HeuristicMessage[] getHeuristicMessages ()
    {
        return coord_.getHeuristicMessages ();
    }

    /**
     * @see AdminTransaction
     */

    public HeuristicMessage[] getHeuristicMessages ( int state )
    {
        HeuristicMessage[] ret = null;
        TxState txstate = convertState ( state );
        if ( txstate != null ) {
            ret = coord_.getHeuristicMessages ( txstate );
        }

        return ret;
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
            Stack<Exception> errors = new Stack<Exception> ();
            errors.push ( rb );
            throw new SysException ( "Error in forced commit: " + rb.getMessage (), errors );
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

}
