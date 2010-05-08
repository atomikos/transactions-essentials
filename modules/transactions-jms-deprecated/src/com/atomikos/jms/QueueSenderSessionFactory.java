//$Id: QueueSenderSessionFactory.java,v 1.2 2006/10/30 10:37:10 guy Exp $
//$Log: QueueSenderSessionFactory.java,v $
//Revision 1.2  2006/10/30 10:37:10  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.1.1.1.4.1  2006/10/20 07:03:13  guy
//Completed JMS 1.1 support
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
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/01/07 17:07:18  guy
//Added JMS receiver support (lightweigh MDB), and JMS queue bridging.
//
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
