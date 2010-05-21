package com.atomikos.icatch;

 /**
  *
  *
  *A non-recoverable participant: it will fail to recover correctly.
  *If used in combination with a heuristic participant, this class
  *can serve for testing the reaction to non-recoverable participants.
  */

public class NonRecoverableParticipant
extends AbstractParticipant
{
      /**
       *Creates a new instance.
       *@param msg The heuristic message to use.
       */
       
    public NonRecoverableParticipant ( HeuristicMessage msg )
    {
        super ( false , msg ); 
    } 
    
     /**
      *Overrides default behaviour: always returns false.
      *
      *@return boolean Always false.
      */
      
    public boolean recover() throws SysException
    {
        return false; 
    }
}
