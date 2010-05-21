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
