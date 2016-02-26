/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.transaction.Status;
import javax.transaction.SystemException;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.AtomikosJMSException;
import com.atomikos.jms.AtomikosTransactionRequiredJMSException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

 /**
  * Common functionality for the sender templates.
  *
  */

public abstract class AbstractJmsSenderTemplate implements JmsSenderTemplate
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AbstractJmsSenderTemplate.class);

	private AtomikosConnectionFactoryBean connectionFactoryBean;
	private String user;
	private String password;
	private Destination destination;
	private String destinationName;
	private Destination replyToDestination;
	private String replyToDestinationName;
	private int deliveryMode;
	private int priority;
	private long timeToLive;
	private boolean inited;

	protected AbstractJmsSenderTemplate()
	{
		// set default values according to Sun's JMS javadocs
        setTimeToLive ( 0 );
        setDeliveryMode ( javax.jms.DeliveryMode.PERSISTENT );
        setPriority ( 4 );
	}

	protected abstract Session getOrRefreshSession ( Connection c ) throws JMSException;

	protected abstract Connection getOrReuseConnection() throws JMSException;

	protected abstract void afterUseWithoutErrors ( Connection c , Session s ) throws JMSException;


	protected void destroy ( Connection c , Session s)
	throws JMSException {

		try {
			if ( s != null ) s.close();
		} catch ( JMSException warn ) {
			LOGGER.logWarning ( this + ": error closing session" , warn);
		}

		try {
			if ( c != null ) c.close();
		} catch ( JMSException warn ) {
			LOGGER.logWarning ( this + ": error closing connection" , warn);
		}

	}

	protected synchronized Connection refreshConnection() throws JMSException {
		Connection connection = null;
	    if ( getDestinationName() == null )
	        throw new JMSException ( "Please call setDestination or setDestinationName first!" );

	    if ( user != null ) {
	        connection = connectionFactoryBean.createConnection (
	                user, password );

	    } else {
	        connection = connectionFactoryBean.createConnection ();
	    }
	    connection.start ();
	    return connection;
	}


	/**
	 * Initializes the session for sending.
	 * Call this method first.
	 */

	public void init() throws JMSException
	{
		if ( ! inited ) {
			if ( connectionFactoryBean == null ) throw new IllegalStateException ( "Property 'atomikosConnectionFactoryBean' must be set first!" );
			if ( getDestinationName() == null ) {
				throw new IllegalStateException ( "Property 'destination' or 'destinationName' must be set first!" );
			}
			StringBuffer msg = new StringBuffer();
			msg.append ( this + ":configured with [" );
			msg.append ( "user=" ).append ( getUser() ).append ( ", " );
			msg.append ( "password=" ).append ( password ).append ( ", " );
			msg.append ( "deliveryMode=" ).append ( getDeliveryMode() ).append ( ", " );
			msg.append ( "timeToLive=" ).append ( getTimeToLive() ).append ( ", " );
			msg.append ( "priority=" ).append ( getPriority() ).append ( ", " );
			msg.append ( "destination=" ).append( getDestinationName() ).append ( ", " );
			msg.append ( "replyToDestination=" ).append ( getReplyToDestinationName() );
			msg.append ( "]" );
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( msg.toString() );
			inited = true;
		}
	}

	private void retrieveDestinationIfNecessary() throws JMSException
	{
		if ( getDestination() == null ) {
			String dName = getDestinationName();
			RetrieveDestinationCallback cb = new RetrieveDestinationCallback ( dName );
			executeCallbackInternal ( cb );
			setDestination ( cb.getDestination() );
		}

	}

	private void retrieveReplyToDestinationIfNecessary() throws JMSException
	{
		if ( getReplyToDestination() == null ) {
			String dName = getReplyToDestinationName();
			if ( dName != null ) {
				RetrieveDestinationCallback cb = new RetrieveDestinationCallback ( dName );
				executeCallbackInternal ( cb );
				setReplyToDestination ( cb.getDestination() );
			}

		}

	}

	/**
	 * Sets the connection factory to use. Required.
	 * @param connectionFactory
	 */
	public void setAtomikosConnectionFactoryBean(AtomikosConnectionFactoryBean connectionFactory) {
		this.connectionFactoryBean = connectionFactory;
	}

	public AtomikosConnectionFactoryBean getAtomikosConnectionFactoryBean() {
		return connectionFactoryBean;
	}

	public Destination getDestination() {
		return destination;
	}


	/**
	 * Sets the (provider-specific) destination name in order
	 * to lookup the destination (rather than providing one directly).
	 *
	 * Required, unless you set the destination directly.
	 *
	 * @param destinationName
	 */

	public void setDestinationName ( String destinationName )
	{
		this.destinationName = destinationName;
	}

	/**
	 * Sets the destination to send to. Required, unless
	 * you set the destinationName instead.
	 *
	 * @param destination
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	private String getName(Destination d, String destinationName ) {
		String ret = destinationName;
		if ( ret == null ) {
			if ( d instanceof Queue ) {
				Queue q = ( Queue ) d;
				try {
					ret = q.getQueueName();
				} catch ( JMSException e ) {
					if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": error retrieving queue name" , e );
				}
			} else if ( d instanceof Topic ) {
				Topic t = ( Topic ) d;
				try {
					ret = t.getTopicName();
				} catch ( JMSException e ) {
					if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": error retrieving topic name" , e );
				}
			}
		}
		return ret;
	}

	protected String getDestinationName() {
		return getName ( getDestination() , destinationName );
	}

	protected String getReplyToDestinationName() {
		return getName ( getReplyToDestination() , replyToDestinationName );
	}

	/**
	 * @return The user to connect with, or null if no explicit authentication
	 *         is to be used.
	 */
	public String getUser() {
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
	public void setReplyToDestination(Destination destination)
	{
		this.replyToDestination = destination;
	}

	/**
	 * Sets the provider-specific replyToDestinationName. Optional.
	 *
	 * @param replyToDestinationName
	 */

	public void setReplyToDestinationName ( String replyToDestinationName )
	{
		this.replyToDestinationName = replyToDestinationName;

	}

	/**
	 * Gets the replyToDestination.
	 *
	 * @return
	 */
	public Destination getReplyToDestination() {
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
	public void setPassword(String password) {
	    this.password = password;
	}

	/**
	 * Set the user to use for explicit authentication (optional). If no explicit
	 * authentication is required then this method should not be called.
	 *
	 * @param user
	 */
	public void setUser(String user) {
	    this.user = user;
	}

	protected void executeCallbackInternal (
			JmsSenderTemplateCallback callback ) throws JMSException {

		init();
		Session  session = null;
	    Connection conn = null;
	    try {
	    	conn = getOrReuseConnection();
	    	session = getOrRefreshSession ( conn );
	    	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Calling callback..." );
	    	callback.doInJmsSession ( session );
	        if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Callback done!" );
	        afterUseWithoutErrors ( conn , session );

	    } catch ( AtomikosTransactionRequiredJMSException notx ) {
	    	destroy ( conn , session );
	    	String msg = "The JMS session you are using requires a JTA transaction context for the calling thread and none was found." + "\n" +
			"Please correct your code to do one of the following: " + "\n" +
			"1. start a JTA transaction before sending any message, or" + "\n" +
			"2. increase the maxPoolSize of the AtomikosConnectionFactoryBean to avoid transaction timeout while waiting for a connection.";
	    	LOGGER.logWarning ( msg );
	    	AtomikosTransactionRequiredJMSException.throwAtomikosTransactionRequiredJMSException ( msg );

	    } catch ( JMSException e ) {
	    	e.printStackTrace();
	    	destroy ( conn , session );
	        String msg = this + ": error in sending JMS message";
	        AtomikosJMSException.throwAtomikosJMSException( msg , e );
	    }
	}

	/* (non-Javadoc)
	 * @see com.atomikos.jms.extra.JmsSenderTemplate#executeCallback(com.atomikos.jms.extra.JmsSenderTemplateCallback)
	 */
	@Override
	public void executeCallback(JmsSenderTemplateCallback callback) throws JMSException {

		init();


		retrieveDestinationIfNecessary();
		retrieveReplyToDestinationIfNecessary();

		UserTransactionManager tm = new UserTransactionManager ();
	    try {
	        if ( tm.getStatus () != Status.STATUS_ACTIVE )
	            throw new JMSException (
	                    "This method requires an active transaction!" );
	    } catch ( SystemException e ) {
	    	LOGGER
	                .logWarning ( this +": error in getting transaction status", e );
	        throw new RuntimeException ( e.getMessage () );
	    }

	    executeCallbackInternal ( callback );

	}



	/**
	 * @return The deliverymode for messages sent in this session.
	 */
	public int getDeliveryMode() {
	    return deliveryMode;
	}

	/**
	 * @return The priority for messages sent in this session.
	 */
	public int getPriority() {
	    return priority;
	}

	/**
	 * @return The timeToLive for messages sent in this session.
	 */
	public long getTimeToLive() {
	    return timeToLive;
	}

	/**
	 *
	 * Set the deliverymode for messages sent in this session (optional). Defaults to
	 * persistent.
	 *
	 * @param
	 */
	public void setDeliveryMode(int i) {
	    deliveryMode = i;
	}

	/**
	 * Set the priority for messages sent in this session (optional). Defaults to 4.
	 *
	 * @param
	 */
	public void setPriority(int i) {
	    priority = i;
	}

	/**
	 * Set the time to live for messages sent in this session (optional). Defaults to 0.
	 *
	 * @param
	 */
	public void setTimeToLive(long l) {
	    timeToLive = l;
	}

	/* (non-Javadoc)
	 * @see com.atomikos.jms.extra.JmsSenderTemplate#sendTextMessage(java.lang.String)
	 */
	@Override
	public void sendTextMessage(String content) throws JMSException {
		retrieveDestinationIfNecessary();
		retrieveReplyToDestinationIfNecessary();
		SendTextMessageCallback cb = new SendTextMessageCallback ( content , getDestination() , getReplyToDestination() , getDeliveryMode() , getPriority() , getTimeToLive() );
		executeCallback ( cb );
	}

	/* (non-Javadoc)
	 * @see com.atomikos.jms.extra.JmsSenderTemplate#sendMapMessage(java.util.Map)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void sendMapMessage(Map content) throws JMSException {
		retrieveDestinationIfNecessary();
		retrieveReplyToDestinationIfNecessary();
		SendMapMessageCallback cb = new SendMapMessageCallback ( content , getDestination() , getReplyToDestination() , getDeliveryMode() , getPriority() , getTimeToLive() );
		executeCallback ( cb );
	}

	/* (non-Javadoc)
	 * @see com.atomikos.jms.extra.JmsSenderTemplate#sendObjectMessage(java.io.Serializable)
	 */
	@Override
	public void sendObjectMessage(Serializable content) throws JMSException {
		retrieveDestinationIfNecessary();
		retrieveReplyToDestinationIfNecessary();
		SendObjectMessageCallback cb = new SendObjectMessageCallback ( content , getDestination() , getReplyToDestination() , getDeliveryMode() , getPriority() , getTimeToLive() );
		executeCallback ( cb );
	}

	/* (non-Javadoc)
	 * @see com.atomikos.jms.extra.JmsSenderTemplate#sendBytesMessage(byte[])
	 */
	@Override
	public void sendBytesMessage(byte[] content) throws JMSException {
		retrieveDestinationIfNecessary();
		retrieveReplyToDestinationIfNecessary();
		SendBytesMessageCallback cb = new SendBytesMessageCallback ( content , getDestination() , getReplyToDestination() , getDeliveryMode() , getPriority() , getTimeToLive() );
		executeCallback ( cb );
	}

	/**
	 * Closes all resources.
	 */
	public void close() {
		try {
			Connection c = getOrReuseConnection();
			Session s = getOrRefreshSession(c);
			destroy(c, s);
		} catch (JMSException e) {
			LOGGER.logWarning ( this + ": error closing" , e );
		}
		connectionFactoryBean.close();
	}

}
