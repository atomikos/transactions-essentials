package com.atomikos.jms;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.XAConnection;
import javax.jms.XATopicConnection;
import javax.jms.XATopicConnectionFactory;
import javax.transaction.xa.XAResource;

import com.atomikos.icatch.system.Configuration;

public class TestTopicConnectionFactory implements XATopicConnectionFactory,
		Serializable {

	   private static int numberOfConnectionsCreated_ = 0;

	    
	   
	    
	    private XAResource xares_;
	    
	    
	    
	    public TestTopicConnectionFactory() {}
	     
	    public TestTopicConnectionFactory ( XAResource xares  )
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
	    
	    
	public XATopicConnection createXATopicConnection() throws JMSException {
		incNumberOfConnectionsCreated();
        if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( "TestQueueConnectionFactory: creating new Connection..." );

        return new TestTopicConnection(xares_);
	}

	public XATopicConnection createXATopicConnection(String arg0, String arg1)
			throws JMSException {
		return createXATopicConnection();
	}

	public XAConnection createXAConnection() throws JMSException {
		return createXATopicConnection();
	}

	public XAConnection createXAConnection(String arg0, String arg1)
			throws JMSException {
		return createXAConnection();
	}

	public TopicConnection createTopicConnection() throws JMSException {
		
		return createXATopicConnection();
	}

	public TopicConnection createTopicConnection(String arg0, String arg1)
			throws JMSException {
		return createXATopicConnection();
	}

	public Connection createConnection() throws JMSException {
		return createXAConnection();
	}

	public Connection createConnection(String arg0, String arg1)
			throws JMSException {
		return createXAConnection();
	}

}
