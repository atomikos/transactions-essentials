/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.extra;

import java.util.Iterator;
import java.util.Map;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;

@SuppressWarnings("rawtypes")
class SendMapMessageCallback extends AbstractSendMessageCallback {

	
	private Map content;

	protected SendMapMessageCallback ( Map content , Destination destination,
			Destination replyToDestination, int deliveryMode, int priority,
			long ttl) {
		super(destination, replyToDestination, deliveryMode, priority, ttl);
		this.content = content;
	}

	public void doInJmsSession ( Session session ) throws JMSException
	{
		MapMessage msg = session.createMapMessage();
		Iterator keys = content.keySet().iterator();
		while ( keys.hasNext() ) {
			String key = ( String ) keys.next();
			Object value = content.get ( key );
			msg.setObject ( key , value );
		}
		sendMessage ( msg  , session );
	}


}
