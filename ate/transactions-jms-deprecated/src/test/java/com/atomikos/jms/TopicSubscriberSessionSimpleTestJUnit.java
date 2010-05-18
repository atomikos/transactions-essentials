package com.atomikos.jms;

import java.util.Properties;

import javax.jms.JMSException;
import javax.transaction.SystemException;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

import junit.framework.TestCase;

public class TopicSubscriberSessionSimpleTestJUnit extends TransactionServiceTestCase 
{
	private static final String USER = "User";
	private static final String PW = "SECRET";
	private static final int TIMEOUT = 10;
	private static final String SELECTOR = "SELECTOR";

	private TopicSubscriberSession session;
	
	private UserTransactionServiceImp uts;
	private TSInitInfo info;

	
	public TopicSubscriberSessionSimpleTestJUnit ( String name ) 
	{
		super ( name );
	}
	
	protected void setUp()
	{
		uts =
            new UserTransactionServiceImp();
        
        info = uts.createTSInitInfo();
        Properties properties = info.getProperties();    
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , getClass().getName() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath());
        properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        uts.init ( info );
        
		session = new TopicSubscriberSession();
	
	}
	
	protected void tearDown()
	{
		session.stopListening();
		
		uts.shutdownForce();
		super.tearDown();
	}
	
	public void testUser()
	{
		session.setUser ( USER );
		assertEquals ( USER , session.getUser() );
	}
	
	public void testPassword()
	{
		session.setPassword ( PW );
	}
	
	public void testTimeout()
	{
		session.setTransactionTimeout ( TIMEOUT );
		assertEquals ( TIMEOUT , session.getTransactionTimeout() );
	}
	
	public void testMessageListener()
	{
		TestMessageListener l = new TestMessageListener();
		session.setMessageListener ( l );
		assertEquals ( l , session.getMessageListener() );
		
		session.setMessageListener ( null );
		assertNull ( session.getMessageListener() );
	}
	
	public void testMessageSelector()
	{
		session.setMessageSelector ( SELECTOR );
		assertEquals ( session.getMessageSelector() , SELECTOR );
	}
	
	public void testSubscriberName()
	{
		assertNull ( session.getSubscriberName() );
		String name = "name";
		session.setSubscriberName(name);
		assertEquals ( name , session.getSubscriberName() );
	}
	
	public void testNoLocal()
	{
		assertFalse ( session.getNoLocal() );
		session.setNoLocal(true);
		assertTrue (session.getNoLocal());
	}
	
	public void testDaemonThreads()
	{
		assertFalse ( session.getDaemonThreads() );
		session.setDaemonThreads ( true );
		assertTrue ( session.getDaemonThreads() );
		session.setDaemonThreads ( false );
		assertFalse ( session.getDaemonThreads() );
	}
	
	public void testNotifyListenerOnClose()
	{
		session.setNotifyListenerOnClose ( true );
		assertTrue ( session.getNotifyListenerOnClose() );
		
	}
	
	public void testDontNotifyListenerOnClose()
	{
		
		session.setNotifyListenerOnClose ( false );
		assertFalse ( session.getNotifyListenerOnClose() );
	}
	
	public void testTopic()
	{
		TestTopic topic = new TestTopic();
		session.setTopic ( topic );
		assertEquals ( topic, session.getTopic() );
	}
	
	public void testTopicConnectionFactoryBean()
	{
		TopicConnectionFactoryBean qcfb = 
				new TopicConnectionFactoryBean();
		session.setTopicConnectionFactoryBean ( qcfb );
		assertEquals ( session.getTopicConnectionFactoryBean() , qcfb );
	}
	
	public void testListeningWithNotifyOnClose()
	throws Exception
	{
		testListening ( true );
	}
	
	public void testListeningWithoutNotifyOnClose()
	throws Exception
	{
		testListening ( false );
	}
	
	private void testListening ( boolean notifyListenerOnClose ) throws JMSException, InterruptedException, SystemException
	{

		try {
			session.startListening();
			fail ( "startListening works without topic");
		}
		catch ( JMSException ok ) {}
		
		TestTopic topic = new TestTopic();
		session.setTopic ( topic );
		
		try {
			  session.startListening();
			  fail ( "startListening works without factory");
		 }
		 catch ( JMSException ok ) {}	
		 
		 session.setTransactionTimeout ( 2 );
		 TestXAResource xares = new TestXAResource();
		 TestTopicConnectionFactory fact = new TestTopicConnectionFactory ( xares );         
		 TopicConnectionFactoryBean qcfb = new TopicConnectionFactoryBean();
	     qcfb.setXaTopicConnectionFactory ( fact );
	     qcfb.setResourceName ( "TESTTOPICRESOURCE" );
		 session.setTopicConnectionFactoryBean ( qcfb );
		 TestMessageListener l = new TestMessageListener();
		 session.setMessageListener ( l );
		 session.setNotifyListenerOnClose ( notifyListenerOnClose );
		 session.startListening();
		 //sleep to allow threads to start
          Thread.sleep ( session.getTransactionTimeout() * 1000 );
		 session.stopListening();
		 Thread.sleep ( 2 * session.getTransactionTimeout() * 1000 );
		 assertEquals ( l.wasClosed() , notifyListenerOnClose );
		 
	}
}
