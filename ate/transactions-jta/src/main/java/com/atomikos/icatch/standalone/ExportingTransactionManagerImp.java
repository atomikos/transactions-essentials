package com.atomikos.icatch.standalone;

import java.util.Stack;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.imp.PropagationImp;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * An exporting TM implementation for the standalone. Needed to make the
 * SubTxThread mechanism work, NOT for propagation across VMs.
 */

class ExportingTransactionManagerImp implements ExportingTransactionManager
{
    /**
     * @see ExportingTransactionManager
     */

    public Propagation getPropagation () throws SysException
    {
        PropagationImp ret = null;

        CompositeTransaction ct = Configuration
                .getCompositeTransactionManager ().getCompositeTransaction ();
        Stack lineage = (Stack) ct.getLineage ().clone ();
        lineage.push ( ct );
        ret = new PropagationImp ( lineage, ct.isSerial (), ct
                .getTransactionControl ().getTimeout () );

        return ret;
    }

    /**
     * @see ExportingTransactionManager
     */

    public void addExtent ( Extent extent ) throws SysException
    {
        throw new SysException ( "Not implemented" );
    }

}
