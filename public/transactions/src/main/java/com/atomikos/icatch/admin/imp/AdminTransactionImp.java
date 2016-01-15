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