/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */
package com.atomikos.timing;

import java.io.Serializable;

/**
 * A common interface for timers.
 * 
 * @author Lars J. Nilsson
 */
public interface AlarmTimer extends Serializable, Runnable {

	public long getTimeout();

	public boolean isActive();

	public void stop();

	public void addAlarmTimerListener(AlarmTimerListener lstnr);

	public void removeAlarmTimerListener(AlarmTimerListener lstnr);
}
