//$Id: CompositeTransactionAdaptor.java,v 1.2 2006/09/21 10:32:28 guy Exp $
//$Log: CompositeTransactionAdaptor.java,v $
//Revision 1.2  2006/09/21 10:32:28  guy
//FIXED 10051 and added JUnit tests.
//
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
//Revision 1.4  2006/03/21 16:13:01  guy
//Added active recovery as a setter.
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
//Revision 1.3  2005/08/08 11:23:27  guy
//Added RollbackException to ExportingTM interface.
//Added public constructor for CompositeTransactionAdaptor.
//
//Revision 1.2  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.1  2004/06/14 08:09:08  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.2  2002/11/07 10:07:56  guy
//Made this class public and added a constructor to import a root
//without lineage. Needed for messaging protocols.
//
//Revision 1.1.2.1  2002/05/22 09:25:29  guy
//Redesigned for new import paradigm.
//

package com.atomikos.icatch.imp;

import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.SysException;

/**
 * 
 * 
 * A composite transaction adaptor for interposition on an imported instance.
 * This allows substitution of the recovery coordinator adaptor.
 */

public class CompositeTransactionAdaptor extends AbstractCompositeTransaction
        implements CompositeCoordinator
{

    private RecoveryCoordinator adaptor_;
    // the adaptor to use for replay requests

    private String root_;
    
    private Boolean isRecoverableWhileActive_;

    // the root TID

    /**
     * Create a new instance.
     * 
     * @param lineage
     *            The parent info, <b>not including</b> the instance being
     *            constructed here!
     * @param serial
     *            True if serial.
     * @param adaptor
     *            The adaptor for replay requests.
     * @param isRecoverableWhileActive 
     *            Whether recoverable in active state or not. Null if not known.
     */

    public CompositeTransactionAdaptor ( Stack lineage , String tid ,
            boolean serial , RecoveryCoordinator adaptor , Boolean isRecoverableWhileActive )
    {
        super ( tid , (Stack) lineage.clone () , serial  );
        adaptor_ = adaptor;
        Stack tmp = (Stack) lineage.clone ();
        CompositeTransaction parent = null;
        while ( !tmp.empty () ) {
            parent = (CompositeTransaction) tmp.pop ();
        }
        root_ = parent.getTid ();
        isRecoverableWhileActive_ = isRecoverableWhileActive;
    }
    
    /**
     * Constructor for testin.
     * @param root
     * @param serial
     * @param adaptor
     */
    
    public CompositeTransactionAdaptor ( String root , boolean serial ,
            RecoveryCoordinator adaptor )
    {
    	 this ( root , serial , adaptor , null );
    }

    /**
     * Constructs a new instance for an imported ROOT. This constructor is
     * needed for message-based propagation where only the root TID is passed.
     * 
     * @param root
     *            The root URI.
     * @param serial
     *            Flag for serial mode.
     * @param adaptor
     *            The adaptor for recovery.
     */

    public CompositeTransactionAdaptor ( String root , boolean serial ,
            RecoveryCoordinator adaptor , Properties properties )
    {
        super ( root , null , serial );
        adaptor_ = adaptor;
        root_ = root;
        if ( properties != null ) properties_ =  ( Properties ) properties.clone();
    }

    /**
     * @see CompositeCoordinator.
     */

    public CompositeCoordinator getCompositeCoordinator () throws SysException
    {
        return this;
    }

    /**
     * @see CompositeCoordinator.
     */

    public String getCoordinatorId ()
    {
        return root_;
    }

    /**
     * @see CompositeCoordinator.
     */

    public HeuristicMessage[] getTags ()
    {
        return null;
    }

    /**
     * @see CompositeCoordinator.
     */

    public RecoveryCoordinator getRecoveryCoordinator ()
    {
        return adaptor_;
    }

    public Boolean isRecoverableWhileActive ()
    {
        return isRecoverableWhileActive_;
    }

    public void setRecoverableWhileActive()
    {
        throw new UnsupportedOperationException();
    }
   
}
