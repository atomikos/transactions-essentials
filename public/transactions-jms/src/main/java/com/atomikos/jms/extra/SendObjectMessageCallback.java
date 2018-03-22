/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

class SendObjectMessageCallback extends AbstractSendMessageCallback
{

	private Serializable content;

	protected SendObjectMessageCallback ( Serializable content , Destination destination,
			Destination replyToDestination, int deliveryMode, int priority,
			long ttl) {
		super(destination, replyToDestination, deliveryMode, priority, ttl);
		this.content = content;
	}

	public void doInJmsSession ( Session session ) throws JMSException
	{
		ObjectMessage msg = session.createObjectMessage ( content );
		sendMessage ( msg , session );
	}

}
