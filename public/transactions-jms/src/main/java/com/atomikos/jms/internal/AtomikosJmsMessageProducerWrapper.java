/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms.internal;

import javax.jms.CompletionListener;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import com.atomikos.datasource.xa.session.SessionHandleState;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

class AtomikosJmsMessageProducerWrapper extends ConsumerProducerSupport implements MessageProducer {
    private static final Logger LOGGER = LoggerFactory.createLogger(AtomikosJmsMessageProducerWrapper.class);

    private MessageProducer delegate;

    AtomikosJmsMessageProducerWrapper(MessageProducer delegate, SessionHandleState state) {
        super(state);
        this.delegate = delegate;
    }

    public void send(Message msg) throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": send ( message )...");
        }
        enlist();
        delegate.send(msg);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": send done.");
        }
    }

    public void close() throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": close...");
        }
        delegate.close();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": close done.");
        }
    }

    public int getDeliveryMode() throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": getDeliveryMode()...");
        }
        int ret = delegate.getDeliveryMode();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": getDeliveryMode() returning " + ret);
        }
        return ret;
    }

    public Destination getDestination() throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": getDestination()...");
        }
        Destination ret = delegate.getDestination();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": getDestination() returning " + ret);
        }
        return ret;
    }

    public boolean getDisableMessageID() throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": getDisableMessageID()...");
        }
        boolean ret = delegate.getDisableMessageID();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": getDisableMessageID() returning " + ret);
        }
        return ret;
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": getDisableMessageTimestamp()...");
        }
        boolean ret = delegate.getDisableMessageTimestamp();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": getDisableMessageTimestamp() returning " + ret);
        }
        return ret;
    }

    public int getPriority() throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": getPriority()...");
        }
        int ret = delegate.getPriority();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": getPriority() returning " + ret);
        }
        return ret;
    }

    public long getTimeToLive() throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": getTimeToLive()...");
        }
        long ret = delegate.getTimeToLive();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": getTimeToLive() returning " + ret);
        }
        return ret;
    }

    public void send(Destination dest, Message msg) throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": send ( destination , message )...");
        }
        enlist();
        delegate.send(dest, msg);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": send done.");
        }
    }

    public void send(Message msg, int deliveryMode, int priority, long timeToLive) throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": send ( message , deliveryMode , priority , timeToLive )...");
        }
        enlist();
        delegate.send(msg, deliveryMode, priority, timeToLive);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": send done.");
        }
    }

    public void send(Destination dest, Message msg, int deliveryMode, int priority, long timeToLive)
            throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": send ( destination , message , deliveryMode , priority , timeToLive )...");
        }
        enlist();
        delegate.send(dest, msg, deliveryMode, priority, timeToLive);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": send done.");
        }
    }

    public void setDeliveryMode(int mode) throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": setDeliveryMode ( " + mode + " )...");
        }
        delegate.setDeliveryMode(mode);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": setDeliveryMode done.");
        }
    }

    public void setDisableMessageID(boolean mode) throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": setDisableMessageID ( " + mode + " )...");
        }
        delegate.setDisableMessageID(mode);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": setDisableMessageID done.");
        }
    }

    public void setDisableMessageTimestamp(boolean mode) throws JMSException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": setDisableMessageTimestamp ( " + mode + " )...");
        }
        delegate.setDisableMessageTimestamp(mode);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": setDisableMessageTimestamp done.");
        }
    }

    public void setPriority(int pty) throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": setPriority ( " + pty + " )...");
        }
        delegate.setPriority(pty);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": setPriority done.");
        }
    }

    public void setTimeToLive(long ttl) throws JMSException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.logDebug(this + ": setTimeToLive ( " + ttl + " )...");
        }
        delegate.setTimeToLive(ttl);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(this + ": setTimeToLive done.");
        }
    }

    public String toString() {
        return "atomikosJmsMessageProducerWrapper for " + delegate;
    }

	@Override
	public void setDeliveryDelay(long deliveryDelay) throws JMSException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug(this + ": setDeliveryDelay ( " + deliveryDelay + " )...");
		}
		delegate.setDeliveryDelay(deliveryDelay);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.logTrace(this + ": setDeliveryDelay done.");
		}
	}

	@Override
	public long getDeliveryDelay() throws JMSException {
		long ret = 0;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug(this + ": getDeliveryDelay()...");
		}
		ret = delegate.getDeliveryDelay();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.logTrace(this + ": getDeliveryDelay() returning " + ret);
		}
		return ret;
	}

	@Override
	public void send(Message message, CompletionListener completionListener) throws JMSException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug(this + ": send ( message , completionListener )..." );
		}
		enlist();
		delegate.send(message, completionListener);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.logTrace(this + ": send done.");
		}
	}

	@Override
	public void send(Message message, int deliveryMode, int priority, long timeToLive,
			CompletionListener completionListener) throws JMSException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug(this + ": send ( message , deliveryMode , priority , timeToLive , completionListener)...");
		}
		enlist();
		delegate.send(message, deliveryMode, priority, timeToLive, completionListener);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.logTrace(this + ": send done.");
		}
	}

	@Override
	public void send(Destination destination, Message message, CompletionListener completionListener)
			throws JMSException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug(this + ": send ( destination , message , completionListener ");
		}
		enlist();
		delegate.send(destination, message, completionListener);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.logTrace(this + ": send done.");
		}
	}

	@Override
	public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive,
			CompletionListener completionListener) throws JMSException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug(this + ": send ( destination , message , deliveryMode , priority , timeToLive , completionListener )...");
		}
		enlist();
		delegate.send(destination, message, deliveryMode, priority, timeToLive, completionListener);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.logTrace(this + ": send done.");
		}
	}

}
