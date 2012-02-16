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

import javax.jms.Destination;
import javax.jms.Queue;

/**
 *
 *
 *
 * A factory for QueueSenderSession instances, allowing a number of sessions to
 * share the same setup configuration. Changes to the factory properties will
 * not affect any previously created sessions.
 *
 *
 */
public class QueueSenderSessionFactory
extends MessageProducerSessionFactory
{

	private QueueConnectionFactoryBean queueConnectionFactoryBean;

    /**
     * @return
     */
    public Queue getQueue ()
    {
        return ( Queue ) getDestination();
    }

    /**
     * @return
     */
    public QueueConnectionFactoryBean getQueueConnectionFactoryBean ()
    {
        return queueConnectionFactoryBean;
    }

    /**
     * Gets the replyTo queue (if any).
     * @return Null if no replyToQueue was set, or if
     * the replyTo destination is a topic.
     */
    public Queue getReplyToQueue ()
    {
        Queue ret = null;
        Destination dest = getReplyToDestination();
        if ( dest instanceof Queue ) {
        	 	ret = ( Queue ) dest;
        }
        return ret;
    }

    /**
     * Sets the queue to send to (required).
     * @param queue
     */
    public void setQueue ( Queue queue )
    {
       setDestination ( queue );
    }

    /**
     * @param bean
     */
    public void setQueueConnectionFactoryBean ( QueueConnectionFactoryBean bean )
    {
        queueConnectionFactoryBean = bean;
    }

    /**
     * @param queue
     */
    public void setReplyToQueue ( Queue queue )
    {
        setReplyToDestination ( queue );
    }

    /**
     * Create a new instance with the current properties. Any later property
     * changes will NOT affect the created instance.
     *
     * @return
     */
    public QueueSenderSession createQueueSenderSession ()
    {
        QueueSenderSession ret = new QueueSenderSession ();
        ret.setDeliveryMode ( getDeliveryMode() );
        ret.setPassword ( getPassword() );
        ret.setPriority ( getPriority() );
        ret.setQueue ( getQueue() );
        ret.setQueueConnectionFactoryBean ( getQueueConnectionFactoryBean() );
        ret.setReplyToDestination ( getReplyToDestination() );
        ret.setTimeToLive ( getTimeToLive() );
        ret.setUser ( getUser() );
        return ret;
    }

	protected MessageProducerSession createMessageProducerSession()
	{
		return createQueueSenderSession();
	}

}
