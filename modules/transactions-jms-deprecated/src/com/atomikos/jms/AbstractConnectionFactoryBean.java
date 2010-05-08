package com.atomikos.jms;

import java.io.Serializable;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

import com.atomikos.util.SerializableObjectFactory;

/**
 * 
 * 
 * Common logic for the connection factory beans.
 *
 */

public abstract class AbstractConnectionFactoryBean 
implements Serializable, Referenceable, ConnectionFactory
{

	protected String resourceName_;
	protected String xaFactoryJndiName_;

	protected AbstractConnectionFactoryBean ( )
	{
		this.xaFactoryJndiName_ = "";
		this.resourceName_ = "someUniqueName";
	}
	
	/**
	 * Sets the JNDI name of the underlying XAConnectionFactory (optional). This is
	 * optional and an alternative to directly supplying the required factory
	 * through setXaConnectionFactory().
	 * 
	 * @param name
	 *            The JNDI name where the XAConnectionFactory can be found.
	 *            It is up to the client to make sure that the name exists and
 *            points to an existing XAConnectionFactory.
	 */
	public void setXaFactoryJndiName ( String name ) 
	{
	    xaFactoryJndiName_ = name;
	
	}

	/**
	 * Retrieve the JNDI name where the XAConnectionFactory is expected.
	 * 
	 * @return String the name or an empty String if not set.
	 */
	public String getXaFactoryJndiName() 
	{
	    return xaFactoryJndiName_;
	}

	/**
	 * Set the unique resource name for this factory (required). A unique
	 * resource name is needed by the transaction service in order to register
	 * and recover the underlying XA transactions. 
	 * Note: the value you set here should not exceed 45 bytes in length.
	 * 
	 * <p><b>MQSeries NOTE:</b> For
	 * IBM MQSeries, the name should include MQSeries_XA_RMI or the XA routines
	 * will not work properly! 
	 * 
	 * @param name
	 *            The unique resource name.
	 */
	public void setResourceName ( String name ) 
	{
	    resourceName_ = name;
	}

	/**
	 * Get the resource name.
	 * 
	 * @return String the unique resource name as previously set.
	 */
	public String getResourceName() 
	{
	    return resourceName_;
	}

	public Reference getReference() throws NamingException 
	{
	    return SerializableObjectFactory.createReference ( this );
	}
	
    /**
     * Initialization method to register the underlying resource for recovery
     * and other init code. 
     * 
     * @throws JMSException
     */
    
    public void init() throws JMSException
    {
    		checkSetup();
    }
    
    protected abstract void checkSetup() throws JMSException;

}
