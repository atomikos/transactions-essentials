/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

/**
 *
 *
 *A test listener for FSMImp testing.
 */

public class TestListener implements FSMEnterListener, FSMPreEnterListener,
			FSMTransitionListener,
			FSMPreTransitionListener
{
    private boolean notified_=false;
    
//    public TestListener()
//    {
//
//    }
    
    public boolean isNotified()
    {
        return notified_;
    }

    public void resetNotified()
    {
        notified_=false;
    }

    public void entered (FSMEnterEvent e)
    {
        notified_=true;
    }

    public void preEnter (FSMEnterEvent e)
    {
        notified_=true;
    }

    public void beforeTransition (FSMTransitionEvent e) 
    {
        notified_=true;
    }

    public void transitionPerformed (FSMTransitionEvent e)
    {
        notified_=true;
    }

    
}
