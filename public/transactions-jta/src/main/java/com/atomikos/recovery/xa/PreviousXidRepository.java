/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
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
	 * Remembers the given XID for later.
	 * @param xidToStoreForNextScan
	 * @param expiration
	 */
	void remember(XID xidToStoreForNextScan, long expiration);

	void forgetXidsExpiredAt(long startOfRecoveryScan);

    boolean isEmpty();

}
