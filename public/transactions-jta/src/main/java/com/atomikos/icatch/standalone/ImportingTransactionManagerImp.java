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
        // Configuration.instance().getCompositeTransactionManager();
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
