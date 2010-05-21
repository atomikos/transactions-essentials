package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.XATopicSession;
import javax.jms.TopicSubscriber;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TransactionalResource;

/**
 * 
 * 
 * A topic session implementation.
 *<p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 */


class JtaTopicSession 
extends DefaultJtaSession
implements TopicSession 
{
	
	JtaTopicSession ( XATopicSession session , 
			TransactionalResource res , XAResource xares )
	{
		super ( session , res , xares );
	}
	
	private TopicSession getTopicSession()
	{
		return ( TopicSession ) getSession();
	}
	
	public TopicSubscriber createSubscriber ( Topic topic ) 
	throws JMSException 
	{
		TopicSubscriber ts = getTopicSession().createSubscriber ( topic );
		return new JtaTopicSubscriber ( ts , getTransactionalResource() , getXAResource() );
	}

	public TopicSubscriber createSubscriber ( 
			Topic topic , String messageSelector , boolean noLocal ) throws JMSException 
	{
		TopicSubscriber ts = getTopicSession().createSubscriber ( topic  , messageSelector, noLocal );
		return new JtaTopicSubscriber ( ts , getTransactionalResource() , getXAResource() );
	
	}

	public TopicPublisher createPublisher (Topic topic ) throws JMSException 
	{
		TopicPublisher tp = getTopicSession().createPublisher ( topic );
		return new JtaTopicPublisher ( tp , getTransactionalResource() , getXAResource() );
	}


}

