package com.atomikos.jms;

import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSession;
import javax.jms.XASession;
import javax.jms.XATopicConnection;
import javax.jms.XATopicSession;

public class TestXATopicConnection implements XATopicConnection {

	private boolean closeCalled;
	private boolean startCalled;
	private boolean stopCalled;
	private String clientId;
	private ExceptionListener exceptionListener;

	public XATopicSession createXATopicSession() throws JMSException {
		
		return null;
	}

	public TopicSession createTopicSession(boolean arg0, int arg1)
			throws JMSException {
		return null;
	}

	public XASession createXASession() throws JMSException {
		return null;
	}

	public Session createSession(boolean arg0, int arg1) throws JMSException {
		
		return null;
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
		
		return exceptionListener;
	}

	public void setExceptionListener(ExceptionListener l)
			throws JMSException {
		this.exceptionListener = l;

	}

	public void start() throws JMSException {
		startCalled = true;

	}

	public void stop() throws JMSException {
		
		stopCalled = true;
	}

	public void close() throws JMSException {
		closeCalled = true;
	}

	public ConnectionConsumer createConnectionConsumer(Destination arg0,
			String arg1, ServerSessionPool arg2, int arg3) throws JMSException {
		
		return null;
	}

	public ConnectionConsumer createDurableConnectionConsumer(Topic arg0,
			String arg1, String arg2, ServerSessionPool arg3, int arg4)
			throws JMSException {
		
		return null;
	}

	public ConnectionConsumer createConnectionConsumer(Topic arg0, String arg1,
			ServerSessionPool arg2, int arg3) throws JMSException {
		
		return null;
	}

	public boolean closeCalled() {
		return closeCalled;
	}

	public boolean startCalled() {
		return startCalled;
	}

	public boolean stopCalled() {
		return stopCalled;
	}

}
