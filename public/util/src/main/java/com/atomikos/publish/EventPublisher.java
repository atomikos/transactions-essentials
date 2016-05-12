/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.publish;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import com.atomikos.icatch.event.Event;
import com.atomikos.icatch.event.EventListener;
import com.atomikos.icatch.event.transaction.TransactionHeuristicEvent;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class EventPublisher {
	
	private static Logger LOGGER = LoggerFactory.createLogger(EventPublisher.class);
	
	private static Set<EventListener> listeners = new HashSet<EventListener>();
	
	static {
		findAllEventListenersInClassPath();
	}
	
	private EventPublisher(){}
	
	private static void findAllEventListenersInClassPath() {
		ServiceLoader<EventListener> loader = ServiceLoader.load(EventListener.class,EventPublisher.class.getClassLoader());
		for (EventListener l : loader) {
			registerEventListener(l);
		}
	}

	public static void publish(Event event) {
		if (event != null) {
			notifyAllListeners(event);
		}
	}

	private static void notifyAllListeners(Event event) {
		if(event instanceof TransactionHeuristicEvent) {
			TransactionHeuristicEvent the = (TransactionHeuristicEvent)event;
			if (listeners.isEmpty()) {
				LOGGER.logError("Transaction " + the.transactionId + " corrupted - contact support@atomikos.com to get help");
			}
		}
		
		
		for(EventListener listener : listeners) {				
			try {
				listener.eventOccurred(event);
			} catch (Exception e) {
				LOGGER.logError("Error notifying listener " + listener, e);
			}
		}
	}

	/**
	 * Useful for testing only. Not safe for other use.
	 * 
	 * @param listener
	 */
	public static void registerEventListener(EventListener listener) {
		listeners.add(listener);
	}

}