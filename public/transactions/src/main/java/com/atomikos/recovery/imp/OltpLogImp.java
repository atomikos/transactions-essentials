/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.recovery.imp;

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.recovery.Repository;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.LogReadException;
import com.atomikos.recovery.OltpLog;

public class OltpLogImp implements OltpLog {


	private Repository repository;

	public void setRepository(Repository repository) {
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
