package com.atomikos.jms;

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
