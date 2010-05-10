//$Id: VoteNoParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: VoteNoParticipant.java,v $
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
//Revision 1.4  2005/08/05 15:04:22  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.3  2004/10/12 13:03:52  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:38:06  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.10.1  2003/06/20 16:31:47  guy
//*** empty log message ***
//
//Revision 1.1  2002/03/01 10:48:04  guy
//Updated to new prepare exception of HeurMixed.
//Added more failure modes and test facilities.
//

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
