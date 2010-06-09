package com.atomikos.persistence.imp;

import java.util.Vector;

import com.atomikos.finitestates.TestTransitionTable;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.util.TestCaseWithTemporaryOutputFolder;

public abstract class AbstractJUnitStateRecoveryManagerTest extends
		TestCaseWithTemporaryOutputFolder 
{
	
	private StateRecoveryManager recmgr;
	
	protected AbstractJUnitStateRecoveryManagerTest ( String name )
	{
		super ( name );
	}
	
	protected abstract StateRecoveryManager getInstanceToTest();
	
	protected void setUp()
	{
		super.setUp();
		recmgr = getInstanceToTest();
	}
	
	protected void tearDown()
	{
		try {
			recmgr.close();
		} catch (LogException e) {
			failTest ( e.getMessage() );
		}
		super.tearDown();
	}
	

	public void testWithNullObjectImage()
	{
		String id1 = new String ("ID1");
        TestStateRecoverable tsr1 = new TestStateRecoverable ( id1 );
        tsr1.setReturnNullObjectImage();
        recmgr.register ( tsr1 );
        //should not generate NPE (cf 10041 bug)
        tsr1.setState ( TestTransitionTable.MIDDLE );
        //if we are here then it works
	}
	
    public void testBasic() throws Exception
    {
        String id1 = new String ("ID1");
        TestStateRecoverable tsr1 = new TestStateRecoverable ( id1 );
        String id2 = new String ("ID2");
        TestStateRecoverable tsr2 = new TestStateRecoverable ( id2 );
        recmgr.register ( tsr1 );
        recmgr.register ( tsr2 );
        
        try {
	  tsr1.setState ( TestTransitionTable.END );
	  //should fail, and no recovered state should appear
	  failTest ("Transition INITIAL -> END should fail");

        }
        catch ( IllegalStateException ill ) {
	  //should happen
        }

        tsr2.setState ( TestTransitionTable.MIDDLE );
        
        recmgr.close();
        recmgr.init();
        
        TestStateRecoverable tsr3 = 
	  ( TestStateRecoverable ) recmgr.recover ( id2 );
        if ( ! tsr3.equals ( tsr2 ) ) 
        		failTest ( id2 + " should have been recovered");
        
        Vector recovered = recmgr.recover();
        if ( !recovered.elements().hasMoreElements() )
        		failTest ("recover() does not work?");
    }
    
   public void testLoad () throws Exception
    {
        //first get the current size for testing checkpoint functionality
        long initialSize = recmgr.recover().size();
        
        //do a number of registers and check log size afterwards
        for ( int i = 0 ; i < 1000 ; i++ ) {
            String id = "LoadTestID" + i ;
            TestStateRecoverable tsr = new TestStateRecoverable ( id );
            recmgr.register ( tsr );
            
            tsr.setState ( TestTransitionTable.MIDDLE );
            tsr.setState ( TestTransitionTable.END );
            //now, the test instance should be cleaned up on the next checkpoint
            //which has already happened since a checkpoint is written after 
            //each flush
        }   
        
        if ( recmgr.recover().size() !=  initialSize )
            failTest ( "Log has grown in size: from " + 
                                              initialSize + " to " + recmgr.recover().size() );
    } 
    

}
