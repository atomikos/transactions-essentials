package com.atomikos.persistence.imp;

import java.util.Vector;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.imp.CoordinatorImp;
import com.atomikos.persistence.RecoverableCoordinator;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.recovery.AdminLog;
import com.atomikos.recovery.CoordinatorLogEntry;
import com.atomikos.recovery.LogException;

public class AdminLogImp implements AdminLog {
	
	private StateRecoveryManager srm;
	
	public AdminLogImp(StateRecoveryManager srm) {
		this.srm = srm;
	}

	@Override
	public CoordinatorLogEntry[] getCoordinatorLogEntries() {
		CoordinatorLogEntry[] ret = new CoordinatorLogEntry[0];
		try {
			Vector<RecoverableCoordinator<TxState>> coordinators = srm.recover();
			ret = new CoordinatorLogEntry[coordinators.size()];
			int i = 0;
			for (RecoverableCoordinator<TxState> rc : coordinators) {
				CoordinatorImp c = (CoordinatorImp) rc;
				ret[i] = c.getCoordinatorLogEntry();
				i++;
			}
		} catch (LogException couldNotRetrieveCoordinators) {
			//TODO log
		}
		return ret;
	}

	@Override
	public void remove(String coordinatorId) {
		try {
			srm.delete(coordinatorId);
		} catch (LogException e) {
			//TODO log
		}
	}

}
