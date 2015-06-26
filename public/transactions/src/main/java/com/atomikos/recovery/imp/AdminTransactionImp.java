package com.atomikos.recovery.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.recovery.CoordinatorLogEntry;

class AdminTransactionImp implements AdminTransaction {

	private final CoordinatorLogEntry coordinatorLogEntry;

	public AdminTransactionImp(CoordinatorLogEntry coordinatorLogEntry) {
		this.coordinatorLogEntry = coordinatorLogEntry;
	}

	@Override
	public String getTid() {
		return coordinatorLogEntry.coordinatorId;
	}

	@Override
	public int getState() {
		switch (coordinatorLogEntry.state) {
		case COMMITTING:
			return STATE_COMMITTING;
		case HEUR_COMMITTED:
			return STATE_HEUR_COMMITTED;
		case HEUR_ABORTED:
			return STATE_HEUR_ABORTED;
		case HEUR_HAZARD:
			return STATE_HEUR_HAZARD;
		case HEUR_MIXED:
			return STATE_HEUR_MIXED;
		default:
			return STATE_UNKNOWN;
		}

	}

	@Override
	public boolean wasCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void forceCommit() throws HeurRollbackException,
			HeurHazardException, HeurMixedException, SysException {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceRollback() throws HeurCommitException, HeurMixedException,
			HeurHazardException, SysException {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceForget() {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getParticipantDetails() {
		// TODO Auto-generated method stub
		return null;
	}

}
