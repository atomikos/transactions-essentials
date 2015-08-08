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

package com.atomikos.icatch.admin.jmx;

import com.atomikos.icatch.admin.AdminTransaction;

/**
 *
 * An MBean wrapper for a heuristic AdminTransaction.
 *
 */

public class JmxHeuristicTransaction extends JmxTransaction implements
        JmxHeuristicTransactionMBean
{

    /**
     * @param adminTransaction
     *            The instance to wrap
     */

    public JmxHeuristicTransaction ( AdminTransaction adminTransaction )
    {
        super ( adminTransaction );

    }

    /**
     * @see com.atomikos.icatch.admin.jmx.JmxHeuristicTransactionMBean#wasCommitted()
     */

    public boolean getCommitted ()
    {

        return getAdminTransaction ().wasCommitted ();
    }

    /**
     * @see com.atomikos.icatch.admin.jmx.JmxHeuristicTransactionMBean#forceForget()
     */

    public void forceForget ()
    {
        getAdminTransaction ().forceForget ();
        unregister ();

    }

}
