package com.atomikos.jms;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueSession;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;

 /**
  *
  *
  *A Test JMS queue connection.
  */

class TestQueueConnection 
implements XAQueueConnection, QueueConnection
{
    
    private XAResource xares_;
    
    TestQueueConnection ( XAResource xares )
    {
        xares_ = xares; 
    }
     
     /**
      *Creates a new session for JTA transactions.
      *@param transacted ignored.
      *@param ackMode ignored.
      */
      
     public QueueSession createQueueSession (
        boolean transacted , int ackMode )
        throws JMSException
    {
        
        
       
        return new TestQueueSession ( xares_ );
    }
    
    public XAQueueSession createXAQueueSession ()
        throws JMSException
    {
        
        
       
        return new TestQueueSession ( xares_ );
    }
    
    public ConnectionConsumer createConnectionConsumer (
        Queue queue , String selector , ServerSessionPool pool , int max )
    throws JMSException
    {
        throw new JMSException ( "Not implemented" ); 
    }
    
    //
    //FOLLOWING ARE METHODS REQUIRED BY JMS
    // 
   
    public void close() 
    throws JMSException
    {
           
    }
    
    public void start() throws JMSException
    {
          
    }
    
    public void setExceptionListener ( ExceptionListener l )
    throws JMSException
    {
          
    }
    
    public ExceptionListener getExceptionListener()
    throws JMSException
    {
          return null;
    }
    
    public ConnectionMetaData getMetaData()
    throws JMSException
    {
          return null;
    }
    
    public void setClientID ( String id )
    throws JMSException
    {
    }
    
    public String getClientID() throws JMSException
    {
        return null; 
    }
    
    public void stop() throws JMSException
    {
    }

	public XASession createXASession() throws JMSException {
		
		return new TestQueueSession ( xares_ );
	}

	public Session createSession(boolean transacted, int ack) throws JMSException {
		
		return createXASession();
	}

	public ConnectionConsumer createConnectionConsumer(Destination arg0, String arg1, ServerSessionPool arg2, int arg3) throws JMSException {
		
		return null;
	}

	public ConnectionConsumer createDurableConnectionConsumer(Topic arg0, String arg1, String arg2, ServerSessionPool arg3, int arg4) throws JMSException {
		
		return null;
	}
    
}
