package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Topic;

public class TestTopic implements Topic 
{
	private static final String NAME = "TEST_TOPIC_NAME";

	public String getTopicName() throws JMSException 
	{
		return NAME;
	}
	
	public String toString()
	{
		return NAME;
	}

}
