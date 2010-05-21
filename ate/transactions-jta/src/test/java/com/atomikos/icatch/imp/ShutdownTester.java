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
