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

package com.atomikos.jms;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(AbstractConnectionFactoryBean.class);

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
