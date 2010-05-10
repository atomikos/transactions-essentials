//$Id: ImportingTransactionManagerImp.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//$Log: ImportingTransactionManagerImp.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:11  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:56  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:13  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/05 15:04:20  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/10/12 13:03:49  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: ImportingTransactionManagerImp.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.2  2003/03/11 06:39:11  guy
//$Id: ImportingTransactionManagerImp.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: ImportingTransactionManagerImp.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//Revision 1.1.4.1  2002/11/17 18:36:40  guy
//Changed terminated: does not throw heuristic.
//
//Revision 1.1  2002/02/03 10:03:46  guy
//Added SubTxThread support by adding Imp/Exp Tm instances.
//

package com.atomikos.icatch.standalone;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;

/**
 * 
 * 
 * A standalone version of the importing TM. Needed for the SubTxThread
 * mechanism to work, NOT for propagation between VMs.
 */

class ImportingTransactionManagerImp implements ImportingTransactionManager
{
    private TransactionService ts_;

    ImportingTransactionManagerImp ( TransactionService ts )
    {
        ts_ = ts;
    }

    /**
     * @see ImportingTransactionManager
     */

    public CompositeTransaction importTransaction ( Propagation propagation ,
            boolean orphancheck , boolean heur_commit ) throws SysException
    {
        CompositeTransaction ret = ts_.recreateCompositeTransaction (
                propagation, orphancheck, heur_commit );
        // CompositeTransactionManager ctm =
        // Configuration.getCompositeTransactionManager();
        // ctm.resume ( ret );
        return ret;
    }

    /**
     * @see ImportingTransactionManager
     */

    public Extent terminated ( boolean commit ) throws SysException,
            RollbackException

    {
        throw new SysException ( "Not implemented" );
    }



}
