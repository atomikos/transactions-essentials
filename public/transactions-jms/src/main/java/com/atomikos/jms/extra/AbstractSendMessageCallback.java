/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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
