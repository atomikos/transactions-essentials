package com.atomikos.finitestates;

/**
 *
 *
 *A source of TransitionEvents.
 */

public interface FSMTransitionEventSource extends Stateful
{   
    public void addFSMTransitionListener(FSMTransitionListener l,
				 Object from, Object to);
	
}
