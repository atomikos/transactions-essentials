//$Id: QueueSenderSession.java,v 1.2 2006/10/30 10:37:10 guy Exp $
//$Log: QueueSenderSession.java,v $
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
//Revision 1.1.1.1  2006/03/09 14:59:15  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2005/05/12 13:45:31  guy
//Corrected bugs.
//
//Revision 1.4  2005/05/11 17:07:14  guy
//Added default settings.
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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;

import com.atomikos.icatch.system.Configuration;


/**
 * 
 * 
 * 
 * This is a <b>long-lived</b> queue sender session, representing a
 * self-refreshing JMS session that can be used to send JMS messages in a
 * transactional way. The client code does not have to worry about refreshing or
 * closing JMS objects explicitly: this is all handled in this class. All the
 * client needs to do is indicate when it wants to start or stop using the
 * session.
 * <p>
 * Note that instances are not meant for concurrent use by different threads:
 * each thread should use a private instance instead.
 * <p>
 * <b>Important: if you change any properties AFTER sending on the session, then
 * you will need to explicitly stop and restart the session to have the changes
 * take effect!</b>
 * 
 */

public class QueueSenderSession
extends MessageProducerSession
{
    /**
     * Default constructor in JavaBean style. Needed to ensure compatibility
     * with third-party frameworks such as Spring.
     */

    public QueueSenderSession ()
    {


    }

    /**
     * If this session is used for sending request/reply messages, then this
     * property indicates the queue to where the replies are to be sent (optional). The
     * session uses this to set the JMSReplyTo header accordingly. This property
     * can be omitted if no reply is needed.
     * 
     * <p>
     * The replyToQueue should be in the same JMS vendor domain as the send
     * queue. To cross domains, configure a bridge for both the request and the
     * reply channels.
     * 
     * @param queue
     *            The queue where a reply should go.
     */

    public void setReplyToQueue ( Queue queue )
    {
        setReplyToDestination ( queue );
    }

    /**
     * Gets the queue where replies are expected, or null if not applicable
     * (or if the replyToDestination is not a queue but a topic).
     * 
     * @return
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
     * @return The queue to send to.
     */

    public Queue getQueue ()
    {
        return ( Queue ) getDestination();
    }

    /**
     * @return The queue connection factory bean.
     */

    public QueueConnectionFactoryBean getQueueConnectionFactoryBean ()
    {
        return ( QueueConnectionFactoryBean ) getAbstractConnectionFactoryBean();
    }

    /**
     * Set the queue to use for sending (required).
     * 
     * @param queue
     *            The queue.
     */

    public void setQueue ( Queue queue )
    {
        setDestination ( queue );
    }

    /**
     * Set the queue connection factory, needed to create or refresh
     * connections (required).
     * 
     * @param bean
     */

    public void setQueueConnectionFactoryBean ( QueueConnectionFactoryBean bean )
    {
        setAbstractConnectionFactoryBean ( bean );
    }

	protected String getDestinationName() 
	{
		String ret = null;
		Queue q = getQueue();
		if ( q != null ) {
			try {
				ret = q.getQueueName();
			} catch ( JMSException e ) {
				Configuration.logDebug ( "QueueSenderSession: error retrieving queue name" , e );
			}
		}
		return ret;
	}

	protected String getReplyToDestinationName() 
	{
		String ret = null;
		Queue q = getReplyToQueue();
		if ( q != null ) {
			try {
				ret = q.getQueueName();
			} catch ( JMSException e ) {
				Configuration.logDebug ( "QueueSenderSession: error retrieving queue name" , e );
			}
		}
		return ret;
	}

}
