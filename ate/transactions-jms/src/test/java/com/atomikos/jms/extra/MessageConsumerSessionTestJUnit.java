package com.atomikos.jms.extra;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.TestMessageListener;
import com.atomikos.jms.TestTextMessage;
import com.atomikos.jms.TestTopic;
import com.atomikos.jms.TestTopicConnectionFactory;
import com.atomikos.jms.TestTopicSubscriber;

public class MessageConsumerSessionTestJUnit 
extends TransactionServiceTestCase 
{
	
	private static final int TIMEOUT = 2;

	private static final String TEXT = "TEXT";
	private static final String CORRID = "ID";
	private static final String TYPE = "TYPE";
	private static final String PROPERTY = "PROPERTY";
	
	private MessageConsumerSession session;
	private TestMessageListener listener;
	private TestXAResource xares;
	private UserTransactionService uts;
	private AtomikosConnectionFactoryBean qcfb;
	private TestTextMessage message;
	
	
	public MessageConsumerSessionTestJUnit ( String name )
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
		session = new MessageConsumerSession(
				new MessageConsumerSessionProperties () {

					public int getTransactionTimeout() {
						
						return TIMEOUT;
					}

					public boolean getUnsubscribeOnClose() {
						return false;
					}
					
				}
		);
		session.setMessageListener ( listener );
		Topic topic = new TestTopic();
		session.setDestination ( topic );
		TestTopicConnectionFactory fact = new TestTopicConnectionFactory ( xares );         
        qcfb = new AtomikosConnectionFactoryBean();
        qcfb.setXaConnectionFactory ( fact );
        qcfb.setUniqueResourceName ( "TESTQUEUERESOURCE" );
		session.setAtomikosConnectionFactoryBean ( qcfb );	
		session.setNotifyListenerOnClose  ( true );
		listener = new TestMessageListener();
		session.setMessageListener ( listener );
		
		try {
			message = new TestTextMessage(); 
			message.setText ( TEXT );
			message.setJMSCorrelationID ( CORRID );
			message.setJMSType ( TYPE );
			message.setObjectProperty("property" , PROPERTY );
			TestTopicSubscriber.setNextMessageToReceive ( message );
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
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
		qcfb.close();
		super.tearDown();
	}
	
	public void testSendWithCommit()
	throws Exception
	{
		
		
		//start the listening process	
		session.startListening();
		
		//sleep until the receive is done
		Thread.sleep ( 1000 * session.getTransactionTimeout() );
		
		TextMessage receivedMessage = ( TextMessage) listener.getLastMessage();
		if ( receivedMessage == null ) failTest ( "Receive does not work");
		if ( !receivedMessage.getText().equals ( TEXT ) )
			failTest ( "Receive does not pass content");
		if ( ! receivedMessage.getJMSCorrelationID().equals ( CORRID ) ) 
			failTest ( "JMS Correlation ID not passed");
		if ( !receivedMessage.getJMSType().equals ( TYPE ) ) 
			failTest ( "JMS type not passed" );
		if ( ! receivedMessage.getObjectProperty("property").equals ( PROPERTY ) )
			failTest ( "JMS property not passed");
		//wait to allow commit threads to work
		sleep();
		
		if ( xares.getLastCommitted() == null ) failTest ( "No XA commit after receive on XAResource " + xares);
		
		if ( ! listener.wasCommitted() ) failTest ( "Listener did not get commit?");
		
	}
	
	public void testSendWithCommitAndDestinationNameSet()
	throws Exception
	{
		
		session.setDestination ( null );
		session.setDestinationName ( "myDestinationName" );
		
		//start the listening process	
		session.startListening();
		
		//sleep until the receive is done
		Thread.sleep ( 1000 * session.getTransactionTimeout() );
		
		TextMessage receivedMessage = ( TextMessage) listener.getLastMessage();
		if ( receivedMessage == null ) failTest ( "Receive does not work");
		if ( !receivedMessage.getText().equals ( TEXT ) )
			failTest ( "Receive does not pass content");
		if ( ! receivedMessage.getJMSCorrelationID().equals ( CORRID ) ) 
			failTest ( "JMS Correlation ID not passed");
		if ( !receivedMessage.getJMSType().equals ( TYPE ) ) 
			failTest ( "JMS type not passed" );
		if ( ! receivedMessage.getObjectProperty("property").equals ( PROPERTY ) )
			failTest ( "JMS property not passed");
		//wait to allow commit threads to work
		sleep();
		
		if ( xares.getLastCommitted() == null ) failTest ( "No XA commit after receive on XAResource " + xares);
		
		if ( ! listener.wasCommitted() ) failTest ( "Listener did not get commit?");
		
	}
	
	public void testSendWithRollback()
	throws Exception
	{
		
		
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
	
		
		//get first connection to trigger recovery of topic
		//otherwise 2 connections will be gotten (one for
		//recovery and one for the test message)
		//NOTE: MUST BE DONE BEFORE STARTING SESSION
		
		TopicConnection c = ( TopicConnection) qcfb.createConnection();
		c.close();
		
        int connections = TestTopicConnectionFactory.getNumberOfConnectionsCreated();
        
		TestTopicSubscriber.setErrorOnNextReceive();
		xares.reset();
        
		session.startListening();
		

		
		//make sure to sleep at least for more than 1 second to incorporate the pool wait time for refresh connection
		Thread.sleep  ( 1000 * ( session.getTransactionTimeout() + 1 ));

        
		//due to the simulated error, the pool must have refreshed the erronous connection
		if ( TestTopicConnectionFactory.getNumberOfConnectionsCreated() != connections + 1 )
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
