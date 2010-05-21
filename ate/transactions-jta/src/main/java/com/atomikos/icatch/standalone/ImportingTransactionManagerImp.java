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
