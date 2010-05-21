package com.atomikos.icatch.standalone;
import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.imp.ReleaseTester;
import com.atomikos.icatch.jta.ExtendedSystemException;

 /**
  *
  *
  *A standalone test class.
  */

public class Test
{
    private static void printError ( Exception e )
    {
        e.printStackTrace();
        if ( e instanceof SysException ) {
            Stack errors = (( SysException ) e).getErrors();
            while ( ! errors.empty () ) {
                System.err.println ( "Nested error: " );
                Exception nxt = ( Exception ) errors.pop();
                printError ( nxt );
            }
        }
        else if ( e instanceof ExtendedSystemException ) {
            Stack errors = (( ExtendedSystemException ) e).getErrors();
            while ( ! errors.empty () ) {
                System.err.println ( "Nested error: " );
                Exception nxt = ( Exception ) errors.pop();
                printError ( nxt );
            }
        }

    }
    public static void test() throws Exception
    {
        //TSInitInfo info = UserTransactionServiceFactory.createTSInitInfo();
        UserTransactionService uts =
            new com.atomikos.icatch.standalone.
                UserTransactionServiceFactory().getUserTransactionService( new Properties() );
        TSInitInfo info = uts.createTSInitInfo();

        com.atomikos.icatch.imp.XARecoveryTester.test ( uts , info );
		info = uts.createTSInitInfo();
        com.atomikos.icatch.jta.ReleaseTester.test ( uts , info );


        info = uts.createTSInitInfo();
        ReleaseTester.test ( uts , info , false );
    }

    public static void main ( String[] args )
    {
        try {
            System.err.println ( "Starting standalone test..." );
            test();
        }

        catch ( Exception e ) {
            printError ( e );
        }
        finally {
            System.err.println ( "Done." );
        }
    }
}

