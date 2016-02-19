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

package com.atomikos.icatch.admin.imp;

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.ParticipantLogEntry;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.recovery.AdminLog;

class AdminTransactionImp implements AdminTransaction {

	private final CoordinatorLogEntry coordinatorLogEntry;
	private AdminLog adminLog;

	public AdminTransactionImp(CoordinatorLogEntry coordinatorLogEntry, AdminLog adminLog) {
		this.coordinatorLogEntry = coordinatorLogEntry;
		this.adminLog = adminLog;
	}

	@Override
	public String getTid() {
		return coordinatorLogEntry.id;
	}

	@Override
	public TxState getState() {
		return coordinatorLogEntry.getResultingState();
	}

	@Override
	public boolean wasCommitted() {
		return coordinatorLogEntry.wasCommitted;
	}

	@Override
	public void forceCommit() throws HeurRollbackException,
			HeurHazardException, HeurMixedException, SysException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forceRollback() throws HeurCommitException, HeurMixedException,
			HeurHazardException, SysException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void forceForget() {
		adminLog.remove(coordinatorLogEntry.id);
	}

	@Override
	public String[] getParticipantDetails() {
		String[] ret = new String[coordinatorLogEntry.participants.length];
		int i = 0;
		for (ParticipantLogEntry ple : coordinatorLogEntry.participants) {
			ret[i] = ple.toString();
			i++;
		}
		return ret;
	}

	@Override
	public boolean hasExpired() {
		return coordinatorLogEntry.hasExpired();
	}

}
