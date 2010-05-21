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
