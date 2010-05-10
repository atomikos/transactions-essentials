package com.atomikos.icatch.imp.thread;

/**
 * A simple way of interacting with the different versions
 * of thread pool instances we support.
 * 
 * @author Lars J. Nilsson
 */
interface ExecutorFactory 
{

	

	/**
	 * @return A new executor, which may, or may not, be pooled
	 * @throws Exception
	 */
	InternalSystemExecutor createExecutor() throws Exception;
	
}
