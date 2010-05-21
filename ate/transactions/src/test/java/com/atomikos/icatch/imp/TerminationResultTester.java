package com.atomikos.icatch.imp;
import java.util.Enumeration;
import java.util.Stack;

import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;


 /**
  *
  *
  *A tester class for the termination result class.
  */
  
  
  public class TerminationResultTester 
  {
      
      private static  void printCombination ( int[] codes)
      {
          for ( int i = 0 ; i < codes.length ; i++ ) {
              System.err.println (  codes [i] );
          }
      }
    
       protected static void testCombination( Propagator prop,
				  int [] testcodes, 
				  TerminationResult result )
        throws Exception
    {
        //what should outcome be?
        boolean mixed = false, hazard = false,
	  commit = false, abort = false;
        StringHeuristicMessage heuristicmsg =
	  new StringHeuristicMessage( "TESTHEURISTIC" );
        HeuristicMessage[] msgs = new HeuristicMessage[1];
        msgs[0] = heuristicmsg;
        Stack heurparticipants = new Stack();
        //for checking if heuristic participants are returned ok

        Stack indoubtparticipants = new Stack();
        //for checking if possibly indoubts are returned ok

        for ( int i  =  0 ; i < testcodes.length ; i++ ) {
	  
	  TestResultParticipant tsp = new TestResultParticipant( testcodes[i],
						    msgs);
	 
	  if ( tsp.heurMixed(false) || tsp.heurAbort()|| tsp.heurHazard(false) )
	      heurparticipants.push( tsp );
	  if ( tsp.heurHazard( false ) )
	      indoubtparticipants.push( tsp );
	  mixed =  ( mixed || tsp.heurMixed( false ) );
	  hazard = ( hazard ||tsp.heurHazard( false ) );
	  abort = ( abort || tsp.heurAbort() );
	  CommitMessage cm = new CommitMessage( tsp,result,false );
	  prop.submitPropagationMessage( cm );
        }
        mixed =  ( mixed || ( commit && hazard ) || 
	      ( commit && abort ) || ( hazard && abort ));
        
        //now wait for results and interpret them
        
       
        result.waitForReplies();
     
        
        if ( mixed ) {
	  if ( result.getResult() != Result.HEUR_MIXED )
	      throw new Exception( "ERROR: anomaly on mixed exception" );
	  }
        else if ( hazard ) {
	  if ( result.getResult() != Result.HEUR_HAZARD )
	      throw new Exception ( "ERROR: anomaly on hazard exception" );
        }
        else if ( abort ) {
	  if ( result.getResult() != Result.HEUR_ROLLBACK )
	      throw new Exception( "ERROR: anomaly on abort exception" );
        }
        if ( !( mixed || abort || hazard )) {
	  if ( result.getResult() != Result.ALL_OK ) {
	      printCombination ( testcodes );
	      throw new Exception( "ERROR: anomaly when result should be OK :"+
			       result.getResult());
	  }
	  if ( result.getMessages() == null ) 
	      throw new Exception( "ERROR: no normal heuristic messages" );
	  else {
	      HeuristicMessage[] returnmsgs= result.getMessages();
	      if ( !returnmsgs[0].equals(heuristicmsg))
		throw new Exception( "ERROR: heuristic message not OK" );
	  }
        }
        else if ( !hazard ) {
	  //test if heuristic message is returned as error msg
	  //this will ONLY be so if not hazard!
	  //since hazard does not get ANY return and makes a new msg

	  HeuristicMessage[] returnmsgs =  result.getErrorMessages( );
	      if ( !returnmsgs[0].equals( heuristicmsg ))
		throw new Exception( "ERROR: heuristic error "+
				"message not OK "+
				returnmsgs[0] );
        }
        
        //check if heuristic reporting works fine
        Enumeration enumm = heurparticipants.elements();
        while ( enumm.hasMoreElements()) {
	  Object next = enumm.nextElement();
	  if ( !result.getHeuristicParticipants().containsKey( next )) {
	      printCombination ( testcodes );
	      throw new Exception( "ERROR: heuristic participants not "+
			      "returned OK" );
	  }
        }
        
        //check if indoubt reporting works ok
        enumm = indoubtparticipants.elements();
        while ( enumm.hasMoreElements()) {
	  Object next = enumm.nextElement();
	  if ( !result.getPossiblyIndoubts().containsKey(next))
	      throw new Exception( "ERROR: indoubt participants not "+
			      "returned OK" );
        }
    }

    public static void test() throws Exception
    {

        
        int[] test1 = {1,2,1};
        int[] test2 = {1,3,1};
        int[] test3 = {1,10,2};
        int[] test4 = {6,6,0};
        int [] test5 = {2,3,4};
        int [] test6 = {0,2,3};
        int[] test7 = {1,3,2,10};
        int [] test8 = {0,0,0};
        Propagator prop = new Propagator( true );
        Propagator.RETRY_INTERVAL = 100;

        try {
	  System.out.println("testing first combination");
	  TerminationResult ps = new TerminationResult( 3 );
	  testCombination( prop,test1,ps );
	   System.out.println("testing second combination");
	  ps = new TerminationResult( 3 );
	  testCombination( prop,test2,ps );  
	
	  ps = new TerminationResult( 3 );
	  testCombination( prop,test3,ps );
	  ps = new TerminationResult( 3 );
	  testCombination( prop,test4,ps );
	  ps  = new TerminationResult( 3 );
	  testCombination( prop,test5,ps );
	  ps = new TerminationResult( 3 );
	  testCombination( prop,test6,ps );
	  ps = new TerminationResult( 4 );
	  testCombination( prop,test7,ps );
	  ps = new TerminationResult( 3 );
	  testCombination( prop,test8,ps );
        }
        finally {
	  Thread.sleep( 100 );
//	  if ( prop != null )
//	      prop.stopThreads( );
        }
    }//test

    public static void main( String args[] )
    {
        try { 
	  Test.getOutput().println("Starting: TerminationResult Test");
	  
	  test();
        }
        catch (Exception e) {
	  
	  Test.getOutput().println(e.getMessage());
        }
        finally {
	   Test.getOutput().println("Done:     TerminationResult Test");
        }
    }
  }
