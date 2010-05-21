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
