package com.atomikos.icatch;


/**
 *
 *
 *A heuristic message implementation.
 */

public class StringHeuristicMessage implements HeuristicMessage
{
    //force set UID for backward log compatibility
    static final long serialVersionUID = -6967918138714056401L;
    
    protected String string_=null;
    
    /**
     *Constructor.
     *
     *@param string The message as a string.
     */

    public StringHeuristicMessage(String string)
    {
        string_=string;
    }

    /**
     *@see com.atomikos.icatch.HeuristicMessage
     */

    public String toString()
    {
        return string_;
    }
}
