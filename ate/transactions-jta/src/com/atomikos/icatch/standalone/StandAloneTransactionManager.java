//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//$Log: StandAloneTransactionManager.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:11  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.2  2006/04/14 12:45:17  guy
//Added properties to TSListener init callback.
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
//Revision 1.4  2006/03/21 16:13:53  guy
//Removed active recovery as global parameter.
//
//Revision 1.3  2006/03/21 13:23:13  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:31:56  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:13  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/05 15:04:20  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.6  2004/10/12 13:03:49  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.5  2004/09/06 09:27:21  guy
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Adapted for new recovery.
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.4  2004/03/25 12:54:02  guy
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Added support for max active transactions.
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.3  2004/03/22 15:38:03  guy
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.2.2.3  2004/01/14 10:38:43  guy
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//*** empty log message ***
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.2.2.2  2003/11/16 09:03:11  guy
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Corrected BUG: output dir property was not used.
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.2.2.1  2003/09/10 14:05:19  guy
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Added support for expired evaluation licenses: a descriptive message is
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//now shown on System.err.
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.2  2003/03/11 06:39:11  guy
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: StandAloneTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//Revision 1.1.4.1  2002/11/17 18:36:40  guy
//Changed terminated: does not throw heuristic.
//
//Revision 1.1  2002/01/23 11:40:02  guy
//Added standalone  package to CVS.
//

package com.atomikos.icatch.standalone;

import java.util.Properties;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.imp.BaseTransactionManager;
import com.atomikos.icatch.imp.TransactionServiceImp;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.util.UniqueIdMgr;

/**
 * 
 * 
 * A standalone TM implementation. No import or export supported.
 */

class StandAloneTransactionManager extends BaseTransactionManager
{

    TransactionServiceImp service_;

    // the TS to delegate to

    /**
     * Create a new instance.
     * 
     * @param tmName
     *            The unique name for the transaction manager.
     * @param srecmgr
     *            The state recover manager.
     * @param console
     *            The console to use.
     * @param outputDirPath
     *            The output directory path.
     * @param maxTimeout
     *            The max timeout value.
     * @param maxActives
     *            The max no of active txs, or negative if not applicable.
     * @param single_threaded_2pc 
     *            Whether 2PC commit should happen in the same thread that started the tx.
     */

    StandAloneTransactionManager ( String tmName ,
            StateRecoveryManager srecmgr , Console console ,
            String outputDirPath , long maxTimeout , int maxActives , 
            boolean single_threaded_2pc )
    {
        super ();
        UniqueIdMgr idmgr = null;

        idmgr = new UniqueIdMgr ( tmName, outputDirPath );
        service_ = new TransactionServiceImp ( tmName, srecmgr, idmgr, console,
                maxTimeout, maxActives , single_threaded_2pc );
    }

    TransactionServiceImp getTransactionService ()
    {
        return service_;
    }

    /**
     * Initializes the TM. Should be called as first method.
     */

    public synchronized void init ( Properties properties ) throws SysException
    {
        super.init ( service_ , properties );
    }

    /**
     * @see BaseTransactionManager
     */

    public void shutdown ( boolean force ) throws SysException,
            IllegalStateException
    {
        service_.shutdown ( force );
    }

    public void addExtent ( Extent extent ) throws SysException
    {
        throw new SysException ( "Not implemented" );
    }

    public Extent terminated ( boolean commit ) throws SysException,
            RollbackException
    {
        throw new SysException ( "Not implemented" );
    }

}
