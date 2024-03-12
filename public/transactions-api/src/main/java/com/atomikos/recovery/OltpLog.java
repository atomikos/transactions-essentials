/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
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

	void write(PendingTransactionRecord pendingTransactionRecord) throws LogWriteException;
	
	void close();
}
