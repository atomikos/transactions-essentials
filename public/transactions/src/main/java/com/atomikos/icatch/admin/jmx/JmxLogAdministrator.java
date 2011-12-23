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

class JmxLogAdministrator implements LogAdministrator
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
