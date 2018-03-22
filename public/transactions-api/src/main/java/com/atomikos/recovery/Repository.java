/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import java.util.Collection;

 /**
  * Abstraction of where transaction log entries are stored and retrieved.
  */
public interface Repository {

	
	void init() throws LogException;
	/**
	 * @throws IllegalArgumentException If the same coordinatorLogEntry is already in the repository. 
	 */
	void put(String id,CoordinatorLogEntry coordinatorLogEntry) throws IllegalArgumentException,LogWriteException;
	
	CoordinatorLogEntry get(String coordinatorId) throws LogReadException;

	Collection<CoordinatorLogEntry> findAllCommittingCoordinatorLogEntries() throws LogReadException;
	
	Collection<CoordinatorLogEntry> getAllCoordinatorLogEntries() throws LogReadException;

	void writeCheckpoint(Collection<CoordinatorLogEntry> checkpointContent) throws LogWriteException;
	
	void close();
}
