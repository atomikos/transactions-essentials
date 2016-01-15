package com.atomikos.persistence.imp;

import java.util.Vector;

import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.imp.CoordinatorImp;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.RecoverableCoordinator;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.recovery.AdminLog;
import com.atomikos.recovery.LogException;

public class AdminLogImp implements AdminLog {
	
	private static final Logger LOGGER = LoggerFactory.createLogger(AdminLogImp.class);
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
		}
		return ret;
	}

	@Override
	public void remove(String coordinatorId) {
		try {
			srm.delete(coordinatorId);
		} catch (LogException e) {
			LOGGER.logWarning("Failed to delete coordinator "+ coordinatorId,e);
		}
	}

}
