
package com.atomikos.icatch.imp;

import junit.framework.TestCase;

import com.atomikos.icatch.Participant;

/**
 * 
 * 
 * 
 *
 * 
 */
public class PropagatorTestJUnit extends TestCase
{

    private static final long DEFAULT_RETRY_INTERVAL =
        Propagator.RETRY_INTERVAL;
    
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
	
	Thread.sleep ( 30 );
	
	tm.setForceFail ( false );
	
	Thread.sleep ( 30 );
	
	if  ( result.getReplies().size() != 1 ) 
	throw new Exception ( "exactly 1 reply should be present for "+
	  "a message" );
	
	
	}
    
    private Propagator prop;

   
    public PropagatorTestJUnit ( String name )
    {
        super ( name );
    }
    
    protected void setUp()
    {
        Propagator.RETRY_INTERVAL = 10;
        prop = new Propagator ( true );
    }
    
    protected void tearDown()
    {
        
        
        Propagator.RETRY_INTERVAL = DEFAULT_RETRY_INTERVAL;
    }
    
    public void testRetryAndFailure()
    throws Exception
    {
        testCombination ( prop , true , true );
    }
    
    public void testRetryWithoutFailure()
    throws Exception
    {
        testCombination ( prop , true , false );
    }
    
    public void testFailureWithoutRetry()
    throws Exception
    {
        testCombination ( prop , false , true );
    }
    
    public void testNoFailureNoRetry()
    throws Exception
    {
        testCombination ( prop , false , false );
    }
    
    

}
