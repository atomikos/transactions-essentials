/**
 * Copyright (C) 2000-2015 Atomikos <info@atomikos.com>
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

package com.atomikos.persistence.imp;

import java.util.Properties;
import java.util.Vector;

import com.atomikos.finitestates.FSMEnterEvent;
import com.atomikos.finitestates.FSMPreEnterListener;
import com.atomikos.icatch.CoordinatorLogEntry;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.persistence.RecoverableCoordinator;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.recovery.LogException;
import com.atomikos.recovery.OltpLog;
import com.atomikos.util.Assert;

/**
 * Default implementation of a state recovery manager.
 */

public class StateRecoveryManagerImp  implements StateRecoveryManager, FSMPreEnterListener<TxState>
{

	private static final String CHECKPOINT_INTERVAL_PROPERTY_NAME = "com.atomikos.icatch.checkpoint_interval";
	private static final String LOG_BASE_DIR_PROPERTY_NAME = "com.atomikos.icatch.log_base_dir";
	private static final String LOG_BASE_NAME_PROPERTY_NAME = "com.atomikos.icatch.log_base_name";
	
	private LogFileLock lock_;


	/**
	 * @see StateRecoveryManager
	 */
	public void register(RecoverableCoordinator<TxState> staterecoverable) {
		Assert.notNull("illegal attempt to register null staterecoverable", staterecoverable);
		TxState[] states = TxState.values();
		for (TxState txState : states) {
			if (txState.isRecoverableState() || txState.isFinalState()) {
				staterecoverable.addFSMPreEnterListener(this, txState);
			}
		}	
	}

	/**
	 * @see FSMPreEnterListener
	 */
	public void preEnter(FSMEnterEvent<TxState> event) throws IllegalStateException {
		TxState state = event.getState();
		
		RecoverableCoordinator<TxState> source = (RecoverableCoordinator<TxState>) event.getSource();
		CoordinatorLogEntry coordinatorLogEntry=source.getCoordinatorLogEntry(state);
		if (coordinatorLogEntry != null) {
			// null images are not logged as per the Recoverable contract
			try {
				oltpLog.write(coordinatorLogEntry);

			} catch (LogException le) {
				le.printStackTrace();
				throw new IllegalStateException("could not flush state image " + le.getMessage() + " " + le.getClass().getName());
			}
		}

	}

	/**
	 * @see StateRecoveryManager
	 */
	public void close() throws LogException {
		oltpLog.close();
		lock_.releaseLock();
	}

	/**
	 * @deprecated obsolete by new recovery.
	 * @see StateRecoveryManager
	 */
	public RecoverableCoordinator<TxState> recover(Object id) throws LogException {
		
		return null;
	}

	/**
	 * @deprecated obsolete by new recovery.
	 * @see StateRecoveryManager
	 */
	public Vector<RecoverableCoordinator<TxState>> recover() throws LogException {
		Vector<RecoverableCoordinator<TxState>> ret = new Vector<RecoverableCoordinator<TxState>>();
		return ret;
	}

	/**
	 * @deprecated obsolete by new recovery.
	 * @see StateRecoveryManager
	 */
	public void delete(Object id) throws LogException {
	}

	
	
	public void init(Properties p) throws LogException {
		ConfigProperties configProperties = new ConfigProperties(p);
        String logdir = configProperties.getLogBaseDir();
        String logname = configProperties.getLogBaseName();
        logdir = Utils.findOrCreateFolder ( logdir );
        
        lock_ = new LogFileLock(logdir, logname);
        lock_.acquireLock();
		
	}
	
	
	private OltpLog oltpLog;
	
	public void setOltpLog(OltpLog oltpLog) {
		this.oltpLog = oltpLog;
	}
	

	

}
