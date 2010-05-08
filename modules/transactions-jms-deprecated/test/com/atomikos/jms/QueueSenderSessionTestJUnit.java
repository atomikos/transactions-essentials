package com.atomikos.jms;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.transaction.UserTransaction;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

public class QueueSenderSessionTestJUnit 
extends TransactionServiceTestCase {

	
	private static final int DELIVERYMODE = 1;
	private static final String PW = "SECRET";
	private static final String USER = "USER";
	private static final int PRIORITY = 1;
	private static final long TTL = 100;
	
	private UserTransactionService uts;
	private UserTransaction utx;
	private TestXAResource xares;
	private QueueSenderSession session;
	private QueueConnectionFactoryBean qcfb;
	private TestQueue queue, replyToQueue;
	private TextMessage msg;
	
	public QueueSenderSessionTestJUnit ( String name ) 
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
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "QueueSenderSessionTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		uts.init ( info );
		utx = uts.getUserTransaction();
		
		xares = new TestXAResource();
		TestQueueConnectionFactory fact = new TestQueueConnectionFactory(xares);
		session = new QueueSenderSession();
		
		qcfb = new QueueConnectionFactoryBean();
		qcfb.setXaQueueConnectionFactory(fact);
		qcfb.setResourceName("TESTQUEUERESOURCE");
		session.setDeliveryMode ( DELIVERYMODE );
		session.setPriority ( PRIORITY );
    		session.setTimeToLive ( TTL );
    		session.setQueueConnectionFactoryBean ( qcfb );
    		queue = new TestQueue();
    		replyToQueue = new TestQueue();
    		session.setQueue ( queue );
    		session.setReplyToQueue ( replyToQueue );
    		try {
				msg = session.createTextMessage();
			} catch (JMSException e) {
				failTest ( e.getMessage() );
			}
    		

		
	}
	
	protected void tearDown()
	{	
		session.stop();
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testSendTextMessageWithCommit()
	throws Exception
	{
		
		String text = "TEXT";
		msg.setText(text);

		utx.begin();
		session.sendMessage(msg);

		if (xares.getLastStarted() == null)
			throw new Exception("No XA enlist for send");
		if (xares.getLastEnded() == null)
			throw new Exception("No XA delist for send");

		utx.commit();

		//sleep to allow threads to work
		sleep();
		
		if (xares.getLastCommitted() == null)
			throw new Exception("No XA commit for send");

		//assert message send is OK
		TextMessage result = (TextMessage) TestQueueSender.getLastMessageSent();
		if ( result == null )
			 failTest ( "No result?" );
		if (!result.getText().equals(text))
			throw new Exception("Message content lost");
		if (result.getJMSPriority() != PRIORITY )
			throw new Exception("JMS priority lost");
		if (result.getJMSDeliveryMode() != DELIVERYMODE )
			throw new Exception("JMS deliveryMode lost");
		if (result.getJMSDestination() != queue )
			throw new Exception("JMS destination lost");
		if (result.getJMSReplyTo() != replyToQueue )
			throw new Exception("JMS replyto lost");
	}
	
	public void testSendTextMessageWithRollback()
	throws Exception
	{
		String text = "TEXT";
		msg.setText(text);
		utx.begin();
		session.sendMessage ( msg );
		
		utx.rollback();
		if ( xares.getLastCommitted() != null )
			throw new Exception ( "XA commit not allowed here" );
    	
   		if ( xares.getLastRolledback() == null )
   			throw new Exception ( "No XA rollback" );
	}
	
	public void testConnectionRefreshOnError()
	throws Exception
	{
		String text = "TEXT";
		msg.setText(text);
  		TestQueueSender.setErrorOnNextSend();
   		int connections = TestQueueConnectionFactory.getNumberOfConnectionsCreated();
   		utx.begin();
   		try {
			session.sendMessage ( msg );
			throw new Exception ( "Error expected");
   		}
   		catch ( JMSException ok ) {}
   		//send again to trigger new connection
		session.sendMessage ( msg );
   		
   		if ( TestQueueConnectionFactory.getNumberOfConnectionsCreated() != connections + 1 )
   			failTest ( "Error on send does not reopen connection?" );
   		
   		
   		utx.rollback();
	}

}
