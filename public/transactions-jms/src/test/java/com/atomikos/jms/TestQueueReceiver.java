package com.atomikos.jms;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueReceiver;

 /**
  *
  *
  *A test queue receiver.
  */

class TestQueueReceiver
implements QueueReceiver
{
    
    private Queue queue_;
    
    private MessageListener listener_;
    
    private static Message nextMessageToReceive_;
    
    private static boolean errorOnNextReceive_ = false;
    
    TestQueueReceiver ( Queue queue )
    {
          queue_ = queue; 
    }
    
    public static void setErrorOnNextReceive()
    {
    	errorOnNextReceive_ = true;
    }
    
    public static void reset()
    {
    	errorOnNextReceive_ = false;
    }
    
    //
    //IMPLEMENTATION OF QUEUERECEIVER
    //
    
    public Queue getQueue() throws JMSException
    {
        return queue_; 
    }
    
    public Message receiveNoWait ()
    throws JMSException
    {
        return receive();
    }
    
    public Message receive ( long timeout )
    throws JMSException
    {
    	try
        {
            Thread.sleep ( timeout );
        }
        catch (InterruptedException e)
        {
           
        }
        return receive();
    }
    
    public Message receive()
    throws JMSException
    {
    	if ( errorOnNextReceive_ ) {
    		errorOnNextReceive_ = false;
    		throw new JMSException ( "Simulated error" );
    	}
    	Message ret = nextMessageToReceive_;
    	//nextMessageToReceive_ = null;
        return ret;
    }
    
    public static void setNextMessageToReceive ( Message m )
    {
    	nextMessageToReceive_ = m;
    }
    
    public String getMessageSelector()
    throws JMSException
    {
        return "None!";
    }
    
    public MessageListener getMessageListener()
    throws JMSException
    {
        return listener_;
    }
    
    public void setMessageListener ( MessageListener l )
    throws JMSException
    {
         if ( listener_ != null && l != null  ) throw new JMSException ( "Only one listener allowed");
         listener_ = l;
    }
    
    
    public void close() throws JMSException
    {
          //do nothing
    }
    
    
}
