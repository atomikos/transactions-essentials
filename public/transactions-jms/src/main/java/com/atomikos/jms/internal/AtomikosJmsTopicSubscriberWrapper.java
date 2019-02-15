/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

class AtomikosJmsTopicSubscriberWrapper extends AtomikosJmsMessageConsumerWrapper implements TopicSubscriber {
    private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsTopicSubscriberWrapper.class);

    public AtomikosJmsTopicSubscriberWrapper(TopicSubscriber delegate, SessionHandleState state) {
        super(delegate, state);

    }

    private TopicSubscriber getDelegateTopicSubscriber() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": getDelegateTopicSubscriber()...");
        }
        TopicSubscriber ret = (TopicSubscriber) getDelegate();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": getDelegateTopicSubscriber() returning " + ret);
        }
        return ret;
    }

    public boolean getNoLocal() throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": getNoLocal()...");
        }
        boolean ret = false;
        try {
            ret = getDelegateTopicSubscriber().getNoLocal();
        } catch (Exception e) {
            handleException(e);
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": getNoLocal() returning " + ret);
        }
        return ret;
    }

    public Topic getTopic() throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": getTopic()...");
        }
        Topic ret = null;
        try {
            ret = getDelegateTopicSubscriber().getTopic();
        } catch (Exception e) {
            handleException(e);
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": getTopic() returning " + ret);
        }
        return ret;
    }

    public String toString() {
        return "atomikosJmsTopicSubscriberWrapper for " + getDelegate();
    }

}
