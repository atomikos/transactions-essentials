package com.atomikos.finitestates;

/**
 *
 *
 *A source of FSMPreEnterEvents.
 */

public interface FSMPreEnterEventSource extends Stateful
{
    
    public void addFSMPreEnterListener(FSMPreEnterListener l,Object state);
    
}
