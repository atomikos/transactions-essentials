package com.atomikos.icatch.imp;
import com.atomikos.icatch.Participant;

 /**
  *
  *
  *A tester class for the propagator.
  */
  
  public class PropagatorTester 
  {
    public static void testCombination ( Propagator prop, boolean retry, 
			         boolean fail ) 
        throws Exception
    {

        Result result = new Result ( 1 ){protected void analyze() 
				throws IllegalStateException{}};

        Participant part = new TestResultParticipant();
        
        TestPropagationMessage tm = 
	  new TestPropagationMessage ( part,result,retry,fail,null );

        prop.submitPropagationMessage ( tm );
        
        Thread.sleep ( 1000 );

        tm.setForceFail ( false );
        
        Thread.sleep ( 1000 );

        if  ( result.getReplies().size() != 1 ) 
	  throw new Exception ( "exactly 1 reply should be present for "+
			  "a message" );

       
    }

    public static void test() throws Exception
    {
        Propagator.RETRY_INTERVAL = 100;
        Propagator prop = null;
        try{
	  
	  prop = new Propagator ( true );
        
	  testCombination ( prop,true,true );
	  testCombination ( prop,true,false );
	  testCombination ( prop,false,true );
	  testCombination ( prop,false,false );
        }
        finally {
//	  if  ( prop != null ) 
//	      prop.stopThreads();
        }
    }

    public static void main ( String[] args ) {
        
        try{
	  Test.getOutput().println ( "Starting: Propagator Test." );
	  
	  test();
        }
        catch  ( Exception e ) {

	  Test.getOutput().println ( "ERROR: "+e.getMessage()+" "+
			       e.getClass().getName() );
        }
        finally {
	  Test.getOutput().println ( "Done:     Propagator Test." );

        }
    }
  }
