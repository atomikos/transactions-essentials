package com.atomikos.icatch.imp.thread;


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
			Configuration.logDebug ( "TaskManager: initializing..." );
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
				Configuration.logInfo ( "THREADS: using JDK thread pooling..." );
				creator = new Java15ExecutorFactory();
			}
			else if ( isClassAvailable ( Java14BackportExecutorFactory.MAIN_CLASS ) ) {
				Configuration.logInfo ( "THREADS: using 1.4 (backport) thread pooling..." );
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
		Configuration.logDebug ( "THREADS: using executor " + executor.getClass());
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
