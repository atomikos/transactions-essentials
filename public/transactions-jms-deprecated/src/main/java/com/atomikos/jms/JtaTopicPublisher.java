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
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(JtaTopicPublisher.class);
   

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
