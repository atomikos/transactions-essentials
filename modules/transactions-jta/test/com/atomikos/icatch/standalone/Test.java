//$Id: Test.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//$Log: Test.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
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
//Revision 1.1.1.1  2006/03/22 13:46:56  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:20  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.8  2005/08/05 15:04:20  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.7  2004/10/12 13:03:49  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.6  2004/03/22 15:38:03  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.5.10.4  2004/03/16 16:51:39  guy
//Corrected tests to make them run
//
//Revision 1.5.10.3  2003/05/30 15:18:51  guy
//Changed UserTransactionFactory.getUserTransaction: added Properties arg.
//
//Revision 1.5.10.2  2003/05/22 15:24:14  guy
//Renamed Configuration to UserTransactionServiceFactory.
//
//Revision 1.5.10.1  2003/05/22 14:23:28  guy
//Updated a bit.
//
//Revision 1.5  2002/03/04 16:36:03  guy
//Added a recovery tester call.
//
//Revision 1.4  2002/02/26 12:45:50  guy
//Added more error analysis.
//
//Revision 1.3  2002/02/26 12:01:02  guy
//Added exception analysis.
//
//Revision 1.2  2002/02/25 14:52:04  guy
//Added JTA test. Updated test infrastructure.
//
//Revision 1.1  2002/02/22 16:54:53  guy
//Added Test class.
//

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

