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
