/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.timing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An alarm timer for use in a pool of threads.
 *
 */
public final class PooledAlarmTimer implements AlarmTimer {

	private final List<AlarmTimerListener> listeners;
	private final long timeout;

	private final Object runMonitor = new Object();
	private boolean runFlag = true;

	public PooledAlarmTimer(long timeout) {
		listeners = new ArrayList<AlarmTimerListener>();
		this.timeout = timeout;
	}

	public void addAlarmTimerListener(AlarmTimerListener lstnr) {
		synchronized(listeners) {
			listeners.add(lstnr);
		}
	}

	public void removeAlarmTimerListener(AlarmTimerListener lstnr) {
		synchronized(listeners) {
			listeners.remove(lstnr);
		}
	}

	public long getTimeout() {
		return timeout;
	}

	public boolean isActive() {
		synchronized(runMonitor) {
			return runFlag;
		}
	}

	public void stop() {
		synchronized(runMonitor) {
			runFlag = false;
			runMonitor.notify();
		}
	}

	public void run() {
		/*
		 * A short comment here, it is possible for Object.wait to
		 * return before the timeout silently, thus we need to double check
		 * the actual elapsed time before calling it quits. /LJN
		 */
		while(isActive()) {
			try {

				doWait(timeout);
			} catch(InterruptedException e) {
				// Question: At this point, the thread pool is most
				// likely shutting down, but I'm not entirely sure, does it
				// mean we should or shouldn't notify waiters? If we shouldn't
				// we should return from this method to kill the thread

				// return;
			}

			if ( isActive() )
				notifyListeners();
		}
	}

	// --- PRIVATE METHODS --- //

	/*
	 * Notify all listeners
	 */
	private void notifyListeners() {
		List<AlarmTimerListener> tempList = new ArrayList<AlarmTimerListener>(listeners);
		for (Iterator<AlarmTimerListener> it = tempList.iterator(); it.hasNext(); ) {
			AlarmTimerListener list = it.next();
			list.alarm(this);
		}
	}

	/*
	 * Wait on run monitor...
	 */
	private void doWait(long millis) throws InterruptedException {
		synchronized(runMonitor) {

			runMonitor.wait(millis);
		}
	}
}
