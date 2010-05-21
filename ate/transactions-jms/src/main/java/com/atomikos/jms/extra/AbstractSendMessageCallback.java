package com.atomikos.jms.extra;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

abstract class AbstractSendMessageCallback implements JmsSenderTemplateCallback 
{

	private Destination replyToDestination;
	private Destination destination;
	private int priority;
	private long ttl;
	private int deliveryMode;
	
	protected AbstractSendMessageCallback ( Destination destination , Destination replyToDestination , int deliveryMode  , int priority , long ttl ) 
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
		
}
