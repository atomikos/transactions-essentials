package com.atomikos.finitestates;

/**
 *
 *
 *A source of PreTransitionEvents.
 */

public interface FSMPreTransitionEventSource extends Stateful
{
    
    public void addFSMPreTransitionListener(FSMPreTransitionListener l,
				    Object from, Object to);

}
