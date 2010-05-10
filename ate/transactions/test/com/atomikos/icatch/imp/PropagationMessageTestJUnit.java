
package com.atomikos.icatch.imp;

import java.util.Stack;

import junit.framework.TestCase;

/**
 * 
 * 
 * 
 *
 * 
 */
public class PropagationMessageTestJUnit extends TestCase
{
    
	protected static Boolean testCombination(Result result,
			      boolean retry,
			      boolean forcefail,
			      Boolean answer) 
			      throws Exception
	{
		TestPropagationMessage tm=
		new TestPropagationMessage(result,retry,forcefail,answer);
		
		boolean retried=tm.submit();
		Stack replies=result.getReplies();
		
		if (replies.size() >1) 
		throw new Exception ("more than one reply for just one message");
		Reply reply=(Reply) replies.pop();
		
		
		if (forcefail && retry && (!reply.isRetried() || !reply.hasFailed()))
		throw new Exception("wrong behaviour if failure and retriable");
		if  (forcefail && !retry && (reply.isRetried() || !reply.hasFailed()))
		throw new Exception("wrong behaviour if failure and "+
			  "not retriable");
		if (!forcefail && retry && (reply.isRetried() || reply.hasFailed() ||
			      reply.getResponse()!=answer))
		throw new Exception("wrong behaviour if no failure and retriable");
		if (!forcefail && !retry &&(reply.isRetried() || reply.hasFailed() ||
			      reply.getResponse()!=answer))
		throw new Exception("wrong behaviour if no failure and "+
			  "not retriable");
		
		return null;
	}
	
	private TestPropagationMessageResult result;
    

    public PropagationMessageTestJUnit ( String name )
    {
        super ( name );
    }
    
    protected void setUp()
    {
        result=
      	  new TestPropagationMessageResult(1);
    }
    
    public void testFailureWithRetry()
    throws Exception
    {
        testCombination ( result , true , true , null );
        
    }
    
    public void testRetryWithoutFailure()
    throws Exception
    {
        testCombination ( result , true , false , null );
    }
    
    public void testFailureWithoutRetry()
    throws Exception
    {
        testCombination ( result , false , true , null );
    }
    
    public void testNoFailureNoRetry()
    throws Exception
    {
        testCombination ( result , false , false , null );
    }
    
    public void testRetryResetAfterSuccess()
    throws Exception
    {
        TestPropagationMessage tm=
      	  new TestPropagationMessage(result,true,true,null);
              
              
              tm.submit();
              //should fail and set retry
              tm.setForceFail(false);
              //simulate success on retry

              //now, retry should be false
              if (tm.submit()) 
      	  throw new Exception("retry is not reset properly for "+
      			  "success after a failure and retried");
    }
    
    

}
