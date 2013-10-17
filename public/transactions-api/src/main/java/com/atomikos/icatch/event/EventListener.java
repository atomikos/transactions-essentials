package com.atomikos.icatch.event;


/**
 * Observer interface for transaction-related domain events. 
 * 
 * External applications/modules can implement this functionality to 
 * be notified of significant events. Implementations are registered
 * via the JDK 6+ ServiceLoader mechanism.
 */
public interface EventListener {

	void eventOccurred(Event event);
	
}