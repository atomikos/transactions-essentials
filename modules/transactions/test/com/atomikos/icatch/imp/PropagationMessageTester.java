//$Id: PropagationMessageTester.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: PropagationMessageTester.java,v $
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
