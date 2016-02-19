/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

package com.atomikos.publish;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import com.atomikos.icatch.event.Event;
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
		for(EventListener listener : listeners) {				
			try {
				listener.eventOccurred(event);
			} catch (Exception e) {
				LOGGER.logWarning("Error notifying listener " + listener, e);
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