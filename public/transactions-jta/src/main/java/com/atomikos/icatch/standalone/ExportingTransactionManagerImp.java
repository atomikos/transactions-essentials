/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.standalone;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(ExportingTransactionManagerImp.class);

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
