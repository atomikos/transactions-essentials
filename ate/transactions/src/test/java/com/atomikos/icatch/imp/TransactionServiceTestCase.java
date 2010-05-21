package com.atomikos.icatch.imp;

import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.TestCaseWithTemporaryOutputFolder;

/**
 * 
 * 
 * 
 *
 * 
 */
public abstract class TransactionServiceTestCase 
extends TestCaseWithTemporaryOutputFolder
{
    private static final long DEFAULT_TIMEOUT = Propagator.RETRY_INTERVAL;
    private static final long DEFAULT_COORDINATOR_TIMEOUT = CoordinatorImp.DEFAULT_TIMEOUT;
    private static final long PROPAGATOR_TIMEOUT = 10;
    protected static final long TX_TIMEOUT = PROPAGATOR_TIMEOUT;
    private static final long SLEEP_TIME = 300 * TX_TIMEOUT;
    
    /**
     * Creates a new instance with the default 
     * output dir for temp files.
     * @param name
     */
    protected TransactionServiceTestCase ( String name )
    {
        super ( name );
    }
    
    /**
     * Creates a new instance with a given output dir.
     * @param name
     * @param tempDir
     */
    protected TransactionServiceTestCase ( String name , String tempDir )
    {
        super ( name , tempDir );
    }
    
    
    /**
     * Sleeps for a while to wait for timeouts.
     *
     */
    protected void sleep() 
    {
        try {
            Thread.currentThread().sleep ( SLEEP_TIME );
        }
        catch ( InterruptedException inter ) {}
    }
    
    
    protected void setUp()
    {
    		super.setUp();
     
    		//remove consoles in case shutdown was not done
        //otherwise failed tests will see all the logging
        //of later tests as well, making error diagnosis hard
        Configuration.removeConsoles();
        
        //set propagator delay very short to minimize test time
        Propagator.RETRY_INTERVAL = PROPAGATOR_TIMEOUT;
        CoordinatorImp.DEFAULT_TIMEOUT = TX_TIMEOUT;
    }
    
    /**
     * Cleans the temporary output files.
     * Does nothing if failed is set to true.
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown()
    {
       
        
        Propagator.RETRY_INTERVAL = DEFAULT_TIMEOUT;
        CoordinatorImp.DEFAULT_TIMEOUT = DEFAULT_COORDINATOR_TIMEOUT;
        
        super.tearDown();
    }
}
