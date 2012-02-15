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

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.transaction.Status;
import javax.transaction.SystemException;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.icatch.system.Configuration;

 /**
  * 
  * 
  * Common logic for the message producer session.
  *
  */

public abstract class MessageProducerSession 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(MessageProducerSession.class);

	private AbstractConnectionFactoryBean connectionFactoryBean;
	private String user;
	private String password;
	private Destination destination;
	private Destination replyToDestination;
	private int deliveryMode;
	private int priority;
	private long timeToLive;
	private transient Connection connection;
	private transient Session session;
	private transient MessageProducer sender;

	protected MessageProducerSession()
	{
        // set default values according to Sun's JMS javadocs
        setTimeToLive ( 0 );
        setDeliveryMode ( javax.jms.DeliveryMode.PERSISTENT );
        setPriority ( 4 );
	}
	
	private synchronized MessageProducer refresh() throws JMSException 
	{
	    MessageProducer ret = null;
	    if ( destination == null )
	        throw new JMSException ( "Please call setDestination first!" );
	
	    if ( user != null ) {
	        connection = connectionFactoryBean.createConnection (
	                user, password );
	
	    } else {
	        connection = connectionFactoryBean.createConnection ();
	    }
	    connection.start ();
	    session = connection.createSession ( true, 0 );
	    ret = session.createProducer ( destination );
	    sender = ret;
	    
	    return ret;
	}

	private synchronized void closeResources() 
	{
	    if ( session != null )
	        try {
	            session.close ();
	        } catch ( JMSException e ) {
	            Configuration.logWarning ( "Error closing JMS session", e );
				Configuration.logWarning ( "MessageProducerSession: linked exception is " , e.getLinkedException() );        
			} finally {
	        	//fix for issue 23119
	        	session = null;
	        }
	        
	    if ( connection != null )
	        try {
	            connection.close ();
	        } catch ( JMSException e ) {
	            Configuration.logWarning ( "Error closing JMS connection", e );
	        	Configuration.logWarning ( "MessageProducerSession: linked exception is " , e.getLinkedException() );        
			} finally {
	        	connection = null;
	        }
	    sender = null;
	}
	
	protected void setAbstractConnectionFactoryBean ( AbstractConnectionFactoryBean bean )
	{
		this.connectionFactoryBean = bean;
	}
	
	protected AbstractConnectionFactoryBean getAbstractConnectionFactoryBean()
	{
		return connectionFactoryBean;
	}
	
	protected Destination getDestination()
	{
		return destination;
	}

	protected void setDestination ( Destination destination )
	{
		this.destination = destination;
	}
	
	protected abstract String getDestinationName();
	
	protected abstract String getReplyToDestinationName();
	
	/**
	 * Initializes the session for sending. 
	 * Call this method first.
	 */
	
	public void init() 
	{
	    StringBuffer msg = new StringBuffer();
	    msg.append ( "MessageProducerSession configured with [" );
	    msg.append ( "user=" ).append ( getUser() ).append ( ", " );
	    msg.append ( "password=" ).append ( password ).append ( ", " );
	    msg.append ( "deliveryMode=" ).append ( getDeliveryMode() ).append ( ", " );
	    msg.append ( "timeToLive=" ).append ( getTimeToLive() ).append ( ", " );
	    msg.append ( "priority=" ).append ( getPriority() ).append ( ", " );
	    msg.append ( "destination=" ).append( getDestinationName() ).append ( ", " );
	    msg.append ( "replyToDestination=" ).append ( getReplyToDestinationName() );
	    msg.append ( "]" );
	    if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( msg.toString() );
	}
	
	/**
	 * @return The user to connect with, or null if no explicit authentication
	 *         is to be used.
	 */
	public String getUser() 
	{
	    return user;
	}
	
	/**
	 * If this session is used for sending request/reply messages, then this
     * property indicates the destination where the replies are to be sent (optional). The
     * session uses this to set the JMSReplyTo header accordingly. This property
     * can be omitted if no reply is needed.
     * 
     * <p>
     * The replyToDestination should be in the same JMS vendor domain as the send
     * queue. To cross domains, configure a bridge for both the request and the
     * reply channels.
	 */
	public void setReplyToDestination ( Destination destination )
	{
		this.replyToDestination = destination;
	}
	
	/**
	 * Gets the replyToDestination.
	 * 
	 * @return
	 */
	public Destination getReplyToDestination()
	{
		return replyToDestination;
	}

	/**
	 * Set the password for explicit authentication (optional). 
	 * This is only required if
	 * the user has also been set.
	 * 
	 * @param password
	 *            The password.
	 */
	public void setPassword ( String password ) 
	{
	    this.password = password;
	}

	/**
	 * Set the user to use for explicit authentication (optional). If no explicit
	 * authentication is required then this method should not be called.
	 * 
	 * @param user
	 */
	public void setUser ( String user ) 
	{
	    this.user = user;
	}

	/**
	 * Send a message to the destination queue, in a transactional way.
	 * 
	 * This method will open or reopen any connections if required, so that the
	 * client doesn't have to worry about that.
	 * 
	 * Note: this method will fail if there is no transaction for the calling
	 * thread.
	 * 
	 * @param message
	 * @throws JMSException
	 *             On failures.
	 */
	public void sendMessage ( Message message ) throws JMSException 
	{
	    UserTransactionManager tm = new UserTransactionManager ();
	    try {
	        if ( tm.getStatus () != Status.STATUS_ACTIVE )
	            throw new JMSException (
	                    "This method requires an active transaction!" );
	    } catch ( SystemException e ) {
	        Configuration
	                .logWarning ( "Error in getting transaction status", e );
	        throw new RuntimeException ( e.getMessage () );
	    }
	
	    try {
	        if ( sender == null )
	            sender = refresh ();
	        if ( replyToDestination != null )
	            message.setJMSReplyTo ( replyToDestination );
	
	        if ( Configuration.isInfoLoggingEnabled() ) Configuration.logInfo ( "Calling send ( " + message + " ,  "
	                + deliveryMode + " , " + priority + " , " + timeToLive
	                + " )..." );
	        sender.send ( message, deliveryMode, priority, timeToLive );
	        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Send done!" );
	
	    } catch ( JMSException e ) {
	        closeResources ();
	        sender = null;
	        Configuration.logWarning ( "MessageProducerSession: error in sending JMS message", e );
	        Configuration.logWarning ( "MessageProducerSession: linked exception is " , e.getLinkedException() );
	        throw e;
	    }
	}

	/**
	 * Create a text message.
	 * 
	 * @return
	 */
	public TextMessage createTextMessage() throws JMSException 
	{
	    TextMessage ret = null;
	
	    try {
	        if ( session == null )
	            refresh ();
	        ret = session.createTextMessage ();
	    } catch ( JMSException e ) {
	        Configuration.logWarning ( "MessageProducerSession: error creating new message", e );
	        Configuration.logWarning ( "MessageProducerSession: linked exception is " , e.getLinkedException() );
	        closeResources ();
	        throw e;
	    }
	
	    return ret;
	}

	/**
	 * Create a map message.
	 * 
	 * @return
	 */
	public MapMessage createMapMessage() throws JMSException 
	{
	    MapMessage ret = null;
	
	    try {
	        if ( session == null )
	            refresh ();
	        ret = session.createMapMessage ();
	    } catch ( JMSException e ) {
	        Configuration.logWarning ( "MessageProducerSession: error creating new message", e );
	        Configuration.logWarning ( "MessageProducerSession: linked exception is " , e.getLinkedException() );
	        closeResources ();
	        throw e;
	    }
	
	    return ret;
	}

	/**
	 * Create an object message.
	 * 
	 * @return
	 */
	public ObjectMessage createObjectMessage() throws JMSException 
	{
	    ObjectMessage ret = null;
	
	    try {
	        if ( session == null )
	            refresh ();
	        ret = session.createObjectMessage ();
	    } catch ( JMSException e ) {
	        Configuration.logWarning ( "MessageProducersession: error creating new message", e );
	        Configuration.logWarning ( "MessageProducerSession: linked exception is " , e.getLinkedException() );
	        closeResources ();
	        throw e;
	    }
	
	    return ret;
	}

	/**
	 * Create a bytes message.
	 * 
	 * @return
	 */
	public BytesMessage createBytesMessage() throws JMSException 
	{
	    BytesMessage ret = null;
	
	    try {
	        if ( session == null )
	            refresh ();
	        ret = session.createBytesMessage ();
	    } catch ( JMSException e ) {
	        Configuration.logWarning ( "MessageProducerSession: error creating new message", e );
	        Configuration.logWarning ( "MessageProducerSession: linked exception is " , e.getLinkedException() );
	        closeResources ();
	        throw e;
	    }
	
	    return ret;
	}

	/**
	 * Create a stream message.
	 * 
	 * @return
	 */
	public StreamMessage createStreamMessage() throws JMSException 
	{
	    StreamMessage ret = null;
	
	    try {
	        if ( session == null )
	            refresh ();
	        ret = session.createStreamMessage ();
	    } catch ( JMSException e ) {
	        Configuration.logWarning ( "MessageProducerSession: error creating new message", e );
	        Configuration.logWarning ( "MessageProducerSession: linked exception is " , e.getLinkedException() );
	        closeResources ();
	        throw e;
	    }
	
	    return ret;
	}

	/**
	 * Close any open resources. This method should be called when the client no
	 * longer needs the session.
	 */
	public void stop() 
	{
	    closeResources ();
	}

	/**
	 * @return The deliverymode for messages sent in this session.
	 */
	public int getDeliveryMode() 
	{
	    return deliveryMode;
	}

	/**
	 * @return The priority for messages sent in this session.
	 */
	public int getPriority() 
	{
	    return priority;
	}

	/**
	 * @return The timeToLive for messages sent in this session.
	 */
	public long getTimeToLive() 
	{
	    return timeToLive;
	}

	/**
	 * 
	 * Set the deliverymode for messages sent in this session (optional). Defaults to
	 * persistent.
	 * 
	 * @param
	 */
	public void setDeliveryMode ( int i ) 
	{
	    deliveryMode = i;
	}

	/**
	 * Set the priority for messages sent in this session (optional).
	 * 
	 * @param
	 */
	public void setPriority ( int i ) 
	{
	    priority = i;
	}

	/**
	 * Set the time to live for messages sent in this session (optional).
	 * 
	 * @param
	 */
	public void setTimeToLive ( long l ) 
	{
	    timeToLive = l;
	}

}
