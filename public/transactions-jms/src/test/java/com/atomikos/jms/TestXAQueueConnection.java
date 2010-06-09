package com.atomikos.jms;

import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueSession;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;

public class TestXAQueueConnection implements XAQueueConnection {

	private String clientId;
	private ExceptionListener listener;
	private boolean startCalled, stopCalled, closeCalled;
	private XAResource xares;
	
	public TestXAQueueConnection ( XAResource xares ) {
		this.xares = xares;
	}
	
	public TestXAQueueConnection () {
		this  ( null );
	}
	
	public XAQueueSession createXAQueueSession() throws JMSException {
	
		return new TestQueueSession ( xares );
	}

	public QueueSession createQueueSession(boolean transacted , int ack )
			throws JMSException {
		if ( ! transacted ) return new TestQueueSession();
		else return new TestQueueSession ( null );
	}

	public XASession createXASession() throws JMSException {

		return createXAQueueSession();
	}

	public Session createSession(boolean transacted , int ack) throws JMSException {
	
		return createQueueSession ( transacted , ack );
	}

	public String getClientID() throws JMSException {
		return clientId;
	}

	public void setClientID(String id) throws JMSException {
		this.clientId = id;

	}

	public ConnectionMetaData getMetaData() throws JMSException {
		
		return null;
	}

	public ExceptionListener getExceptionListener() throws JMSException {
		
		return listener;
	}

	public void setExceptionListener(ExceptionListener l)
			throws JMSException {
		this.listener = l;
	}

	public void start() throws JMSException {
		startCalled = true;
	}
	
	public boolean startCalled()
	{
		return startCalled;
	}

	public void stop() throws JMSException {
		stopCalled = true;

	}
	
	public boolean stopCalled()
	{
		return stopCalled;
	}

	public void close() throws JMSException {
		closeCalled = true;

	}

	public boolean closeCalled()
	{
		return closeCalled;
	}
	public ConnectionConsumer createConnectionConsumer(Destination arg0,
			String arg1, ServerSessionPool arg2, int arg3) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public ConnectionConsumer createDurableConnectionConsumer(Topic arg0,
			String arg1, String arg2, ServerSessionPool arg3, int arg4)
			throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	public ConnectionConsumer createConnectionConsumer(Queue arg0, String arg1,
			ServerSessionPool arg2, int arg3) throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

}
