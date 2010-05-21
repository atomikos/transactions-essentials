package com.atomikos.icatch.imp;
import com.atomikos.icatch.Participant;

 /**
  *
  *
  *A tester class for the Result class.
  */
  
  public class ResultTester 
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

    public static void test() throws Exception
    {
        Participant part = new TestResultParticipant();
        Result result = new Result(1){protected void analyze() 
				throws IllegalStateException{}};

        testCombination(part,result,true,true,null); 
        result = new Result(1){protected void analyze() 
				throws IllegalStateException{}};
        testCombination(part,result,true,false,null); 
        result = new Result(1){protected void analyze() 
				throws IllegalStateException{}};
        testCombination(part,result,false,true,null);  
        result = new Result(1){protected void analyze() 
				throws IllegalStateException{}};
        testCombination(part,result,false,false,null); 
        
    }
    
    public static void main(String args[])
    {
        try {
	  Test.getOutput().println("Starting: Result Test");
	  
	  test();
        }
        catch (Exception e) {
	  Test.getOutput().println("Error during test of Result: "+
			       e.getMessage()+" "+
			       e.getClass().getName());
        }
        finally{
	  Test.getOutput().println("Done: Result Test");
        }
    }
  }
