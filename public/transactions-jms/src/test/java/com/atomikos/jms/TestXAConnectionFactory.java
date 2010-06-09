package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;

import com.atomikos.datasource.xa.TestXAResource;

public class TestXAConnectionFactory implements XAConnectionFactory {

	public static TestXAResource xares = new TestXAResource();
	//static public to allow test access
	
	private static int createCount = 0;
	
	public static int getCreateCount() {
		return createCount;
	}
	
	public XAConnection createXAConnection() throws JMSException {
		createCount++;
		return new TestQueueConnection ( xares );
	}

	public XAConnection createXAConnection(String user, String pw)
			throws JMSException {
		return createXAConnection();
	}
	
}

