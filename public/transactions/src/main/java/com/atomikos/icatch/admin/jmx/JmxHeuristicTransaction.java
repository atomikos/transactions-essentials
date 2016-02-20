/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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
