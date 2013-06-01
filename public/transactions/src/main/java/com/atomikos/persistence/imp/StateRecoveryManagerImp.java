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

package com.atomikos.persistence.imp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.ObjectLog;
import com.atomikos.persistence.Utils;
import com.atomikos.util.ClassLoadingHelper;

/**
 * Default implementation of a state recovery manager.
 */

public class StateRecoveryManagerImp extends AbstractStateRecoveryManager
{

	private static final String WRITE_AHEAD_OBJECT_LOG_CLASSNAME = "com.atomikos.persistence.imp.WriteAheadObjectLog";
	private static final Logger LOGGER = LoggerFactory.createLogger(StateRecoveryManagerImp.class);
	public void init(Properties p) throws LogException {
		long chckpt = Long.valueOf( Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME, p ) );

        String logdir = Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME, p );
        String logname = Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME, p );
        logdir = Utils.findOrCreateFolder ( logdir );
        
        boolean serializableLogging= "true".equals(Utils.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.SERIALIZABLE_LOGGING_PROPERTY_NAME, p ));
        
        LogStream logstream=null;	
		try {
			if (serializableLogging) {
				  logstream = new FileLogStream ( logdir, logname );
			} else {
				  logstream = new com.atomikos.persistence.dataserializable.FileLogStream ( logdir, logname );
		    }
			
			objectlog_ = new StreamObjectLog ( logstream, chckpt );
			
			try {
				ObjectLog objectLog = createWriteAheadObjectLogIfAvailableOnClasspath(objectlog_);
				objectlog_ = objectLog;
			} catch (Exception writeAheadObjectLogInstantiationFailed) {
				LOGGER.logInfo(WRITE_AHEAD_OBJECT_LOG_CLASSNAME+" instantiation failed - falling back to default");
			}
			
			objectlog_.init();
		} catch (IOException e) {
			throw new LogException(e.getMessage(), e);
		}
		
	}
	private ObjectLog createWriteAheadObjectLogIfAvailableOnClasspath(ObjectLog normalObjectLog)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		Class<ObjectLog>  theClass =  ClassLoadingHelper.loadClass(WRITE_AHEAD_OBJECT_LOG_CLASSNAME);
		ObjectLog objectLog = theClass.newInstance();
		Method delegateMethod = theClass.getMethod("setDelegate", AbstractObjectLog.class);
		delegateMethod.invoke(objectLog, normalObjectLog);
		LOGGER.logInfo("Instantiated write-ahead logging - this constitutes a license violation if you are not a paying customer!");
		return objectLog;
	}

	

}
