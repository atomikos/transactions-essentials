
//Revision 1.3  2005/01/07 17:07:31  guy
//Added tests for JMS receiver support (lightweigh MDB), and JMS queue bridging.
//
//Revision 1.2.2.1  2005/02/25 11:05:28  guy
//Added tests for MQSeries XAConnection limitations.
//
//Moved to here from datasource.xa.jms.test
//Merged-in changes from branch redesign-4-2003.
//Merged in changes from transactionsJTA100 branch.
//Revision 1.1.2.1  2002/09/13 09:02:33  guy
//Added test classes for the JMS adaptors.
//



package com.atomikos.jms;
import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.XAConnection;
import javax.jms.XAQueueConnection;
import javax.jms.XAQueueConnectionFactory;
import javax.transaction.xa.XAResource;

import com.atomikos.icatch.system.Configuration;

 /**
  *
  *
  *A test queue connection factory.
  */

public class TestQueueConnectionFactory 
implements XAQueueConnectionFactory, Serializable
{
   

    private static int numberOfConnectionsCreated_ = 0;

    private static boolean errorOnNextCreate_ = false;
    
    public static void setErrorOnNextCreate()
    {
    		errorOnNextCreate_ = true;
    }
    
  
    
    private XAResource xares_;
    
    
    
    public TestQueueConnectionFactory() {}
     
    public TestQueueConnectionFactory ( XAResource xares  )
    {
         xares_ = xares;
    }
    
   
    private static void incNumberOfConnectionsCreated()
    {
    	numberOfConnectionsCreated_++;
    }
     
    public static int getNumberOfConnectionsCreated()
    {
    	return numberOfConnectionsCreated_;
    }
 
    public XAQueueConnection createXAQueueConnection() 
    throws JMSException
    {

        
        incNumberOfConnectionsCreated();
        if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( "TestQueueConnectionFactory: creating new Connection..." );

        if ( errorOnNextCreate_ ) {
        		errorOnNextCreate_ = false;
        		throw new JMSException ( "Simulated error" );
        }
        return new TestQueueConnection ( xares_  );
    }
    
    
      
    public XAQueueConnection createXAQueueConnection ( 
        String user , String pw )
    throws JMSException
    {
            return createXAQueueConnection();
    }
    
    public QueueConnection createQueueConnection()
    throws JMSException
    {
        return null;
    }
    
    public QueueConnection createQueueConnection ( String user , String pw )
    throws JMSException
    {
        return null; 
    }

	public XAConnection createXAConnection() throws JMSException {
		
		return createXAQueueConnection();
	}

	public XAConnection createXAConnection(String arg0, String arg1) throws JMSException {
		
		return null;
	}

	public Connection createConnection() throws JMSException {
		
		return null;
	}

	public Connection createConnection(String arg0, String arg1) throws JMSException {
		
		return null;
	}
   
    
    
}
