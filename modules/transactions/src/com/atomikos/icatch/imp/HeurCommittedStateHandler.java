//$Id: HeurCommittedStateHandler.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: HeurCommittedStateHandler.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:40  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.3  2003/06/20 16:31:32  guy
//*** empty log message ***
//
//Revision 1.1.2.2  2003/05/12 07:00:08  guy
//Redesigned Coordinator with STATE PATTERN.
//

package com.atomikos.icatch.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;

/**
 * 
 * 
 * A state handler for the heuristic committed coordinator state.
 */

class HeurCommittedStateHandler extends CoordinatorStateHandler
{
    HeurCommittedStateHandler ( CoordinatorImp coordinator )
    {
        super ( coordinator );
    }

    HeurCommittedStateHandler ( CoordinatorStateHandler previous )
    {
        super ( previous );
    }

    protected Object getState ()
    {
        return TxState.HEUR_COMMITTED;
    }

    protected void onTimeout ()
    {
        // nothing to do here
    }

    protected void setGlobalSiblingCount ( int count )
    {
        // nothing to do here
    }

    protected  int prepare () throws RollbackException,
            java.lang.IllegalStateException, HeurHazardException,
            HeurMixedException, SysException
    {

        throw new HeurHazardException ( getHeuristicMessages () );
    }

    protected HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurMixedException,
            HeurHazardException, java.lang.IllegalStateException,
            RollbackException, SysException
    {

        // heur outcome same as global outcome
        // ->terminated state
        TerminatedStateHandler termStateHandler = new TerminatedStateHandler (
                this );
        getCoordinator ().setStateHandler ( termStateHandler );
        return getHeuristicMessages ();

    }

    protected HeuristicMessage[] rollback ()
            throws HeurCommitException, HeurMixedException, SysException,
            HeurHazardException, java.lang.IllegalStateException
    {

        throw new HeurCommitException ( getHeuristicMessages () );
    }

}