package com.atomikos.jms;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

public class BridgeTestJUnit extends TransactionServiceTestCase 
{
	
	//utility method to fill msg headers
	private static void fillBridgeHeaders ( Message msg )
	throws JMSException
	{
		msg.setObjectProperty ( "testproperty" , new Integer ( 12 ) );
		Integer p = ( Integer ) msg.getObjectProperty ( "testproperty" );
				
		if ( p == null || p.intValue() != 12 )
			throw new JMSException ( "Object properties not OK in TestMessage: " + p );
    	    	
		msg.setJMSCorrelationID ( "ID" );
		msg.setJMSDeliveryMode ( 123 );
		msg.setJMSPriority ( 1 );
		msg.setJMSType ( "type" );
    	
	}
    
	//utility method to check headers set by
	//fillHeaders method
	private static void checkBridgeHeaders ( Message msg )
	throws Exception
	{
		Integer p = ( Integer ) msg.getObjectProperty ( "testproperty" );
		if ( p == null || p.intValue() != 12 )
			fail ( "Object properties not preserved by bridge: " + p );
		String id = msg.getJMSCorrelationID();
		if ( id == null || !id.equals ("ID"))
			fail ( "JMS Correlation ID not preserved by bridge");
		int del = msg.getJMSDeliveryMode();
		if ( del != 123 ) 
			fail ( "JMS deliveryMode not preserved by bridge");
		if ( msg.getJMSPriority() != 1 )
			fail ( "JMS priority not preserved by bridge");
		String type = msg.getJMSType();
		if ( type == null || ! type.equals ( "type") )
			fail ( "JMS type not preserved by bridge");
	}
    
	private static Message testBridgingForMessage ( Message tmsg , long delay )
	throws Exception
	{

		fillBridgeHeaders ( tmsg );
		long now = System.currentTimeMillis();
		long expiration = now + 100000;
		tmsg.setJMSExpiration ( expiration );
		TestQueueReceiver.setNextMessageToReceive ( tmsg );
    	
		//sleep until the receive is done
		Thread.sleep ( delay );
		
		tmsg = TestQueueSender.getLastMessageSent();
		if ( tmsg == null ) fail ( "Bridge doesn't work");
		checkBridgeHeaders ( tmsg );
		    	
		return tmsg;
	}
	
	
	private UserTransactionService uts;
	private QueueReceiverSessionPool pool;
	private long delay;
	private TestXAResource xares;

	public BridgeTestJUnit ( String name )
	{
		super ( name );
	}
	
	protected void setUp()
	{
		super.setUp();
		uts =
            new UserTransactionServiceImp();
		
		
	    TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "BridgeTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		uts.init ( info );
		xares = new TestXAResource();
		TestQueueConnectionFactory fact = new TestQueueConnectionFactory ( xares );         
		QueueConnectionFactoryBean qcfb = new QueueConnectionFactoryBean();
		qcfb.setXaQueueConnectionFactory ( fact );
		qcfb.setResourceName ( "TESTBRIDGEQUEUERESOURCE" );	
		
		TestQueue destQueue = new TestQueue();
		TestQueue sourceQueue = new TestQueue();
		Bridge bridge = new Bridge();
		TestQueueReceiver.setNextMessageToReceive ( null );
		TestQueueSender.reset();
		TestQueueReceiver.reset();
		QueueSenderSessionFactory factory = 
			new QueueSenderSessionFactory();
		factory.setDeliveryMode ( 1 );
		factory.setPriority ( 2 );
		factory.setQueue ( destQueue );
		factory.setQueueConnectionFactoryBean ( qcfb );
		factory.setReplyToQueue ( null );
		factory.setTimeToLive ( 111 );

		bridge.setDestinationSessionFactory ( factory );
    	
		pool =
			new QueueReceiverSessionPool();
		pool.setMessageListener ( bridge );
		pool.setPoolSize ( 1 );
		pool.setQueue ( sourceQueue );
		pool.setQueueConnectionFactoryBean ( qcfb );
		pool.setTransactionTimeout ( 2 );
		pool.setNotifyListenerOnClose ( true );
		try {
			pool.start();
		} catch (JMSException e) {
			failTest ( e.getMessage() );
		}
		//DON't set delay too high to avoid side-effect
		//of test classes: messages will get consumed
		//twice by the bridge (while we wait) and this
		//will cause errors in Streamed msgs
		delay =  1500 * pool.getTransactionTimeout();
	}
	
	protected void tearDown()
	{
		pool.stop();
		
		//sleep to let threads exit
		//otherwise the TS will REstart
		//by thread activity
		try {
			Thread.sleep ( 1000 * pool.getTransactionTimeout() );
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		uts.shutdown ( true );
		super.tearDown();
	}
	
	
	public void testTextMessage()
	throws Exception
	{
		TestTextMessage tmsg = new TestTextMessage();
		String text = "TEXT";
		tmsg.setText ( text );
    	

		tmsg = ( TestTextMessage ) testBridgingForMessage ( tmsg , delay );
		if ( ! text.equals (  tmsg.getText() ) )
			failTest ( "Bridge doesn't pass text");
    	
	}
	
	public void testObjectMessage()
	throws Exception
	{
		ObjectMessage omsg = new TestObjectMessage();
		Serializable obj = new Integer ( 4 );
		omsg.setObject ( obj );
		omsg = ( ObjectMessage ) testBridgingForMessage ( omsg , delay );
		if ( ! obj.equals ( omsg.getObject())) 
			failTest ( "Bridge doesn't preserve object content");
		
	}
	
	public void testMapMessage()
	throws Exception
	{
		MapMessage mmsg = new TestMapMessage();
		Serializable obj = new Integer ( 4 );
		mmsg.setObject ( "testobject" , obj );
		mmsg = ( MapMessage ) testBridgingForMessage ( mmsg , delay );
		if ( ! obj.equals ( mmsg.getObject ("testobject")))
			failTest ( "Bridge doesn't preserve map content");
		
	}
	
	public void testRollbackAfterErrorOnReceive()
	throws Exception
	{
		TextMessage tmsg = new TestTextMessage();
		String text = "TEXT";
		tmsg.setText ( text );
		
		long now = System.currentTimeMillis();
		long expiration = now + 100000;
		tmsg.setJMSExpiration ( expiration );
		TestQueueReceiver.setNextMessageToReceive ( tmsg );
		TestQueueReceiver.setErrorOnNextReceive();
    	
		//sleep until the receive is done
		Thread.sleep ( delay );
		
		pool.stop();
		
		if ( xares.getLastRolledback() == null )  
			failTest ( "Bridge failure does not rollback?");
	}
	
	public void testRollbackAfterErrorOnSend()
	throws Exception
	{
		TextMessage tmsg = new TestTextMessage();
		String text = "TEXT";
		tmsg.setText ( text );
		
		long now = System.currentTimeMillis();
		long expiration = now + 100000;
		tmsg.setJMSExpiration ( expiration );
		TestQueueReceiver.setNextMessageToReceive ( tmsg );
		TestQueueSender.setErrorOnNextSend();
    	
		//sleep until the receive is done
		Thread.sleep ( delay );
		
		pool.stop();
		
		if ( xares.getLastRolledback() == null )  
			failTest ( "Bridge failure does not rollback?");
	}
}
