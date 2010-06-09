package com.atomikos.icatch.imp;

import junit.framework.TestCase;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;

/**
 * 
 * 
 * 
 *
 * 
 */
public class TerminationResultTestJUnit extends TestCase
{

    private static final long DEFAULT_TIMEOUT =
        Propagator.RETRY_INTERVAL;
    
    
                               
    
    private TerminationResult result;
    private Propagator prop;
    private HeuristicMessage[] msgs;
    
   
    public TerminationResultTestJUnit ( String name )
    {
        super(name);
    }
    
    protected void setUp()
    {
        Propagator.RETRY_INTERVAL = 10;
        prop  = new Propagator ( true );
        result = new TerminationResult ( 3 );
        
        StringHeuristicMessage heuristicmsg =
      	  new StringHeuristicMessage( "TESTHEURISTIC" );
        msgs = new HeuristicMessage[1];
        msgs[0] = heuristicmsg;
    }
    
    protected void tearDown()
    {
        //if ( prop != null ) prop.stopThreads();
        
        Propagator.RETRY_INTERVAL = DEFAULT_TIMEOUT;
        
    }
    
    public void testCommitWithoutErrors() throws InterruptedException
    {
        CommitMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.ALL_OK );
        
        if ( result.getMessages() == null ) 
  	      fail ( "ERROR: no normal heuristic messages" );
  	  else {
  	      HeuristicMessage[] returnmsgs= result.getMessages();
  	      if ( !returnmsgs[0].equals(msgs[0]))
  	          fail ( "ERROR: heuristic message not OK" );
  	  }
    }

    public void testCommitWithOneMixed() throws Exception
    {
        CommitMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 6 , msgs );
        cm = new CommitMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_MIXED );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p1 )  );
        assertNotNull ( result.getErrorMessages() );
        assertEquals ( result.getErrorMessages()[0] , msgs[0]);
        
    }
    
    public void testCommitWithOneHazard() throws Exception
    {
        CommitMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 7 , msgs );
        cm = new CommitMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_HAZARD );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p1 )  );
        assertTrue ( result.getPossiblyIndoubts().containsKey ( p1 ) );
        assertNotNull ( result.getErrorMessages() );
    }
    
    public void testCommitWithOneHeurAborted() throws Exception
    {
        CommitMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 8 , msgs );
        cm = new CommitMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_MIXED );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p1 )  );
        assertNotNull ( result.getErrorMessages() );
        assertEquals ( result.getErrorMessages()[0] , msgs[0]);
    }
    
    public void testCommitWithOneMixedAndOneHazard() throws Exception
    {
        CommitMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 6 , msgs );
        cm = new CommitMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 7 , msgs );
        cm = new CommitMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_MIXED );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p1 )  );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p2 )  );
        assertTrue ( result.getPossiblyIndoubts().containsKey ( p2 ));
        assertNotNull ( result.getErrorMessages() );
        assertEquals ( result.getErrorMessages()[0] , msgs[0]);
        
    }
    
    
    public void testCommitWithAllHeurAbort() throws Exception
    {
        CommitMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 8 , msgs );
        cm = new CommitMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 8 , msgs );
        cm = new CommitMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 8 , msgs );
        cm = new CommitMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_ROLLBACK );
        
        if ( result.getMessages() != null ) 
    	      fail ( "ERROR: no normal heuristic messages should be there" );
    	  	
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p1 )  );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p2 )  );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p3 ));
        assertNotNull ( result.getErrorMessages() );
        assertEquals ( result.getErrorMessages()[0] , msgs[0]);
    }
    
    public void testCommitWithOneMixedAndOneHeurAbort() throws Exception
    {
        CommitMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 6 , msgs );
        cm = new CommitMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 8 , msgs );
        cm = new CommitMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_MIXED );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p1 )  );
        assertTrue ( result.getHeuristicParticipants().containsKey(p2) );
        assertNotNull ( result.getErrorMessages() );
    }
    
    public void testCommitWithOneHazardAndOneHeurAbort() throws Exception
    {
        CommitMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 7 , msgs );
        cm = new CommitMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 8 , msgs );
        cm = new CommitMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new CommitMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_MIXED );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getHeuristicParticipants().containsKey ( p1 )  );
        assertTrue ( result.getHeuristicParticipants().containsKey(p2) );
        assertTrue ( result.getPossiblyIndoubts().containsKey(p1));
        assertNotNull ( result.getErrorMessages() );
    }
    
    public void testRollbackWithoutErrorsAfterPrepare() throws Exception
    {
        RollbackMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.ALL_OK );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertTrue ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getPossiblyIndoubts().isEmpty()  );
        assertNull ( result.getErrorMessages() );
    }
    
    public void testRollbackWithOneHeurMixedAfterPrepare() throws Exception
    {
        RollbackMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 6 , msgs );
        cm = new RollbackMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_MIXED );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getPossiblyIndoubts().isEmpty()  );
        assertNotNull ( result.getErrorMessages() );
        assertEquals ( result.getErrorMessages()[0] , msgs[0]);
    }
    
    public void testRollbackWithOneHeurHazardAfterPrepare() throws Exception
    {
        RollbackMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 7 , msgs );
        cm = new RollbackMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_HAZARD );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertFalse ( result.getPossiblyIndoubts().isEmpty()  );
        
    }
    
    public void testRollbackWithOneHeurCommittedAfterPrepare() throws Exception
    {
        RollbackMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 5 , msgs );
        cm = new RollbackMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_MIXED );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getPossiblyIndoubts().isEmpty()  );
        assertNotNull ( result.getErrorMessages() );
        assertEquals ( result.getErrorMessages()[0] , msgs[0]);
    }
    
    public void testRollbackWithAllHeurCommittedAfterPrepare() throws Exception
    {
        RollbackMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 5 , msgs );
        cm = new RollbackMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 5 , msgs );
        cm = new RollbackMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 5 , msgs );
        cm = new RollbackMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_COMMIT );
        
        
    	  
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertTrue ( result.getPossiblyIndoubts().isEmpty()  );
        assertNotNull ( result.getErrorMessages() );
        assertEquals ( result.getErrorMessages()[0] , msgs[0]);
    }
    
    public void testRollbackWithOneheurHazardAndOneHeurMixedAfterPrepare() throws Exception
    {
        RollbackMessage cm = null;
        TestResultParticipant p1 = new TestResultParticipant ( 6 , msgs );
        cm = new RollbackMessage( p1 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p2 = new TestResultParticipant ( 7 , msgs );
        cm = new RollbackMessage( p2 ,result,true );
        prop.submitPropagationMessage( cm );
        TestResultParticipant p3 = new TestResultParticipant ( 10 , msgs );
        cm = new RollbackMessage( p3 ,result,true );
        prop.submitPropagationMessage( cm );
        
        result.waitForReplies();
        
        assertEquals ( result.getResult() , Result.HEUR_MIXED );
        
        if ( result.getMessages() == null ) 
    	      fail ( "ERROR: no normal heuristic messages" );
    	  	else {
    	      HeuristicMessage[] returnmsgs= result.getMessages();
    	      if ( !returnmsgs[0].equals(msgs[0]))
    	          fail ( "ERROR: heuristic message not OK" );
    	  }
        assertFalse ( result.getHeuristicParticipants().isEmpty() );
        assertFalse ( result.getPossiblyIndoubts().isEmpty()  );
        assertNotNull ( result.getErrorMessages() );
        assertEquals ( result.getErrorMessages()[0] , msgs[0]);
    }
    
    
}
