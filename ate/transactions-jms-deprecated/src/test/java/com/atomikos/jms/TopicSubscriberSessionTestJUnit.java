package com.atomikos.jms;

import java.util.Properties;

import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TextMessage;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

public class TopicSubscriberSessionTestJUnit 
extends TransactionServiceTestCase 
{
	
	private static final int TIMEOUT = 2;
	
	private TopicSubscriberSession session;
	private TestMessageListener listener;
	private TestXAResource xares;
	private UserTransactionService uts;
	private TopicConnectionFactoryBean qcfb;

	public TopicSubscriberSessionTestJUnit ( String name )
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
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TopicSubscriberSessionTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		uts.init ( info );
		
		xares = new TestXAResource();
		session = new TopicSubscriberSession();
		session.setTransactionTimeout ( TIMEOUT );
		session.setMessageListener ( listener );
		Topic topic = new TestTopic();
		session.setTopic ( topic );
		TestTopicConnectionFactory fact = new TestTopicConnectionFactory ( xares );         
        qcfb = new TopicConnectionFactoryBean();
        qcfb.setXaTopicConnectionFactory ( fact );
        qcfb.setResourceName ( getClass().getSimpleName() );
		session.setTopicConnectionFactoryBean ( qcfb );	
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
		TestTopicSubscriber.setNextMessageToReceive ( message );
		
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
		TestTopicSubscriber.setNextMessageToReceive ( message );
		
		listener.setSimulateError ( true );
		
		//start the listening process	
		session.startListening();
		
		
		
		TestTopicSubscriber.setNextMessageToReceive ( message );
		
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
		TestTopicSubscriber.setNextMessageToReceive ( message );
		
		//get first connection to trigger recovery of topic
		//otherwise 2 connections will be gotten (one for
		//recovery and one for the test message)
		//NOTE: MUST BE DONE BEFORE STARTING SESSION
		
		TopicConnection c = qcfb.createTopicConnection();
		c.close();
		
        int connections = TestTopicConnectionFactory.getNumberOfConnectionsCreated();
        
        
		session.startListening();
		

		
		TestTopicSubscriber.setNextMessageToReceive ( message );
		TestTopicSubscriber.setErrorOnNextReceive();
		xares.reset();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );

        
		//assert that the error in receive() has caused the creation of a new connection
        //note: this must be 2: one for initialization of session and one for renewal 
        //after simulated error
		if ( TestTopicConnectionFactory.getNumberOfConnectionsCreated() != connections + 2 )
					failTest( "Unexpected number of connections created: " + 
					connections + " vs " + TestTopicConnectionFactory.getNumberOfConnectionsCreated()  );	
		

		TestTopicSubscriber.setNextMessageToReceive ( message );
		xares.reset();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );		
						
		session.stopListening();
		
		//assert that the close event is forwarded to listener
		Thread.sleep (1000 * session.getTransactionTimeout() );
		if ( !listener.wasClosed() )
			failTest ( "notifyListenerOnClose fails");
	}
	
}
