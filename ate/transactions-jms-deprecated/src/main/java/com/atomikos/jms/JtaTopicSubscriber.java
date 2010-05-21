package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TransactionalResource;

/**
 * 
 * 
 * A topic subscriber implementation.
 *<p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 */


class JtaTopicSubscriber 
extends DefaultJtaMessageConsumer
implements HeuristicTopicSubscriber 
{
	
	JtaTopicSubscriber ( TopicSubscriber subscriber ,
			TransactionalResource res ,
            XAResource xares )
    {
		super ( subscriber , res , xares );
    }


	private TopicSubscriber getTopicSubscriber()
	{
		return ( TopicSubscriber ) getMessageConsumer();
	}
	
	public Topic getTopic() throws JMSException 
	{
		return getTopicSubscriber().getTopic();
	}

	public boolean getNoLocal() throws JMSException 
	{
		//TODO check if this needs to be ignored
		return getTopicSubscriber().getNoLocal();
	}

	

}
