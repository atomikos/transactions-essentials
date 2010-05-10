//$Id: StateRecoveryManagerTester.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: StateRecoveryManagerTester.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:21  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/09 15:24:47  guy
//Updated javadoc.
//
//Revision 1.6  2004/10/27 10:28:31  guy
//Removed support for max_active log entries.
//
//Revision 1.5  2004/10/18 08:48:55  guy
//Added a VolatileStateRecoveryManager to support disabled recovery.
//
//Revision 1.4  2004/03/25 12:54:58  guy
//Added test for respecting the max size
//
//Revision 1.3  2002/02/20 10:10:36  guy
//Updated testing to include checkpoint validation.
//
//Revision 1.2  2002/02/18 13:32:33  guy
//Added test files to package under CVS.
//
//Revision 1.1  2002/01/29 12:34:24  guy
//Added test files to package dir.
//

package com.atomikos.persistence.imp;
import java.util.Vector;

import com.atomikos.finitestates.TestTransitionTable;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.StateRecoveryManager;

/**
 *
 *
 *A test class for a state recovery manager impl.
 */

public class StateRecoveryManagerTester
{
    protected StateRecoveryManager recmgr_ ;


    public StateRecoveryManagerTester ( StateRecoveryManager statemgr )
        throws LogException
    {
        recmgr_ = statemgr;
        recmgr_.init();
    }

    private void testBasic() throws Exception
    {
        String id1 = new String ("ID1");
        TestStateRecoverable tsr1 = new TestStateRecoverable ( id1 );
        String id2 = new String ("ID2");
        TestStateRecoverable tsr2 = new TestStateRecoverable ( id2 );
        recmgr_.register ( tsr1 );
        recmgr_.register ( tsr2 );
        
        try {
	  tsr1.setState ( TestTransitionTable.END );
	  //should fail, and no recovered state should appear
	  throw new Exception ("Transition INITIAL -> END should fail");

        }
        catch ( IllegalStateException ill ) {
	  //should happen
        }

        tsr2.setState ( TestTransitionTable.MIDDLE );
        
        recmgr_.close();
        recmgr_.init();
        
        TestStateRecoverable tsr3 = 
	  ( TestStateRecoverable ) recmgr_.recover ( id2 );
        if ( ! tsr3.equals ( tsr2 ) ) 
	  throw new Exception ( id2 + " should have been recovered");
        
        Vector recovered = recmgr_.recover();
        if ( !recovered.elements().hasMoreElements() )
	  throw new Exception ("recover() does not work?");
    }
    
    private void testLoad () throws Exception
    {
        //first get the current size for testing checkpoint functionality
        long initialSize = recmgr_.recover().size();
        
        //do a number of registers and check log size afterwards
        for ( int i = 0 ; i < 1000 ; i++ ) {
            String id = "LoadTestID" + i ;
            TestStateRecoverable tsr = new TestStateRecoverable ( id );
            recmgr_.register ( tsr );
            
            tsr.setState ( TestTransitionTable.MIDDLE );
            tsr.setState ( TestTransitionTable.END );
            //now, the test instance should be cleaned up on the next checkpoint
            //which has already happened since a checkpoint is written after 
            //each flush
        }   
        
        if ( recmgr_.recover().size() !=  initialSize )
            throw new Exception ( "Log has grown in size: from " + 
                                              initialSize + " to " + recmgr_.recover().size() );
    } 
    

    
//    private void testMaxSize() throws Exception
//    {
//		      FileLogStream logs = new FileLogStream ( "./" , "teststatewithsizelog"  , null );
//			  StreamObjectLog log = new StreamObjectLog ( logs , 2 , 1 , null );
//			  StateRecoveryManager smgr = new StateRecoveryManagerImp ( log );
//			  smgr.init();
//		
//		try
//        {
//            //do a number of registers
//            for ( int i = 0 ; i < 3 ; i++ ) {
//            	String id = "MaxSizeTestID" + i ;
//            	TestStateRecoverable tsr = new TestStateRecoverable ( id );
//            	smgr.register ( tsr );
//                
//            	tsr.setState ( TestTransitionTable.MIDDLE );
//            
//            } 
//            
//            throw new Exception ( "Log does not enforce size limitation?");
//        }
//        catch (IllegalStateException e)
//        {
//            //should happen
//        }  
//    }
    
    public void test() throws Exception
    {
        testBasic();
        testLoad ();
        //testMaxSize();
    }

    
    public static void main( String[] args ) 
    {
        System.out.println ( "Starting: StateRecoveryManagerTester");
        try {
                FileLogStream logs = new FileLogStream ( "./" , "teststatelog"  , null );
	  StreamObjectLog log = new StreamObjectLog ( logs , 1 , null );
	  StateRecoveryManager smgr = new StateRecoveryManagerImp ( log );
	  smgr.init();
	  StateRecoveryManagerTester tester =
	      new StateRecoveryManagerTester ( smgr );
	  tester.test ();
	  
	  //redo the tests with a VolatileStateRecoveryManager
	  smgr = new VolatileStateRecoveryManager();
	  tester = new StateRecoveryManagerTester ( smgr );
	  tester.test();
	  
	  
        }
        catch ( Exception e ) {
	  System.out.println ( "ERROR: " + e.getMessage() );
	  e.printStackTrace();
        }
        finally {
	  System.out.println ( "Done:    StateRecoveryManagerTester.");
        }
    }

}
