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
