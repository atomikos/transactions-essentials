/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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

public class CompositeTransactionAdaptor extends AbstractCompositeTransaction
        implements CompositeCoordinator
{

	private static final long serialVersionUID = 6361601412982044104L;

	private RecoveryCoordinator adaptorForReplayRequests_; 

    private String root_;

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

    public CompositeTransactionAdaptor ( Stack lineage , String tid ,
            boolean serial , RecoveryCoordinator adaptor  )
    {
        super ( tid , (Stack) lineage.clone () , serial  );
        adaptorForReplayRequests_ = adaptor;
        Stack tmp = (Stack) lineage.clone();
        CompositeTransaction parent = null;
        while ( !tmp.empty () ) {
            parent = (CompositeTransaction) tmp.pop();
        }
        root_ = parent.getTid();
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

    public String getCoordinatorId ()
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

}
