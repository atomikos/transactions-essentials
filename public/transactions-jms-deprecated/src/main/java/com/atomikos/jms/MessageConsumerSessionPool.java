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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * Common functionality for pooled listener sessions.
 *
 */

public abstract class MessageConsumerSessionPool 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(MessageConsumerSessionPool.class);

	private AbstractConnectionFactoryBean connectionFactoryBean;
	private MessageListener messageListener;
	private String user;
	private String password;
	private Destination destination;
	private int transactionTimeout;
	private int poolSize;
	private List sessions;
	private boolean daemonThreads;
	private boolean notifyListenerOnClose;
	private String messageSelector;
	private ExceptionListener exceptionListener;

	protected MessageConsumerSessionPool()
	{
		sessions = new ArrayList ();
		notifyListenerOnClose = false;
        setPoolSize ( 1 );
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
	
	protected void setDestination ( Destination dest )
	{
		this.destination = dest;
	}
	
	/**
	 * Sets whether threads should be daemon threads or not (optional).
	 * Default is false.
	 * @param value If true then threads will be daemon threads.
	 */
	public void setDaemonThreads ( boolean value ) 
	{
			this.daemonThreads = value;
	}

	/**
	 * Tests whether threads are daemon threads.
	 * @return True if threads are deamons.
	 */
	public boolean getDaemonThreads() 
	{
			return daemonThreads;
	}

	/**
	 * 
	 * Get the message listener if any.
	 * 
	 * @return
	 */
	public MessageListener getMessageListener() 
	{
	    return messageListener;
	}

	/**
	 * Get the transaction timeout.
	 * 
	 * @return
	 */
	public int getTransactionTimeout() 
	{
	    return transactionTimeout;
	}

	/**
	 * Get the user for connecting, or null if the default user should be used.
	 * 
	 * @return
	 */
	public String getUser() 
	{
	    return user;
	}

	/**
	 * Set the message listener to use (required). 
	 * The same instance will be used for each
	 * session in the pool, meaning that instances need to be thread-safe. Only
	 * one listener is allowed at a time. Call this method with a null argument
	 * to unset the listener.
	 * 
	 * @param listener
	 */
	public void setMessageListener ( MessageListener listener ) 
	{
	
	    messageListener = listener;
	    Iterator it = sessions.iterator ();
	    while ( it.hasNext () ) {
	        MessageConsumerSession s = (MessageConsumerSession) it.next ();
	        s.setMessageListener ( listener );
	    }
	}

	/**
	 * Set the password if explicit authentication is needed (optional). 
	 * You need to set this if the user is also set.
	 * 
	 * @param string
	 */
	public void setPassword ( String string ) 
	{
	    password = string;
	}

	/**
	 * Set the transaction timeout in seconds (optional).
	 * 
	 * @param i
	 */
	public void setTransactionTimeout ( int i ) 
	{
	    transactionTimeout = i;
	}

	/**
	 * Set the user to use for explicit authentication (optional). 
	 * Don't set this property
	 * if you want to use the default authentication.
	 * 
	 * @param string
	 */
	public void setUser ( String string ) 
	{
	    user = string;
	}

	/**
	 * Get the message selector (if any)
	 * 
	 * @return The selector, or null if none.
	 */
	public String getMessageSelector() 
	{
	    return this.messageSelector;
	}

	/**
	 * Set the message selector to use (optional).
	 * 
	 * @param selector
	 */
	public void setMessageSelector ( String selector ) 
	{
	    this.messageSelector = selector;
	}

	/**
	 * Get the size of the pool.
	 * 
	 * @return
	 */
	public int getPoolSize() 
	{
	    return poolSize;
	}

	/**
	 * Sets the size of the session pool.
	 * 
	 * @param i
	 */
	public void setPoolSize ( int i ) 
	{
	    poolSize = i;
	}

	/**
	 * Gets the exception listener (if any). 
	 * @return Null if no ExceptionListener was set.
	 */
	public ExceptionListener getExceptionListener() 
	{
		return exceptionListener;
	}

	/**
	 * Sets the exception listener. The listener will be
	 * notified of connection-level JMS errors.
	 * 
	 * @param exceptionListener
	 */
	public void setExceptionListener ( ExceptionListener exceptionListener ) 
	{
		this.exceptionListener = exceptionListener;
	}
	
	protected abstract MessageConsumerSession createSession();
	
	protected abstract boolean getNoLocal();
	
	protected abstract String getSubscriberName();
	
	/**
	 * Start listening for messages.
	 * 
	 * @throws JMSException
	 */
	public void start() throws JMSException 
	{
	    if ( destination == null )
	        throw new JMSException (
	                "MessageConsumerSessionPool: destination not specified" );
	    if ( connectionFactoryBean == null )
	        throw new JMSException (
	                "MessageConsumerSessionPool: factory not set" );
	    if ( messageListener == null )
	        throw new JMSException (
	                "MessageConsumerSessionPool: messageListener not set" );
	    for ( int i = 0; i < poolSize; i++ ) {
	        MessageConsumerSession s = createSession();
	        s.setMessageListener ( messageListener );
	        s.setPassword ( password );
	        s.setUser ( user );
	        s.setDestination ( destination );
	        s.setAbstractConnectionFactoryBean ( connectionFactoryBean );
	        s.setTransactionTimeout ( transactionTimeout );
	        s.setDaemonThreads ( daemonThreads );
	        s.setNotifyListenerOnClose ( notifyListenerOnClose );
	        s.setMessageSelector ( getMessageSelector () );
	        s.setExceptionListener ( exceptionListener );
	        //set subscriber name with suffix to ensure unique names
	        if ( getSubscriberName() != null ) s.setSubscriberName ( getSubscriberName() + "-" + i );
	        s.setNoLocal ( getNoLocal() );
	        try {
	            s.startListening ();
	            // System.out.println ( "MessageConsumerSessionPool: started
	            // session");
	        } catch ( Exception e ) {
	            LOGGER.logWarning ( "Error starting pool", e );
	        }
	        sessions.add ( s );
	    }
	
	    // set listener again to trigger listening
	    setMessageListener ( messageListener );
	}

	/**
	 * Stop listening for messages. If <b>notifyListenerOnClose</b> is set then
	 * calling this method will notify the listener by calling its onMessage
	 * method with a null argument (and also without transaction context).
	 * 
	 */
	public void stop() 
	{
	    Iterator it = sessions.iterator ();
	    while ( it.hasNext () ) {
	        MessageConsumerSession s = (MessageConsumerSession) it.next ();
	        s.stopListening ();
	    }
	}

	/**
	 * Getter to check whether the listener is notified on close.
	 * 
	 * @return
	 */
	public boolean getNotifyListenerOnClose() 
	{
	    return notifyListenerOnClose;
	}

	/**
	 * Set whether the listener should be notified of close events on the pool
	 * (optional).
	 * 
	 * @param b
	 *            If true, then the listener will receive a null message if the
	 *            pool is closed.
	 */
	public void setNotifyListenerOnClose ( boolean b ) 
	{
	    notifyListenerOnClose = b;
	    Iterator it = sessions.iterator ();
	    while ( it.hasNext () ) {
	      	MessageConsumerSession s = (MessageConsumerSession) it.next ();
	        s.setNotifyListenerOnClose ( b );
	    }
	}

}
