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
