/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;


 /**
  * Non-default OLTP logging can be enabled by registering an instance
  * of this interface via the ServiceLoader mechanism of the JDK.
  * At most one instance is allowed. If none is found, then default logging will be used.
  */

public interface OltpLogFactory {

	/**
	 * @param properties The init properties picked up .
	 */
	public OltpLog createOltpLog();
	
}
