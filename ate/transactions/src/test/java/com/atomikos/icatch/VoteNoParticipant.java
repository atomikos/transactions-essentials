package com.atomikos.icatch;

 /**
  *
  *
  *A test participant that will vote no on prepare.
  */

public class VoteNoParticipant
extends AbstractParticipant
{
    public VoteNoParticipant()
    {
        super ( false , new StringHeuristicMessage ( "VoteNoParticipant" ) );
    } 
    
     /**
      *Overrides default behaviour: will fail.
      *
      *@exception RollbackException Thrown every time this
      *method is invoked.
      */
      
    public int prepare()
        throws RollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     SysException
    {
          throw new RollbackException();
    }   
}
