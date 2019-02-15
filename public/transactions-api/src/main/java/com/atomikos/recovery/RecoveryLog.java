/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import java.util.Collection;

 /**
  * Handle to the transaction logs for recovery purposes.
  */

public interface RecoveryLog {

	void close(long timeout);

	public void forgetCommittingCoordinatorsExpiredSince(long expiry);

	Collection<PendingTransactionRecord> getExpiredPendingTransactionRecordsAt(long time) throws LogReadException;

	void forgetIndoubtCoordinatorsExpiredSince(long momentInThePast);
	
}
