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

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.XASession;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.TransactionInProgressException;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * Common session functionality for queues and topics.
 * 
 *
 */

class DefaultJtaSession implements Session 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(DefaultJtaSession.class);

	private XASession session_;
	private TransactionalResource res_;
	private XAResource xares_;
	
	DefaultJtaSession ( XASession session , TransactionalResource res , XAResource xares )
	{
		session_ = session;
		res_ = res;
		xares_ = xares;
	}
    
	
	protected Session getSession() 
	{
        Session ret = null;
        try {
             ret = session_.getSession();
        }
        catch ( JMSException e ) {
            //TODO log
            throw new RuntimeException ( e );
        }
		return ret;
	}
	
	protected TransactionalResource getTransactionalResource()
	{
		return res_;
	}
	
	protected XAResource getXAResource()
	{
		return xares_;
	}

	public TemporaryQueue createTemporaryQueue() throws JMSException 
	{
	    return getSession().createTemporaryQueue ();
	}

	public QueueBrowser createBrowser ( Queue queue, String messageSelector ) throws JMSException 
	{
	    return getSession().createBrowser ( queue, messageSelector );
	}

	public QueueBrowser createBrowser ( Queue queue ) throws JMSException 
	{
	    return getSession().createBrowser ( queue );
	}

	public Queue createQueue ( String name ) throws JMSException 
	{
	    return getSession().createQueue ( name );
	}

	public void commit() throws JMSException 
	{
	    throw new TransactionInProgressException (
	            "XA Session: commit not allowed on session" );
	}

	public void rollback() throws JMSException 
	{
	    throw new TransactionInProgressException (
	            "XA Session: rollback not allowed on session" );
	}

	public boolean getTransacted() throws JMSException 
	{
	    return true;
	}

	public void run() 
	{
	    getSession().run ();
	}

	public void setMessageListener ( MessageListener l ) throws JMSException 
	{
	    getSession().setMessageListener ( l );
	}

	public MessageListener getMessageListener() throws JMSException 
	{
	    return getSession().getMessageListener ();
	}

	public void recover() throws JMSException 
	{
	    throw new javax.jms.IllegalStateException (
	            "Transacted session: recover not allowed" );
	}

	public void close() throws JMSException 
	{
	    if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Closing JMS session..." );
	    session_.close ();
	    if ( Configuration.isInfoLoggingEnabled() ) Configuration.logInfo ( "Closed JMS session" );
	}

	public TextMessage createTextMessage() throws JMSException 
	{
	    return getSession().createTextMessage ();
	}

	public TextMessage createTextMessage ( String text ) throws JMSException 
	{
	    return getSession().createTextMessage ( text );
	}

	public StreamMessage createStreamMessage() throws JMSException 
	{
	    return getSession().createStreamMessage ();
	}

	public ObjectMessage createObjectMessage ( java.io.Serializable o ) throws JMSException 
	{
	    return getSession().createObjectMessage ( o );
	}

	public ObjectMessage createObjectMessage() throws JMSException 
	{
	    return getSession().createObjectMessage ();
	}

	public Message createMessage() throws JMSException 
	{
	    return getSession().createMessage ();
	}

	public MapMessage createMapMessage() throws JMSException 
	{
	    return getSession().createMapMessage ();
	}

	public BytesMessage createBytesMessage() throws JMSException 
	{
	    return getSession().createBytesMessage ();
	}

	public int getAcknowledgeMode() throws JMSException 
	{
		return getSession().getAcknowledgeMode();
	}

	
	public Topic createTopic ( String topicName ) throws JMSException 
	{
		return getSession().createTopic ( topicName );
	}

	public TopicSubscriber createDurableSubscriber ( Topic topic, String name ) throws JMSException 
	{
		TopicSubscriber s = getSession().createDurableSubscriber ( topic , name );
		return new JtaTopicSubscriber ( s , res_ , xares_ );
	}

	public TopicSubscriber createDurableSubscriber ( Topic topic, String name, String selector, boolean noLocal ) throws JMSException 
	{
		TopicSubscriber s = getSession().createDurableSubscriber ( topic , name , selector , noLocal );
		return new JtaTopicSubscriber ( s , res_ , xares_ );
	}

	public TemporaryTopic createTemporaryTopic() throws JMSException 
	{
		return getSession().createTemporaryTopic();
	}

	public void unsubscribe ( String name ) throws JMSException 
	{
		getSession().unsubscribe ( name );
		
	}

	public MessageProducer createProducer ( Destination dest ) 
	throws JMSException {
		MessageProducer mp = getSession().createProducer ( dest );
		return new DefaultJtaMessageProducer ( mp , res_ , xares_ );
		
	}

	public MessageConsumer createConsumer ( Destination dest ) throws 
	JMSException {
		MessageConsumer mc = getSession().createConsumer ( dest );
		return new DefaultJtaMessageConsumer ( mc , res_ , xares_ );
	}

	public MessageConsumer createConsumer (
			Destination dest , String messageSelector ) throws JMSException {
		MessageConsumer mc = getSession().createConsumer ( dest , messageSelector );
		return new DefaultJtaMessageConsumer ( mc , res_ , xares_ );
	}

	public MessageConsumer createConsumer ( Destination dest , String messageSelector , 
			boolean noLocal ) throws JMSException {
		//TODO: check what with noLocal?
		MessageConsumer mc = getSession().createConsumer ( dest , messageSelector , noLocal );
		return new DefaultJtaMessageConsumer ( mc , res_ , xares_ );
	}
	

}

