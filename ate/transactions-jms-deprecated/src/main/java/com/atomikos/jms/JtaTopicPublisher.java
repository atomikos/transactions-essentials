package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;

/**
 * 
 * 
 * A topic publisher wrapper that enlists/delists before and after publishing.
 *<p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 */


class JtaTopicPublisher 
extends DefaultJtaMessageProducer
implements HeuristicTopicPublisher 
{
   

    /**
     * Creates a new instance.
     * 
     * @param publisher
     *            The publisher to wrap.
     * @param res
     *            The resource to use.
     * @param xares
     *            The XAResource
     */

    JtaTopicPublisher ( TopicPublisher publisher , TransactionalResource res ,
            XAResource xares )
    {
    		super ( publisher , res , xares );
    }

	public void publish ( Message msg, HeuristicMessage hmsg ) throws JMSException 
	{
		//FIXED 10107
		sendToDefaultDestination ( msg , hmsg );
		//sendToDestination ( getDestination() , msg , hmsg );
		
	}

	public void publish ( Message msg, int deliveryMode, int priority, long timeToLive, HeuristicMessage hmsg ) throws JMSException 
	{
		//FIXED 10107
		sendToDefaultDestination ( msg , deliveryMode , priority , timeToLive , hmsg );
		
	}

	public void publish ( Topic t, Message msg, HeuristicMessage hmsg ) throws JMSException 
	{
		sendToDestination ( t , msg , hmsg );
		
	}

	public void publish ( Topic t, Message msg, int deliveryMode, int priority, long timeToLive, HeuristicMessage hmsg ) throws JMSException 
	{
		sendToDestination ( t , msg , deliveryMode , priority , timeToLive , hmsg );
		
	}

	public void publish ( Message msg, String hmsg ) throws JMSException 
	{
		StringHeuristicMessage shmsg = new StringHeuristicMessage ( hmsg );
		//FIXED 10107
		sendToDefaultDestination ( msg , shmsg );
		
	}

	public void publish ( Message msg, int deliveryMode, int priority, long timeToLive, String hmsg ) throws JMSException 
	{
		StringHeuristicMessage shmsg = new StringHeuristicMessage ( hmsg );
		//FIX FOR 10107
		sendToDefaultDestination ( msg , deliveryMode , priority , timeToLive , shmsg );
		
	}

	public void publish ( Topic t, Message msg, String hmsg ) throws JMSException 
	{
		StringHeuristicMessage shmsg = new StringHeuristicMessage ( hmsg );
		sendToDestination ( t , msg , shmsg );
		
	}

	public void publish ( Topic t, Message msg, int deliveryMode, int priority, long timeToLive, String hmsg ) throws JMSException 
	{
		StringHeuristicMessage shmsg = new StringHeuristicMessage ( hmsg );
		sendToDestination ( t , msg , deliveryMode , priority , timeToLive , shmsg );
		
	}

	public Topic getTopic() throws JMSException 
	{
		return ( Topic ) getDestination();
	}

	public void publish ( Message msg ) throws JMSException 
	{
		send ( msg );
		
	}

	public void publish ( Message msg , int deliveryMode , int priority ,  long ttl )  throws JMSException 
	{
		send ( msg , deliveryMode , priority , ttl );
		
	}

	public void publish (Topic t , Message msg ) throws JMSException 
	{
		send ( t , msg );
		
	}

	public void publish ( Topic t , Message msg , int deliveryMode , int priority , long ttl ) throws JMSException 
	{
		send ( t , msg , deliveryMode , priority , ttl );
		
	}




}
