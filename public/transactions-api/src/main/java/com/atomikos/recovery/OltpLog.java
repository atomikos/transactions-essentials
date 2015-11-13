package com.atomikos.recovery;

public interface OltpLog {

	void write(CoordinatorLogEntry coordinatorLogEntry) throws LogWriteException,IllegalStateException;
}
