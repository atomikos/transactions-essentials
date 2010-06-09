package com.atomikos.jms;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.jms.JmsTransactionalResource;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.jta.UserTransactionImp;

 /**
  *
  *
  *A release tester for the JMS adaptor framework.
  */


public class ReleaseTester
{
	
	private static void fail ( String msg )
	throws Exception
	{
		throw new Exception ( msg );
	}
  
    /**
     *Test sending of messages for the JTA-JMS classes.
     */
     
    public static void testSend ( UserTransactionService uts , 
        QueueSession session , TestXAResource xares )
    throws Exception
    {
        TransactionManager tm =
            uts.getTransactionManager();
        Transaction tx = null;
        HeuristicQueueSender sender = null;
        sender = ( HeuristicQueueSender ) session.createSender ( null );
        StringHeuristicMessage hmsg =
            new StringHeuristicMessage ( "TestMessage" );

        
        //
        //CASE SEND-1: test a simple send without tx
        //
        
        xares.reset();
        try {
            sender.send ( null , hmsg );
            throw new Exception ( "ERROR: send without tx works?" );
        }
        catch ( JMSException e ) {
            //should happen: no tx started!
        }
        
        //
        //CASE SEND-2: test simple send with tx, and commit
        //
        
        xares.reset();
        tm.begin();
        //send null msg, merely to test enlist /delist
        sender.send ( null , hmsg );
        
        if ( xares.getLastStarted() == null )
            throw new Exception ( "Send does not cause enlist??" );
        if ( xares.getLastEnded() == null )
            throw new Exception ( "Send does not end with delist??" );
        if ( ! xares.getLastStarted().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Send does enlist/delist with # Xid??" );
        
        //now, commit
        tm.commit();
        
        //assert that commit is done in xa 
        if ( xares.getLastCommitted() == null )
            throw new Exception ( "Send: tx commit not propagated to xa?" );
        if ( !xares.getLastCommitted().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Send: tx commit with # Xid?" );
            
        //
        //CASE SEND-3: test simple send with tx, and rollback
        //
        
        xares.reset();
        tm.begin();
        //send null msg, merely to test enlist /delist
        sender.send ( null , hmsg );
        
        if ( xares.getLastStarted() == null )
            throw new Exception ( "Send does not cause enlist??" );
        if ( xares.getLastEnded() == null )
            throw new Exception ( "Send does not end with delist??" );
        if ( ! xares.getLastStarted().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Send does enlist/delist with # Xid??" );
        
        //now, rollback
        tm.rollback();
        
        //assert that rollback is done in xa 
        if ( xares.getLastRolledback() == null )
            throw new Exception ( "Send: tx rollback not propagated to xa?" );
        if ( !xares.getLastRolledback().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Send: tx rollback with # Xid?" );
        
        //
        //CASE SEND-4: test simple send with tx, and setRollbackOnly
        //
        
        xares.reset();
        tm.begin();
        tx = tm.getTransaction();
        //send null msg, merely to test enlist /delist
        sender.send ( null , hmsg );
        
        if ( xares.getLastStarted() == null )
            throw new Exception ( "Send does not cause enlist??" );
        if ( xares.getLastEnded() == null )
            throw new Exception ( "Send does not end with delist??" );
        if ( ! xares.getLastStarted().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Send does enlist/delist with # Xid??" );
        
        //now, rollback
        tx.setRollbackOnly();
        
        //try to commit
        try {
            tx.commit();
            throw new Exception ( "Commit works after setRollbackOnly?" );
        }
        catch ( javax.transaction.RollbackException rb ) {
            //should happen due to setRollbackOnly
        }
        
        //assert that rollback is done in xa 
        if ( xares.getLastRolledback() == null )
            throw new Exception ( "Send: tx rollback not propagated to xa?" );
        if ( !xares.getLastRolledback().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Send: tx rollback with # Xid?" );
        
    }
    
    public static void testReceive ( UserTransactionService uts ,
        QueueSession session , TestXAResource xares )
    throws Exception
    {
        TransactionManager tm =
            uts.getTransactionManager();
        Transaction tx = null;
        HeuristicQueueReceiver receiver = null;
        receiver = ( HeuristicQueueReceiver ) session.createReceiver ( null );
        StringHeuristicMessage hmsg =
            new StringHeuristicMessage ( "TestMessage" );
        Message msg = null;
        
        //
        //CASE REC-1: simple receive without a tx.
        //
        xares.reset();
        try {
            msg = receiver.receive ( "" );
            throw new Exception ( "Error: receive works without tx?" );
        }
        catch ( JMSException e ) {
            //should happen: no tx yet!
        }
        
        //
        //CASE REC-2: simple receive with tx commit
        //
        xares.reset();
        tm.begin();
        msg = receiver.receive ( "" );
         if ( xares.getLastStarted() == null )
            throw new Exception ( "Receive does not cause enlist??" );
        if ( xares.getLastEnded() == null )
            throw new Exception ( "Receive does not end with delist??" );
        if ( ! xares.getLastStarted().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Receive does enlist/delist with # Xid??" );
        
        //now, commit
        tm.commit();
        
        //assert that commit is done in xa 
        if ( xares.getLastCommitted() == null )
            throw new Exception ( "Receive: tx commit not propagated to xa?" );
        if ( !xares.getLastCommitted().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Receive: tx commit with # Xid?" );
            
        //
        //CASE REC-3: simple receive with tx rollback
        //
        xares.reset();
        tm.begin();
        msg = receiver.receive ( "" );
         if ( xares.getLastStarted() == null )
            throw new Exception ( "Receive does not cause enlist??" );
        if ( xares.getLastEnded() == null )
            throw new Exception ( "Receive does not end with delist??" );
        if ( ! xares.getLastStarted().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Receive does enlist/delist with # Xid??" );
        
        //now, rollback
        tm.rollback();
        
        //assert that rb is done in xa 
        if ( xares.getLastRolledback() == null )
            throw new Exception ( "Receive: tx rollback not propagated to xa?" );
        if ( !xares.getLastRolledback().equals ( xares.getLastEnded() ) )
            throw new Exception ( "Receive: tx rollback with # Xid?" );
      
        
    }


    
    /**
     * Test the basic queue receiver session (MDB) functionality.
     * @param qcfb
     * @throws Exception
     */
	public static void testQueueReceiverSessionBasics ( QueueConnectionFactoryBean qcfb )
	throws Exception
	{
		
		QueueReceiverSession session = new QueueReceiverSession();
		TestMessageListener listener = new TestMessageListener();
		session.setMessageListener ( listener );
		if ( session.getMessageListener() !=  listener ) 
			throw new Exception ( "set/getMessageListener not ok");
		String user = "USER";
		session.setUser ( user );
		if ( !session.getUser().equals ( user ) ) 
			throw new Exception ( "set/getUser not ok");
		
		//assert that start fails since no queue yet
		try {
			session.startListening();
			throw new Exception ( "startListening works without queue");
		}
		catch ( JMSException ok ) {}
		
		Queue queue = new TestQueue();
		session.setQueue ( queue );
		if ( !session.getQueue().getQueueName().equals ( queue.getQueueName() ))
			throw new Exception ( "set/getQueue not ok");
			
			
		//assert that start fails since no queuefactory yet
			  try {
				  session.startListening();
				  throw new Exception ( "startListening works without factory");
			  }
			  catch ( JMSException ok ) {}			
						
		session.setQueueConnectionFactoryBean ( qcfb );
		if ( session.getQueueConnectionFactoryBean() != qcfb )
			throw new Exception ( "get/setQueueConnectionFactoryBean not ok");
		session.setTransactionTimeout ( 50 );
		if ( session.getTransactionTimeout() != 50 ) 
			throw new Exception ( "get/setTransactionTimeout not ok");
			
		session.setNotifyListenerOnClose ( true );
		if ( !session.getNotifyListenerOnClose() ) 
			fail ( "get/setNotifyListenerOnClose not ok");
		
		session.setNotifyListenerOnClose ( false );
		if ( session.getNotifyListenerOnClose() ) 
			fail ( "get/setNotifyListenerOnClose not ok");
		
		session.stopListening();
		
	}
	
	public static void testNotifyListenerOnClose ( QueueConnectionFactoryBean qcfb , boolean notify )
	throws Exception
	{
		QueueReceiverSession session = new QueueReceiverSession();
		session.setTransactionTimeout ( 2 );
		TestMessageListener listener = new TestMessageListener();
		session.setMessageListener ( listener );
		Queue queue = new TestQueue();
		session.setQueue ( queue );
		session.setQueueConnectionFactoryBean ( qcfb );	
		session.setNotifyListenerOnClose  ( notify );
		session.startListening();		
		
		
		
		session.stopListening();
		
		//assert that the close event is forwarded to listener
		Thread.sleep (1000 * session.getTransactionTimeout() );
		if ( notify && !listener.wasClosed() )
			fail ( "notifyListenerOnClose fails");
		
		if ( !notify && listener.wasClosed() )
			fail ( "notifyListenerOnClose fails");
		
	}
	
	public static void testQueueReceiverSession ( QueueConnectionFactoryBean qcfb , TestXAResource xares )
	throws Exception
	{
		QueueReceiverSession session = new QueueReceiverSession();
		session.setTransactionTimeout ( 2 );
		TestMessageListener listener = new TestMessageListener();
		session.setMessageListener ( listener );
		Queue queue = new TestQueue();
		session.setQueue ( queue );
		session.setQueueConnectionFactoryBean ( qcfb );	
		session.setNotifyListenerOnClose  ( true );
		
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
		if ( receivedMessage == null ) throw new Exception ( "Receive does not work");
		if ( !receivedMessage.getText().equals ( text ) )
			throw new Exception ( "Receive does not pass content");
		if ( ! receivedMessage.getJMSCorrelationID().equals ( corrId ) ) 
			throw new Exception ( "JMS Correlation ID not passed");
		if ( !receivedMessage.getJMSType().equals ( type ) ) 
			throw new Exception ( "JMS type not passed" );
		if ( ! receivedMessage.getObjectProperty("property").equals ( property ) )
			throw new Exception ( "JMS property not passed");
						
		if ( xares.getLastCommitted() == null ) throw new Exception ( "No XA commit after receive");
		
		if ( ! listener.wasCommitted() ) throw new Exception ( "Listener did not get commit?");
		
		listener.setSimulateError ( true );
		
		TestQueueReceiver.setNextMessageToReceive ( message );
		
		xares.reset();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );
		if ( xares.getLastCommitted() != null ) throw new Exception ( "No XA rollback after failed receive");
		if ( listener.wasCommitted() ) throw new Exception ( "Listener got commit after error?");

		//ASSERT THAT UNEXPECTED JMS ERRORS REFRESH CONNECTION
		int connections = TestQueueConnectionFactory.getNumberOfConnectionsCreated();
		TestQueueReceiver.setNextMessageToReceive ( message );
		TestQueueReceiver.setErrorOnNextReceive();
		xares.reset();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );

		//assert that the error in receive() has caused the creation of a new connection
		if ( TestQueueConnectionFactory.getNumberOfConnectionsCreated() != connections + 1 )
					fail ( "Error in receive does not refresh connection: " + 
					connections + " vs " + TestQueueConnectionFactory.getNumberOfConnectionsCreated()  );	
		

		TestQueueReceiver.setNextMessageToReceive ( message );
		xares.reset();
		Thread.sleep  ( 1000 * session.getTransactionTimeout() );		
						
		session.stopListening();
		
		//assert that the close event is forwarded to listener
		Thread.sleep (1000 * session.getTransactionTimeout() );
		if ( !listener.wasClosed() )
			fail ( "notifyListenerOnClose fails");
	}
    
    public static void testQueueSenderSessionBasics (
    	QueueConnectionFactoryBean qcfb )
    	throws Exception
    {
    	QueueSenderSession session = new QueueSenderSession();
    	session.setDeliveryMode ( 1 );
    	if ( session.getDeliveryMode() != 1 )
    		throw new Exception ( "get/setDeliveryMode fails" );
    	String pw = "SECRET";
    	session.setPassword ( pw );
    	String user = "USER";
    	session.setUser ( user );
    	if ( !session.getUser().equals ( user ) )
    		throw new Exception ( "get/setUser fails" );
    	session.setPriority ( 1 );
    	if ( session.getPriority() != 1 )
    		throw new Exception ( "get/setPriority fails" );
    	TestQueue queue = new TestQueue();
    	session.setQueue ( queue );
    	if ( session.getQueue() != queue )
    		throw new Exception ( "set/getQueue fails" );
    	
    	session.setQueueConnectionFactoryBean ( qcfb );
    	if ( session.getQueueConnectionFactoryBean() != qcfb )
    		throw new Exception ( "get/setQueueConnectionFactory fails" );
    	session.setReplyToQueue ( queue );
    	if ( session.getReplyToQueue() != queue )
    		throw new Exception ( "set/getReplyToQueue fails" );
    	session.setTimeToLive ( 100 );
    	if ( session.getTimeToLive() != 100 )
    		throw new Exception ( "set/getTimeToLive fails");
    	
    	session.stop();
    	
    }
    
    public static void testQueueSenderSession ( 
    	QueueConnectionFactoryBean qcfb , TestXAResource xares )
    	throws Exception
    {
    	xares.reset();
    	UserTransactionImp utx = new UserTransactionImp();
    	int deliveryMode = 12;
    	int priority = 11;
    	long ttl = 100;
    	TestQueue queue = new TestQueue();
    
    	QueueSenderSession session =
    		new QueueSenderSession();
    	session.setDeliveryMode ( deliveryMode );
    	session.setPriority ( priority );
    	session.setTimeToLive ( ttl );
    	session.setQueueConnectionFactoryBean ( qcfb );
    	session.setQueue ( queue );
    	session.setReplyToQueue ( queue );
    	TextMessage msg = session.createTextMessage();
    	String text = "TEXT";
    	msg.setText ( text );
    	
    	utx.begin();
    	session.sendMessage ( msg );
 
		if ( xares.getLastStarted() == null )
			throw new Exception ( "No XA enlist for send");
		if ( xares.getLastEnded() == null )
			throw new Exception ( "No XA delist for send");
			    	
    	utx.commit();
    	
    	if ( xares.getLastCommitted() == null )
    		throw new Exception ( "No XA commit for send" );
    	
    	//assert message send is OK
    	TextMessage result = ( TextMessage ) TestQueueSender.getLastMessageSent();
    	if ( !result.getText().equals ( text ) )
    		throw new Exception ( "Message content lost");
    	if ( result.getJMSPriority() != priority )
    		throw new Exception ( "JMS priority lost");
    	if ( result.getJMSDeliveryMode() != deliveryMode )
    		throw new Exception ( "JMS deliveryMode lost" );
    	if ( result.getJMSDestination() != queue )
    		throw new Exception ( "JMS destination lost");
    	if ( result.getJMSReplyTo() != queue )
    		throw new Exception ( "JMS replyto lost");
    	
    	xares.reset();
    	
		utx.begin();
		session.sendMessage ( msg );
		
		utx.rollback();
		if ( xares.getLastCommitted() != null )
			throw new Exception ( "XA commit not allowed here" );
    	
   		if ( xares.getLastRolledback() == null )
   			throw new Exception ( "No XA rollback" );
   		
   		//assert that a send error triggers a new connection
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
   			fail ( "Error on send does not reopen connection?" );
   		
   		
   		utx.rollback();
   		session.stop();
   		
    }
    
    public static void testQueueSenderSessionFactory ( QueueConnectionFactoryBean qcfb )
    throws Exception 
    {
    	QueueSenderSessionFactory factory = new QueueSenderSessionFactory();
    	factory.setDeliveryMode ( 1 );
    	if ( factory.getDeliveryMode() != 1 ) fail ( "get/setDeliveryMode fails" );
    	factory.setPriority(2);
    	if ( factory.getPriority() != 2 ) fail ( "get/setPriority fails" );
    	TestQueue queue = new TestQueue();
    	factory.setQueue ( queue );
    	if ( factory.getQueue() != queue ) fail ( "get/setQueue fails" );
    	factory.setQueueConnectionFactoryBean ( qcfb );
    	if ( factory.getQueueConnectionFactoryBean() != qcfb ) fail ( "get/setQueueConnectionFactoryBean fails ");
    	factory.setReplyToQueue ( queue );
    	if ( factory.getReplyToQueue() != queue ) fail ( "get/setReplyToQueue fails" );
    	factory.setTimeToLive ( 111 );
    	if ( factory.getTimeToLive() != 111 ) fail ( "get/setTimeToLive fails" );
    	factory.setUser ( "user" );
    	if ( !factory.getUser().equals ("user")) fail ( "get/setUser fails" );
    	
    	//assert that the sender session gets the same properties 
    	QueueSenderSession session = factory.createQueueSenderSession();
    	if ( session.getDeliveryMode() != 1 ) fail ( "deliveryMode not inherited");
    	if ( session.getPriority() != 2 ) fail ( "priority not inherited" );
    	if ( session.getQueue() != queue ) fail ( "queue not inherited" );
    	if ( session.getQueueConnectionFactoryBean() != qcfb ) fail ( "queueConnectionFactoryBean not inherited" );
    	if ( session.getReplyToQueue() != queue ) fail ( "queue not inherited" );
    	if ( session.getTimeToLive() != 111 ) fail ( "TTL not inherited" );
    	if ( !session.getUser().equals ("user")) fail ( "user not inherited" );
    	
    }
    


    
	public static void testQueueConnectionFactoryBean()
	throws Exception
	{
		TestXAResource xares = new TestXAResource();
		TestQueueConnectionFactory f = new TestQueueConnectionFactory();
		QueueConnectionFactoryBean bean = new QueueConnectionFactoryBean();
		bean.setXaQueueConnectionFactory(f);
		bean.setResourceName("TestQueueConnectionFactoryBean");
		Connection c = bean.createQueueConnection();
        
		c.close();
	}
    
    //
    //MQSeries v5: assert that no new XAConnection is created
    //during send: number of connections is limited, and this
    //would close the user's connection!!!
    //
    public static void testNumberOfConnections ( 
    	UserTransactionService uts , 
    	QueueSession session )
    throws Exception
    {
		StringHeuristicMessage hmsg =
					new StringHeuristicMessage ( "TestMessage" );
    	UserTransaction utx = uts.getUserTransaction();
    	utx.begin();
    	int connections = TestQueueConnectionFactory.getNumberOfConnectionsCreated();
    	
    	
    	HeuristicQueueSender sender = ( HeuristicQueueSender) session.createSender ( null );
    	sender.send (  null , hmsg );
    	
    	sender.close();
    	
    	utx.commit();
    	
    	if ( connections != TestQueueConnectionFactory.getNumberOfConnectionsCreated())
    		throw new Exception ( "MQ problem: sending creates new XAConnection");
    }
    
    public static void test() 
    throws Exception
    {
         //first, setup the tm and connection
         TestXAResource xares = new TestXAResource();
         TestQueueConnectionFactory fact =
            new TestQueueConnectionFactory ( xares );
        JtaQueueConnectionFactory jtaFact =
            new JtaQueueConnectionFactory ( "TestJmsResource" , fact );        
        
        JmsTransactionalResource res =
            jtaFact.getTransactionalResource();
        if ( res == null )
            throw new Exception ( 
            "JtaConnectionFactory: no resource created?" );
        
//
//        TSInitInfo info =
//            UserTransactionServiceFactory.createTSInitInfo();
        UserTransactionService uts =
            new com.atomikos.icatch.standalone.UserTransactionServiceFactory().
                getUserTransactionService ( new Properties() );

        TSInitInfo info = uts.createTSInitInfo();
        info.registerResource ( res );
        uts.init ( info );
        
        QueueConnection conn = jtaFact.createQueueConnection();
        if ( conn == null )
            throw new Exception ( 
            "JtaConnectionFactory: creates null connection?" );
        QueueSession session = conn.createQueueSession ( true , 0 );
        
         //now, do the real tests
         testSend ( uts , session , xares );
         testReceive ( uts , session , xares );
         testNumberOfConnections ( uts , session );
         
         //shutdown of tm and connection
         session.close();
         conn.close();
         
         testQueueConnectionFactoryBean ();
         
         uts.shutdown ( true );
         
		 xares = new TestXAResource();
		 fact = new TestQueueConnectionFactory ( xares );         
         QueueConnectionFactoryBean qcfb = new QueueConnectionFactoryBean();
         qcfb.setXaQueueConnectionFactory ( fact );
         qcfb.setResourceName ( ReleaseTester.class.getClass().getSimpleName() );
		 testQueueConnectionFactoryBean();
		 qcfb = new QueueConnectionFactoryBean();
		 qcfb.setXaQueueConnectionFactory ( fact );
		 qcfb.setResourceName ( "TESTBRIDGEQUEUERESOURCE" );
		 //testBridge ( qcfb );
         testQueueReceiverSessionBasics ( qcfb );
         testQueueReceiverSession ( qcfb , xares );
         testQueueSenderSessionBasics ( qcfb );
		 testQueueSenderSession ( qcfb , xares );
		 testQueueSenderSessionFactory ( qcfb );
		 testNotifyListenerOnClose ( qcfb , true );
		 testNotifyListenerOnClose ( qcfb , false );
		 
		
		 
    }
 
    public static void main ( String[] args )
    {
        System.err.println ( "Starting JMS release test..." );
        try {
			
            test(); 
        }
        catch ( Exception e ) {
            e.printStackTrace(); 
        } 
        finally {
            System.err.println ( "Done." ); 
        }
    } 
}
