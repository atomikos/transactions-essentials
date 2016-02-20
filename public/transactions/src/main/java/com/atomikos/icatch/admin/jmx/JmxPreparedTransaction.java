/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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
