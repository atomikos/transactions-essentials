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
