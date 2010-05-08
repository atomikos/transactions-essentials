package com.atomikos.jms.extra;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageListener;

import com.atomikos.icatch.system.Configuration;
import com.atomikos.jms.AtomikosConnectionFactoryBean;

 /**
  * 
  * A message-driven container for asynchronously receiving JMS messages
  * from a topic or queue, within a managed JTA transaction context.
  * 
  * Upon start, an instance of this class will create a number of
  * concurrent sessions that listen for incoming messages on the same destination.
  * MessageListener instances should be thread-safe if the pool size is larger
  * than one. Note: in general, after start() any changed properties are only
  * effective on the next start() event.
  * 
  * <p>
  * <b>IMPORTANT:</b> the transactional behaviour guarantees redelivery after failures.
  * As a side-effect, this can lead to so-called <em>poison messages</em>: messages
  * whose processing repeatedly fails due to some recurring error (for instance, a primary
  * key violation in the database, a NullPointerException, ...). Poison messages are problematic
  * because they can prevent other messages from being processed, and block the system.
  *  
  * To avoid poison messages, make sure that your MessageListener implementation 
  * only throws a <b>RuntimeException</b> when the problem is <em>transient</em>. In that
  * case, the system will perform rollback and the message will be redelivered
  * facing a clean system state. All non-transient errors (i.e., those that happen
  * each time a message is delivered) indicate problems at the application level
  * and should be dealt with by writing better application code.
  */

public class MessageDrivenContainer 
implements MessageConsumerSessionProperties
{
	private static final int DEFAULT_TIMEOUT = 30;

	
	private AtomikosConnectionFactoryBean connectionFactoryBean;
	private MessageListener messageListener;
	private String user;
	private String password;
	private Destination destination;
	private String destinationName;
	private int transactionTimeout;
	private int poolSize;
	private List sessions;
	private boolean daemonThreads;
	private boolean notifyListenerOnClose;
	private String messageSelector;
	private ExceptionListener exceptionListener;
	private String subscriberName;
	private boolean noLocal;
	private boolean unsubscribeOnClose;
	
	public MessageDrivenContainer()
	{
		sessions = new ArrayList ();
		notifyListenerOnClose = false;
        setPoolSize ( 1 );
        setTransactionTimeout ( DEFAULT_TIMEOUT );
	}
	
	private MessageConsumerSession createSession() 
	{
		return new MessageConsumerSession ( this );
	}
	
	/**
	 * Sets the connection factory to use. Required.
	 * @param bean
	 */
	public void setAtomikosConnectionFactoryBean ( AtomikosConnectionFactoryBean bean )
	{
		this.connectionFactoryBean = bean;
	}
	
	public AtomikosConnectionFactoryBean getAtomikosConnectionFactoryBean()
	{
		return connectionFactoryBean;
	}
	
	/**
	 * Gets the destination. 
	 * 
	 * @return The destination, or null if not set.
	 */
	public Destination getDestination()
	{
		return destination;
	}
	
	/**
	 * Sets the JMS destination to listen on (required unless the destinationName is set instead).
	 * 
	 * @param dest
	 */
	public void setDestination ( Destination dest )
	{
		this.destination = dest;
	}
	
	/**
	 * Gets the destination name.
	 * 
	 * @return The name, or null if not set.
	 */
	public String getDestinationName()
	{
		return destinationName;
	}
	
	/**
	 * Sets the JMS provider-specific destination name
	 * (required unless the destination is set directly). 
	 * 
	 * @param destinationName
	 */
	public void setDestinationName ( String destinationName )
	{
		this.destinationName = destinationName;
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
	 * Sets the size of the session pool (optional).
	 * Default is 1.
	 * 
	 * @param size 
	 */
	public void setPoolSize ( int size ) 
	{
	    poolSize = size;
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
	 * Sets the exception listener (optional). The listener will be
	 * notified of connection-level JMS errors.
	 * 
	 * @param exceptionListener
	 */
	public void setExceptionListener ( ExceptionListener exceptionListener ) 
	{
		this.exceptionListener = exceptionListener;
	}
	
	/**
	 * Test if this instance will receive sends from the same connection.
	 * 
	 * @return
	 */
	public boolean isNoLocal() {
		return noLocal;
	}
	
	/**
	 * Sets whether or not this topic should receive sends from the 
	 * same connection (optional). 
	 * 
	 * @param noLocal 
	 */

	public void setNoLocal(boolean noLocal) {
		this.noLocal = noLocal;
	}

	/**
	 * Gets the subscriber name (for durable subscribers).
	 * @return The name, or null if not set (no durable subscriber).
	 */
	
	public String getSubscriberName() {
		return subscriberName;
	}

	/**
	 * Sets the name to use for durable subscriptions (optional).
	 * <br>
	 * <b>Note: this name will be appended with a suffix to ensure uniqueness
	 * among instances in the pool. Otherwise, the JMS back-end would see 
	 * multiple instances subscribing with the same name - an error.</b>
	 * 
	 * @param subscriberName
	 */
	
	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
	}

	protected boolean getNoLocal() {
		
		return isNoLocal();
	}
	
	/**
	 * Start listening for messages.
	 * 
	 * @throws JMSException
	 */
	public void start() throws JMSException 
	{
	    if ( destination == null && destinationName == null )
	        throw new JMSException (
	                "MessageDrivenContainer: destination not specified" );
	    if ( connectionFactoryBean == null )
	        throw new JMSException (
	                "MessageDrivenContainer: factory not set" );
	    if ( messageListener == null )
	        throw new JMSException (
	                "MessageDrivenContainer: messageListener not set" );
	    for ( int i = 0; i < poolSize; i++ ) {
	        MessageConsumerSession s = createSession();
	        s.setMessageListener ( messageListener );
	        s.setPassword ( password );
	        s.setUser ( user );
	        s.setDestination ( destination );
	        s.setDestinationName ( destinationName );
	        s.setAtomikosConnectionFactoryBean ( connectionFactoryBean );
	        s.setDaemonThreads ( daemonThreads );
	        s.setNotifyListenerOnClose ( notifyListenerOnClose );
	        s.setMessageSelector ( getMessageSelector () );
	        s.setExceptionListener ( exceptionListener );
	        s.setNoLocal( noLocal );
	        s.setSubscriberName( subscriberName );
	        //set subscriber name with suffix to ensure unique names
	        if ( getSubscriberName() != null ) s.setSubscriberName ( getSubscriberName() + "-" + i );
	        s.setNoLocal ( getNoLocal() );
	        try {
	            s.startListening ();
	            // System.out.println ( "MessageDrivenContainer: started
	            // session");
	        } catch ( Exception e ) {
	            Configuration.logWarning ( "Error starting pool", e );
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
	 * (optional). Default is false.
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
	
	/**
	 * Sets whether unsubscribe should be done at closing time (optional). Default is false.
	 * 
	 * @param b If true, then unsubscribe will be done at closing time. This only applies to 
	 * durable subscribers (i.e., cases where subscriberName is set).
	 */
	public void setUnsubscribeOnClose ( boolean b ) 
	{
		this.unsubscribeOnClose = b;
	}
	
	/**
	 * Getter to test if unsubscribe should be called on close.
	 */
	
	public boolean getUnsubscribeOnClose()
	{
		return unsubscribeOnClose;
	}

}
