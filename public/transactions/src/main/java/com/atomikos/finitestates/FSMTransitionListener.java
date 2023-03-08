/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
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
	*A method to be called BEFORE the specified transition takes place.
	*Since the transition still has to happen, no listener can be sure
	*that the event notification eventually leads to the transition.
	*This is because the state machine process can fail after the notice,
	*or the target state can be prevented somehow.
	*
	*@param e The transition that will be attempted.
	*@exception IllegalStateException on failure.
	*/

	public void beforeTransition(FSMTransitionEvent e) 
	    throws IllegalStateException;

	
    /**
     *A method to be called AFTER the specified transition is done.
     *
     *@param e The transition that was made.
     *
     */
    
    public void transitionPerformed(FSMTransitionEvent e);
    
}
