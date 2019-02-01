/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.event;


/**
 * Observer interface for transaction-related domain events. 
 * 
 * External applications/modules can implement this functionality to 
 * be notified of significant events. Implementations are registered
 * via the JDK 6+ ServiceLoader mechanism.
 * 
 * CAUTION: event notification is synchronous, so registering listeners
 * may impact performance of the core!
 */
public interface EventListener {

	void eventOccurred(Event event);
	
}