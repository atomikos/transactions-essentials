package com.atomikos.timing;

/**
 * A common interface for timers.
 * 
 * @author Lars J. Nilsson
 */
public interface AlarmTimer extends Runnable {

	public long getTimeout();

	public boolean isActive();

	public void stop();

	public void addAlarmTimerListener(AlarmTimerListener lstnr);

	public void removeAlarmTimerListener(AlarmTimerListener lstnr);

}
