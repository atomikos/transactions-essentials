//$Id: QueueReceiverSession.java,v 1.3 2006/10/30 10:37:10 guy Exp $
//$Log: QueueReceiverSession.java,v $
//Revision 1.3  2006/10/30 10:37:10  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.2.2.7  2006/10/20 15:01:44  guy
//Added comments for poison message handling
//
//Revision 1.2.2.6  2006/10/20 14:59:05  guy
//Added comments for poison message handling
//
//Revision 1.2.2.5  2006/10/20 07:03:13  guy
//Completed JMS 1.1 support
//
//Revision 1.2.2.4  2006/10/16 10:40:57  guy
//Corrected setDaemonThreads
//
//Revision 1.2.2.3  2006/10/16 07:46:48  guy
//FIXED 10087
//
//Revision 1.2.2.2  2006/10/06 08:53:39  guy
//FIXED 10084
//
//Revision 1.2.2.1  2006/10/05 08:57:43  guy
//FIXED 10082
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
//Revision 1.5  2005/05/16 09:12:45  guy
//Added comments.
//
//Revision 1.4  2005/05/16 09:06:21  guy
//Added non-XA mode support for JtaQueueConnection
//and messageSelector for ReceiverSessions.
//
//Revision 1.3  2005/05/14 12:44:38  guy
//Nothing changed.
//
//Revision 1.2  2005/05/11 10:41:24  guy
//Updated javadoc.
//
//Revision 1.1  2005/01/07 17:07:18  guy
//Added JMS receiver support (lightweigh MDB), and JMS queue bridging.
//
package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Queue;

import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * 
 * 
 * A light-weight alternative for message-driven beans: the QueueReceiverSession
 * allows MessageListener instances to listen on JMS messages in a transactional
 * way (non-transactional mode is not supported). This class is implemented as a
 * JavaBean to facilitate integration in third-party frameworks. This session is
 * <b>long-lived</b> in the sense that underlying JMS resources are refreshed
 * regularly. In particular, clients should not worry about the connection or
 * session timing out. This allows this class to be used for server-type
 * applications.
 * <p>
 * <b>IMPORTANT:</b> the transactional behaviour guarantees redelivery after failures.
 * As a side-effect, this can lead to so-called <em>poison messages</em>: messages
 * whose processing repeatedly fails due to some recurring error (for instance, a primary
 * key violation in the database, a NullPointerException, ...). 
 * Poison messages are problematic
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
 * <p>
 * <b>Instances of this class consume JMS messages in a single thread only. Use
 * QueueReceiverSessionPool if you want to have multiple concurrent threads for
 * the same queue.</b>
 * 
 */

// TODO check if non-transactional mode is desirable and feasible
// TODO test message selector
public class QueueReceiverSession
extends MessageConsumerSession
{
    /**
     * Create a new instance. Configuration should be done via the setters.
     */

    public QueueReceiverSession ()
    {
        
       
    }
    
    /**
     * Set the queue connection factory to use.
     * 
     * @param factory
     *            The queue connection factory. This factory needs to be
     *            configured independently before this method is called.
     */

    public void setQueueConnectionFactoryBean (
            QueueConnectionFactoryBean factory )
    {
        setAbstractConnectionFactoryBean ( factory );
    }

    /**
     * Get the queue connection factory.
     * 
     * @return
     */

    public QueueConnectionFactoryBean getQueueConnectionFactoryBean ()
    {
        return ( QueueConnectionFactoryBean ) getAbstractConnectionFactoryBean();
    }

    /**
     * Set the queue to use for listening on.
     * 
     * @param queue
     *            This is the queue where messages are to be taken off
     *            transactionally.
     */

    public void setQueue ( Queue queue )
    {
        setDestination ( queue );
    }

    /**
     * Get the queue.
     * 
     * @return
     */
    public Queue getQueue ()
    {
        return ( Queue ) getDestination();
    }

	protected String getDestinationName() 
	{
		String ret = null;
		Queue queue = getQueue();
		if (queue != null ) {
			try {
				ret = queue.getQueueName();
			} catch ( JMSException e ) {
				Configuration.logDebug ( "QueueReceiverSession: error retrieving queue name" , e );
			}
		}
		return ret;
	}

	protected boolean getNoLocal() {
		//only relevant for topics -> false here
		return false;
	}

	protected String getSubscriberName() {
		//only relevant for topics -> null
		return null;
	}

	protected void setNoLocal(boolean value) {
		//ignore for queues
		
	}

	protected void setSubscriberName(String name) {
		//ignore for queues
		
	}

  

}
