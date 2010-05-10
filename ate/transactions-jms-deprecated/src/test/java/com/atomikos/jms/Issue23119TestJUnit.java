package com.atomikos.jms;

import java.io.Serializable;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

public class Issue23119TestJUnit extends TransactionServiceTestCase 
{

	private UserTransactionService uts;
	private UserTransaction utx;
	private QueueSenderSession session;
	
	public Issue23119TestJUnit ( String name ) 
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
		
		
		session = new QueueSenderSession();
		session.setAbstractConnectionFactoryBean ( new TestQueueConnectionFactoryBean() );
		Queue queue = new TestQueue();
		session.setQueue ( queue );
		
	}
	
	protected void tearDown()
	{	
		session.stop();
		uts.shutdown ( true );
		super.tearDown();
	}
	
	public void testIssue23119() throws Exception 
	{
		TextMessage msg = null;
		String errorMessage = null;
		try {
			msg = session.createTextMessage();
			fail ( "session does not correctly simulate error" );
		} catch ( JMSException ok ) {
			errorMessage = ok.getMessage();
		}
		assertNotNull ( errorMessage );
		//if the session refresh works correctly then a second time should
		//give a different error msg - see the TestQueueSession 
		try {
			msg = session.createTextMessage();
			fail ( "session does not correctly simulate error" );
		} catch ( JMSException ok ) {
			//error should now be a DIFFERENT msg
			assertFalse ( "underlying session not refreshed on errors!!!" , errorMessage.equals ( ok.getMessage() ) );
		}
		
	}
	

	private static class TestQueueSession implements QueueSession 
	{

		public QueueBrowser createBrowser(Queue arg0) throws JMSException {
			
			return null;
		}

		public QueueBrowser createBrowser(Queue arg0, String arg1)
				throws JMSException {
			
			return null;
		}

		public Queue createQueue(String arg0) throws JMSException {
		
			return null;
		}

		public QueueReceiver createReceiver(Queue arg0) throws JMSException {
			
			return null;
		}

		public QueueReceiver createReceiver(Queue arg0, String arg1)
				throws JMSException {
			
			return null;
		}

		public QueueSender createSender(Queue arg0) throws JMSException {
			
			return null;
		}

		public TemporaryQueue createTemporaryQueue() throws JMSException {
			
			return null;
		}

		public void close() throws JMSException {
		
			throw new JMSException ( "Simulated error" );
		}

		public void commit() throws JMSException {
			
			
		}

		public BytesMessage createBytesMessage() throws JMSException {
			
			return null;
		}

		public MessageConsumer createConsumer(Destination arg0)
				throws JMSException {
			return null;
		}

		public MessageConsumer createConsumer(Destination arg0, String arg1)
				throws JMSException {
			
			return null;
		}

		public MessageConsumer createConsumer(Destination arg0, String arg1,
				boolean arg2) throws JMSException {
			
			return null;
		}

		public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1)
				throws JMSException {
			
			return null;
		}

		public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1,
				String arg2, boolean arg3) throws JMSException {
			
			return null;
		}

		public MapMessage createMapMessage() throws JMSException {
			
			return null;
		}

		public Message createMessage() throws JMSException {
			
			return null;
		}

		public ObjectMessage createObjectMessage() throws JMSException {
			
			return null;
		}

		public ObjectMessage createObjectMessage(Serializable arg0)
				throws JMSException {
			
			return null;
		}

		public MessageProducer createProducer(Destination arg0)
				throws JMSException {
			
			return null;
		}

		public StreamMessage createStreamMessage() throws JMSException {
			
			return null;
		}

		public TemporaryTopic createTemporaryTopic() throws JMSException {
			
			return null;
		}

		public TextMessage createTextMessage() throws JMSException {
			
			return createTextMessage ( null );
		}

		public TextMessage createTextMessage(String arg0) throws JMSException {
			
			throw new JMSException ( this.toString() );
		}

		public Topic createTopic(String arg0) throws JMSException {
			
			return null;
		}

		public int getAcknowledgeMode() throws JMSException {
			
			return 0;
		}

		public MessageListener getMessageListener() throws JMSException {
			
			return null;
		}

		public boolean getTransacted() throws JMSException {
			
			return false;
		}

		public void recover() throws JMSException {
			
		}

		public void rollback() throws JMSException {
			
		}

		public void run() {
			
		}

		public void setMessageListener(MessageListener arg0)
				throws JMSException {
			
		}

		public void unsubscribe(String arg0) throws JMSException {
			
			
		}
		
	}
	
	private static class TestQueueConnection implements QueueConnection
	{

		public ConnectionConsumer createConnectionConsumer(Queue arg0,
				String arg1, ServerSessionPool arg2, int arg3)
				throws JMSException {
			return null;
		}

		public QueueSession createQueueSession(boolean arg0, int arg1)
				throws JMSException {
			return null;
		}

		public void close() throws JMSException {
			
		}

		public ConnectionConsumer createConnectionConsumer(Destination arg0,
				String arg1, ServerSessionPool arg2, int arg3)
				throws JMSException {
			return null;
		}

		public ConnectionConsumer createDurableConnectionConsumer(Topic arg0,
				String arg1, String arg2, ServerSessionPool arg3, int arg4)
				throws JMSException {
			return null;
		}

		public Session createSession(boolean arg0, int arg1)
				throws JMSException {
			return new TestQueueSession();
		}

		public String getClientID() throws JMSException {
			return null;
		}

		public ExceptionListener getExceptionListener() throws JMSException {
			return null;
		}

		public ConnectionMetaData getMetaData() throws JMSException {
			return null;
		}

		public void setClientID(String arg0) throws JMSException {
			
		}

		public void setExceptionListener(ExceptionListener arg0)
				throws JMSException {
			
		}

		public void start() throws JMSException {
			
		}

		public void stop() throws JMSException {
			
		}
		
	}
	
	private static class TestQueueConnectionFactoryBean extends AbstractConnectionFactoryBean
	{

		protected void checkSetup() throws JMSException {
			
			
		}

		public Connection createConnection() throws JMSException {
			return new TestQueueConnection();
		}

		public Connection createConnection(String arg0, String arg1)
				throws JMSException {
			return new TestQueueConnection();
		}
		
	}
}


