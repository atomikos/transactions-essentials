/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.publish;

import java.util.HashSet;
import java.util.Set;

import com.atomikos.icatch.event.Event;
import com.atomikos.icatch.event.EventListener;
import com.atomikos.icatch.event.transaction.ParticipantHeuristicEvent;
import com.atomikos.icatch.event.transaction.TransactionHeuristicEvent;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public enum EventPublisher {
	INSTANCE;
	
	private static Logger LOGGER = LoggerFactory.createLogger(EventPublisher.class);
	
	private Set<EventListener> listeners = new HashSet<>();
	
	private boolean alreadyWarned = false;
	
	private EventPublisher(){}

	public void publish(Event event) {
		if (event != null) {
			notifyAllListeners(event);
		}
	}

	private void notifyAllListeners(Event event) {
	    warnIfNoListeners(event);				
		for (EventListener listener : listeners) {				
			try {
				listener.eventOccurred(event);
			} catch (Exception e) {
				LOGGER.logError("Error notifying listener " + listener, e);
			}
		}
	}

    private void warnIfNoListeners(Event event) {
        if (listeners.isEmpty()) {
	        if (!alreadyWarned) {
	            LOGGER.logWarning("No event listeners are configured - you may want to consider https://www.atomikos.com/Main/ExtremeTransactions for detailed monitoring functionality...");
	        }
	        if (logEvent(event)) {
	            LOGGER.logWarning(event.toString());
	        }
	        alreadyWarned = true;
	    }
    }

	private boolean logEvent(Event event) {
        return !alreadyWarned || event instanceof ParticipantHeuristicEvent || event instanceof TransactionHeuristicEvent;
    }

    /**
	 * For internal use only - listeners should register via the ServiceLoader mechanism.
	 * 
	 * @param listener
	 */
	public void registerEventListener(EventListener listener) {
	    LOGGER.logInfo("Registering EventListener: " + listener);
		listeners.add(listener);
	}

}