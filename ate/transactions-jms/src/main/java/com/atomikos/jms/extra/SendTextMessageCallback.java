package com.atomikos.jms.extra;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

class SendTextMessageCallback extends AbstractSendMessageCallback
{

	private String text;
	
	public SendTextMessageCallback ( String text , Destination destination , Destination replyToDestination , int deliveryMode  , int priority , long ttl ) {
		super ( destination , replyToDestination , deliveryMode , priority , ttl );
		this.text = text;
	}
	
	public void doInJmsSession ( Session session ) throws JMSException
	{
		TextMessage msg = session.createTextMessage();
		msg.setText ( text );
		sendMessage( msg , session );
	}

}
