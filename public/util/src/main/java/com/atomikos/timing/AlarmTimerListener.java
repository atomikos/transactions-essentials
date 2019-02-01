/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */


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
