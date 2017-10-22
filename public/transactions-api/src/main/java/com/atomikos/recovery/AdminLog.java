/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;


 /**
  * Handle to the transactions log for admin purposes.
  */
public interface AdminLog {

	CoordinatorLogEntry[] getCoordinatorLogEntries();

	void remove(String coordinatorId);

}
