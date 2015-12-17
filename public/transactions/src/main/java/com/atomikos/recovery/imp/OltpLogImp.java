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
		assertEntryIsAllowedInCurrentState(coordinatorLogEntry);
		repository.put(coordinatorLogEntry.id, coordinatorLogEntry);
	}

	private void assertEntryIsAllowedInCurrentState(CoordinatorLogEntry coordinatorLogEntry) throws IllegalStateException, LogReadException {
		CoordinatorLogEntry existing = repository
				.get(coordinatorLogEntry.id);
		
		if (!coordinatorLogEntry.transitionAllowedFrom(existing)) {
			String existingState = "NONE";
			if (existing != null) {
				existingState = existing.getResultingState().toString();
			}
			throw new IllegalStateException("Existing entry: " + existingState + 
					" incompatible with new entry: " + coordinatorLogEntry.getResultingState());
		}
	}


	@Override
	public void close() {
		repository.close();
	}


}
