/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

 /**
  * Marker interface for system components whose order of init
  * and close is important for correct behavior of the 
  * transaction system. The knowledge of ordering is supposed to 
  * be present elsewhere - in the system configuration.
  */

public interface OrderedLifecycleComponent {

	/**
	 * 
	 * @throws Exception Implementations are free to narrow the exception
	 * or even not throw anything.
	 */
	
	void init() throws Exception;
	
	/**
	 * 
	 * @throws Exception Implementations are free to narrow the exception
	 * or even not throw anything.
	 */
	
	void close() throws Exception;
}
