package com.atomikos.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

public class TestTopicPublisher implements TopicPublisher {

	private static boolean error;
	private static Message lastMsg;
	
	private Topic topic;
	private boolean disableMessageId;
	private boolean disableMessageTimestamp;
	private int deliveryMode;
	private int pty;
	private long ttl;
	
	private boolean closeCalled;
	
	public static Message getLastMessageSent()
	{
		return lastMsg;
	}
	
	private  void testError ( Message msg , int deliveryMode, int priority, 
	        long timeToLive) throws JMSException
	{
		lastMsg = msg;
		if ( error ) {
			error = false;
			throw new JMSException ( "Simulated error");
		}
		if ( msg ==  null ) {
			//happens during JtaTopicConnectionFactory testing
			return;
		}
		msg.setJMSDeliveryMode(deliveryMode);
	  	msg.setJMSPriority(priority);
	 	long now = System.currentTimeMillis();
	 	msg.setJMSExpiration ( now+ timeToLive );
	  	msg.setJMSDestination ( topic );
	}
	
	public TestTopicPublisher ( Topic topic )
	{
		this.topic = topic;
	}
	
	public TestTopicPublisher ( Destination d )
	{
		this.topic = ( Topic ) d;
	
	}
	
	public Topic getTopic() throws JMSException {
		return topic;
	}

	public void publish(Message arg0) throws JMSException {
		testError ( arg0 , deliveryMode , pty , ttl );

	}

	public void publish(Message arg0, int arg1, int arg2, long arg3)
			throws JMSException {
		testError ( arg0 , arg1 , arg2 , arg3 );

	}

	public void publish(Topic arg0, Message arg1) throws JMSException {
		testError ( arg1, deliveryMode , pty , ttl );

	}

	public void publish(Topic arg0, Message arg1, int arg2, int arg3, long arg4)
			throws JMSException {
		testError ( arg1 , arg2 , arg3 , arg4);

	}

	public void setDisableMessageID(boolean val) throws JMSException {
		this.disableMessageId = val;

	}

	public boolean getDisableMessageID() throws JMSException {
		return disableMessageId;
	}

	public void setDisableMessageTimestamp(boolean val) throws JMSException {
		this.disableMessageTimestamp = val;

	}

	public boolean getDisableMessageTimestamp() throws JMSException {
		return disableMessageTimestamp;
	}

	public void setDeliveryMode(int val) throws JMSException {
		this.deliveryMode = val;

	}

	public int getDeliveryMode() throws JMSException {
		return deliveryMode;
	}

	public void setPriority(int pty) throws JMSException {
		this.pty = pty;

	}

	public int getPriority() throws JMSException {
		return pty;
	}

	public void setTimeToLive(long ttl) throws JMSException {
		this.ttl =ttl;

	}

	public long getTimeToLive() throws JMSException {
		return ttl;
	}

	public Destination getDestination() throws JMSException {
		return topic;
	}

	public void close() throws JMSException {
		closeCalled = true;

	}
	
	public boolean closeCalled()
	{
		return closeCalled;
	}

	public void send(Message arg0) throws JMSException {
		
		testError ( arg0 , deliveryMode , pty , ttl );
	}

	public void send(Message arg0, int arg1, int arg2, long arg3)
			throws JMSException {
		testError ( arg0 , arg1 , arg2 , arg3 );
	}

	public void send(Destination arg0, Message arg1) throws JMSException {
		if ( getDestination() != null )
			throw new UnsupportedOperationException ( "ISSUE 10107" );
		testError ( arg1 , deliveryMode , pty , ttl );

	}

	public void send(Destination arg0, Message arg1, int arg2, int arg3,
			long arg4) throws JMSException {
		
		if ( getDestination() != null )
			throw new UnsupportedOperationException ( "ISSUE 10107" );
		testError ( arg1 , arg2 , arg3 ,arg4 );
	}

	public static void setErrorOnNextSend() 
	{
		error = true;
		
	}

}
