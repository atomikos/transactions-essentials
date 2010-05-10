package com.atomikos.icatch.imp.thread;


/**
 * 
 * This is a system replacement interface for the java1.5 Executor
 * interface, it is used for backporting thread pools.
 * 
 * @author Lars J. Nilsson
 */
interface InternalSystemExecutor 
{

	/**
	 * @param task Task to execute, never null
	 */
	
	public void execute ( Runnable task );
	
	/**
	 * Shutdown underlying thread pool.
	 */
	public void shutdown();
	
}
