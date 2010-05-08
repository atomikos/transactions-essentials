//Revision 1.1.1.1.4.4  2007/01/29 11:44:49  guy
//FIXED 10107
//
//Revision 1.1.1.1.4.3  2006/10/13 13:07:08  guy
//ADDED 1010
//
//Revision 1.1.1.1.4.2  2006/10/10 15:07:40  guy
//Added JMS tests
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
//
//Revision 1.1.2.1  2002/09/13 09:02:34  guy
//Added test classes for the JMS adaptors.
//

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
