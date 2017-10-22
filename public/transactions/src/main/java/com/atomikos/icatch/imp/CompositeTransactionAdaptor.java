/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.SysException;

/**
 * A composite transaction adaptor for inter-position on an imported instance.
 * This allows substitution of the recovery coordinator adaptor.
 */

class CompositeTransactionAdaptor extends AbstractCompositeTransaction
        implements CompositeCoordinator
{

	private static final long serialVersionUID = 6361601412982044104L;

	private RecoveryCoordinator adaptorForReplayRequests_; 

    private String root_;
    
    private String coordinatorId;

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
     */
    @SuppressWarnings("unchecked")
    public CompositeTransactionAdaptor ( Stack<CompositeTransaction> lineage , String tid ,
            boolean serial , RecoveryCoordinator adaptor  )
    {
        super ( tid , (Stack<CompositeTransaction>) lineage.clone () , serial  );
        adaptorForReplayRequests_ = adaptor;
        Stack<CompositeTransaction> tmp = (Stack<CompositeTransaction>) lineage.clone();
        CompositeTransaction parent = null;
        while ( !tmp.empty () ) {
            parent = tmp.pop();
        }
        root_ = parent.getTid();
        this.coordinatorId = lineage.peek().getCompositeCoordinator().getCoordinatorId();
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
        adaptorForReplayRequests_ = adaptor;
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

    public String getRootId ()
    {
        return root_;
    }

    /**
     * @see CompositeCoordinator.
     */

    public RecoveryCoordinator getRecoveryCoordinator ()
    {
        return adaptorForReplayRequests_;
    }

	@Override
	public String getCoordinatorId() {
		return coordinatorId;
	}


}
