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

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.XATopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.DefaultXidFactory;
import com.atomikos.datasource.xa.XidFactory;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * Use this class to access JMS topics within your JTA transactions: rollback of
 * the transaction will also cancel any messages sent or received. Instances of
 * this class need a JMS vendor-specific instance of XATopicConnectionFactory to
 * work with. Check your JMS-vendor's documentation on how to do that. Instances
 * can be set up in a GUI wizard tool and saved on disk or in JNDI. No explicit
 * registration with the transaction engine is necessary: this class does
 * everything automatically. As soon as an instance is created, it is fully
 * capable of interacting with the Atomikos transaction manager, and will
 * transparently take part in active transactions.
 * 
 * <p>
 * <b>Note: any property changes made AFTER getting the first connection
 * will NOT have any effect!</b>
 * <p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 */


public class TopicConnectionFactoryBean 
extends AbstractConnectionFactoryBean
implements TopicConnectionFactory
{
	private transient JtaTopicConnectionFactory factory_;
	
	private XATopicConnectionFactory xaFactory_;
	//where to get the XA connections from
	
	
	   protected synchronized void checkSetup () throws JMSException
	    {
	        factory_ = JtaTopicConnectionFactory.getInstance ( resourceName_ );

	        if ( factory_ != null )
	            return;
	        else {
	            // ONLY attempt getOrCreate if getInstance fails; to avoid
	            // overhead of synchronization of getOrCreate

	            XidFactory xidFactory = null;

	            xidFactory = new DefaultXidFactory ();

	            if ( xaFactory_ == null && "".equals ( xaFactoryJndiName_ ) )
	                throw new JMSException (
	                        "TopicConnectionFactoryBean: XATopicConnectionFactory not set?" );
	            if ( !"".equals ( xaFactoryJndiName_ ) ) {
	                try {

	                    // lookup factory in JNDI
	                    Context ctx = new InitialContext ();
	                    Context env = (Context) ctx.lookup ( "java:comp/env" );
	                    xaFactory_ = (XATopicConnectionFactory) env
	                            .lookup ( xaFactoryJndiName_ );

	                } catch ( NamingException ne ) {
	                    throw new JMSException (
	                            "TopicConnectionFactoryBean: error retrieving factory: "
	                                    + ne.getMessage () );
	                }
	            }
	            
	            factory_ = JtaTopicConnectionFactory.getOrCreate ( resourceName_,
	                    xaFactory_, xidFactory );
	            TransactionalResource res = factory_.getTransactionalResource ();
	            if ( Configuration.getResource ( res.getName () ) == null )
	                Configuration.addResource ( res );
	            
	            StringBuffer msg = new StringBuffer();
	            msg.append ( "TopicConnectionFactoryBean configured with [" );
	            msg.append ( "resourceName=" ).append(resourceName_).append (", ");
	            msg.append ( "xaFactoryJndiName=" ).append( xaFactoryJndiName_ );
	            msg.append ( "]" );
	            Configuration.logDebug ( msg.toString() );
	            
	            Configuration.logWarning ( "WARNING: class " + getClass().getName() + " is deprecated!" );
	        }
	    }
	
    /**
     * Sets the XATopicConnectionFactory to use. 
     * This method is optional and an
     * alternative to setXaFactoryJndiName.
     * 
     * @param xaFactory
     *            The object to use.
     */
	
	public void setXaTopicConnectionFactory ( XATopicConnectionFactory factory )
	{
		this.xaFactory_ = factory;
	}
	
    /**
     * Get the XATopicConnectionFactory as previously set.
     * 
     * @return XATopicConnectionFactory The factory, or null if only the JNDI
     *         name was set.
     */
	
	public XATopicConnectionFactory getXaTopicConnectionFactory()
	{
		return xaFactory_;
	}
	
	/**
	 * Creates a default topic connection.
	 */
	public TopicConnection createTopicConnection() throws JMSException 
	{
		checkSetup ();
        return factory_.createTopicConnection ();
	}

	/**
	 * Creates a topic connection with given user credentials.
	 */
	public TopicConnection createTopicConnection ( 
			String userName , String password )
			throws JMSException 
	{
		checkSetup ();
        return factory_.createTopicConnection ( userName , password );
	}

	/**
	 * Creates a default connection.
	 */
	public Connection createConnection() throws JMSException 
	{
		return createTopicConnection();
	}

	/**
	 * Creates a default connection with given user credentials.
	 */
	public Connection createConnection ( 
			String userName , String password )
			throws JMSException 
	{
		return createTopicConnection ( userName , password );
	}

	

	public boolean equals ( Object o )
	{
		boolean ret = false;
		if ( o instanceof TopicConnectionFactoryBean ) {
			TopicConnectionFactoryBean other = ( TopicConnectionFactoryBean ) o;
			if ( resourceName_ != null ) {
				ret = resourceName_.equals( other.resourceName_ );
			}
			else {
				//without a resource name we can't compare here
				ret = super.equals ( o );
			}
		}
		return ret;
	}
	
	public int hashCode()
	{
		int ret = 0;
		if ( resourceName_ != null ) ret = resourceName_.hashCode();
		else ret = super.hashCode();
		return ret;
	}





}
