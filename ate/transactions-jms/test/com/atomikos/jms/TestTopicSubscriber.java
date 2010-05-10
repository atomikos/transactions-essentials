package com.atomikos.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

public class TestTopicSubscriber implements TopicSubscriber {

	private Topic topic;
	
    
    private MessageListener listener;
    
    private static Message nextMessageToReceive;
    
    private static boolean errorOnNextReceive = false;
    
    public static void setErrorOnNextReceive()
    {
    	errorOnNextReceive = true;
    }
    
    public static void reset()
    {
    	errorOnNextReceive = false;
    }
	
	public TestTopicSubscriber ( Topic topic )
	{
		this.topic = topic;
	}
	
	public TestTopicSubscriber ( Destination d )
	{
		this.topic = ( Topic )d;
	}
	
	public Topic getTopic() throws JMSException {

		return topic;
	}

	public boolean getNoLocal() throws JMSException {
		
		return false;
	}

	public String getMessageSelector() throws JMSException {
		
		return null;
	}

	public MessageListener getMessageListener() throws JMSException {
		
		return listener;
	}

	public void setMessageListener(MessageListener l) throws JMSException {
		this.listener = l;

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
    	if ( errorOnNextReceive ) {
    		errorOnNextReceive = false;
    		throw new JMSException ( "Simulated error" );
    	}
    	Message ret = nextMessageToReceive;
    	//nextMessageToReceive_ = null;
        return ret;
    }
    
    public static void setNextMessageToReceive ( Message m )
    {
    	nextMessageToReceive = m;
    }
	public void close() throws JMSException {
		

	}

}
