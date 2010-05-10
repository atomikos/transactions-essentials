//$Id: QueueReceiverSessionPool.java,v 1.3 2006/10/30 10:37:10 guy Exp $
//$Log: QueueReceiverSessionPool.java,v $
//Revision 1.3  2006/10/30 10:37:10  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.2.2.5  2006/10/20 15:01:44  guy
//Added comments for poison message handling
//
//Revision 1.2.2.4  2006/10/20 14:59:05  guy
//Added comments for poison message handling
//
//Revision 1.2.2.3  2006/10/20 07:03:13  guy
//Completed JMS 1.1 support
//
//Revision 1.2.2.2  2006/10/16 10:40:57  guy
//Corrected setDaemonThreads
//
//Revision 1.2.2.1  2006/10/16 07:46:48  guy
//FIXED 10087
//
//Revision 1.2  2006/09/22 11:53:27  guy
//ADDED 1003
//
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:32  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:05  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:15  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/05/16 09:06:21  guy
//Added non-XA mode support for JtaQueueConnection
//and messageSelector for ReceiverSessions.
//
//Revision 1.3  2005/05/11 10:41:24  guy
//Updated javadoc.
//
//Revision 1.2  2005/01/07 17:13:09  guy
//Updated comments.
//
//Revision 1.1  2005/01/07 17:07:18  guy
//Added JMS receiver support (lightweigh MDB), and JMS queue bridging.
//
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
