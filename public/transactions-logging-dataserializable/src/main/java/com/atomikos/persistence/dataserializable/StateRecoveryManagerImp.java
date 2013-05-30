/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

package com.atomikos.persistence.dataserializable;

import java.io.IOException;
import java.util.Properties;

import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.Utils;
import com.atomikos.persistence.imp.AbstractStateRecoveryManager;
import com.atomikos.persistence.imp.StreamObjectLog;

/**
 * Default implementation of a state recovery manager.
 */

public class StateRecoveryManagerImp extends AbstractStateRecoveryManager 
{

	public void init(Properties p) throws LogException {
        long chckpt = Long.valueOf( Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME, p ) );

        String logdir = Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME, p );
        String logname = Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME, p );
        logdir = Utils.findOrCreateFolder ( logdir );
		LogStream logstream;
		try {
			logstream = new FileLogStream ( logdir, logname );
			StreamObjectLog streamObjectLog = new StreamObjectLog ( logstream, chckpt );
			objectlog_=new VeryFastObjectLog(streamObjectLog);
			objectlog_.init();
		} catch (IOException e) {
			throw new LogException(e.getMessage(), e);
		}
	}

	
	public int getOrder() {
		return 10;
	}

}
