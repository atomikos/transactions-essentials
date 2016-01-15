package com.atomikos.recovery;

import com.atomikos.icatch.CoordinatorLogEntry;

public interface OltpLog {

	void write(CoordinatorLogEntry coordinatorLogEntry) throws LogException,IllegalStateException;

	void close();
}
