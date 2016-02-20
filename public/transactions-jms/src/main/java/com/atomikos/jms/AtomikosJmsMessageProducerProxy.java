/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

class AtomikosJmsMessageProducerProxy extends ConsumerProducerSupport implements
		MessageProducer {
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsMessageProducerProxy.class);
	
	private MessageProducer delegate;
	
	AtomikosJmsMessageProducerProxy ( MessageProducer delegate , SessionHandleState state ) 
	{
		super ( state );
		this.delegate = delegate;
	}
	
	public void send ( Message msg ) throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": send ( message )..." );
		enlist ( );
		delegate.send ( msg );
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": send done." );
	}

	public void close() throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo( this + ": close..." );
		delegate.close();
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": close done." );
	}

	public int getDeliveryMode() throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": getDeliveryMode()..." );
		int ret = delegate.getDeliveryMode();
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getDeliveryMode() returning " + ret );
		return ret;
	}

	public Destination getDestination() throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": getDestination()..." );
		Destination ret = delegate.getDestination();
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getDestination() returning " + ret );
		return ret;
	}

	public boolean getDisableMessageID() throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": getDisableMessageID()..." );
		boolean ret = delegate.getDisableMessageID();
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getDisableMessageID() returning " + ret );
		return ret;
	}

	public boolean getDisableMessageTimestamp() throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": getDisableMessageTimestamp()..." );
		boolean ret = delegate.getDisableMessageTimestamp();
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getDisableMessageTimestamp() returning " + ret );
		return ret;
	}

	public int getPriority() throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": getPriority()..." );
		int ret =  delegate.getPriority();
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getPriority() returning " + ret );
		return ret;
	}

	public long getTimeToLive() throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": getTimeToLive()..." );
		long ret =  delegate.getTimeToLive();
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getTimeToLive() returning " + ret );
		return ret;
	}



	public void send(Destination dest , Message msg ) throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": send ( destination , message )..." );
		enlist ( );
		delegate.send ( dest , msg );
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": send done." );
	}

	public void send(Message msg , int deliveryMode , int priority , long timeToLive )
			throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": send ( message , deliveryMode , priority , timeToLive )..." );
		enlist ( );
		delegate.send (  msg , deliveryMode , priority , timeToLive );
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": send done." );
	}

	public void send ( Destination dest , Message msg , int deliveryMode , int priority ,
			long timeToLive ) throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": send ( destination , message , deliveryMode , priority , timeToLive )..." );
		enlist ( );
		delegate.send (  dest , msg , deliveryMode , priority , timeToLive );
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": send done." );
	}

	public void setDeliveryMode ( int mode ) throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": setDeliveryMode ( " + mode + " )..." );
		delegate.setDeliveryMode ( mode );
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": setDeliveryMode done." );
	}

	public void setDisableMessageID ( boolean mode ) throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": setDisableMessageID ( " + mode + " )..." );
		delegate.setDisableMessageID ( mode );
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": setDisableMessageID done." );
	}

	public void setDisableMessageTimestamp ( boolean mode ) throws JMSException {
		
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": setDisableMessageTimestamp ( " + mode + " )..." );
		delegate.setDisableMessageTimestamp ( mode );
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": setDisableMessageTimestamp done." );
	}

	public void setPriority ( int pty ) throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": setPriority ( " + pty + " )..." );
		delegate.setPriority ( pty );
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": setPriority done." );
	}

	public void setTimeToLive ( long ttl ) throws JMSException {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": setTimeToLive ( " + ttl + " )..." );
		delegate.setTimeToLive ( ttl );
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": setTimeToLive done." );
	}
	
	public String toString() 
	{
		return "atomikos MessageProducer proxy for " + delegate;
	}

}
