/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin.jmx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * This class acts both as a JMX MBean ( allowing registration ) and as a
 * registry (keeping server references statically). Clients can get a handle to
 * a particular MBean server.
 */

public class JmxRegistry implements JmxRegistryMBean
{

    private static Map<JmxRegistry,MBeanServer> servers = new HashMap<JmxRegistry,MBeanServer>();
    // key=instance that received server handle; value=server handle

    public static Iterator<Entry<JmxRegistry,MBeanServer>> getServers ()
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
