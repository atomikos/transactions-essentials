package com.atomikos.jms;

import javax.jms.Queue;


/**
 * 
 * 
 * 
 * A pool of QueueReceiverSession instances, for activating multiple threads on
 * the same queue. Upon start, an instance of this class will create a number of
 * concurrent sessions that listen for incoming messages on the same queue.
 * MessageListener instances should be thread-safe if the pool size is larger
 * than one. Note: in general, after start() any changed properties are only
 * effective on the next start() event.
 * 
 * <p>
 * <b>IMPORTANT:</b> the transactional behaviour guarantees redelivery after failures.
 * As a side-effect, this can lead to so-called <em>poison messages</em>: messages
 * whose processing repeatedly fails due to some recurring error (for instance, a primary
 * key violation in the database, a NullPointerException, ...). Poison messages are problematic
 * because they can prevent other messages from being processed, and block the system.
 * Some messaging systems
 * provide a way to deal with poison messages, others don't. In that case, it is up 
 * to the registered MessageListener to detect poison messages. The easiest way to 
 * detect these is by inspecting the <code>JMSRedelivered</code> header and/or the
 * (sometimes available) JMS property called <code>JMSXDeliveryCount</code>. For instance, if the
 * delivery count is too high then your application may choose to send the message to
 * a dedicated problem queue and thereby avoid processing errors. Whatever you do, beware
 * that messages are sometimes correctly redelivered, in particular after a prior crash of the
 * application.
 * 
 * 
 */

// @todo test: assert that properties are propagated to sessions by using object
// properties of Message and testListener to check
public class QueueReceiverSessionPool
extends MessageConsumerSessionPool
{
    public QueueReceiverSessionPool ()
    {
        
        
        // setTransactionTimeout ( 30 );
    }
    
    /**
     * Get the queue to listen on.
     * 
     * @return
     */
    public Queue getQueue ()
    {
        return ( Queue ) getDestination();
    }

    /**
     * 
     * Get the connection factory bean.
     * 
     * @return
     */
    public QueueConnectionFactoryBean getQueueConnectionFactoryBean ()
    {
        return ( QueueConnectionFactoryBean ) getAbstractConnectionFactoryBean();
    }

    /**
     * Set the queue to listen on (required).
     * 
     * @param queue
     */
    public void setQueue ( Queue queue )
    {
        setDestination ( queue );
    }

    /**
     * Set the connection factory to use (required).
     * 
     * @param bean
     */
    public void setQueueConnectionFactoryBean ( QueueConnectionFactoryBean bean )
    {
        setAbstractConnectionFactoryBean ( bean );
    }

	protected MessageConsumerSession createSession() 
	{
		return new QueueReceiverSession();
	}

	protected boolean getNoLocal() {
		//irrelevant for queues
		return false;
	}

	protected String getSubscriberName() {
		//irrelevant for queues
		return null;
	}

}
