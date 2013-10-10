package com.atomikos.icatch.publish;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.atomikos.icatch.event.EventListener;
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
		// TODO Auto-generated method stub
		
	}

	public static void publish(Serializable event) {
		if (event != null) {
			notifyAllListeners(event);
		}
	}

	private static void notifyAllListeners(Serializable event) {
		for(EventListener listener : listeners) {				
			try {
				listener.eventOccurred(event);
			} catch (Exception e) {
				LOGGER.logWarning("Error notifying listener", e);
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