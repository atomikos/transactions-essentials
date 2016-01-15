package com.atomikos.recovery;

import com.atomikos.icatch.CoordinatorLogEntry;

public interface AdminLog {

	CoordinatorLogEntry[] getCoordinatorLogEntries();

	void remove(String coordinatorId);

}
