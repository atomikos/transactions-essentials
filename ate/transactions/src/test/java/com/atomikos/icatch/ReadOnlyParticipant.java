package com.atomikos.icatch;

 /**
  *
  *
  *A readonly participant.
  */

public class ReadOnlyParticipant 
extends AbstractParticipant
{
     /**
      *Creates a new instance.
      */
      
    public ReadOnlyParticipant()
    {
        super ( true , new StringHeuristicMessage ( "ReadOnlyParticipant" ) );
    }
    
     /**
      *Creates a new instance.
      *@param msg The heuristic message to use.
      */
      
    public ReadOnlyParticipant ( HeuristicMessage msg )
    {
        super ( true , msg ); 
    } 
}

