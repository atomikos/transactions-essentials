package com.atomikos.recovery.imp;

import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.OltpLog;

public class OltpLogImp implements OltpLog {


	private CoordinatorLogEntryRepository repository;

	public void setRepository(CoordinatorLogEntryRepository repository) {
		this.repository = repository;
	}

	
	@Override
	public void write(CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalStateException, LogException {
		if (!entryAllowed(coordinatorLogEntry)) {
			throw new IllegalStateException();
		}
		repository.put(coordinatorLogEntry.coordinatorId, coordinatorLogEntry);
	}

	private boolean entryAllowed(CoordinatorLogEntry coordinatorLogEntry) throws LogReadException {
		CoordinatorLogEntry existing = repository
				.get(coordinatorLogEntry.coordinatorId);
		
		return coordinatorLogEntry.transitionAllowedFrom(existing);
	}


	@Override
	public void close() {
		repository.close();
	}


}
