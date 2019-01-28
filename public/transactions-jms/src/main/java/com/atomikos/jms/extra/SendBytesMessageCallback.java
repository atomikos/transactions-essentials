/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

class SendBytesMessageCallback extends AbstractSendMessageCallback
{

	private byte[] content;

	protected SendBytesMessageCallback ( byte[] content , Destination destination,
			Destination replyToDestination, int deliveryMode, int priority,
			long ttl) {
		super(destination, replyToDestination, deliveryMode, priority, ttl);
		this.content = content;
	}

	public void doInJmsSession ( Session session ) throws JMSException
	{
		BytesMessage msg = session.createBytesMessage();
		msg.writeBytes ( content );
		sendMessage ( msg , session );
	}

}
