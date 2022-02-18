/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.SysException;

/**
 * A composite transaction adaptor for inter-position on an imported instance.
 * This allows substitution of the recovery coordinator adaptor.
 */

public class CompositeTransactionAdaptor extends AbstractCompositeTransaction
        implements CompositeCoordinator
{

	private static final long serialVersionUID = 6361601412982044104L;

	private RecoveryCoordinator adaptorForReplayRequests; 

    private String root;
    
    private String coordinatorId;


    /**
     * Constructor for testin.
     * @param root
     * @param serial
     * @param adaptor
     */

    public CompositeTransactionAdaptor ( String root , boolean serial ,
            RecoveryCoordinator adaptor )
    {
        super ( root , null , serial );
        adaptorForReplayRequests = adaptor;
        this.root = root;
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
        return root;
    }

    /**
     * @see CompositeCoordinator.
     */

    public RecoveryCoordinator getRecoveryCoordinator ()
    {
        return adaptorForReplayRequests;
    }

	@Override
	public String getCoordinatorId() {
		return coordinatorId;
	}


}
