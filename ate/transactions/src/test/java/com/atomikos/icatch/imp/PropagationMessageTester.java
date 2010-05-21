package com.atomikos.icatch.imp;
import java.util.Stack;


/**
 *
 *
 *A tester class for propagation messages.
 */
 
 public class PropagationMessageTester 
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

    public static void test() throws Exception
    {
       
        TestPropagationMessageResult result=
	  new TestPropagationMessageResult(1);
        
        testCombination(result,true,true,null);
        testCombination(result,true,false,null);
        testCombination(result,false,false,null);
        testCombination(result,false,true,null);
        
       
       
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
    
    public static void main(String[] args)
    {
        try {
	  Test.getOutput().println("Starting: PropagationMessage Test");
	  test();
        }
        catch (Exception e) {
	  Test.getOutput().println("Unexpected error in testing "+
			       "PropagationMessage: "
			       +e.getMessage()+" "+
			       e.getClass().getName());
        }
        finally {
	  Test.getOutput().println("Done : PropagationMessage Test");
        }
    }
    
 }
