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

import javax.jms.JMSException;
import javax.jms.Topic;

import com.atomikos.icatch.system.Configuration;

/**
* 
* 
* A light-weight alternative for message-driven beans: the TopicReceiverSession
* allows MessageListener instances to listen on JMS messages in a transactional
* way (non-transactional mode is not supported). This class is implemented as a
* JavaBean to facilitate integration in third-party frameworks. This session is
* <b>long-lived</b> in the sense that underlying JMS resources are refreshed
* regularly. In particular, clients should not worry about the connection or
* session timing out. This allows this class to be used for server-type
* applications.
* <p>
* <b>IMPORTANT:</b> the transactional behaviour guarantees redelivery after failures.
* As a side-effect, this can lead to so-called <em>poison messages</em>: messages
* whose processing repeatedly fails due to some recurring error (for instance, a primary
* key violation in the database, a NullPointerException, ...). Poison messages are problematic
* because they can prevent other messages from being processed, and block the system.
* Some messaging systems
* provide a way to deal with poison messages, others don't. In that case, it is up 
* to the registered MessageListener to detect poison messages. The easiest way to 
* detect these is by inspecting the <code>JMSRedelivered</code> header and/or the
* (sometimes available) JMS property called <code>JMSXDeliveryCount</code>. For instance, if the
* delivery count is too high then your application may choose to send the message to
* a dedicated problem queue and thereby avoid processing errors. Whatever you do, beware
* that messages are sometimes correctly redelivered, in particular after a prior crash of the
* application.
* <p>
* <b>
* Instances of this class consume JMS messages in a single thread only. Use
* TopicReceiverSessionPool if you want to have multiple concurrent threads for
* the same topic.
* </b>
* <p>
* Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>. 
*/

public class TopicSubscriberSession extends MessageConsumerSession 
{
	
	private boolean noLocal;
	
	private String subscriberName;
	
	/**
	 * Creates a new instance to be configured via the setters.
	 */
	
	public TopicSubscriberSession() 
	{
		
	}
	
	/**
	 * Sets the connection factory to use.
	 * @param factory This factory needs to be
     *            configured independently before this method is called.
	 */
	public void setTopicConnectionFactoryBean ( TopicConnectionFactoryBean factory )
	{
		setAbstractConnectionFactoryBean ( factory );
	}

	/**
	 * Gets the connection factory used.
	 * @return
	 */
	public TopicConnectionFactoryBean getTopicConnectionFactoryBean()
	{
		return ( TopicConnectionFactoryBean ) getAbstractConnectionFactoryBean();
	}
	
	/**
	 * Sets the topic to listen on.
	 * 
	 * @param topic Transactional receives will come from this topic.
	 */
	public void setTopic ( Topic topic )
	{
		setDestination ( topic );
	}
	
	/**
	 * Gets the topic to listen on.
	 * @return
	 */
	public Topic getTopic()
	{
		return ( Topic ) getDestination();
	}

	protected String getDestinationName() 
	{
		String ret = null;
		Topic topic = getTopic();
		if ( topic != null ) {
			try {
				ret = topic.getTopicName();
			} catch ( JMSException e ) {
				Configuration.logDebug ( "TopicSubscriberSession: error retrieving topic name" , e );
			}
		}
		return ret;
	}

	
	/**
	 * Gets the NoLocal flag value.
	 * 
	 * @return True if set.
	 */

	public boolean getNoLocal() {
		return noLocal;
	}

	/**
	 * Gets the subscriber name.
	 * 
	 * @return The name, or null if not set.
	 */
	public String getSubscriberName() {
		return subscriberName;
	}
	
	/**
	 * Sets nolocal value (optional).
	 * If true, then the subscribers will not receive publications made from their connection.
	 * 
	 * @param value Defaults to false.
	 */
	
	public void setNoLocal ( boolean value ) 
	{
		this.noLocal = value;
	}
	
	/**
	 * Sets the durable subscriber name (optional).
	 * If null, no durable subscribers are used.
	 * 
	 * @param name
	 */
	
	public void setSubscriberName ( String name ) 
	{
		this.subscriberName = name;
	}

}
