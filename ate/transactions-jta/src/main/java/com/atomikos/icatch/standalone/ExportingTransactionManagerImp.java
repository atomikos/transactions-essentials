//$Id: ExportingTransactionManagerImp.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//$Log: ExportingTransactionManagerImp.java,v $
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
//Revision 1.2  2004/10/12 13:03:49  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/02/03 10:03:46  guy
//Added SubTxThread support by adding Imp/Exp Tm instances.
//

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
