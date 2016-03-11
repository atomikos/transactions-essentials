/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import com.atomikos.icatch.CoordinatorLogEntry;

 /**
  * Handle to the transactions log for admin purposes.
  */
public interface AdminLog {

	CoordinatorLogEntry[] getCoordinatorLogEntries();

	void remove(String coordinatorId);

}
