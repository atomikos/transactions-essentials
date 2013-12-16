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

package com.atomikos.thread;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * 
 */

public class TaskManager {
	private static final Logger LOGGER = LoggerFactory.createLogger(TaskManager.class);

	private static TaskManager singleton;

	private ThreadPoolExecutor executor;

	/**
	 * Gets the singleton instance.
	 * 
	 * @return
	 */
	public static synchronized final TaskManager getInstance() {
		if (singleton == null) {
			if (LOGGER.isDebugEnabled())
				LOGGER.logDebug("TaskManager: initializing...");
			singleton = new TaskManager();
		}
		return singleton;
	}

	protected TaskManager() {
		init();
	}

	private void init() {
		SynchronousQueue<Runnable> synchronousQueue = new SynchronousQueue<Runnable>();
		executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, new Long(60L),
				TimeUnit.SECONDS, synchronousQueue, new AtomikosThreadFactory());

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
		if (executor == null) {
			// happens on restart of TS within same VM
			init();
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
			if (LOGGER.isDebugEnabled())
				LOGGER.logDebug("ThreadFactory: creating new thread: "+ realName);
			Thread thread = new Thread(group, r, realName);
			thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
			thread.setDaemon(true);
			return thread;
		}

	}

}
