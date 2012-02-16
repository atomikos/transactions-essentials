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

import javax.jms.Destination;
import javax.jms.Topic;

/**
 * 
 * 
 * A factory for TopicPublisherSession objects.
 *
 * <p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 */
public class TopicPublisherSessionFactory 
extends MessageProducerSessionFactory 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(TopicPublisherSessionFactory.class);
	
	private TopicConnectionFactoryBean factory;
	
	public TopicPublisherSessionFactory()
	{		
		super();
	}
	
	/**
	 * Sets the topic connection factory to use (required).
	 * @param factory
	 */
	public void setTopicConnectionFactoryBean ( 
			TopicConnectionFactoryBean factory )
	{
		this.factory = factory;
	}
	
	/**
	 * Gets the topic connection factory.
	 * @return
	 */
	public TopicConnectionFactoryBean getTopicConnectionFactoryBean()
	{
		return factory;
	}
	
	/**
	 * Sets the topic to send to (required).
	 * @param topic
	 */
	public void setTopic ( Topic topic )
	{
		setDestination  ( topic );
	}
	
	/**
	 * Gets the topic to send to.
	 * @return
	 */
	public Topic getTopic()
	{
		return ( Topic ) getDestination();
	}
	
	/**
	 * Sets the topic to reply to (optional).
	 * @param topic
	 */
	public void setReplyToTopic ( Topic topic )
	{
		setReplyToDestination ( topic );
	}
	
	/**
	 * Gets the topic to reply to (if any).
	 * @return Null if no topic was set or
	 * if the replyTo destination is a queue
	 * instead of a topic.
	 */
	public Topic getReplyToTopic()
	{
		Topic ret = null;
		Destination dest = getReplyToDestination();
		if ( dest instanceof Topic ) {
			ret = ( Topic ) dest;
		}
		return ret;
	}
	
	
	/**
	 * Creates a new topic publisher session.
	 * @return
	 */
	public TopicPublisherSession createTopicPublisherSession()
	{
		TopicPublisherSession ret = new TopicPublisherSession();
		ret.setDeliveryMode ( getDeliveryMode() );
	    ret.setPassword ( getPassword() );
	    ret.setPriority ( getPriority() );
	    ret.setTopic ( getTopic() );
	    ret.setTopicConnectionFactoryBean ( factory );
	        ret.setReplyToDestination ( getReplyToDestination() );
	        ret.setTimeToLive ( getTimeToLive() );
	        ret.setUser ( getUser() );		
		return ret;
	}

	protected MessageProducerSession createMessageProducerSession() 
	{
		return createTopicPublisherSession();
	}

}
