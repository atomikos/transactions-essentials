package com.atomikos.persistence.imp;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.imp.CoordinatorImp;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.RecoverableCoordinator;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.recovery.ParticipantLogEntry;
import com.atomikos.recovery.RecoveryException;
import com.atomikos.recovery.RecoveryLog;

public class RecoveryLogImp implements RecoveryLog {
	
	private StateRecoveryManager srm;
	private Vector<RecoverableCoordinator<TxState>> recoveredCoordinators;
	
	public RecoveryLogImp(StateRecoveryManager srm) {
		this.srm = srm;
	}

	@Override
	public void terminated(ParticipantLogEntry entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminatedWithHeuristicRollback(ParticipantLogEntry entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<ParticipantLogEntry> getCommittingParticipants() throws RecoveryException {
		Set<ParticipantLogEntry> ret = new HashSet<ParticipantLogEntry>();
		try {
			recoveredCoordinators = srm.recover();
		} catch (LogException e) {
			throw new RecoveryException(e);
		}
		for (RecoverableCoordinator rc : recoveredCoordinators) {
			CoordinatorImp c = (CoordinatorImp) rc;
			ret.addAll(c.getParticipantLogEntries());
		}
		return ret;
	}

	@Override
	public void presumedAborting(ParticipantLogEntry entry)
			throws IllegalStateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminatedWithHeuristicCommit(ParticipantLogEntry entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminatedWithHeuristicHazard(ParticipantLogEntry entry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void terminatedWithHeuristicMixed(ParticipantLogEntry entry) {
		// TODO Auto-generated method stub

	}

}
