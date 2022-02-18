/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.timing;


/**
 * A common interface for timers.
 * 
 * @author Lars J. Nilsson
 */
public interface AlarmTimer extends Runnable {

	public long getTimeout();

	public boolean isActive();

	public void stopTimer();

	public void addAlarmTimerListener(AlarmTimerListener lstnr);

	public void removeAlarmTimerListener(AlarmTimerListener lstnr);

}
