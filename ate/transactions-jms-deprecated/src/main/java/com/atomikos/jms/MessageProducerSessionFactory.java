package com.atomikos.jms;

import javax.jms.Destination;

 /**
  * 
  * 
  * Common functionality for sender session factories.
  *
  */

public abstract class MessageProducerSessionFactory 
{

	
	private String user;
	private String password;
	private Destination destination;
	private Destination replyToDestination;
	private int deliveryMode;
	private int priority;
	private long timeToLive;
	
	protected Destination getDestination()
	{
		return destination;
	}
	
	protected void setDestination ( Destination dest )
	{
		this.destination = dest;
	}
	
	/**
	 * Sets the destination to reply to (optional).
	 * @param dest
	 */
	public void setReplyToDestination ( Destination dest )
	{
		this.replyToDestination = dest;
	}
	
	/**
	 * Gets the destination to reply to (if any).
	 * @return
	 */
	public Destination getReplyToDestination()
	{
		return replyToDestination;
	}

	/**
	 * Gets the delivery mode.
	 * 
	 * @return
	 */
	
	public int getDeliveryMode() 
	{
	    return deliveryMode;
	}

	/**
	 * Gets the priority.
	 * 
	 * @return
	 */
	
	public int getPriority() 
	{
	    return priority;
	}

	/**
	 * Gets the time to live.
	 * @return
	 */
	public long getTimeToLive() 
	{
	    return timeToLive;
	}

	/**
	 * Gets the user (if any).
	 * @return
	 */
	public String getUser() 
	{
	    return user;
	}

	/**
	 * Sets the JMS delivery mode for sending (optional).
	 * @param mode
	 */
	public void setDeliveryMode ( int mode ) 
	{
	    deliveryMode = mode;
	}

	/**
	 * Sets the password (optional - only required
	 * if the user is set).
	 * @param pw
	 */
	public void setPassword ( String pw ) 
	{
	    password =  pw;
	}
	
	protected String getPassword()
	{
		return password;
	}

	/**
	 * Sets the priority for sending (optional).
	 * @param pty
	 */
	public void setPriority ( int pty ) 
	{
	    priority = pty;
	}

	/**
	 * Sets the time to live for messages sent (optional).
	 * @param ttl
	 */
	public void setTimeToLive ( long ttl ) 
	{
	    timeToLive = ttl;
	}

	/**
	 * Sets the user name to use for sending (optional).
	 * @param user
	 */
	public void setUser ( String user ) 
	{
	    this.user = user;
	}
	
	/**
	 * Creates a new message producer session.
	 * 
	 * @return
	 */
	protected abstract MessageProducerSession 
	createMessageProducerSession();

}
