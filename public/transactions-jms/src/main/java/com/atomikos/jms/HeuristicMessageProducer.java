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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

/**
 * 
 * 
 * A message producer with support for heuristic information. This information
 * is kept in the logs and can help to provide details in case of indoubt or
 * heuristic XA transactions during receive.
 */

public interface HeuristicMessageProducer extends MessageProducer 
{
	/**
	 * Sends a message with a given heuristic message for resolving indoubt cases.
	 * 
	 * @param msg
	 * @param heuristicMessage
	 * @throws JMSException
	 */
	
	public void send ( Message msg , String heuristicMessage ) throws JMSException;
	
	/**
	 * Sends a message to a destination with a given heuristic message for resolving indoubt cases.
	 * 
	 * @param dest
	 * @param msg
	 * @param heuristicMessage
	 * @throws JMSException
	 */
	
	public void send ( Destination dest , Message msg , String heuristicMessage ) throws JMSException;

	/**
	 * Sends a message with the given parameters and a given heuristic message for resolving indoubt cases.
	 * @param msg
	 * @param deliveryMode
	 * @param priority
	 * @param timeToLive
	 * @param heuristicMessage
	 * @throws JMSException
	 */
	public void send ( Message msg , int deliveryMode, int priority, long timeToLive , String heuristicMessage )
	throws JMSException;
	
	/**
	 * Sends a message with the given parameters and a given heuristic message for resolving indoubt cases.
	 * 
	 * @param dest
	 * @param msg
	 * @param deliveryMode
	 * @param priority
	 * @param timeToLive
	 * @param heuristicMessage
	 * @throws JMSException
	 */
	
	public void send ( Destination dest , Message msg , int deliveryMode, int priority, long timeToLive , String heuristicMessage ) 
	throws JMSException;
}
