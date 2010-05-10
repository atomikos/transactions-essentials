package com.atomikos.finitestates;

import java.util.EventListener;

/**
*
*
*A listener interface for FSMTransitionEvents.
*/
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
