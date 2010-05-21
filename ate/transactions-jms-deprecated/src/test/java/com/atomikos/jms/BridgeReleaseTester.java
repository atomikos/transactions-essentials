package com.atomikos.jms;
import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import com.atomikos.datasource.xa.TestXAResource;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */
public class BridgeReleaseTester
{


	private static void fail ( String msg )
	throws Exception
	{
		throw new Exception ( msg );
	}
	
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
    
	public static void testBridge ( QueueConnectionFactoryBean qcfb )
	throws Exception
	{
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
    	
		QueueReceiverSessionPool pool =
			new QueueReceiverSessionPool();
		pool.setMessageListener ( bridge );
		pool.setPoolSize ( 1 );
		pool.setQueue ( sourceQueue );
		pool.setQueueConnectionFactoryBean ( qcfb );
		pool.setTransactionTimeout ( 2 );
		pool.setNotifyListenerOnClose ( true );
		pool.start();
    	
		//CASE 1: assert text message is passed OK
		TestTextMessage tmsg = new TestTextMessage();
		String text = "TEXT";
		tmsg.setText ( text );
    	
		//DON't set delay too high to avoid side-effect
		//of test classes: messages will get consumed
		//twice by the bridge (while we wait) and this
		//will cause errors in Streamed msgs
		long delay =  1500 * pool.getTransactionTimeout();
		tmsg = ( TestTextMessage ) testBridgingForMessage ( tmsg , delay );
		if ( ! text.equals (  tmsg.getText() ) )
			fail ( "Bridge doesn't pass text");
    	
		//CASE 2: assert that object message is passed ok
		ObjectMessage omsg = new TestObjectMessage();
		Serializable obj = new Integer ( 4 );
		omsg.setObject ( obj );
		omsg = ( ObjectMessage ) testBridgingForMessage ( omsg , delay );
		if ( ! obj.equals ( omsg.getObject())) 
			fail ( "Bridge doesn't preserve object content");
		
		
		//CASE 3: assert that map message is passed ok
		MapMessage mmsg = new TestMapMessage();
		mmsg.setObject ( "testobject" , obj );
		mmsg = ( MapMessage ) testBridgingForMessage ( mmsg , delay );
		if ( ! obj.equals ( mmsg.getObject ("testobject")))
			fail ( "Bridge doesn't preserve map content");
		
		//CASE 4: assert that stream message is passed ok
		//COMMENTED OUT SINCE THIS INTERFERES WITH THREADS
		//IN TEST CLASSES
//		StreamMessage smsg = new TestStreamMessage();
//		smsg.writeObject ( obj );
//		smsg.reset();
//		smsg = ( StreamMessage ) testBridgingForMessage ( smsg , delay );
//		smsg.reset();
//		Object o = smsg.readObject();
//		if ( ! obj.equals ( o ) )
//			fail ( "Bridge doesn't preserve stream object content");
//			
		    	
		pool.stop();
    	
	}
	
	public static void testBridgeRollback ( 
		QueueConnectionFactoryBean qcfb , TestXAResource xares )
		throws Exception
	{
		xares.reset();
		TestQueue destQueue = new TestQueue();
		TestQueue sourceQueue = new TestQueue();
		TestBridge bridge = new TestBridge();
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
    	
		QueueReceiverSessionPool pool =
			new QueueReceiverSessionPool();
		pool.setMessageListener ( bridge );
		pool.setPoolSize ( 1 );
		pool.setQueue ( sourceQueue );
		pool.setQueueConnectionFactoryBean ( qcfb );
		pool.setTransactionTimeout ( 2 );
		pool.setNotifyListenerOnClose ( true );
		pool.start();	
		
		TextMessage tmsg = new TestTextMessage();
		String text = "TEXT";
		tmsg.setText ( text );
		
		long now = System.currentTimeMillis();
		long expiration = now + 100000;
		tmsg.setJMSExpiration ( expiration );
		TestQueueReceiver.setNextMessageToReceive ( tmsg );
    	
		//sleep until the receive is done
		Thread.sleep ( 1500 );
		
		
		
		pool.stop();
		
		if ( xares.getLastRolledback() == null )  
			fail ( "Bridge failure does not rollback?");
	}
	
	public static void test ()
	throws Exception
	{

         
		TestXAResource xares = new TestXAResource();
		TestQueueConnectionFactory fact = new TestQueueConnectionFactory ( xares );         
		QueueConnectionFactoryBean qcfb = new QueueConnectionFactoryBean();
		qcfb.setXaQueueConnectionFactory ( fact );
		qcfb.setResourceName ( "TESTBRIDGEQUEUERESOURCE" );	
		testBridge ( qcfb );
		testBridgeRollback ( qcfb , xares );	
		//@todo Assert bridge works with different threads
	}

    public static void main(String[] args)
    throws Exception
    {
    	System.out.println ( "Starting BridgeReleaseTester" );
    	test();
    	System.out.println ( "Done" );
    }
}
