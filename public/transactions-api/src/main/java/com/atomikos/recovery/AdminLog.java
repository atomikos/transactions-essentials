package com.atomikos.recovery;

public interface AdminLog {

	CoordinatorLogEntry[] getCoordinatorLogEntries();

}
