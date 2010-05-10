package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.icatch.system.Configuration;

class AtomikosJmsMessageConsumerProxy extends ConsumerProducerSupport implements
		HeuristicMessageConsumer {
	
	
	private MessageConsumer delegate;
	
	public AtomikosJmsMessageConsumerProxy ( MessageConsumer delegate , SessionHandleState state ) {
		super ( state );
		this.delegate = delegate;
	}
	
	protected MessageConsumer getDelegate() 
	{
		return delegate;
	}
	
	public Message receive ( String hmsg ) throws JMSException {
		Configuration.logInfo ( this + ": receive ( " + hmsg + " )..." );
		Message ret = null;
		try {
			enlist ( hmsg );
			ret = delegate.receive();
		} catch (Exception e) {
			handleException ( e );
		}
		Configuration.logDebug ( this + ": receive returning " + ret );
		return ret;
	}

	public Message receive ( long timeout , String hmsg ) throws JMSException {
		Configuration.logInfo ( this + ": receive ( " + timeout + " , " + hmsg + " )..." );
		
		Message ret = null;
		try {
			enlist ( hmsg );
			ret = delegate.receive ( timeout );
		} catch (Exception e) {
			handleException ( e );
		}
		Configuration.logDebug ( this + ": receive returning " + ret );
		return ret;
	}

	public Message receiveNoWait ( String hmsg ) throws JMSException {
		Configuration.logInfo ( this + ": receiveNoWait ( " + hmsg + " )..." );
		
		Message ret = null;
		try {
			enlist ( hmsg );
			ret = delegate.receiveNoWait();
		} catch (Exception e) {
			handleException ( e );
		}
		Configuration.logDebug ( this + ": receiveNoWait returning " + ret );
		return ret;
	}

	public void close() throws JMSException {
		//note: delist is done at session level!
		Configuration.logInfo ( this + ": close..." );
		try {
			delegate.close();
		} catch (Exception e) {
			handleException ( e );
		}
		Configuration.logDebug ( this + ": close done." );	
	}

	public MessageListener getMessageListener() throws JMSException {
		Configuration.logInfo ( this + ": getMessageListener()..." );
		MessageListener ret = null;
		try {
			ret = delegate.getMessageListener();
		} catch (Exception e) {
			handleException ( e );
		}
		Configuration.logDebug ( this + ": getMessageListener() returning " + ret );
		return ret;
	}

	public String getMessageSelector() throws JMSException {
		Configuration.logInfo ( this + ": getMessageSelector()..." );
		String ret = null;
		try {
			ret = delegate.getMessageSelector();
		} catch (Exception e) {
			handleException ( e );
		}
		Configuration.logDebug ( this + ": getMessageSelector() returning " + ret );
		return ret;
	}

	public Message receive() throws JMSException {
		Configuration.logInfo ( this + ": receive()..." );
		Message ret = receive ( null );
		Configuration.logDebug ( this + ": receive() returning " + ret );
		return ret;
	}

	public Message receive ( long timeout ) throws JMSException {
		Configuration.logInfo ( this + ": receive ( " + timeout + " )..." );
		Message ret =  receive ( timeout , null );
		Configuration.logDebug ( this + ": receive() returning " + ret );
		return ret;
	}

	public Message receiveNoWait() throws JMSException {
		Configuration.logInfo ( this + ": receiveNoWait()..." );
		Message ret = receiveNoWait ( null );
		Configuration.logDebug ( this + ": receiveNoWait() returning " + ret );
		return ret;
	}

	public void setMessageListener ( MessageListener listener ) throws JMSException {
		Configuration.logInfo ( this + ": setMessageListener ( " + listener + " )..." );
		try {
			delegate.setMessageListener ( listener );
		}catch (Exception e) {
			handleException ( e );
		}
		Configuration.logDebug ( this + ": setMessageListener done." );
	}
	
	public String toString() 
	{
		return "atomikos MessageConsumer proxy for " + delegate;
	}

}
