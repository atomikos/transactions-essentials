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

package com.atomikos.icatch.imp.thread;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;


import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.ClassLoadingHelper;

/**
 * This singleton manages system executors for several components.
 * This class will check the runtime classes
 * for the 1.5 java.util.concurrent package, if failing that it will look for the 1.4 backport, and
 * failing that revert to a "two threads per transaction" strategy.
 * 
 * @author Lars J. Nilsson
 */

public class TaskManager 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TaskManager.class);

	private static TaskManager singleton;
	
	private InternalSystemExecutor executor;
	

	
	/**
	 * Gets the singleton instance. 
	 * 
	 * @return
	 */
	public static synchronized final TaskManager getInstance() 
	{
		if ( singleton == null ) {
			if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "TaskManager: initializing..." );
			singleton = new TaskManager();
		}
		return singleton;
	}

	protected TaskManager ()
	{		
			init();
	}

	private void init() 
	{
		ExecutorFactory creator;
		try {
			if ( isClassAvailable ( Java15ExecutorFactory.MAIN_CLASS ) ) {
				if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( "THREADS: using JDK thread pooling..." );
				creator = new Java15ExecutorFactory();
			}
			else if ( isClassAvailable ( Java14BackportExecutorFactory.MAIN_CLASS ) ) {
				if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( "THREADS: using 1.4 (backport) thread pooling..." );
				creator = new Java14BackportExecutorFactory();
			}
			else {
				Configuration.logWarning ( "THREADS: pooling NOT enabled!" );
				creator = new TrivialExecutorFactory();
			}
		} catch(Exception e) {
			Configuration.logWarning ( "THREADS: Illegal setup, thread pooling is NOT enabled!", e);
			creator = new TrivialExecutorFactory();
		}
		
		try {
			executor = creator.createExecutor();
		} catch (Exception e) {
			Configuration.logWarning("Failed to create system executor; Received message: " + e.getMessage() + "; Failling back to a trivial executor.", e);
			executor = new TrivialSystemExecutor();
		}
		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "THREADS: using executor " + executor.getClass());
	}
	
	/**
	 * Notification of shutdown to close all pooled threads.
	 *
	 */
	public synchronized void shutdown()
	{
		if (executor != null) {
			executor.shutdown();
			executor = null;
		}
	}
	
	/**
	 * Schedules a task for execution by a thread.
	 * 
	 * @param task
	 */
	public void executeTask ( Runnable task ) 
	{
		if ( executor == null ) {
			//happens on restart of TS within same VM
			init();
		}
		executor.execute ( task );
	}
	
	private boolean isClassAvailable(String clazz)
	{
		try {
			ClassLoadingHelper.loadClass(clazz);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
