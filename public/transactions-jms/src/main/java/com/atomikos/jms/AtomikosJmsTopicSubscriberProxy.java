/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.jms;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

class AtomikosJmsTopicSubscriberProxy extends AtomikosJmsMessageConsumerProxy
		implements TopicSubscriber {
	private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsTopicSubscriberProxy.class);

	public AtomikosJmsTopicSubscriberProxy ( TopicSubscriber delegate,
			SessionHandleState state ) {
		super ( delegate , state );
		
	}
	
	private TopicSubscriber getDelegateTopicSubscriber()
	{
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": getDelegateTopicSubscriber()..." );
		TopicSubscriber ret =  ( TopicSubscriber ) getDelegate();
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getDelegateTopicSubscriber() returning " + ret );
		return ret;
	}

	public boolean getNoLocal() throws JMSException 
	{
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": getNoLocal()..." );
		boolean ret = false;
		try {
			ret = getDelegateTopicSubscriber().getNoLocal();
		} catch (Exception e) {
			handleException(e);
		}
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this + ": getNoLocal() returning " + ret );
		return ret;
	}

	public Topic getTopic() throws JMSException 
	{
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( this + ": getTopic()..." );
		Topic ret = null;
		try {
			ret = getDelegateTopicSubscriber().getTopic();
		} catch (Exception e) {
			handleException ( e );
		}
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug (  this + ": getTopic() returning " + ret );
		return ret;
	}
	
	public String toString() 
	{
		return "atomikos TopicSubscriber proxy for " + getDelegate();
	}

}
