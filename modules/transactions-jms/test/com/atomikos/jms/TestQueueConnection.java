//$Id: TestQueueConnection.java,v 1.2 2006/10/30 10:37:11 guy Exp $
//$Log: TestQueueConnection.java,v $
//Revision 1.2  2006/10/30 10:37:11  guy
//Merged in changes of 3.1.0 release
//
//Revision 1.1.1.1.4.2  2006/10/13 13:07:08  guy
//ADDED 1010
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
//Revision 1.2  2004/10/12 13:04:39  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: TestQueueConnection.java,v 1.2 2006/10/30 10:37:11 guy Exp $
//Revision 1.1  2004/09/18 12:56:59  guy
//$Id: TestQueueConnection.java,v 1.2 2006/10/30 10:37:11 guy Exp $
//Moved to here from datasource.xa.jms.test
//$Id: TestQueueConnection.java,v 1.2 2006/10/30 10:37:11 guy Exp $
//
//$Id: TestQueueConnection.java,v 1.2 2006/10/30 10:37:11 guy Exp $
//Revision 1.2  2003/03/11 06:43:02  guy
//$Id: TestQueueConnection.java,v 1.2 2006/10/30 10:37:11 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: TestQueueConnection.java,v 1.2 2006/10/30 10:37:11 guy Exp $
//
//Revision 1.1.2.1  2002/09/13 09:02:33  guy
//Added test classes for the JMS adaptors.
//



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
