/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa.session;


public interface SessionHandleStateChangeListener {
	
	/**
	 * fired when all contexts of the SessionHandleState changed to terminated state
	 */
	void onTerminated();

}
