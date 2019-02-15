/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery.xa;

import java.util.List;

import com.atomikos.datasource.xa.XID;

public interface PreviousXidRepository {

	List<XID> findXidsExpiredAt(long startOfRecoveryScan);

	/**
	 * Remembers the given Xids for later. Xids that are already known are ignored.
	 * @param xidsToStoreForNextScan
	 * @param expiration
	 */
	void remember(List<XID> xidsToStoreForNextScan, long expiration);

	void forgetXidsExpiredAt(long startOfRecoveryScan);

}
