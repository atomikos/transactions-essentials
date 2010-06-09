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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.transaction.xa.XAResource;

import sun.misc.GC.LatencyRequest;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * Common superclass for the JTA message producers.
 *
 */

class DefaultJtaMessageProducer implements MessageProducer 
{

	
	private TransactionalResource res_;
	private XAResourceTransaction restx_;
	private XAResource xares_;
	private MessageProducer sender_;
	private String nameOfLastThreadToEnlist_;
	
	protected DefaultJtaMessageProducer ( 
			MessageProducer producer , 
			TransactionalResource res , XAResource xares
	)
	{
		this.sender_ = producer;
		this.res_ = res;
		this.restx_ = null;
		this.xares_ = xares;
		this.nameOfLastThreadToEnlist_ = "";
	}

	protected MessageProducer getMessageProducer()
	{
		return sender_;
	}
	
	/**
	 * Start a resource tx.
	 * 
	 * @param msg
	 *            The heuristic message to use, null if none.
	 * @exception JMSException
	 *                If already enlisted.
	 */
	protected synchronized void enlist ( HeuristicMessage msg )  throws JMSException 
	{
		String callingThreadName = Thread.currentThread().getName();
	    if ( restx_ != null ) {
	    	String m = "JtaMessageProducer.enlist: already enlisted";
	    	if ( !callingThreadName.equals ( nameOfLastThreadToEnlist_ ) ) 
	    		m = "JtaMessageProducer.enlist: Illegal multi-threaded use: object already enlisted by different thread [" + nameOfLastThreadToEnlist_ + "]";
	        throw new JMSException ( m );
	    }
	
	    
	    CompositeTransactionManager ctm = Configuration
	            .getCompositeTransactionManager ();
	
	    if ( ctm == null )
	        throw new JMSException (
	                "JTA MessageProducer: requires Atomikos TransactionsEssentials to be running! Please make sure to start a transaction first." );
	
	    CompositeTransaction ct = ctm.getCompositeTransaction ();
	    if ( ct == null || ct.getProperty (  TransactionManagerImp.JTA_PROPERTY_NAME ) == null )
	        throw new JMSException (
	                "JTA transaction required for JtaMessageProducer" );
	
	    restx_ = (XAResourceTransaction) res_.getResourceTransaction ( ct );
	    restx_.setXAResource ( xares_ );
	    restx_.resume ();
	    if ( msg != null )
	        restx_.addHeuristicMessage ( msg );
	    nameOfLastThreadToEnlist_ = callingThreadName;
	}

	/**
	 * End the resource tx.
	 * 
	 * @exception JMSException
	 *                If not previously enlisted.
	 */
	protected synchronized void delist() throws JMSException 
	{
	    if ( restx_ == null )
	        throw new JMSException ( "JtaMessageProducer.delist: not enlisted" );
	    restx_.suspend ();
	    restx_ = null;
	}

	public void setDisableMessageID ( boolean val ) throws JMSException 
	{
	    sender_.setDisableMessageID ( val );
	}

	public boolean getDisableMessageID() throws JMSException 
	{
	    return sender_.getDisableMessageID ();
	}

	public void setDisableMessageTimestamp ( boolean value ) throws JMSException 
	{
	    sender_.setDisableMessageTimestamp ( value );
	}

	public boolean getDisableMessageTimestamp() throws JMSException 
	{
		
	    return sender_.getDisableMessageTimestamp ();
	}

	public void setDeliveryMode ( int mode ) throws JMSException 
	{
	    sender_.setDeliveryMode ( mode );
	}

	public int getDeliveryMode() throws JMSException 
	{
	    return sender_.getDeliveryMode ();
	}

	public void setPriority ( int p ) throws JMSException 
	{
	    sender_.setPriority ( p );
	}

	public int getPriority() throws JMSException 
	{
	    return sender_.getPriority ();
	}

	public void setTimeToLive ( long ttl ) throws JMSException 
	{
	    sender_.setTimeToLive ( ttl );
	}

	public long getTimeToLive() throws JMSException 
	{
	    return sender_.getTimeToLive ();
	}

	public Destination getDestination() throws JMSException 
	{
		return sender_.getDestination();
	}

    public void close () throws JMSException
    {
        sender_.close ();
    }

	public void send ( Message msg ) throws JMSException 
	{
		//FIXED 10107
		sendToDefaultDestination ( msg , null );
		//send ( getDestination() , msg );
	}

	public void send ( Message msg, int deliveryMode, int priority, long timeToLive ) throws JMSException 
	{
		//FIXED 10107
		sendToDefaultDestination ( msg , deliveryMode , priority , timeToLive , null );
	    //send ( getDestination() , msg , deliveryMode , priority , timeToLive );
	}

	public void send (  Destination dest, Message msg ) throws JMSException 
	{
	    HeuristicMessage hmsg = new StringHeuristicMessage (
	            "Sending of JMS Message with ID: " + msg.getJMSMessageID () );
	    sendToDestination ( dest , msg , hmsg );
		
	}

	public void send ( Destination dest, Message msg, int deliveryMode, int priority, long timeToLive ) throws JMSException 
	{
		
		HeuristicMessage hmsg = new StringHeuristicMessage (
	            "Sending of JMS Message with ID: " + msg.getJMSMessageID () );
		//FIXED 20744
		sendToDestination ( dest , msg , deliveryMode , priority , timeToLive , hmsg );
		//sendToDestination ( getDestination() , msg , deliveryMode , priority , timeToLive , hmsg );
		
	}
	
	protected  void sendToDefaultDestination (  Message msg, int deliveryMode, int priority, long timeToLive , HeuristicMessage hmsg )
	throws JMSException
	{
		enlist ( hmsg );
		try {
	        sender_.send ( msg, deliveryMode, priority, timeToLive );
	    } finally {
	        delist ();
	    }
		
	}
	
	protected void sendToDefaultDestination ( Message msg , HeuristicMessage hmsg ) 
	throws JMSException
	{
		enlist ( hmsg );
		try {
	        sender_.send (  msg );
	    } finally {
	        delist ();
	    }
	}
	
	protected  void sendToDestination ( Destination dest, Message msg, int deliveryMode, int priority, long timeToLive , HeuristicMessage hmsg )
	throws JMSException
	{
		enlist ( hmsg );
		try {
	        sender_.send ( dest , msg, deliveryMode, priority, timeToLive );
	    } finally {
	        delist ();
	    }
		
	}

	protected void sendToDestination ( Destination dest , Message msg , HeuristicMessage hmsg ) 
	throws JMSException
	{
		enlist ( hmsg );
		try {
	        sender_.send ( dest , msg );
	    } finally {
	        delist ();
	    }
	}
	
}
