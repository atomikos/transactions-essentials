package com.atomikos.jms.extra;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.transaction.xa.Xid;


import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.TestQueue;
import com.atomikos.jms.TestQueueConnectionFactory;
import com.atomikos.jms.TestQueueSender;
import com.atomikos.jms.TestXAConnectionFactory;


public class ConcurrentJmsSenderTemplateIntegrationTestJUnit extends TransactionServiceTestCase 
{
	private UserTransactionServiceImp uts;
	private TSInitInfo info;
	private AtomikosConnectionFactoryBean cf;
	private TransactionManagerImp tm = null;
	private TestXAResource xares;

	public ConcurrentJmsSenderTemplateIntegrationTestJUnit(String name) {
		super(name);
	}

	private ConcurrentJmsSenderTemplate template;
	
	protected void setUp() 
	{
		
		uts =
            new UserTransactionServiceImp();
        
        info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "ConcurrentJmsTemplateTestJUnit" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir());
        properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        uts.init ( info );
        
		xares = new TestXAResource();
		TestQueueConnectionFactory fact = new TestQueueConnectionFactory(xares);
		
		cf = new AtomikosConnectionFactoryBean();
		cf.setXaConnectionFactory(fact);
		cf.setUniqueResourceName("TESTQUEUERESOURCE");
		cf.setMaxPoolSize(3);
        tm = (TransactionManagerImp) TransactionManagerImp.getTransactionManager();
        TestXAConnectionFactory.xares.reset();

		template = new ConcurrentJmsSenderTemplate();
		template.setAtomikosConnectionFactoryBean( cf );
		template.setDeliveryMode ( DeliveryMode.PERSISTENT );
		template.setDestination ( new TestQueue() );
		
	}

	protected void tearDown() 
	{
		uts.shutdown(true);
		cf.close();
		super.tearDown();
	}

	public void testSendWithoutTransactionThrowsMeaningfulException() throws Exception 
	{
		template.init();
		
		try {
			template.sendTextMessage ( "Message without tx" );
			fail ( "No exception on send without tx" );
		} catch ( JMSException ok ) {
			assertEquals ( "This method requires an active transaction!" , ok.getMessage() );
		}
	}
	
	
	public void testSendTextMessage() throws Exception
	{
		template.init();
		tm.begin();
		String content = "Test";
		template.sendTextMessage ( content );
		Xid xid = xares.getLastStarted();
		assertNotNull ( xid );
		tm.rollback();
		Xid xid2 = xares.getLastEnded();
		assertNotNull ( xid2 );
		assertSame ( xid , xid2 );
		xid2 = xares.getLastRolledback();
		assertSame ( xid , xid2 );
	}
	
	public void testSendMapMessage() throws Exception
	{
		template.init();
		tm.begin();
		Map content = new HashMap();
		content.put( "key", "value" );
		template.sendMapMessage ( content );
		Xid xid = xares.getLastStarted();
		assertNotNull ( xid );
		tm.rollback();
		Xid xid2 = xares.getLastEnded();
		assertNotNull ( xid2 );
		assertSame ( xid , xid2 );
		xid2 = xares.getLastRolledback();
		assertSame ( xid , xid2 );
	}
	
	public void testSendObjectMessage() throws Exception
	{
		template.init();
		tm.begin();
		Serializable content = new String ( "test" );
		template.sendObjectMessage ( content );
		Xid xid = xares.getLastStarted();
		assertNotNull ( xid );
		tm.rollback();
		Xid xid2 = xares.getLastEnded();
		assertNotNull ( xid2 );
		assertSame ( xid , xid2 );
		xid2 = xares.getLastRolledback();
		assertSame ( xid , xid2 );
	}

	public void testSendWithDestinationNameLookup() throws Exception 
	{
		template.setDestination ( null );
		template.setDestinationName ( "myQueueName" );
		template.init();
		tm.begin();
		Serializable content = new String ( "test" );
		template.sendObjectMessage ( content );
		Xid xid = xares.getLastStarted();
		assertNotNull ( xid );
		tm.rollback();
		Xid xid2 = xares.getLastEnded();
		assertNotNull ( xid2 );
		assertSame ( xid , xid2 );
		xid2 = xares.getLastRolledback();
		assertSame ( xid , xid2 );
	}
	
// DISABLED: BYTES MESSAGE NOT SUPPORTED BY MOCK SESSION	
//	public void testSendBytesMessage() throws Exception 
//	{
//		tm.begin();
//		byte[] content = new byte[5];
//		template.sendBytesMessage ( content );
//		Xid xid = xares.getLastStarted();
//		assertNotNull ( xid );
//		tm.rollback();
//		Xid xid2 = xares.getLastEnded();
//		assertNotNull ( xid2 );
//		assertSame ( xid , xid2 );
//		xid2 = xares.getLastRolledback();
//		assertSame ( xid , xid2 );
//	}
	
	public void testConnectionRefreshOnError()
	throws Exception
	{
		String text = "TEXT";  		
		TestQueueSender.setErrorOnNextSend();
   		int connections = TestQueueConnectionFactory.getNumberOfConnectionsCreated();
   		tm.begin();
   		try {
			template.sendTextMessage ( text );
			throw new Exception ( "Error expected");
   		}
   		catch ( JMSException ok ) {}
   		//send again to trigger new connection
		template.sendTextMessage ( text );
   		
   		assertTrue (  "Errors do not refresh connnection?" , TestQueueConnectionFactory.getNumberOfConnectionsCreated() > connections );
   		
   		
   		tm.rollback();
	}
	
	public void testNoConnectionRefreshOnNoErrorWithinSameTransaction()
	throws Exception
	{
		String text = "TEXT";  		
   		tm.begin();
   		
		template.sendTextMessage ( text );
		
		int connections = TestQueueConnectionFactory.getNumberOfConnectionsCreated();
   		
		template.sendTextMessage ( text );
   		
   		assertEquals (   "Connection recycling does not work?" , connections , TestQueueConnectionFactory.getNumberOfConnectionsCreated() );
   		
   		
   		tm.rollback();
	}
	
	public void testNoConnectionRefreshOnNoErrorAcrossTransactions()
	throws Exception
	{
		String text = "TEXT";  		
   		tm.begin();
   		
		template.sendTextMessage ( text );
		tm.rollback();
		tm.begin();
		
		int connections = TestQueueConnectionFactory.getNumberOfConnectionsCreated();
   		
		template.sendTextMessage ( text );
   		
   		assertEquals (   "Normal send refreshes connnection?" , connections , TestQueueConnectionFactory.getNumberOfConnectionsCreated() );
   		
   		
   		tm.rollback();
	}
	
	public void testSendTextMessageWithCommit()
	throws Exception
	{
		
		String text = "TEXT";
		tm.begin();
		template.sendTextMessage(text);

		if (xares.getLastStarted() == null)
			throw new Exception("No XA enlist for send");


		tm.commit();
		if (xares.getLastEnded() == null)
			throw new Exception("No XA delist for send");

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
		if (result.getJMSPriority() != template.getPriority() )
			throw new Exception("JMS priority lost");
		if (result.getJMSDeliveryMode() != template.getDeliveryMode() )
			throw new Exception("JMS deliveryMode lost");
		if (result.getJMSDestination() != template.getDestination() )
			throw new Exception("JMS destination lost");
		if (result.getJMSReplyTo() != template.getReplyToDestination() )
			throw new Exception("JMS replyto lost");
	}
}
