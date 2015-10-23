package com.atomikos.recovery;

import java.util.Collection;
import java.util.Properties;

public interface CoordinatorLogEntryRepository {

	
	void init(Properties properties);
	/**
	 * @throws IllegalArgumentException If the same coordinatorLogEntry is already in the repository. 
	 */
	void put(String id,CoordinatorLogEntry coordinatorLogEntry) throws IllegalArgumentException,LogWriteException;
	
	void remove(String id);

	CoordinatorLogEntry get(String coordinatorId);

	Collection<ParticipantLogEntry> findAllCommittingParticipants();
	
	void close();
}
