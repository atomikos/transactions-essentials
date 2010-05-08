package com.atomikos.datasource.xa.jmx;

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
