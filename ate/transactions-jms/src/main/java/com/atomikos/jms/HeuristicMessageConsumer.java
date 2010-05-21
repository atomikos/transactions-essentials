package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

import com.atomikos.icatch.HeuristicMessage;

/**
 * 
 * 
 * A message consumer with support for heuristic information. This information
 * is kept in the logs and can help to provide details in case of indoubt or
 * heuristic XA transactions during receive.
 */

public interface HeuristicMessageConsumer extends MessageConsumer
{


    /**
     * Block until a message is there, and use the supplied heuristic
     * information.
     * 
     * @param hmsg
     *            The heuristic information to show in case of problems.
     * @return Message The JMS message.
     * @throws JMSException
     */
    public Message receive ( String hmsg ) throws JMSException;



    /**
     * Block until a message is there, but use the supplied heuristic
     * information.
     * 
     * @param hmsg
     *            The heuristic information to show in case of problems.
     * @param timeout
     *            The timeout for receive.
     * @return Message The message or null on timeout.
     * @exception JMSException
     *                On error.
     */

    public Message receive ( long timeout ,  String hmsg ) throws JMSException;


    /**
     * Do not block until a message is there, and use the supplied heuristic
     * information.
     * 
     * @param hmsg
     *            The heuristic information to show in case of problems.
     * @return Message The message, or null if none.
     * @exception JMSException
     *                On error.
     */

    public Message receiveNoWait ( String hmsg ) throws JMSException;

}
