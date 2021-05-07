/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.thread;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * Scheduling logic for tasks/threads.
 */

public enum TaskManager {
	SINGLETON;
	
	private static final Logger LOGGER = LoggerFactory.createLogger(TaskManager.class);
	
	private ThreadPoolExecutor executor;
	

	private synchronized ThreadPoolExecutor init() {
		if (executor==null) {
			SynchronousQueue<Runnable> synchronousQueue = new SynchronousQueue<Runnable>();
			executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, new Long(60L),
					TimeUnit.SECONDS, synchronousQueue, new AtomikosThreadFactory());
		}
		return executor;
	}

	/**
	 * Notification of shutdown to close all pooled threads.
	 * 
	 */
	public synchronized void shutdown() {
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
	public void executeTask(Runnable task) {
		ThreadPoolExecutor executor = this.executor; //Required by double-check ideom (there is no happens-before relationhips between 2 reads)
		if (executor == null) {
			// happens on restart of TS within same VM
			executor = init();
		}
		executor.execute(task);
	}

	private static class AtomikosThreadFactory implements
			java.util.concurrent.ThreadFactory {

		private volatile AtomicInteger count = new AtomicInteger(0);
		private final ThreadGroup group;

		private AtomikosThreadFactory() {
			SecurityManager sm = System.getSecurityManager();
			group = (sm != null ? sm.getThreadGroup() : Thread.currentThread()
					.getThreadGroup());
		}

		@Override
		public Thread newThread(Runnable r) {
			String realName = "Atomikos:" + count.incrementAndGet();
			if (LOGGER.isTraceEnabled())
				LOGGER.logTrace("ThreadFactory: creating new thread: "+ realName);
			Thread thread = new Thread(group, r, realName);
			thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
			thread.setDaemon(true);
			return thread;
		}

	}

}
