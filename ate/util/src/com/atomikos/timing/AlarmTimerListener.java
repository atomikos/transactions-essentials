
package com.atomikos.timing;

/**
 *
 *
 *A listener for timer events.
 */

public interface AlarmTimerListener 
{
    /**
     *Notify the instance of an alarm coming from a timer.
     *
     *@param timer The timer raising the alarm.
     */

    public void alarm(AlarmTimer timer);
}
