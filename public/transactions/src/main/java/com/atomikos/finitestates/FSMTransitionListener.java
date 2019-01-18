/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.finitestates;

import java.util.EventListener;

public interface FSMTransitionListener extends EventListener
{
    
    /**
     *A method to be called AFTER the specified transition is done.
     *
     *@param e The transition that was made.
     *
     */
    
    public void transitionPerformed(FSMTransitionEvent e);
    
}
