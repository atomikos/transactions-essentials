package com.atomikos.recovery.imp;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.admin.AdminTransaction;

class AdminTransactionImp implements AdminTransaction {

	private final String tid;

	public AdminTransactionImp(String tid) {
		this.tid = tid;
	}

	@Override
	public String getTid() {
		return tid;
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
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
