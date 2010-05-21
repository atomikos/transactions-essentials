package com.atomikos.datasource;
import com.atomikos.icatch.HeuristicMessage;

public class HeuristicException extends ResourceException
{
    protected HeuristicMessage[] myMsgs=null;

    

    /**
     *Constructor.
     *
     */
    public HeuristicException(String msg)
    {
        super(msg);
    }
    
    /**
     *Constructor.
     *@param msgs an array of heuristic messages,
     *or null if none.
     */
    public HeuristicException(HeuristicMessage[] msgs)
    {
        super("Heuristic Exception");
        myMsgs=msgs;
    }
    
    /**
     *Get any heuristic messages.
     *
     *@return HeuristicMessage[] A list of messages, or null if none.
     */
    public HeuristicMessage[] getHeuristicMessages(){
        return myMsgs;
    }
}
