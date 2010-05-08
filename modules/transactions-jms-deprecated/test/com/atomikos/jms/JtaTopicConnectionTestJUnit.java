package com.atomikos.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import junit.framework.TestCase;

public class JtaTopicConnectionTestJUnit extends TestCase {

	private JtaTopicConnection conn;
	private TestXATopicConnection xaconn;
	
	protected void setUp() throws Exception {
		super.setUp();
		xaconn = new TestXATopicConnection();
		conn = new JtaTopicConnection ( xaconn , null);
	}
	
	public void testCreateConsumerWithServerPool()
	{
		try {
			conn.createConnectionConsumer(null , null , null , 0);
			fail ();
		} catch (JMSException ok) {
			
		}
		try {
			conn.createDurableConnectionConsumer(null , null , null , null, 0);
			fail ();
		} catch (JMSException ok) {
			
		}
	
	}
	
	public void testClose() throws JMSException
	{
		assertFalse ( xaconn.closeCalled() );
		conn.close();
		assertTrue ( xaconn.closeCalled() );
	}

	public void testStart() throws Exception
	{
		assertFalse (xaconn.startCalled());
		conn.start();
		assertTrue (xaconn.startCalled());
	}
	
	public void testStop() throws Exception
	{
		assertFalse (xaconn.stopCalled());
		conn.stop();
		assertTrue (xaconn.stopCalled());
	}

	public void testExceptionListener() throws Exception
	{
		ExceptionListener l = new ExceptionListener() {

			public void onException(JMSException arg0) {
				
			}
			
		};
		
		assertNull ( conn.getExceptionListener() );
		conn.setExceptionListener ( l );
		assertEquals ( l , conn.getExceptionListener() );
	}
	
	public void testMetaData() throws JMSException
	{
		assertNull ( conn.getMetaData() );
	}
	
	public void testClientId() throws Exception
	{
		String id = "id";
		assertNull ( conn.getClientID() );
		conn.setClientID ( id );
		assertEquals ( id , conn.getClientID() );
	}
	

}
