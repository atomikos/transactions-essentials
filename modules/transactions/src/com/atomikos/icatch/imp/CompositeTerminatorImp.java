//$Id: CompositeTerminatorImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Id: CompositeTerminatorImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: CompositeTerminatorImp.java,v $
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
//Revision 1.3  2006/03/21 13:22:56  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.9  2005/09/01 10:01:58  guy
//Modified: getHeuristicMessages return messages, not strings.
//Otherwise, activities can't throw heuristic errors.
//
//Revision 1.8  2005/08/16 07:59:53  guy
//Changed to set orphan info already on (sub)tx commit;
//to make sure it is there for 1PC commit requests from
//remote client TM.
//
//Revision 1.7  2005/08/11 09:24:05  guy
//Debugged compensation.
//
//Revision 1.6  2005/08/10 16:23:03  guy
//Debugged/adapted for compensation and dito testing.
//
//Revision 1.5  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.4  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/11/24 10:20:15  guy
//Updated error messages.
//Revision 1.1.1.1.14.2  2005/08/04 13:25:13  guy
//Moved compensation to datasource/compensation.
//Redesigned compensation to prevent early prepared.
//Added presumed compensation approach after crash/timeout.
//
//Revision 1.2  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//Revision 1.1.1.1.14.1  2004/06/14 08:09:08  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.1.1.8.5  2002/11/07 12:52:27  guy
//Adapted to new Propagation (without Protocol, which is now in Transport).
//
//Revision 1.1.1.1.8.4  2002/10/28 13:07:17  guy
//Corrected compensation: this now creates a NEW and UNIQUE coordinator
//that will be early prepared.
//
//Revision 1.1.1.1.8.3  2002/10/28 11:00:03  guy
//Improved design: added TSListener mechanism, which is used at recovery of compensators. Improved handling of compensation (orphan detection).
//
//Revision 1.1.1.1.8.2  2002/10/25 13:00:48  guy
//Adapted TransactionService.createCompositeTransaction: removed unnecessary heur_commit argument.
//
//Revision 1.1.1.1.8.1  2002/05/22 09:25:29  guy
//Redesigned for new import paradigm.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.1  2001/03/23 17:01:59  pardon
//Added some files to repository.
//

package com.atomikos.icatch.imp;

import java.util.Dictionary;
import java.util.Stack;

import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;
import com.atomikos.icatch.TransactionService;

/**
 * 
 * 
 * A terminator implementation.
 */

class CompositeTerminatorImp implements CompositeTerminator
{
    protected CoordinatorImp coordinator_ = null;

    protected CompositeTransactionImp transaction_ = null;

    protected TransactionService ts_ = null;

    /**
     * Constructor.
     */

    CompositeTerminatorImp ( TransactionService ts ,
            CompositeTransactionImp transaction , CoordinatorImp coordinator )
    {
        ts_ = ts;
        coordinator_ = coordinator;
        transaction_ = transaction;

    }


    /**
     * @see CompositeTerminator
     */

    public void commit () throws HeurRollbackException, HeurMixedException,
            HeurHazardException, SysException, java.lang.SecurityException,
            RollbackException
    {
    		Stack errors = new Stack ();
        TransactionControl control = transaction_.getTransactionControl ();

        transaction_.doCommit ();

        // SET SIBLING INFO: NEEDED FOR
        // INCOMING 1PC REQUEST FROM
        // REMOTE CLIENT
        Dictionary cascadelist = control.getExtent ().getRemoteParticipants ();
        coordinator_.setGlobalSiblingCount ( coordinator_
                .getLocalSiblingCount () );
        coordinator_.setCascadeList ( cascadelist );

        if ( transaction_.isRoot () ) {
            try {

                coordinator_.terminate ( true );
            }

            catch ( RollbackException rb ) {
                throw rb;
            } catch ( HeurHazardException hh ) {
                throw hh;
            } catch ( HeurRollbackException hr ) {
                throw hr;
            } catch ( HeurMixedException hm ) {
                throw hm;
            } catch ( SysException se ) {
                throw se;
            } catch ( Exception e ) {
                errors.push ( e );
                throw new SysException (
                        "Unexpected error: " + e.getMessage (), errors );
            }
        }

    }



    /**
     * @see CompositeTerminator
     */

    public void rollback () throws IllegalStateException, SysException
    {
        Stack errors = new Stack ();

        transaction_.doRollback ();

        if ( transaction_.isRoot () )
            try {
                coordinator_.terminate ( false );
            } catch ( Exception e ) {
                errors.push ( e );
                throw new SysException ( "Unexpected error in rollback: "
                        + e.getMessage (), errors );
            }
    }

}
