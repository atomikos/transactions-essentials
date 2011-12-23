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

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.admin.AdminTransaction;

/**
 * An MBean wrapper for a prepared AdminTransaction.
 */

public class JmxPreparedTransaction extends JmxTransaction implements
        JmxPreparedTransactionMBean
{

    /**
     * @param adminTransaction
     *            The wrapped instance.
     */

    public JmxPreparedTransaction ( AdminTransaction adminTransaction )
    {
        super ( adminTransaction );

    }

    /**
     * @see com.atomikos.icatch.admin.jmx.PreparedTransactionMBean#forceCommit()
     */

    public void forceCommit () throws HeurRollbackException,
            HeurHazardException, HeurMixedException, SysException
    {
        try {
            getAdminTransaction ().forceCommit ();
        } finally {
            unregister ();
        }

    }

    /**
     * @see com.atomikos.icatch.admin.jmx.PreparedTransactionMBean#forceRollback()
     */

    public void forceRollback () throws HeurCommitException,
            HeurHazardException, HeurMixedException, SysException
    {
        try {
            getAdminTransaction ().forceRollback ();
        } finally {
            unregister ();
        }

    }

}
