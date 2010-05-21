package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.icatch.system.Configuration;

class AtomikosJmsTopicSubscriberProxy extends AtomikosJmsMessageConsumerProxy
		implements TopicSubscriber {

	public AtomikosJmsTopicSubscriberProxy ( TopicSubscriber delegate,
			SessionHandleState state ) {
		super ( delegate , state );
		
	}
	
	private TopicSubscriber getDelegateTopicSubscriber()
	{
		Configuration.logInfo ( this + ": getDelegateTopicSubscriber()..." );
		TopicSubscriber ret =  ( TopicSubscriber ) getDelegate();
		Configuration.logDebug ( this + ": getDelegateTopicSubscriber() returning " + ret );
		return ret;
	}

	public boolean getNoLocal() throws JMSException 
	{
		Configuration.logInfo ( this + ": getNoLocal()..." );
		boolean ret = false;
		try {
			ret = getDelegateTopicSubscriber().getNoLocal();
		} catch (Exception e) {
			handleException(e);
		}
		Configuration.logDebug ( this + ": getNoLocal() returning " + ret );
		return ret;
	}

	public Topic getTopic() throws JMSException 
	{
		Configuration.logInfo ( this + ": getTopic()..." );
		Topic ret = null;
		try {
			ret = getDelegateTopicSubscriber().getTopic();
		} catch (Exception e) {
			handleException ( e );
		}
		Configuration.logDebug (  this + ": getTopic() returning " + ret );
		return ret;
	}
	
	public String toString() 
	{
		return "atomikos TopicSubscriber proxy for " + getDelegate();
	}

}
