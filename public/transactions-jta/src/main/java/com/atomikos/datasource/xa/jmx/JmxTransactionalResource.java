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

package com.atomikos.datasource.xa.jmx;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.atomikos.datasource.xa.DefaultXidFactory;
import com.atomikos.datasource.xa.XATransactionalResource;

/**
 * 
 * 
 * 
 * 
 * 
 * JMX management bean for resources.
 */

public class JmxTransactionalResource implements JmxTransactionalResourceMBean,
        MBeanRegistration
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(JmxTransactionalResource.class);

    private XATransactionalResource resource;

    private XAResourceConfig config;

    private String localName;

    // the locally unique name property of the corresponding resource
    // needed because the full name may have unacceptable format

    public JmxTransactionalResource ( XATransactionalResource resource ,
            XAResourceConfig config , String localName )
    {
        super ();
        this.resource = resource;
        this.config = config;
        this.localName = localName;
        setUseWeakCompare ( config.usesWeakCompare () );
        setAcceptAllXAResources ( config.acceptsAllXAResources () );

    }

    /*
     * @see com.atomikos.datasource.xa.JmxTransactionalResourceMBean#getUseWeakCompare()
     */
    public boolean getUseWeakCompare ()
    {
        return resource.usesWeakCompare ();
    }

    /*
     * @see com.atomikos.datasource.xa.JmxTransactionalResourceMBean#setUseWeakCompare(boolean)
     */
    public void setUseWeakCompare ( boolean value )
    {
        resource.useWeakCompare ( value );
        config.setUseWeakCompare ( value );

    }

    /*
     * @see javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer,
     *      javax.management.ObjectName)
     */
    public ObjectName preRegister ( MBeanServer arg0 , ObjectName name )
            throws Exception
    {
        if ( name == null )
            name = new ObjectName ( "atomikos.resources", "name", resource
                    .getName () );

        return name;
    }

    /*
     * @see javax.management.MBeanRegistration#postRegister(java.lang.Boolean)
     */
    public void postRegister ( Boolean arg0 )
    {

    }

    /*
     * @see javax.management.MBeanRegistration#preDeregister()
     */
    public void preDeregister () throws Exception
    {

    }

    /*
     * @see javax.management.MBeanRegistration#postDeregister()
     */
    public void postDeregister ()
    {

    }

    /**
     * @see com.atomikos.datasource.xa.jmx.JmxTransactionalResourceMBean#getAcceptsAllXAResources()
     */
    public boolean getAcceptAllXAResources ()
    {
        return resource.acceptsAllXAResources ();
    }

    /**
     * @see com.atomikos.datasource.xa.jmx.JmxTransactionalResourceMBean#setAcceptsAllXAResources(boolean)
     */
    public void setAcceptAllXAResources ( boolean val )
    {
        resource.setAcceptAllXAResources ( val );
        config.setAcceptAllXAResources ( val );

    }

}
