//$Id: ResultTester.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: ResultTester.java,v $
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
//Revision 1.1  2002/02/18 13:32:20  guy
//Added test files to package under CVS.
//

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
