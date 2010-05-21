package com.atomikos.icatch;


/**
 *
 *
 *A heuristic extension supporting messages.
 */

public class HeurCommitException extends Exception
{
    protected HeuristicMessage[] msgs_=null;

    /**
     *Constructor.
     *@param msgs an array of heuristic messages,
     *or null if none.
     */
    public HeurCommitException(HeuristicMessage[] msgs)
    {
        super("Heuristic Exception");
        msgs_=msgs;
    }
    
    /**
     *Get any heuristic messages.
     *
     *@return HeuristicMessage[] A list of messages, or null if none.
     */
    public HeuristicMessage[] getHeuristicMessages(){
        return msgs_;
    }

    
}
