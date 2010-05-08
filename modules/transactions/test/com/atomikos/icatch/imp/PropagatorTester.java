//$Id: PropagatorTester.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: PropagatorTester.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:18  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/02/18 13:32:19  guy
//Added test files to package under CVS.
//

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
