package com.atomikos.jms;

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
