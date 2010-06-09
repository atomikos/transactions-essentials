package com.atomikos.jms;

import java.lang.reflect.Proxy;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TopicSubscriber;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.IntraVmObjectRegistry;


public class AtomikosJmsIntegrationTestJUnit extends TransactionServiceTestCase {

	private UserTransactionServiceImp uts;
	private TSInitInfo info;
	private AtomikosConnectionFactoryBean cf;
	private TransactionManagerImp tm = null;

	public AtomikosJmsIntegrationTestJUnit(String name) {
		super(name);
	}
	
	protected void setUp() {
		super.setUp();
		
		uts =
            new UserTransactionServiceImp();
        
        info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "AtomikosJmsIntegrationTestJUnit" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir());
        properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        uts.init ( info );
        
        cf = new AtomikosConnectionFactoryBean();
        cf.setUniqueResourceName ( getName() );
        cf.setXaConnectionFactoryClassName ( "com.atomikos.jms.TestXAConnectionFactory" );
        cf.setXaProperties( new Properties() );
        tm = (TransactionManagerImp) TransactionManagerImp.getTransactionManager();
        TestXAConnectionFactory.xares.reset();
	}
	
	protected void tearDown() {
		uts.shutdown(true);
		cf.close();
		super.tearDown();
		
	}
	
	public void testInitAndClose() throws JMSException 
	{
		XATransactionalResource res = null;
		cf.close();
		assertNull ( "Resource found in Configuration before init?" , Configuration.getResource( cf.getUniqueResourceName() ) );
		try {
			IntraVmObjectRegistry.getResource ( cf.getUniqueResourceName() );
			fail ( "Resource bound in registry before init" );
		} catch ( NamingException ok ) {}
		cf.init();
		assertNotNull ( "Resource not found in Configuration after init?" , Configuration.getResource( cf.getUniqueResourceName() ) );
		try {
			IntraVmObjectRegistry.getResource ( cf.getUniqueResourceName() );
		} catch (NameNotFoundException e) {
			fail ( "Resource not found in registry after init?" );
		}
		res = (XATransactionalResource) Configuration.getResource ( cf.getUniqueResourceName() );
		assertNotNull ( "Resource not found in registry after init?" , res );
		assertFalse ( res.isClosed() );
		cf.close();
		//duplicate close should be ok
		cf.close();
		
		//test for case 26005
		assertTrue ( res.isClosed() );
		
		assertNull ( "Resource found in Configuration after close?" , Configuration.getResource( cf.getUniqueResourceName() ) );
		try {
			IntraVmObjectRegistry.getResource ( cf.getUniqueResourceName() );
			fail ( "Resource bound in registry after close" );
		} catch ( NamingException ok ) {}
		
		
	}
	
	public void testBasicSendInXaMode() throws Exception
	{
		cf.init();
		Connection c = cf.createConnection();
		assertNotNull ( c );
		assertTrue ( "wrong proxy class!" , Proxy.getInvocationHandler ( c ) instanceof AtomikosJmsConnectionProxy );
		AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler ( c );
		assertFalse (proxy.isAvailable());
		assertFalse (proxy.isErroneous());
		assertFalse(proxy.isInTransaction(null));
		Session s = c.createSession ( true , 0 );
		assertFalse ( "localTransactionMode should be disabled by default" , cf.getLocalTransactionMode() );
		assertTrue ( "session not xa-enabled whereas localTransactionMode is false and transacted flag true" , Proxy.getInvocationHandler(s) instanceof AtomikosJmsXaSessionProxy );
		MessageProducer mp = s.createProducer(null);
		assertTrue ( "wrong kind of message producer" , mp instanceof AtomikosJmsMessageProducerProxy );
		try {
			mp.send ( null );
			fail ( "send works without a JTA tx" );
		} catch ( AtomikosJMSException ok ) {}
		tm.begin();
		assertNull ( "enlist before send?" , TestXAConnectionFactory.xares.getLastStarted() );
		mp.send ( null );
		Xid xid = TestXAConnectionFactory.xares.getLastStarted();
		assertNotNull ( "no enlist done by send?" , xid );
		assertNull ( "XA end should be done on close of session!" , TestXAConnectionFactory.xares.getLastEnded() );
		s.close();
		assertEquals ( "XA end not called on session.close?" , xid , TestXAConnectionFactory.xares.getLastEnded() );
		assertNull ( "XA commit done before JTA commit?" , TestXAConnectionFactory.xares.getLastCommitted() );
		tm.commit();
		assertEquals ( "XA commit not called on JTA commit?" , xid , TestXAConnectionFactory.xares.getLastCommitted() );
		c.close();
		
	}
	
	public void testBasicReceiveInXaMode() throws Exception
	{
		cf.init();
		Connection c = cf.createConnection();
		assertNotNull ( c );
		assertTrue ( "wrong proxy class!" , Proxy.getInvocationHandler ( c ) instanceof AtomikosJmsConnectionProxy );
		AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler ( c );
		assertFalse (proxy.isAvailable());
		assertFalse (proxy.isErroneous());
		assertFalse(proxy.isInTransaction(null));
		Session s = c.createSession ( true , 0 );
		assertFalse ( "localTransactionMode should be disabled by default" , cf.getLocalTransactionMode() );
		assertTrue ( "session not xa-enabled whereas localTransactionMode is false and transacted flag true" , Proxy.getInvocationHandler(s) instanceof AtomikosJmsXaSessionProxy );
		MessageConsumer mc = s.createConsumer(null);
		assertTrue ( "wrong kind of message consumer" , mc instanceof AtomikosJmsMessageConsumerProxy );
		try {
			mc.receive();
			fail ( "receive works without a JTA tx" );
		} catch ( AtomikosJMSException ok ) {}
		tm.begin();
		assertNull ( "enlist before receive?" , TestXAConnectionFactory.xares.getLastStarted() );
		mc.receive();
		Xid xid = TestXAConnectionFactory.xares.getLastStarted();
		assertNotNull ( "no enlist done by receive?" , xid );
		assertNull ( "XA end should be done on close of session!" , TestXAConnectionFactory.xares.getLastEnded() );
		s.close();
		assertEquals ( "XA end not called on session.close?" , xid , TestXAConnectionFactory.xares.getLastEnded() );
		assertNull ( "XA commit done before JTA commit?" , TestXAConnectionFactory.xares.getLastCommitted() );
		tm.commit();
		assertEquals ( "XA commit not called on JTA commit?" , xid , TestXAConnectionFactory.xares.getLastCommitted() );
		c.close();
		
	}
	
	public void testBasicSubscribeInXaMode() throws Exception
	{
		cf.init();
		Connection c = cf.createConnection();
		assertNotNull ( c );
		assertTrue ( "wrong proxy class!" , Proxy.getInvocationHandler ( c ) instanceof AtomikosJmsConnectionProxy );
		AtomikosJmsConnectionProxy proxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler ( c );
		assertFalse (proxy.isAvailable());
		assertFalse (proxy.isErroneous());
		assertFalse(proxy.isInTransaction(null));
		Session s = c.createSession ( true , 0 );
		assertFalse ( "localTransactionMode should be disabled by default" , cf.getLocalTransactionMode() );
		assertTrue ( "session not xa-enabled whereas localTransactionMode is false and transacted flag true" , Proxy.getInvocationHandler(s) instanceof AtomikosJmsXaSessionProxy );
		TopicSubscriber mc = s.createDurableSubscriber( null , "name" );
		assertTrue ( "wrong kind of subscriber" , mc instanceof AtomikosJmsTopicSubscriberProxy );
		try {
			mc.receive();
			fail ( "receive works without a JTA tx" );
		} catch ( AtomikosJMSException ok ) {}
		tm.begin();
		assertNull ( "enlist before receive?" , TestXAConnectionFactory.xares.getLastStarted() );
		mc.receive();
		Xid xid = TestXAConnectionFactory.xares.getLastStarted();
		assertNotNull ( "no enlist done by receive?" , xid );
		assertNull ( "XA end should be done on close of session!" , TestXAConnectionFactory.xares.getLastEnded() );
		s.close();
		assertEquals ( "XA end not called on session.close?" , xid , TestXAConnectionFactory.xares.getLastEnded() );
		assertNull ( "XA commit done before JTA commit?" , TestXAConnectionFactory.xares.getLastCommitted() );
		tm.commit();
		assertEquals ( "XA commit not called on JTA commit?" , xid , TestXAConnectionFactory.xares.getLastCommitted() );
		c.close();
		
	}
	
	public void testNoEnlistDoneWithActivityContext() throws Exception 
	{
	
		cf.init();
		Connection c = cf.createConnection();
		CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
		assertNull ( "existing transation before test" , ctm.getCompositeTransaction() );
		CompositeTransaction ct = ctm.createCompositeTransaction(1000);
		
		Session s = c.createSession ( true , 0 );
		assertFalse ( "localTransactionMode should be disabled by default" , cf.getLocalTransactionMode() );
		assertTrue ( "session not xa-enabled whereas localTransactionMode is false and transacted flag true" , Proxy.getInvocationHandler(s) instanceof AtomikosJmsXaSessionProxy );
		MessageConsumer mc = s.createConsumer(null);
		assertTrue ( "wrong kind of message consumer" , mc instanceof AtomikosJmsMessageConsumerProxy );
		assertNull ( "enlist before receive?" , TestXAConnectionFactory.xares.getLastStarted() );
		try {
			mc.receive();
			fail ( "receive works without a JTA tx" );
		} catch ( AtomikosJMSException ok ) {}
		Xid xid = TestXAConnectionFactory.xares.getLastStarted();
		assertNull ( "enlist done by receive for NON-JTA transaction" , xid );
		s.close();
		ct.rollback();
		c.close();
	}
	
	public void testXaModeWithCommitAfterClose() throws Exception
	{
		cf.init();
		Connection c = cf.createConnection();
		Session s = c.createSession ( true , 0 );
		MessageConsumer mc = s.createConsumer(null);
		tm.begin();
		mc.receive();
		//close session -> assert not available for pool!
		s.close();
		AtomikosJmsXaSessionProxy sproxy = (AtomikosJmsXaSessionProxy ) Proxy.getInvocationHandler( s);
		//tx still active -> proxy should not be available, even though session is closed!
		assertFalse ( "session proxy available before tx commit" , sproxy.isAvailable() );
		AtomikosJmsConnectionProxy cproxy = (AtomikosJmsConnectionProxy) Proxy.getInvocationHandler(c);
		assertFalse ( "connection proxy available before tx commit" , cproxy.isAvailable() );
		//close connection -> assert not available for pool!
		c.close();
		assertFalse ( "session proxy available before tx commit" , sproxy.isAvailable() );
		assertFalse ( "connection proxy available before tx commit" , cproxy.isAvailable() );
		//tx commit should release the connection and session to the pool!
		tm.commit();
		assertTrue ( "session proxy NOT available after tx commit" , sproxy.isAvailable() );
		assertTrue ( "connection proxy NOT available after tx commit" , cproxy.isAvailable() );
	}
	
	public void testBasicConnectionReuse() throws Exception 
	{
		int created = TestXAConnectionFactory.getCreateCount();
		cf.init();
		assertEquals ( 1, cf.getMinPoolSize() );
		assertTrue ( "no extra connection created by pool init?" , TestXAConnectionFactory.getCreateCount() > created );
		created = TestXAConnectionFactory.getCreateCount();
		Connection c = cf.createConnection();
		assertEquals ( "pool does return initial connection?"  , created ,TestXAConnectionFactory.getCreateCount() );
		c.close();
		c = cf.createConnection();
		assertEquals ( "pool does not reuse previous connection?"  , created ,TestXAConnectionFactory.getCreateCount() );
		c.close();
	}
	
	public void testCreateSessionInNonTransactedMode() throws Exception 
	{
		cf.init();
		Connection c = cf.createConnection();
		Session s = c.createSession ( false , 0 );
		assertTrue ( "session xa-enabled whereas transacted flag false" , Proxy.getInvocationHandler(s) instanceof AtomikosJmsNonXaSessionProxy );
		MessageConsumer mc = s.createConsumer(null);
		mc.receive();
		assertNull ( "enlist done in non-tx mode?" , TestXAConnectionFactory.xares.getLastStarted() );
		s.close();
		c.close();
		
	}

	public void testCreateSessionInLocalTransactedMode() throws Exception 
	{
		cf.setLocalTransactionMode(true);
		cf.init();
		Connection c = cf.createConnection();
		Session s = c.createSession ( true , 0 );
		assertTrue ( "session xa-enabled whereas localTransationMode is enabled" , Proxy.getInvocationHandler(s) instanceof AtomikosJmsNonXaSessionProxy );
		MessageConsumer mc = s.createConsumer(null);
		mc.receive();
		assertNull ( "enlist done in non-tx mode?" , TestXAConnectionFactory.xares.getLastStarted() );
		s.close();
		c.close();
		
	}
	
	public void testXaModeSendWithoutRequiredJtaContext() throws Exception
	{
		cf.init();
		Connection c = cf.createConnection();
		Session s = c.createSession ( true , 0 );
		MessageProducer mp = s.createProducer ( null );
		try {
			mp.send ( null );
			fail ( "No error when sending without JTA context?" );
		} catch ( AtomikosJMSException ok ) {
			String expectedMsg = "The JMS session you are using requires a JTA transaction context for the calling thread and none was found.\n" +
				"Please correct your code to do one of the following: \n" + 
				"1. start a JTA transaction if you want your JMS operations to be subject to JTA commit/rollback, or\n" +
				"2. increase the maxPoolSize of the AtomikosConnectionFactoryBean to avoid transaction timeout while waiting for a connection, or\n" +
				"3. create a non-transacted session and do session acknowledgment yourself, or\n" +
				"4. set localTransactionMode to true so connection-level commit/rollback are enabled."; 
			
			assertEquals ( expectedMsg , ok.getMessage() );
		}
		s.close();
		c.close();
	}
	
	public void testInitWithWrongXaConnectionFactoryClassName() throws JMSException {
		final String name = "com.example.NonExistentClass";
		cf.setXaConnectionFactoryClassName ( name );
		try {
			cf.init();
			fail ( "init works with a nonexistent XAConnectionFactory class?" );
		} catch ( AtomikosJMSException ok ) {
			String expectedMsg = "The class '" + name +
					"' specified by property 'xaConnectionFactoryClassName' of class AtomikosConnectionFactoryBean could not be found in the classpath. " +
					"Please make sure the spelling in your setup is correct, and that the required jar(s) are in the classpath.";
			assertEquals ( "wrong exception message" , expectedMsg , ok.getMessage() );
		}
	}

	public void testInitWithInvalidXaConnectionFactoryClassName() throws JMSException {
		final String name = "java.lang.String";
		cf.setXaConnectionFactoryClassName ( name );
		try {
			cf.init();
			fail ( "init works with an invalid XAConnectionFactory class?" );
		} catch ( AtomikosJMSException ok ) {
			String expectedMsg = "The class '" + name +
					"' specified by property 'xaConnectionFactoryClassName' of class AtomikosConnectionFactoryBean does not implement the required interface javax.jms.XAConnectionFactory. " +
					"Please make sure the spelling in your setup is correct, and check your JMS driver vendor's documentation.";
			assertEquals ( "wrong exception message" , expectedMsg , ok.getMessage() );
		}
	}
	
	public void testInitWithoutRequiredUniqueResourceName() throws JMSException {
		cf.setUniqueResourceName ( null );
		try {
			cf.init();
			fail ( "Init works without unique resource name" );
		} catch ( AtomikosJMSException ok ) {
			String expectedMsg = "Property 'uniqueResourceName' of class AtomikosConnectionFactoryBean cannot be null.";
			ok.printStackTrace();
			assertEquals ( "wrong exception message" , expectedMsg , ok.getMessage() );
		}
	}
	
	public void testInitWithoutRequiredXaConnectionFactoryClassName() throws JMSException 
	{
		cf.setXaConnectionFactoryClassName ( null );
		try {
			cf.init();
			fail ( "Init works without xa connection factory class name" );
		} catch ( AtomikosJMSException ok ) {
			String expectedMsg = "Property 'xaConnectionFactoryClassName' of class AtomikosConnectionFactoryBean cannot be null.";
			ok.printStackTrace();
			assertEquals ( "wrong exception message" , expectedMsg , ok.getMessage() );
		}
	}
	
	public void testInitWithoutRequiredXaProperties() throws Exception 
	{
		cf.setXaProperties ( null );
		try {
			cf.init();
			fail ( "Init works without xa properties" );
		} catch ( AtomikosJMSException ok ) {
			String expectedMsg = "Property 'xaProperties' of class AtomikosConnectionFactoryBean cannot be null.";
			ok.printStackTrace();
			assertEquals ( "wrong exception message" , expectedMsg , ok.getMessage() );
		}
	}
	
	public void testUsingClosedConnectionNotAllowed() throws JMSException 
	{
		cf.init();
		Connection c = cf.createConnection();
		c.close();
		try {
			c.createSession ( true , 0 );
			fail ( "Connection can be used after close?" );
		} catch ( javax.jms.IllegalStateException ok ) {
			String expectedMsg = "Connection is closed already - calling method createSession no longer allowed.";
			assertEquals ( expectedMsg , ok.getMessage() );
		}
	}
	
	public void testUsingClosedXaSessionNotAllowed() throws Exception
	{
		cf.init();
		Connection c = cf.createConnection();
		Session s = c.createSession( true , 0 );
		s.close();
		try {
			s.createBytesMessage();
			fail ( "Using a session after close is allowed?" );
		} catch ( javax.jms.IllegalStateException ok ) {
			String expectedMsg = "Session was closed already - calling createBytesMessage is no longer allowed.";
			assertEquals ( expectedMsg , ok.getMessage() );
		}
		c.close();
		
	}
	
	public void testUsingClosedNonXaSessionNotAllowed() throws Exception
	{
		cf.init();
		Connection c = cf.createConnection();
		Session s = c.createSession( false , 0 );
		s.close();
		try {
			s.createBytesMessage();
			fail ( "Using a session after close is allowed?" );
		} catch ( javax.jms.IllegalStateException ok ) {
			String expectedMsg = "Session was closed already - calling createBytesMessage is no longer allowed.";
			assertEquals ( expectedMsg , ok.getMessage() );
		}
		c.close();
		
	}
	
	public void testMeaningfulExceptionMessageIfPoolExhausted() throws Exception
	{
		cf.setMaxPoolSize ( 1 );
		cf.setBorrowConnectionTimeout( 1 );
		cf.setMaintenanceInterval( cf.getBorrowConnectionTimeout() * 10 );
		cf.init();
		Connection c = cf.createConnection();
		//getting second connection should fail
		try {
			cf.createConnection();
			fail ( "gotten connection when pool should be exhausted?" );
		} catch ( AtomikosJMSException ok ) {
			String expectedMsg = "Connection pool exhausted - try increasing 'maxPoolSize' and/or 'borrowConnectionTimeout' on the AtomikosConnectionFactoryBean.";
			assertEquals ( expectedMsg , ok.getMessage() );
		}
	}
	
	public void testInitAfterGetReferenceWorks() throws Exception 
	{
		//see case 28388
		cf.getReference();
		cf.init();
	}
	
}
