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
