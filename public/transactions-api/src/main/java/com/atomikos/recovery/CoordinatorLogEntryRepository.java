package com.atomikos.recovery;

public interface CoordinatorLogEntryRepository {

	void put(String id,CoordinatorLogEntry coordinatorLogEntry);
	
	void remove(String id);

	CoordinatorLogEntry get(String coordinatorId);

}
