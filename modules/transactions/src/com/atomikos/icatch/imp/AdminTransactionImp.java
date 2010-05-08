//$Id: AdminTransactionImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: AdminTransactionImp.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.3  2006/03/21 13:22:55  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2004/11/24 10:20:15  guy
//Updated error messages.
//
//Revision 1.6  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.5  2004/08/30 07:16:23  guy
//Added admin tx state: active.
//
//Revision 1.4  2004/03/22 15:36:52  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.3.10.2  2003/06/20 16:31:32  guy
//*** empty log message ***
//
//Revision 1.3.10.1  2003/05/12 07:00:07  guy
//Redesigned Coordinator with STATE PATTERN.
//
//Revision 1.3  2002/03/11 11:54:02  guy
//Corrected forceRollback and dito commit to use heuristic indication.
//
//Revision 1.2  2002/01/10 08:57:29  guy
//Added getTags method to AdminTransactionImp
//
//Revision 1.1  2002/01/08 15:21:26  guy
//Updated to new LogAdministrator paradigm.
//

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
 * 
 * 
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

    static Object convertState ( int state )
    {
        Object ret = null;

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
        Object state = coord_.getState ();

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
        Object txstate = convertState ( state );
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
            Stack errors = new Stack ();
            errors.push ( rb );
            throw new SysException ( "Error in forced commit: "
                    + rb.getMessage (), errors );
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
