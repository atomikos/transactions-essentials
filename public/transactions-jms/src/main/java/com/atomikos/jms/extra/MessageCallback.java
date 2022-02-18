/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

abstract class MessageCallback implements JmsSenderTemplateCallback
{

	private final Destination replyToDestination;
	private final Destination destination;
	private final int priority;
	private final long ttl;
	private final int deliveryMode;

	protected MessageCallback ( Destination destination , Destination replyToDestination , int deliveryMode  , int priority , long ttl )
	{
		this.destination = destination;
		this.replyToDestination = replyToDestination;
		this.priority = priority;
		this.ttl = ttl;
		this.deliveryMode = deliveryMode;
	}

	protected void sendMessage ( Message m , Session s ) throws JMSException
	{
	    if ( replyToDestination != null )
            m.setJMSReplyTo ( replyToDestination );
        MessageProducer mp = s.createProducer( destination );
        mp.send ( m , deliveryMode, priority, ttl );
        mp.close();
	}
	 @Override
	public void doInJmsSession(Session session) throws JMSException {
		 sendMessage(createMessage(session), session);
	}
	

	abstract Message createMessage(Session session) throws JMSException;


}
