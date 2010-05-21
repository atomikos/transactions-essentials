package com.atomikos.icatch;
import java.io.Serializable;
/**
 *
 *
 *A message to help resolving heuristic problem cases.
 *Instances can be given to the resource, which will keep
 *them and return them as part of a heuristic exception.
 *
 */

public interface HeuristicMessage extends Serializable
{
    /**
     *Get the description of the heuristically terminated
     *work.
     *@return String the description in string format.
     */
    public String toString();

}
