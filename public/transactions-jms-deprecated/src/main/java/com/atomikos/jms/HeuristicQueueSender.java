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
