/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.pool;


public interface XPooledConnectionEventListener 
{
	
	/**
	 * fired when a connection changed its state to terminated 
	 * @param connection
	 */
	void onXPooledConnectionTerminated(XPooledConnection connection);

}
