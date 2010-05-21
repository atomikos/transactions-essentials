package com.atomikos.icatch.admin.imp;

import com.atomikos.icatch.HeuristicMessage;

/**
 * 
 * 
 * A descriptor class containing state, message pairs.
 */

class StateDescriptor
{
    /**
     * The object denoting the state.
     */

    public Object state;

    /**
     * The heuristic message that goes with it.
     */

    public HeuristicMessage[] messages;

    /**
     * Create a new instance.
     * 
     * @param state
     *            The state.
     * @param messages
     *            The messages describing the work done.
     */

    public StateDescriptor ( Object state , HeuristicMessage[] messages )
    {
        this.state = state;
        this.messages = messages;
    }
}
