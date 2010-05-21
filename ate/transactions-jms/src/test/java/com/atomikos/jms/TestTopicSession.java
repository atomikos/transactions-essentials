

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
import javax.jms.Topic;

import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.TemporaryQueue;
import javax.jms.TopicSubscriber;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryTopic;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.TransactionInProgressException;
import javax.jms.XATopicSession;
import javax.transaction.xa.XAResource;

 /**
  *
  *
  *A special test session for Atomikos JTA.
  */

public class TestTopicSession 
implements TopicSession, XATopicSession
{
	
	private static boolean unsubscribeCalled = false;
	
	public static void reset() {
		unsubscribeCalled = false;
	}
	
	public static boolean wasUnsubscribeCalled() {
		return unsubscribeCalled;
	}
	
    private XAResource xares_;
    
    private boolean closeCalled;
    private boolean createTopicCalled;
    
    private MessageListener ml;

	private boolean createBrowserWithQueueCalled;

	private boolean createBrowserWithQueueAndSelectorCalled;

	private int ackMode;

    
    TestTopicSession ( XAResource xares )
    {
          xares_ = xares;
    }   
    
    public TemporaryTopic createTemporaryTopic()
    throws JMSException
    {
        return null;
    }
    
  
    
    public Topic createTopic ( String name )
    throws JMSException
    {
    	  createTopicCalled = true;
    	  final String tname = name;
          return new Topic() {

			public String getTopicName() throws JMSException {
				return tname;
			}
        	  
          };
    }
    
    public boolean createTopicCalled()
    {
    	return createTopicCalled;
    }
    
    public void commit()
    throws JMSException
    {
          throw new TransactionInProgressException ( 
          "XA Session: commit not allowed on session" );
    }
     
     
    public void rollback()
    throws JMSException
    {
          throw new TransactionInProgressException ( 
          "XA Session: rollback not allowed on session" );
    }
    
    public boolean getTransacted()
    throws JMSException
    {
        return true;
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
    
    public TopicSession getTopicSession()
    {
        return this;   
    }
    public XAResource getXAResource()
    {
        return xares_; 
    }

	public int getAcknowledgeMode() throws JMSException {
		
		return ackMode;
	}
	
	public void setAcknowledgeMode ( int i )
	{
		ackMode = i;
	}

	public MessageProducer createProducer(Destination d) throws JMSException {
		return new TestTopicPublisher ( d );
		
	}

	public MessageConsumer createConsumer(Destination d) throws JMSException {
		
		return new TestTopicSubscriber ( d);
	}

	public MessageConsumer createConsumer(Destination d, String arg1) throws JMSException {
		
		return createConsumer ( d );
	}

	public MessageConsumer createConsumer(Destination d, String arg1, boolean arg2) throws JMSException {
	
		return createConsumer ( d );
	}


	public TopicSubscriber createDurableSubscriber(Topic d, String arg1) throws JMSException {
		
		return new TestTopicSubscriber ( d);
	}

	public TopicSubscriber createDurableSubscriber(Topic d, String arg1, String arg2, boolean arg3) throws JMSException {
		
		return new TestTopicSubscriber ( d);
	}

	

	public void unsubscribe(String name) throws JMSException {
		this.unsubscribeCalled = true;
		if ( closeCalled ) throw new JMSException ( "Session already closed" );
	}

	public Session getSession() throws JMSException {
		return this;
	}

	public TopicSubscriber createSubscriber(Topic d) throws JMSException {
		
		return new TestTopicSubscriber ( d );
	}

	public TopicSubscriber createSubscriber(Topic d, String selector, boolean noLocal) throws JMSException {
		
		return new TestTopicSubscriber ( d );
	}

	public TopicPublisher createPublisher(Topic d) throws JMSException {
		
		return new TestTopicPublisher ( d );
	}

	public Queue createQueue(String arg0) throws JMSException {
		
		return null;
	}

	public QueueBrowser createBrowser(Queue arg0) throws JMSException {
	
		createBrowserWithQueueCalled = true;
		return null;
	}

	public QueueBrowser createBrowser(Queue arg0, String arg1) throws JMSException {
		
		createBrowserWithQueueAndSelectorCalled = true;
		return null;
	}

	public TemporaryQueue createTemporaryQueue() throws JMSException {
		
		return null;
	}

	public boolean createBrowserWithQueueCalled() {
		return createBrowserWithQueueCalled;
	}

	public boolean createBrowserWithQueueAndSelectorCalled() {
		return createBrowserWithQueueAndSelectorCalled;
	}

    
    
}
