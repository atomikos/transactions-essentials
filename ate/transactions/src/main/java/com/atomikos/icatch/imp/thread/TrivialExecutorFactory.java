package com.atomikos.icatch.imp.thread;

/**
 * This creator exclusively creates trivial executors, ie. the "new thread"
 * strategy without pooling.
 * 
 * @author Lars J. Nilsson
 */

class TrivialExecutorFactory implements ExecutorFactory 
{

	public InternalSystemExecutor createExecutor() throws Exception
	{
		return new TrivialSystemExecutor();
	}
}
