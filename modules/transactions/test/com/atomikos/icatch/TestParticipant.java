//$Id: TestParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: TestParticipant.java,v $
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
//Revision 1.2  2004/06/30 11:35:57  guy
//*** empty log message ***
//
//Revision 1.1  2002/02/18 13:32:27  guy
//Added test files to package under CVS.
//

package com.atomikos.icatch;

 /**
  *
  *
  *A test instance of a participant, merely for testing the overhead.
  */
  
  public class TestParticipant extends AbstractParticipant
  {
	  private static int nextCommitSequence = 0; 
	  
	  private static synchronized int getNextCommitSequence() {
		  return nextCommitSequence++;
	  }
      
	  private long commitSequence;
      
      public TestParticipant() 
      {
              super ( false , new StringHeuristicMessage ( "Test participant!" ) );
      	
      }
  	
      public long getCommitSequence() {
    	  return commitSequence;
      }
    
      public HeuristicMessage[] commit ( boolean onePhase )
      throws HeurRollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     RollbackException,
	     SysException
	     { 
    	    commitSequence = getNextCommitSequence();
    	  	return super.commit ( onePhase );
	     }
    
  }
