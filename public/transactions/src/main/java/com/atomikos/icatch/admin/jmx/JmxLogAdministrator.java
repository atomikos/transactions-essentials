/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin.jmx;

import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.LogControl;

/**
 * A JMX LogAdministrator, implemented as a Singleton. The instance can be
 * registered in the TSInitInfo for the JMX management to work. <b>In addition,
 * you should register a in instance of the JmxTransactionService MBean in your
 * MBean server: that will make the administration functions available via JMX.</b>
 * Instances of the JmxTransactionService will collaborate with this class to
 * perform the JMX administration of the pending transactions.
 */

public class JmxLogAdministrator implements LogAdministrator
{

    private static final JmxLogAdministrator theInstance = new JmxLogAdministrator ();

    public static JmxLogAdministrator getInstance ()
    {
        return theInstance;
    }

    private LogControl logControl;

    protected JmxLogAdministrator ()
    {
        super ();
    }

    /**
     * @see com.atomikos.icatch.admin.LogAdministrator#registerLogControl(com.atomikos.icatch.admin.LogControl)
     */

    public synchronized void registerLogControl ( LogControl control )
    {
        logControl = control;

    }

    /**
     * Gets the LogControl.
     *
     * @return LogControl, or null if unregistered.
     */

    public synchronized LogControl getLogControl ()
    {
        return logControl;
    }

    /**
     * @see com.atomikos.icatch.admin.LogAdministrator#deregisterLogControl(com.atomikos.icatch.admin.LogControl)
     */

    public synchronized void deregisterLogControl ( LogControl control )
    {
        logControl = null;

    }

}
