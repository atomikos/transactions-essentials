package com.atomikos.recovery;

import java.util.Collection;

public interface CoordinatorLogEntryRepository {

	/**
	 * @throws IllegalArgumentException If the same coordinatorLogEntry is already in the repository. 
	 */
	void put(String id,CoordinatorLogEntry coordinatorLogEntry) throws IllegalArgumentException;
	
	void remove(String id);

	CoordinatorLogEntry get(String coordinatorId);

	Collection<ParticipantLogEntry> findAllCommittingParticipants();
}
