package com.atomikos.jms;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSender;

 /**
  *
  *
  *A test sender.
  */

public class TestQueueSender 
implements QueueSender
{
    
    
    private Queue queue_;
    
    private static Message lastMessageSent_;
    
    private static boolean errorOnNextSend_ = false;
    
    
    
    public static void setErrorOnNextSend()
    {
    	errorOnNextSend_ = true;
    }
    
    public TestQueueSender ( Queue queue )
    {
        queue_ = queue; 
    }
    
    public static void reset()
    {
    	errorOnNextSend_ = false;
    	lastMessageSent_ = null;
    }

    
    private boolean disableMessageId, disableMessageTimestamp, closeCalled;
    private int deliveryMode, priority;
    private long ttl;
   
    
     /**
      *@see QueueSender
      */
      
    public void send ( Message msg )
    throws JMSException
    {
         //do nothing 
    }
    
    /**
      *@see QueueSender
      */
      
    public void send ( Message msg , int deliveryMode, int priority, 
        long timeToLive)
    throws JMSException
    {
    	if ( errorOnNextSend_ ) {
    		errorOnNextSend_ = false;
    		throw new JMSException ( "Simulated error" );
    	}
		msg.setJMSDeliveryMode(deliveryMode);
	  	msg.setJMSPriority(priority);
	 	long now = System.currentTimeMillis();
	 	msg.setJMSExpiration ( now+ timeToLive );
	  	lastMessageSent_ = msg;
	  	msg.setJMSDestination ( queue_ );
    }
    
    public static Message getLastMessageSent()
    {
    	return lastMessageSent_;
    }
    
    /**
      *@see QueueSender
      */
      
    public void send ( Queue q , Message msg )
    throws JMSException
    {
    		 send ( msg , 0 , 1 , 100 );
    }
    
    
    
    /**
      *@see QueueSender
      */
      
    public void send ( Queue q, Message msg, int deliveryMode, int priority, 
        long timeToLive )
    throws JMSException
    {
    	send ( msg , deliveryMode , priority , timeToLive );
    }	
    
     /**
      *@see QueueSender
      */
      
      public Queue getQueue()
      throws JMSException
      {
          return queue_; 
      }
    
    //
    //BELOW IS IMPLEMENTATION OF JMS MESSAGEPRODUCER
    //
    
    public void setDisableMessageID ( boolean val )
    throws JMSException
    {
        this.disableMessageId = val;
    }
    
    public boolean getDisableMessageID ()
    throws JMSException
    {
        return disableMessageId;
    }
    
    public void setDisableMessageTimestamp ( boolean value )
    throws JMSException
    {
        //do nothing
    		this.disableMessageTimestamp = value;
    }
    
    public boolean getDisableMessageTimestamp()
    throws JMSException
    {
        return disableMessageTimestamp;
    }
    
    public void setDeliveryMode ( int mode )
    throws JMSException
    {
        //do nothing
    		this.deliveryMode = mode;
    }
    
    public int getDeliveryMode() 
    throws JMSException
    {
        return deliveryMode;
    }
  
    
    public void setPriority ( int p )
    throws JMSException
    {
        //do nothing
    		this.priority = p;
    }
    
    public int getPriority()
    throws JMSException
    {
        return priority;
    }
    
    public void setTimeToLive ( long ttl )
    throws JMSException
    {
          //do nothing
    		this.ttl = ttl;
    }
    
    public long getTimeToLive()
    throws JMSException
    {
        return ttl; 
    }
    
    public void close()
    throws JMSException
    {
        //do nothing
    		closeCalled = true;
    }
    
    public boolean closeCalled()
    {
    	return closeCalled;
    }

	public Destination getDestination() throws JMSException {
		return getQueue();
		
	}

	public void send(Destination arg0, Message msg ) throws JMSException {
		if ( getDestination() != null ) 
			throw new UnsupportedOperationException ( "ISSUE 10107" );
		send ( null, msg );
		
	}

	public void send(Destination arg0, Message msg, int deliveryMode, int priority , long timeToLive ) throws JMSException {
	
		if ( getDestination() != null ) 
			throw new UnsupportedOperationException ( "ISSUE 10107" );
		send ( msg , deliveryMode , priority , timeToLive );
	}
    
    
}
