package com.atomikos.icatch.imp.thread;

import com.atomikos.icatch.system.Configuration;

/**
 * This is an executor which creates a new thread for each
 * invocation. It uses the SystemThreadFactorySPI for the task.
 */
public class TrivialSystemExecutor implements InternalSystemExecutor
{
	
	public TrivialSystemExecutor()
	{
	}
	
	public void execute(Runnable targ)
	{
		Configuration.logDebug("(T) executing task: " + targ);
		Thread newThread = ThreadFactory.getInstance().newThread(targ);
		newThread.start();
	}
	
	public void shutdown()
	{
	}

}