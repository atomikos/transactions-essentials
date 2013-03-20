package com.atomikos.persistence.imp;

import java.util.Properties;
import java.util.Vector;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.atomikos.icatch.TxState;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.StateRecoverable;
import com.atomikos.persistence.StateRecoveryManager;

public class VolatileStateRecoveryManagerTestJUnit
//FIXME : not sure what to do yet...
//extends
//		AbstractJUnitStateRecoveryManagerTest 
		{

	public VolatileStateRecoveryManagerTestJUnit() {
		// TODO Auto-generated constructor stub
	}
	protected StateRecoveryManager getInstanceToTest() {
		return new VolatileStateRecoveryManager();
	}


	private StateRecoveryManager recmgr;
	
//	protected AbstractJUnitStateRecoveryManagerTest ( String name )
//	{
//		super ( name );
//	}
//	
	
	
	Properties props;
	@Before
	public void setUp()
	{
	
		props= new Properties();
    	props.put(AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME,10);
    	props.put(AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME,"/.");
    	props.put(AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME, "test");
		recmgr = getInstanceToTest();
	}
	
	@After
	public void tearDown()
	{
		try {
			recmgr.close();
		} catch (LogException e) {
			Assert.fail( e.getMessage() );
		}
		
	}
	

	@Test
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
	@Test
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
	  Assert.fail("Transition INITIAL -> END should fail");

        }
        catch ( IllegalStateException ill ) {
	  //should happen
        }

        tsr2.setState ( TestTransitionTable.MIDDLE );
        
        recmgr.close();
        recmgr.init(props);
        
        TestStateRecoverable tsr3 = ( TestStateRecoverable ) recmgr.recover ( id2 );
        if ( ! tsr3.equals ( tsr2 ) ) 
        		Assert.fail ( id2 + " should have been recovered");
        
        Vector<StateRecoverable<TxState>> recovered = recmgr.recover();
        if ( !recovered.elements().hasMoreElements() )
        	Assert.fail ("recover() does not work?");
    }
	@Test
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
        	Assert.fail ( "Log has grown in size: from " + 
                                              initialSize + " to " + recmgr.recover().size() );
    } 
    
}
