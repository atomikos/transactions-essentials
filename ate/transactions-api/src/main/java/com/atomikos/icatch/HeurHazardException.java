package com.atomikos.icatch;


/**
 *
 *
 *A heuristic hazard exception propagates to the root
 *as indication of cases where 2PC commit or abort was not
 *acknowledge by all participants.
 *Heuristic information about the identity and nature of the
 *lost participants can be included.
 */

public class HeurHazardException extends Exception
{
    protected HeuristicMessage[] msgs_=null;

    /**
     *Constructor.
     *@param msgs An array of heuristic messages, or null if none.
     */

    public HeurHazardException(HeuristicMessage[] msgs)
    {
        super ("Heuristic Exception");
        msgs_=msgs;
    }

    /**
     *Get the heuristic messages.
     *
     *@return HeuristicMessage[] An array, possibly null.
     */

    public HeuristicMessage[] getHeuristicMessages()
    {
        return msgs_;
    }
}
