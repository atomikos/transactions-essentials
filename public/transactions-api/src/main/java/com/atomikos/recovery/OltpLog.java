/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import com.atomikos.icatch.CoordinatorLogEntry;

public interface OltpLog {

	void write(CoordinatorLogEntry coordinatorLogEntry) throws LogException,IllegalStateException;

	void close();
}
