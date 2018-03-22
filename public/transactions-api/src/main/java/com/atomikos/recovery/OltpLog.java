/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;


 /**
  * Handle to the transaction logs for writing during transaction processing.
  */
public interface OltpLog {

	void write(CoordinatorLogEntry coordinatorLogEntry) throws LogException,IllegalStateException;

	void close();
}
