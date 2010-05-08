package com.atomikos.icatch.imp.thread;

import com.atomikos.icatch.system.Configuration;

/**
 * 
 * A singleton factory for creating new threads.
 * 
 * @author guy
 *
 */
final class ThreadFactory 
{
	
	static final ThreadFactory singleton = new ThreadFactory ( "Atomikos" );
	
	static ThreadFactory getInstance() 
	{
		return singleton;
	}
	
	private final String name;
	private int count;
	private final ThreadGroup group;
	
	private ThreadFactory ( String threadBaseName )
	{
		SecurityManager sm = System.getSecurityManager();
        group = ( sm != null ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup() );
		this.name = threadBaseName;
	}
	
	String getBaseName() 
	{
		return name;
	}
	
	Thread newThread ( Runnable r ) 
	{
		String realName = name + ":" + incCount();
		Configuration.logDebug ( "ThreadFactory: creating new thread: " + realName );
		Thread thread = new Thread ( group , r , realName );
		thread.setContextClassLoader( Thread.currentThread().getContextClassLoader() );
		thread.setDaemon ( true );
		return thread;
	}

	private synchronized int incCount()
	{
		return count++;
	}

}
