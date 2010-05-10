//$Id: Test.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: Test.java,v $
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
//Revision 1.5  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.4  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/03/22 15:36:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.2.10.1  2004/01/14 10:38:25  guy
//Corrected forget to not block.
//
//Revision 1.2  2002/03/06 15:25:16  guy
//Added test facilities.
//
//Revision 1.1  2002/02/18 13:32:20  guy
//Added test files to package under CVS.
//
//Revision 1.6  2001/03/22 16:03:47  pardon
//Moved proxies to trmi; added trmi package; made abstract CompTx base class.
//
//Revision 1.5  2001/03/07 18:57:36  pardon
//Continued on CoordinatorImp.
//
//Revision 1.4  2001/03/06 19:05:39  pardon
//Adapted heuristic message passing and completed Participant implementation.
//
//Revision 1.3  2001/03/05 19:14:39  pardon
//Continued working on 2pc messaging.
//
//Revision 1.2  2001/02/28 18:09:43  pardon
//Implemented classes for CoordinatorImp: propagation logic.
//
//Revision 1.1  2001/02/25 11:14:02  pardon
//Added a lot of testing facilities.
//

package com.atomikos.icatch.imp;
import java.io.PrintStream;
import java.lang.reflect.Method;

/**
 *
 *
 *This class is for testing only; it contains global test 
 *parameters.
 */

public abstract class Test
{
 
    protected static PrintStream output_=System.err;
    //the output for testing
    
    /**
     *Get the test output.
     *
     *@return PrintStream The output for tests.
     */

    public static PrintStream getOutput() 
    {
        return output_;
    }

    /**
     *Set test output to new destination.
     *
     *@param printstream The new output destination.
     */

    public static void setOutput(PrintStream printstream)
    {
        output_=printstream;
    }

    protected static void doTest(String className) throws Exception
    { 
        String prefix="com.atomikos.icatch.";
        
        output_.println("Starting: "+prefix+className+" Test.");
        Class c=Class.forName(prefix+className);
        Method m=c.getDeclaredMethod("test",null);
        m.invoke(null,null);
        output_.println("Done:     "+prefix+className+" Test.");
        
    }
    
    
    /**
     *Test the whole package.
     */
    public static void main(String[] args)
    {
       
        try {
	  output_.println("Starting: Test.");
	  doTest ( "CompTxImpTester" );
	  doTest("PropagationMessageTester");
	  doTest("ResultTester");
	  doTest("PropagatorTester");
	  doTest("TerminationResultTester");
	  doTest("CompositeTransactionBaseTester");
                doTest("CoordinatorImpTester");
	  doTest ( "RecoveryTester" );
	  doTest ( "TestUserTransactionService");
        }
        catch (Exception e) {
	  output_.println("ERROR in Test :"+e.getMessage()+" "+
		        e.getClass().getName());
		e.printStackTrace();
        }
        finally {
	  output_.println("Done:     Test.");
        }
    }
    
}
