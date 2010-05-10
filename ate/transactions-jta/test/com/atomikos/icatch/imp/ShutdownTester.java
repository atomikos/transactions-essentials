//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//$Log: ShutdownTester.java,v $
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
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:18  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2005/08/05 15:04:22  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.4  2004/10/12 13:03:51  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.3  2004/03/22 15:38:06  guy
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.2.2.3  2003/06/20 16:31:47  guy
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//*** empty log message ***
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.2.2.2  2003/05/30 15:18:57  guy
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Changed UserTransactionFactory.getUserTransaction: added Properties arg.
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.2.2.1  2003/05/22 15:24:34  guy
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Adjusted to new UserTransactionServiceFactory.
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.2  2003/03/11 06:39:13  guy
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: ShutdownTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//Revision 1.1.2.2  2002/11/14 15:01:57  guy
//Adapted to new (redesigned) paradigm: getTx based in tid and suspend/resume should not work with a stack.
//
//Revision 1.1.2.1  2002/10/09 17:13:49  guy
//Added test for shutdown with wait option.
//

package com.atomikos.icatch.imp;
import java.util.Properties;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.HeuristicParticipant;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.admin.imp.LocalLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;

 /**
  *
  *
  *A test program that verifies shutdown with wait option.
  *Executing this program will create a heuristic transaction and
  *call shutdown(false). The VM should only exit AFTER the GUI
  *administrator window has been used to terminate the tx.
  */

public class ShutdownTester
{
    public static void main ( String args[] ) throws Exception
    {
        UserTransactionService uts = null;
        
        try {
            //first, do setup
            //TSInitInfo info = UserTransactionServiceFactory.createTSInitInfo();
            uts =   new com.atomikos.icatch.standalone.
                    UserTransactionServiceFactory().getUserTransactionService ( new Properties() );
            TSInitInfo info = uts.createTSInitInfo();
            LocalLogAdministrator admin =
                new LocalLogAdministrator ( "AdminTool" , false );
            info.registerLogAdministrator ( admin );


            uts.init ( info );
            
            
            //now, create a heuristic transaction
            CompositeTransactionManager ctm =
                uts.getCompositeTransactionManager();
            CompositeTransaction ct = 
                ctm.createCompositeTransaction ( 30000 );
            HeuristicMessage msg =
                new StringHeuristicMessage ( "Simulation of heuristic" );
            
                
            HeuristicParticipant p = 
                new HeuristicParticipant ( msg );
            //p.setFailMode ( HeuristicParticipant.FAIL_HEUR_MIXED );
            ct.addParticipant ( p );
            
            ct.getTransactionControl().getTerminator().commit();
        
        }
        catch ( Throwable e ) {
            System.err.println ( e.getClass().getName() + e.getMessage() );
            e.printStackTrace(); 
        }
        finally {
            uts.shutdown ( false );
            System.err.println ( "Done" );
            System.exit ( 0 );
        }
    } 
}
