
//Revision 1.1.1.1.4.3  2006/12/07 13:33:21  guy
//FIXED 10099
//
//Revision 1.1.1.1.4.2  2006/10/20 07:03:14  guy
//Completed JMS 1.1 support
//
//Revision 1.1.1.1.4.1  2006/10/10 14:01:44  guy
//ADDED 1011
//
//Revision 1.1.1.1  2006/08/29 10:01:14  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/01/07 17:07:31  guy
//Added tests for JMS receiver support (lightweigh MDB), and JMS queue bridging.
//
//Revision 1.2  2004/10/12 13:04:39  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
package com.atomikos.jms;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.TransactionInProgressException;
import javax.jms.XAQueueSession;
import javax.transaction.xa.XAResource;

 /**
  *
  *
  *A special test session for Atomikos JTA.
  */

class TestQueueSession 
implements QueueSession, XAQueueSession
{
    private XAResource xares_;
    
    private boolean closeCalled;
    private boolean createTemporaryQueueCalled;
    private boolean createBrowserWithQueueAndSelectorCalled;
    private boolean createBrowserCalled;
    private boolean createSenderCalled;
    private boolean createReceiverWithSelectorCalled;
    private boolean createReceiverCalled;
    private boolean createQueueCalled;
    
    private MessageListener ml;
    private boolean allowCommitRollback;
    
    TestQueueSession ( XAResource xares )
    {
          xares_ = xares;
          this.allowCommitRollback = true;
    } 
    
    TestQueueSession()
    {
    	this.allowCommitRollback = false;
    }
    
    public TemporaryQueue createTemporaryQueue()
    throws JMSException
    {
    		createTemporaryQueueCalled = true;
        return null;
    }
    
    public boolean createTemporaryQueueCalled()
    {
    		return createTemporaryQueueCalled;
    }
    
    public QueueBrowser createBrowser ( 
        Queue   queue , String messageSelector )
    throws JMSException
    {
    		createBrowserWithQueueAndSelectorCalled = true;
        return null;
    }
    
    public boolean createBrowserWithQueueAndSelectorCalled()
    {
    		return createBrowserWithQueueAndSelectorCalled;
    }
    
    public QueueBrowser createBrowser ( Queue queue )
    throws JMSException
    {
    		createBrowserCalled = true;
          return null;
    }
    
    public boolean createBrowserCalled() throws Exception
    {
    		return createBrowserCalled;
    }
    
    public QueueSender createSender ( Queue queue )
    throws JMSException
    {
    	
    		createSenderCalled = true;
    		if ( isCloseCalled() ) throw new JMSException ( "session already closed" );
          return new TestQueueSender ( queue );
    }
    
    public boolean createSenderCalled()
    throws Exception
    	{
    		return createSenderCalled;
    }
    
    public QueueReceiver createReceiver ( 
        Queue q , String selector )
    throws JMSException
    {
    		createReceiverWithSelectorCalled = true;
        return new TestQueueReceiver ( null );
    }
    
    public boolean createReceiverWithSelectorCalled()
    {
    		return createReceiverWithSelectorCalled;
    }
    
    public QueueReceiver createReceiver ( 
        Queue q  )
    throws JMSException
    {
    		createReceiverCalled = true;
        return new TestQueueReceiver ( null );
    }
    
    public boolean createReceiverCalled()
    {
    		return createReceiverCalled;
    }
    
    public Queue createQueue ( String name )
    throws JMSException
    {
    	  final String qName = name;
    	  createQueueCalled = true;
          return new Queue() {

			public String getQueueName() throws JMSException {
				return qName;
			}
        	  
          };
    }
    
    public boolean createQueueCalled()
    {
    	return createQueueCalled;
    }
    
    public void commit()
    throws JMSException
    {
    	if ( !allowCommitRollback ) { 
    		String msg = "commit not allowed on session";
    		if ( xares_ != null ) throw new TransactionInProgressException ( msg );
    		else throw new javax.jms.IllegalStateException ( msg );
    	}
    }
     
     
    public void rollback()
    throws JMSException
    {
    	if ( !allowCommitRollback ) { 
    		String msg = "rollback not allowed on session";
    		if ( xares_ != null ) throw new TransactionInProgressException ( msg );
    		else throw new javax.jms.IllegalStateException ( msg );
    	}
    }
    
    public boolean getTransacted()
    throws JMSException
    {
        return allowCommitRollback;
    }
    
    public void run()
    {
        return;
    }
    
    public void setMessageListener ( MessageListener l )
    throws JMSException 
    {
        this.ml = l;
    }
    
    
    public MessageListener getMessageListener()
    throws JMSException
    {
          return ml;
    }
    
    public void recover()
    throws JMSException
    {
        throw new javax.jms.IllegalStateException ( 
        "Transacted session: recover not allowed" ); 
    }
    
    public void close()
    throws JMSException
    {
          closeCalled = true;
          //do nothing
    }
    
    public boolean isCloseCalled()
    {
    		return closeCalled;
    }
    
   
    
    public TextMessage createTextMessage()
    throws JMSException
    {
          return  createTextMessage ( null );
    }
    
    public TextMessage createTextMessage ( String text )
    throws JMSException
    {
        TextMessage ret =  new TestTextMessage();
        ret.setText ( text );
        return ret;
    }
    
    public StreamMessage createStreamMessage()
    throws JMSException
    {
        return new TestStreamMessage();
    }
    
    public ObjectMessage createObjectMessage ( java.io.Serializable o )
    throws JMSException
    {
        ObjectMessage ret = new TestObjectMessage();
        ret.setObject(o );
        return ret;
    }
    
    public ObjectMessage createObjectMessage()
    throws JMSException
    {
        return createObjectMessage ( null );
    }
    
    public Message createMessage()
    throws JMSException
    {
        return new TestTextMessage();
    }
    
    public MapMessage createMapMessage()
    throws JMSException
    {
        return new TestMapMessage();
    }
    
    public BytesMessage createBytesMessage()
    throws JMSException
    {
		throw new JMSException ( "Not Implemented");
    }
    
    public QueueSession getQueueSession()
    {
        return this;   
    }
    public XAResource getXAResource()
    {
        return xares_; 
    }

	public int getAcknowledgeMode() throws JMSException {
		
		return 0;
	}

	public MessageProducer createProducer(Destination dest) throws JMSException {
		
		return createSender ( ( Queue ) dest );
	}

	public MessageConsumer createConsumer(Destination dest) throws JMSException {
		
		return createReceiver ( ( Queue ) dest );
	}

	public MessageConsumer createConsumer(Destination dest, String selector) throws JMSException {
		
		return createConsumer ( ( Queue ) dest );
	}

	public MessageConsumer createConsumer(Destination dest, String selector, boolean noLocal ) throws JMSException {
	
		return createConsumer ( ( Queue ) dest );
	}

	public Topic createTopic(String arg0) throws JMSException {
		
		return null;
	}

	public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1) throws JMSException {
		
		return new TestTopicSubscriber( arg0 );
	}

	public TopicSubscriber createDurableSubscriber(Topic arg0, String arg1, String arg2, boolean arg3) throws JMSException {
		
		return new TestTopicSubscriber( arg0 );
	}

	public TemporaryTopic createTemporaryTopic() throws JMSException {
		return null;
	}

	public void unsubscribe(String arg0) throws JMSException {
		
	}

	public Session getSession() throws JMSException {
		return this;
	}
    
    
}
