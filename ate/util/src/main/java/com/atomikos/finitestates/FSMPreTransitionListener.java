package com.atomikos.finitestates;

import java.util.EventListener;

/**
*
*
*A listener interface for FSMTransition events, but one that wishes
*to be notified BEFORE the transition is exposed to other threads.
*
*
*/

public interface FSMPreTransitionListener extends EventListener
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

}
