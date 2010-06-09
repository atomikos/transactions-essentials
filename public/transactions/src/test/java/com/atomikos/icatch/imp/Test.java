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
