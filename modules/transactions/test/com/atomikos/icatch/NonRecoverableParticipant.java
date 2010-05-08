//$Id: NonRecoverableParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: NonRecoverableParticipant.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:06  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:17  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/05 15:04:21  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:51  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/02/12 21:19:45  guy
//Added generic test files.
//

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
