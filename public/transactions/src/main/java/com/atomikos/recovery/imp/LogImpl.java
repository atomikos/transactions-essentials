package com.atomikos.recovery.imp;

import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.CoordinatorLogEntryRepository;
import com.atomikos.recovery.OltpLog;
import com.atomikos.recovery.OltpLogException;

public class LogImpl implements OltpLog {

	private CoordinatorLogEntryRepository repository;
	

	public void setRepository(CoordinatorLogEntryRepository repository) {
		this.repository = repository;
	}

	@Override
	public void write(CoordinatorLogEntry coordinatorLogEntry)
			throws IllegalStateException {
		if (entryAllowed(coordinatorLogEntry)) {
			repository.put(coordinatorLogEntry.coordinatorId,
					coordinatorLogEntry);
		} else {
			throw new IllegalStateException();
		}

	}

	private boolean entryAllowed(CoordinatorLogEntry coordinatorLogEntry) {
		CoordinatorLogEntry existing = repository.get(coordinatorLogEntry.coordinatorId);
		return coordinatorLogEntry.transitionAllowedFrom(existing);
	}

	@Override
	public void remove(String coordinatorId) throws OltpLogException {
		repository.remove(coordinatorId);
	}

}
