package com.atomikos.jms.extra;

import java.util.Properties;

import javax.jms.JMSException;
import javax.transaction.SystemException;

import junit.framework.TestCase;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.jms.AtomikosConnectionFactoryBean;
import com.atomikos.jms.TestMessageListener;
import com.atomikos.jms.TestTopic;
import com.atomikos.jms.TestTopicConnectionFactory;
import com.atomikos.jms.TestTopicSession;
import com.atomikos.jms.TestTopicSubscriber;

public class MessageConsumerSessionSimpleTestJUnit extends TransactionServiceTestCase 
{
	private static final String USER = "User";
	private static final String PW = "SECRET";
	private static final int TIMEOUT = 10;
	private static final String SELECTOR = "SELECTOR";
	private static boolean unsubscribeOnClose = false;
	
	private UserTransactionServiceImp uts;
	private TSInitInfo info;

	private MessageConsumerSession session;
	
	public MessageConsumerSessionSimpleTestJUnit ( String name ) 
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
		TestTopicSession.reset();
		
		session = new MessageConsumerSession (
				new MessageConsumerSessionProperties () {

					public int getTransactionTimeout() {
						
						return 2;
					}
					
					public boolean getUnsubscribeOnClose() {
						return unsubscribeOnClose;
					}
				}
		);
	
	}
	
	protected void tearDown()
	{
		session.stopListening();
		unsubscribeOnClose = false;

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
		assertEquals ( 2 , session.getTransactionTimeout() );
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
		session.setDestination ( topic );
		assertEquals ( topic, session.getDestination() );
	}
	
	public void testDestinationName()
	{
		session.setDestination ( null );
		String dName = "myDestinationName";
		session.setDestinationName ( dName );
		assertEquals ( dName , session.getDestinationName() );
	}
	
	public void testAtomikosConnectionFactoryBean()
	{
		AtomikosConnectionFactoryBean qcfb = 
				new AtomikosConnectionFactoryBean();
		session.setAtomikosConnectionFactoryBean ( qcfb );
		assertEquals ( session.getAtomikosConnectionFactoryBean() , qcfb );
	}
	
	public void testListeningWithNotifyOnClose()
	throws Exception
	{
		
		testListening ( null , false , true );
	}
	
	public void testListeningWithoutNotifyOnClose()
	throws Exception
	{
		testListening ( null, false , false );
	}
	
	public void testListeningDurableSubscriberWithUnsubscribeOnCloseAndWithoutNotifyOnClose()
	throws Exception
	{
		final String subscriberName = "name";
		final boolean unsubscribeOnClose = true;
		testListening ( subscriberName , unsubscribeOnClose , false );
	}
	
	public void testListeningDurableSubscriberWithoutUnsubscribeOnCloseAndWithoutNotifyOnClose()
	throws Exception
	{
		final String subscriberName = "name";
		final boolean unsubscribeOnClose = false;
		testListening ( subscriberName , unsubscribeOnClose , false );
	}
	
	public void testListeningDurableSubscriberWithoutUnsubscribeOnCloseAndWithNotifyOnClose()
	throws Exception
	{
		final String subscriberName = "name";
		final boolean unsubscribeOnClose = false;
		testListening ( subscriberName , unsubscribeOnClose , true );
	}
	
	public void testListeningDurableSubscriberWithUnsubscribeOnCloseAndWithNotifyOnClose()
	throws Exception
	{
		final String subscriberName = "name";
		final boolean unsubscribeOnClose = true;
		testListening ( subscriberName , unsubscribeOnClose , true );
	}
	
	private void testListening ( String subscriberName , boolean unsubscribeOnClose , boolean notifyListenerOnClose ) throws JMSException, InterruptedException, SystemException
	{
		session.setSubscriberName ( subscriberName );
		MessageConsumerSessionSimpleTestJUnit.unsubscribeOnClose = unsubscribeOnClose;

		try {
			session.startListening();
			fail ( "startListening works without topic");
		}
		catch ( JMSException ok ) {}
		
		TestTopic topic = new TestTopic();
		session.setDestination ( topic );
		
		try {
			  session.startListening();
			  fail ( "startListening works without factory");
		 }
		 catch ( JMSException ok ) {}	
		 
		 //session.setTransactionTimeout ( 2 );
		 TestXAResource xares = new TestXAResource();
		 TestTopicConnectionFactory fact = new TestTopicConnectionFactory ( xares );         
		 AtomikosConnectionFactoryBean qcfb = new AtomikosConnectionFactoryBean();
	     qcfb.setXaConnectionFactory ( fact );
	     qcfb.setUniqueResourceName ( "TESTTOPICRESOURCE" );
		 session.setAtomikosConnectionFactoryBean ( qcfb );
		 TestMessageListener l = new TestMessageListener();
		 session.setMessageListener ( l );
		 session.setNotifyListenerOnClose ( notifyListenerOnClose );
		 session.startListening();
		 //sleep to allow threads to start
          Thread.sleep ( session.getTransactionTimeout() * 1000 );
		 session.stopListening();
		 Thread.sleep ( 2 * session.getTransactionTimeout() * 1000 );
		 assertEquals ( l.wasClosed() , notifyListenerOnClose );
		 assertEquals ( unsubscribeOnClose , TestTopicSession.wasUnsubscribeCalled() );
		 qcfb.close();
		 
	}
}
