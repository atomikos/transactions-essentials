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

package com.atomikos.icatch.admin.jmx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * This class acts both as a JMX MBean ( allowing registration ) and as a
 * registry (keeping server references statically). Clients can get a handle to
 * a particular MBean server.
 */

public class JmxRegistry implements JmxRegistryMBean
{

    private static Map servers = new HashMap ();
    // key=instance that received server handle; value=server handle

    public static Iterator getServers ()
    {
        return servers.entrySet ().iterator ();
    }

    protected static synchronized void addServer ( JmxRegistry adder ,
            MBeanServer server )
    {
        servers.put ( adder, server );
    }

    protected static synchronized void removeServer ( JmxRegistry adder )
    {
        servers.remove ( adder );
    }

    public JmxRegistry ()
    {
        super ();

    }

    public ObjectName preRegister ( MBeanServer arg0 , ObjectName arg1 )
            throws Exception
    {
        addServer ( this, arg0 );
        return null;
    }

    public void postRegister ( Boolean arg0 )
    {

    }

    public void preDeregister () throws Exception
    {

        removeServer ( this );
    }

    public void postDeregister ()
    {

    }

}
