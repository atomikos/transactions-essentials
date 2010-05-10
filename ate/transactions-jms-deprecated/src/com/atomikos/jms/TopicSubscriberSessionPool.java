package com.atomikos.jms;

import javax.jms.Topic;


 /**
  * 
  * 
  * A pool for topic subscribers.
  * For activating multiple threads on
  * the same topic. Upon start, an instance of this class will create a number of
  * concurrent sessions that listen for incoming messages on the same topic.
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
  * Some messaging systems
  * provide a way to deal with poison messages, others don't. In that case, it is up 
  * to the registered MessageListener to detect poison messages. The easiest way to 
  * detect these is by inspecting the <code>JMSRedelivered</code> header and/or the
  * (sometimes available) JMS property called <code>JMSXDeliveryCount</code>. For instance, if the
  * delivery count is too high then your application may choose to send the message to
  * a dedicated problem queue and thereby avoid processing errors. Whatever you do, beware
  * that messages are sometimes correctly redelivered, in particular after a prior crash of the
  * application.
  * 
  * <p>
  * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
  *
  */

public class TopicSubscriberSessionPool extends MessageConsumerSessionPool 
{

	private boolean noLocal;
	private String subscriberName;
	
	protected MessageConsumerSession createSession() 
	{		
		return new TopicSubscriberSession();
	}

	/**
	 * Gets the topic to listen on.
	 * 
	 * @return
	 */
	public Topic getTopic()
	{
		return ( Topic ) getDestination();
	}
	
	/**
	 * Sets the topic to listen on (required).
	 * @param topic
	 */
	
	public void setTopic ( Topic topic )
	{
		setDestination ( topic );
	}
	
	/**
	 * Sets the topic connection factory to use (required).
	 * @param bean
	 */
	public void setTopicConnectionFactoryBean ( TopicConnectionFactoryBean bean )
	{
		setAbstractConnectionFactoryBean ( bean );
	}
	
	/**
	 * Gets the topic connection factory.
	 */
	public TopicConnectionFactoryBean getTopicConnectionFactoryBean()
	{
		return ( TopicConnectionFactoryBean ) getAbstractConnectionFactoryBean();
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
}
