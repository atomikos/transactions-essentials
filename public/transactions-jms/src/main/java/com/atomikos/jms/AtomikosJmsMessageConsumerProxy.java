/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

class AtomikosJmsMessageConsumerProxy extends ConsumerProducerSupport implements
		MessageConsumer {
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsMessageConsumerProxy.class);
	
	
	private MessageConsumer delegate;
	
	public AtomikosJmsMessageConsumerProxy ( MessageConsumer delegate , SessionHandleState state ) {
		super ( state );
		this.delegate = delegate;
	}
	
	protected MessageConsumer getDelegate() 
	{
		return delegate;
	}
	
	public Message receive() throws JMSException {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": receive()..." );
		Message ret = null;
		try {
			enlist();
			ret = delegate.receive();
		} catch (Exception e) {
			handleException ( e );
		}
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": receive returning " + ret );
		return ret;
	}

	public Message receive ( long timeout ) throws JMSException {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": receive ( " + timeout + ")..." );
		
		Message ret = null;
		try {
			enlist();
			ret = delegate.receive ( timeout );
		} catch (Exception e) {
			handleException ( e );
		}
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": receive returning " + ret );
		return ret;
	}

	public Message receiveNoWait() throws JMSException {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": receiveNoWait()..." );
		
		Message ret = null;
		try {
			enlist();
			ret = delegate.receiveNoWait();
		} catch (Exception e) {
			handleException ( e );
		}
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": receiveNoWait returning " + ret );
		return ret;
	}

	public void close() throws JMSException {
		//note: delist is done at session level!
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": close..." );
		try {
			delegate.close();
		} catch (Exception e) {
			handleException ( e );
		}
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": close done." );	
	}

	public MessageListener getMessageListener() throws JMSException {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getMessageListener()..." );
		MessageListener ret = null;
		try {
			ret = delegate.getMessageListener();
		} catch (Exception e) {
			handleException ( e );
		}
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": getMessageListener() returning " + ret );
		return ret;
	}

	public String getMessageSelector() throws JMSException {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getMessageSelector()..." );
		String ret = null;
		try {
			ret = delegate.getMessageSelector();
		} catch (Exception e) {
			handleException ( e );
		}
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": getMessageSelector() returning " + ret );
		return ret;
	}

	public void setMessageListener ( MessageListener listener ) throws JMSException {
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": setMessageListener ( " + listener + " )..." );
		try {
			delegate.setMessageListener ( listener );
		}catch (Exception e) {
			handleException ( e );
		}
		if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this + ": setMessageListener done." );
	}
	
	public String toString() 
	{
		return "atomikos MessageConsumer proxy for " + delegate;
	}

}
