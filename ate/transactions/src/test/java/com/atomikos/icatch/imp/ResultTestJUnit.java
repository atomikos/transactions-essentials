
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
public class ResultTestJUnit extends TestCase
{
   
    protected static void testCombination(Participant part, Result result,
			  boolean retry, boolean forcefail,
			  Boolean answer) throws Exception
	{
	  
		  TestPropagationMessage tm =  new TestPropagationMessage(part,
							  result,
							  retry,
							  forcefail,
							  answer);
		  
		  tm.submit();
		  //now simulate retry
		  tm.submit();
		  
		  if (retry)
		if (forcefail && result.replies_.size() != 0) 
		    throw new Exception("ERROR: wrong behaviour if retried "+
				      "with failure");
		else if (!forcefail && result.replies_.size() != 1)
		    throw new Exception("ERROR: wrong behaviour if retried "+
				      "without failure");
		  
		  if (!retry)
		if (result.replies_.size()  != 1)
		    throw new Exception("ERROR: wrong behaviour if duplicate msg");
		  
	}    
    
    private Result result;
    
    private Participant part;

   
    public ResultTestJUnit ( String name )
    {
        super ( name );
    }

    protected void setUp()
    {
        part = new TestResultParticipant();
        result = new Result(1){protected void analyze() 
				throws IllegalStateException{}};

    }
    
    public void testFailureAndRetry()
    throws Exception
    {
        testCombination ( part , result , true , true , null );
    }
    
    public void testFailureNoRetry()
    throws Exception
    {
        testCombination ( part , result , false , true , null );
    }
    
    public void testRetryNoFailure()
    throws Exception
    {
        testCombination ( part , result , true , false , null );
    }
    
    public void testNoRetryNoFailure()
    throws Exception
    {
        testCombination ( part , result , false , false, null );
    }
}
