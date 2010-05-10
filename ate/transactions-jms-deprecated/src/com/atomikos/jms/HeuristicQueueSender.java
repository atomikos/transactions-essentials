//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//$Log: HeuristicQueueSender.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:12  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:31  guy
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
//Revision 1.4  2005/08/09 15:25:21  guy
//Updated javadoc.
//
//Revision 1.3  2004/10/13 14:16:16  guy
//Updated javadoc and added String-based methods for heuristic receiver/sender.
//
//Revision 1.2  2004/10/11 13:40:06  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.1.1.1  2004/09/18 12:42:50  guy
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Added separate JMS module.
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.3  2004/03/22 15:39:38  guy
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.2.2.1  2003/06/20 16:32:10  guy
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//*** empty log message ***
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.2  2003/03/11 06:43:00  guy
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: HeuristicQueueSender.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//Revision 1.1.2.1  2002/09/12 14:23:57  guy
//Added JMS wrapper classes to make JMS transactional in a transparant way.
//

package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSender;

import com.atomikos.icatch.HeuristicMessage;

/**
 * 
 * 
 * A queue sender that supports heuristic log information about the nature of
 * indoubt XA sends. This allows the administration at an application-level: any
 * indoubt messages can be easily identified in the log, and their
 * application-level impact is clear. All queue senders that you create via the
 * Atomikos QueueConnectionFactory classes are of this type. The functionality
 * can be accessed by type-casting.
 */

public interface HeuristicQueueSender extends QueueSender
{
    /**
     * Send a message with given heuristic info.
     * 
     * @param msg
     *            The message to send.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void send ( Message msg , HeuristicMessage hmsg )
            throws JMSException;

    /**
     * Send a message with given heuristic info.
     * 
     * @param msg
     *            The message to send.
     * @param deliveryMode
     *            The JMS delivery mode.
     * @param priority
     *            The JMS priority.
     * @param timeToLive
     *            The JMS time to live.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void send ( Message msg , int deliveryMode , int priority ,
            long timeToLive , HeuristicMessage hmsg ) throws JMSException;

    /**
     * Send a message on a queue with given heuristic info.
     * 
     * @param q
     *            The queue to send to.
     * @param msg
     *            The message to send.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void send ( Queue q , Message msg , HeuristicMessage hmsg )
            throws JMSException;

    /**
     * Send a message on a queue with given heuristic info.
     * 
     * @param q
     *            The queue to send to.
     * @param msg
     *            The message to send.
     * @param deliveryMode
     *            The JMS delivery mode.
     * @param priority
     *            The JMS priority.
     * @param timeToLive
     *            The JMS time to live.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void send ( Queue q , Message msg , int deliveryMode , int priority ,
            long timeToLive , HeuristicMessage hmsg ) throws JMSException;

    /**
     * Send a message with given heuristic info.
     * 
     * @param msg
     *            The message to send.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void send ( Message msg , String hmsg ) throws JMSException;

    /**
     * Send a message with given heuristic info.
     * 
     * @param msg
     *            The message to send.
     * @param deliveryMode
     *            The JMS delivery mode.
     * @param priority
     *            The JMS priority.
     * @param timeToLive
     *            The JMS time to live.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void send ( Message msg , int deliveryMode , int priority ,
            long timeToLive , String hmsg ) throws JMSException;

    /**
     * Send a message on a queue with given heuristic info.
     * 
     * @param q
     *            The queue to send to.
     * @param msg
     *            The message to send.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void send ( Queue q , Message msg , String hmsg )
            throws JMSException;

    /**
     * Send a message on a queue with given heuristic info.
     * 
     * @param q
     *            The queue to send to.
     * @param msg
     *            The message to send.
     * @param deliveryMode
     *            The JMS delivery mode.
     * @param priority
     *            The JMS priority.
     * @param timeToLive
     *            The JMS time to live.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void send ( Queue q , Message msg , int deliveryMode , int priority ,
            long timeToLive , String hmsg ) throws JMSException;
}
