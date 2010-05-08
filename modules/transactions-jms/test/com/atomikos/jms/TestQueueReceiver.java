//$ID$
//$Log: TestQueueReceiver.java,v $
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
//$ID$
//Revision 1.1  2004/09/18 12:56:59  guy
//$ID$
//Moved to here from datasource.xa.jms.test
//$ID$
//
//$ID$
//Revision 1.2  2003/03/11 06:43:02  guy
//$ID$
//Merged in changes from transactionsJTA100 branch.
//$ID$
//
//Revision 1.1.2.1  2002/09/13 09:02:33  guy
//Added test classes for the JMS adaptors.
//

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
