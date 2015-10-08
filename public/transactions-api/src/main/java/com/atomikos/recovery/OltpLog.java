package com.atomikos.recovery;

public interface OltpLog {

	void write(CoordinatorLogEntry coordinatorLogEntry) throws OltpLogException,IllegalStateException;
	
	void remove(String coordinatorId) throws OltpLogException;
}
