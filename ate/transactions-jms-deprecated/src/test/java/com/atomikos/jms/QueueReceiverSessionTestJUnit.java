package com.atomikos.jms;

import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.TextMessage;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

public class QueueReceiverSessionTestJUnit extends TransactionServiceTestCase 
{
	
	private static final int TIMEOUT = 2;
	
	private QueueReceiverSession session;
	private TestMessageListener listener;
	private TestXAResource xares;
	private UserTransactionService uts;
	private QueueConnectionFactoryBean qcfb;
	private TestExceptionListener exceptionListener;
	
	public QueueReceiverSessionTestJUnit ( String name )
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
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "QueueReceiverSessionTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		uts.init ( info );
		
		xares = new TestXAResource();
		session = new QueueReceiverSession();
		session.setTransactionTimeout ( TIMEOUT );
		session.setMessageListener ( listener );
		Queue queue = new TestQueue();
		session.setQueue ( queue );
		exceptionListener = new TestExceptionListener();
		session.setExceptionListener ( exceptionListener );
		TestQueueConnectionFactory fact = new TestQueueConnectionFactory ( xares );         
        qcfb = new QueueConnectionFactoryBean();
        qcfb.setXaQueueConnectionFactory ( fact );
        qcfb.setResourceName ( "TESTQUEUERESOURCE" );
		session.setQueueConnectionFactoryBean ( qcfb );	
		session.setNotifyListenerOnClose  ( true );
		listener = new TestMessageListener();
		session.setMessageListener ( listener );
	}
	
	
	protected void tearDown()
	{
		
		session.stopListening();
		
		//sleep to allow threads to exit
		//or threads will trigger restart!!!
		try {
			Thread.sleep ( TIMEOUT * 1000 );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testSendWithCommit()
	throws Exception
	{
		TestTextMessage message = new TestTextMessage(); 
		String text = "TEXT";
		message.setText ( text );
		String corrId = "ID";
		message.setJMSCorrelationID ( corrId );
		String type = "TYPE";
		message.setJMSType ( type );
		String property = "PROPERTY";
		message.setObjectProperty("property" , property );
		TestQueueReceiver.setNextMessageToReceive ( message );
		
		//start the listening process	
		session.startListening();
		
		//sleep until the receive is done
		Thread.sleep ( 1000 * session.getTransactionTimeout() );
		
		TextMessage receivedMessage = ( TextMessage) listener.getLastMessage();
		if ( receivedMessage == null ) failTest ( "Receive does not work");
		if ( !receivedMessage.getText().equals ( text ) )
			failTest ( "Receive does not pass content");
		if ( ! receivedMessage.getJMSCorrelationID().equals ( corrId ) ) 
			failTest ( "JMS Correlation ID not passed");
		if ( !receivedMessage.getJMSType().equals ( type ) ) 
			failTest ( "JMS type not passed" );
		if ( ! receivedMessage.getObjectProperty("property").equals ( property ) )
			failTest ( "JMS property not passed");
		//wait to allow commit threads to work
		sleep();
		
		if ( xares.getLastCommitted() == null ) failTest ( "No XA commit after receive on XAResource " + xares);
		
		if ( ! listener.wasCommitted() ) failTest ( "Listener did not get commit?");
		
	}
	
	public void testSendWithRollback()
	throws Exception
	{
		TestTextMessage message = new TestTextMessage(); 
		String text = "TEXT";
		message.setText ( text );
		String corrId = "ID";
		message.setJMSCorrelationID ( corrId );
		String type = "TYPE";
		message.setJMSType ( type );
		String property = "PROPERTY";
		message.setObjectProperty("property" , property );
		TestQueueReceiver.setNextMessageToReceive ( message );
		
		listener.setSimulateError ( true );
		
		//start the listening process	
		session.startListening();
		
		
		
		TestQueueReceiver.setNextMessageToReceive ( message );
		
		xares.reset();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );
		if ( xares.getLastCommitted() != null ) failTest ( "No XA rollback after failed receive");
		if ( listener.wasCommitted() ) failTest ( "Listener got commit after error" );
	}
	
	public void testRefreshConnectionOnErrors()
	throws Exception
	{
		
		TestTextMessage message = new TestTextMessage(); 
		String text = "TEXT";
		message.setText ( text );
		String corrId = "ID";
		message.setJMSCorrelationID ( corrId );
		String type = "TYPE";
		message.setJMSType ( type );
		String property = "PROPERTY";
		message.setObjectProperty("property" , property );
		TestQueueReceiver.setNextMessageToReceive ( message );
		
		//get first connection to trigger recovery of queue
		//otherwise 2 connections will be gotten (one for
		//recovery and one for the test message)
		//NOTE: MUST BE DONE BEFORE STARTING SESSION
		
		QueueConnection c = qcfb.createQueueConnection();
		c.close();
		
        int connections = TestQueueConnectionFactory.getNumberOfConnectionsCreated();
        
        
		session.startListening();
		

		
		TestQueueReceiver.setNextMessageToReceive ( message );
		TestQueueReceiver.setErrorOnNextReceive();
		xares.reset();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );

        
		//assert that the error in receive() has caused the creation of a new connection
        //note: this must be 2: one for initialization of session and one for renewal 
        //after simulated error
		if ( TestQueueConnectionFactory.getNumberOfConnectionsCreated() != connections + 2 )
					failTest( "Unexpected number of connections created: " + 
					connections + " vs " + TestQueueConnectionFactory.getNumberOfConnectionsCreated()  );	
		

		TestQueueReceiver.setNextMessageToReceive ( message );
		xares.reset();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );		
						
		session.stopListening();
		
		//assert that the close event is forwarded to listener
		Thread.sleep (1000 * session.getTransactionTimeout() );
		if ( !listener.wasClosed() )
			failTest ( "notifyListenerOnClose fails");
	}
	
	public void testExceptionListenerNotifiedOnCreateConnectionError()
	throws Exception
	{
		assertFalse ( exceptionListener.wasNotified() );
		//get first connection to trigger recovery of queue
		//otherwise 2 connections will be gotten (one for
		//recovery and one for the test message)
		//NOTE: MUST BE DONE BEFORE STARTING SESSION
		
		QueueConnection c = qcfb.createQueueConnection();
		c.close();
		TestQueueConnectionFactory.setErrorOnNextCreate();
		session.startListening();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );		
		session.stopListening();
		assertTrue ( exceptionListener.wasNotified() );
	}
	
	public void testExceptionListenerNotifiedOnReceiveConnectionError()
	throws Exception
	{
		assertFalse ( exceptionListener.wasNotified() );
		
		TestQueueReceiver.setErrorOnNextReceive();
		session.startListening();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );		
		session.stopListening();
		assertTrue ( exceptionListener.wasNotified() );
	}
	
}
