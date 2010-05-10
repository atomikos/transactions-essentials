package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

import com.atomikos.icatch.HeuristicMessage;

/**
 * 
 * 
 * A topic publisher that supports heuristic log information about the nature of
 * indoubt XA sends. This allows the administration at an application-level: any
 * indoubt messages can be easily identified in the log, and their
 * application-level impact is clear. All topic publishers that you create via the
 * Atomikos ConnectionFactory classes are of this type. The functionality
 * can be accessed by type-casting.
 * <p>
 * Topic functionality in this product was sponsored by <a href="http://www.webtide.com">Webtide</a>.
 */

public interface HeuristicTopicPublisher extends TopicPublisher 
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

    public void publish ( Message msg , HeuristicMessage hmsg )
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

    public void publish ( Message msg , int deliveryMode , int priority ,
            long timeToLive , HeuristicMessage hmsg ) throws JMSException;

    /**
     * Send a message on a topic with given heuristic info.
     * 
     * @param t
     *            The topic to send to.
     * @param msg
     *            The message to send.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void publish ( Topic t, Message msg , HeuristicMessage hmsg )
            throws JMSException;

    /**
     * Send a message on a topic with given heuristic info.
     * 
     * @param t
     *            The topic to send to.
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

    public void publish ( Topic t , Message msg , int deliveryMode , int priority ,
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

    public void publish ( Message msg , String hmsg ) throws JMSException;

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

    public void publish ( Message msg , int deliveryMode , int priority ,
            long timeToLive , String hmsg ) throws JMSException;

    /**
     * Send a message on a topic with given heuristic info.
     * 
     * @param t
     *            The topic to send to.
     * @param msg
     *            The message to send.
     * @param HeuristicMessage
     *            The log info message for this send operation. This information
     *            will be shown in case of heuristic problems.
     */

    public void publish ( Topic t , Message msg , String hmsg )
            throws JMSException;

    /**
     * Send a message on a topic with given heuristic info.
     * 
     * @param t
     *            The topic to send to.
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

    public void publish ( Topic t , Message msg , int deliveryMode , int priority ,
            long timeToLive , String hmsg ) throws JMSException;
	
}
