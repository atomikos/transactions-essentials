
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//$Log: ReleaseTester.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.2  2006/04/11 11:42:39  guy
//Extracted init properties as constants and replaced all literal references.
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
//Revision 1.2  2006/03/21 13:23:31  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:18  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.28  2005/08/09 15:24:10  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.27  2005/08/05 15:04:21  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.26  2004/11/01 11:01:40  guy
//Updated.
//
//Revision 1.25  2004/10/30 16:22:00  guy
//Added more output on exception.
//
//Revision 1.24  2004/10/12 13:03:51  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.23  2004/10/01 15:16:18  guy
//Adapted to new JBoss integration tuning.
//
//Revision 1.22  2004/09/01 13:39:18  guy
//Merged changes from TransactionsRMI 1.22.
//
//Revision 1.21  2004/03/22 17:19:32  guy
//Corrected test: actives should be set in any case, not just for propagation
//
//Revision 1.20  2004/03/22 16:28:05  guy
//Corrected test properties to include new prefix
//
//Revision 1.19  2004/03/22 15:38:06  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.18  2003/08/27 06:23:55  guy
//Adapted to RMI-IIOP.

//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//$Log: ReleaseTester.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:13  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.2  2006/04/11 11:42:39  guy
//Extracted init properties as constants and replaced all literal references.
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
//Revision 1.2  2006/03/21 13:23:31  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:18  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.28  2005/08/09 15:24:10  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.27  2005/08/05 15:04:21  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.26  2004/11/01 11:01:40  guy
//Updated.
//
//Revision 1.25  2004/10/30 16:22:00  guy
//Added more output on exception.
//
//Revision 1.24  2004/10/12 13:03:51  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.23  2004/10/01 15:16:18  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Adapted to new JBoss integration tuning.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.22  2004/09/01 13:39:18  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Merged changes from TransactionsRMI 1.22.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.21  2004/03/22 17:19:32  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Corrected test: actives should be set in any case, not just for propagation
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.20  2004/03/22 16:28:05  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Corrected test properties to include new prefix
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.19  2004/03/22 15:38:06  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.17.2.5  2004/03/16 16:51:41  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Corrected tests to make them run
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.17.2.4  2004/01/14 10:38:46  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//*** empty log message ***
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.17.2.3  2003/06/20 16:31:47  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//*** empty log message ***
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.17.2.2  2003/05/30 15:18:57  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Changed UserTransactionFactory.getUserTransaction: added Properties arg.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.17.2.1  2003/05/22 15:24:34  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Adjusted to new UserTransactionServiceFactory.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.17  2003/04/03 12:26:05  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Added tests for recovery and LogAdministration in case of propagation.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.16  2003/03/26 19:36:11  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Added tests for XAResource rollback.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.15  2003/03/23 15:20:00  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Corrected BUG in 1PC requested from remote TM in RMI. Added tests for this.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.14  2003/03/22 09:52:24  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Added test case for READONLY commit through local terminator.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Revision 1.13  2003/03/11 06:39:13  guy
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: ReleaseTester.java,v 1.1.1.1 2006/08/29 10:01:13 guy Exp $
//
//Revision 1.12.4.5  2003/01/31 15:45:31  guy
//Adapted to set/get Properties in TSInitInfo.
//
//Revision 1.12.4.4  2002/12/22 17:09:04  guy
//Added test utility and test case for improved beforeCompletion().
//
//Revision 1.12.4.3  2002/11/18 17:50:51  guy
//Corrected tests.
//
//Revision 1.12.4.2  2002/11/14 15:01:57  guy
//Adapted to new (redesigned) paradigm: getTx based in tid and suspend/resume should not work with a stack.
//
//Revision 1.12.4.1  2002/08/29 07:23:23  guy
//Adapted to new JdbcTransactionalResource (improved XAResource support).
//
//Revision 1.12  2002/03/11 11:54:33  guy
//Added more info on print of assertion failure.
//
//Revision 1.11  2002/03/11 11:44:28  guy
//Update test to use a different resource name.
//
//Revision 1.10  2002/03/04 16:36:45  guy
//Expanded heuristic test range to 4 instead of 3.
//
//Revision 1.9  2002/03/03 11:20:05  guy
//Adapted to setTag which is now in TransactionControl.
//Also getState is only in CompositeTransaction, no longer in TransactionControl.
//
//Revision 1.8  2002/03/02 16:56:31  guy
//Added some tests.
//
//Revision 1.7  2002/03/01 16:48:45  guy
//Added test facilities for testing heuristic error combinations
//during 2PC.
//
//Revision 1.6  2002/03/01 10:48:04  guy
//Updated to new prepare exception of HeurMixed.
//Added more failure modes and test facilities.
//
//Revision 1.5  2002/02/25 14:52:19  guy
//Updated test infrastructure.
//
//Revision 1.4  2002/02/22 17:28:48  guy
//Updated: nocom.atomikos.icatch.RollbackException in addParticipant and registerSynch.
//
//Revision 1.3  2002/02/22 16:55:22  guy
//Updated tests.
//

package com.atomikos.icatch.imp;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import javax.naming.Context;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.TestRecoverableResource;
import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.CompositeTransactionStub;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.ExtentParticipant;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.HeuristicParticipant;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.ReadOnlyParticipant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.TestParticipant;
import com.atomikos.icatch.TestSubTxAwareParticipant;
import com.atomikos.icatch.TestSynchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.VoteNoParticipant;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;

/**
  *
  *
  *A test class for testing a specific release.
  *This class provides testing at the public (user) level of all core
  *transaction management interfaces and functions.
  */
  
public class ReleaseTester
{
    static TestXAResource xaRes_ = new TestXAResource();
    
     /**	
       *Utility function called by testHeuristics. This method
       *tries to commit a transaction whose participant set
       *behaves according to the supplied error flag codes.
       *@param uts The transaction service.
       *@param combination The combination of error codes
       *that indicates the desired participant behaviour.
       *
       *@exception HeurRollbackException If an unexpected
       *heurist rollback error happens.
       *@exception HeurHazardException If a heuristic hazard
       *case arises inappropriately.
       *@exception HeurMixedException If a heuristic mixed
       *case arises inappropriately
       *@excetpoin Exception On general errors.
       */
       
     private static void testCombination ( 
          UserTransactionService uts , int[] combination )
          throws HeurRollbackException, 
          HeurHazardException, HeurMixedException ,
          Exception
      {
          CompositeTransactionManager ctm =
              uts.getCompositeTransactionManager();
          
          CompositeTransaction ct = 
              ctm.createCompositeTransaction ( 1000 );
          
          CompositeTerminator term =
              ct.getTransactionControl().getTerminator();
          
          //add the right set of participants to the transaction
          for ( int i = 0 ; i < combination.length ; i++ ) {
              StringHeuristicMessage msg =
                  new StringHeuristicMessage ( "Heuristic participant" );
              HeuristicParticipant hPart = 
                  new HeuristicParticipant ( msg );
              hPart.setFailMode ( combination[i] );
              ct.addParticipant ( hPart );
          }
          
          //calculate the expected result of commit
          int expected = 
              HeuristicParticipant.getExpectedCommitResult ( combination );
          
          try {
              ct.commit();
              //acceptable expected values are 
              //FAIL_HEUR_COMMIT and NO_FAIL
              if ( ! ( expected == HeuristicParticipant.FAIL_HEUR_COMMIT ||
                       expected == HeuristicParticipant.NO_FAIL ) )
                      throw new Exception ( 
                      "Unexpected success of commit: expected " + expected );
          }
          catch ( HeurRollbackException hr ) {
              if ( expected != HeuristicParticipant.FAIL_HEUR_ROLLBACK )
                  throw hr;
          }
          catch ( HeurMixedException hm ) {
              if ( expected != HeuristicParticipant.FAIL_HEUR_MIXED )
                  throw hm; 
          } 
          catch ( HeurHazardException hh ) {
              if ( expected != HeuristicParticipant.FAIL_HEUR_HAZARD )
                  throw hh;
          }
          
          
      }
      
       /**
        *Test all heuristic combinations during 2PC commit.
        *@param uts The transaction service handle.
        */
        
      private static void testHeuristicCommit ( 
          UserTransactionService uts )
          throws Exception
      {
             //test all combinations in the range 0-3
             //but NOT including 4 (heuristic commit)
             //since this exception does not occur
             //on global commit
             
             System.err.println ( "Testing heuristics, this will take some time!" );
             for ( int i1 = 0 ; i1 <= 3 ; i1++ ) {
                for ( int i2 = 0 ; i2 <= 3 ; i2++ ) {
                  
                  for ( int i3 = 0 ; i3 <= 3 ; i3++ ) {
                    for ( int i4 = 0 ; i4 <= 3 ; i4++ ) {
                        System.err.print ( "." );
                        try {
                          int[] combination = { i1 , i2 , i3 , i4 };
                          testCombination ( uts , combination );
                        }
                        catch ( Exception e ) {
                        	e.printStackTrace();
                            throw new Exception ( 
                                "Heuristic state error: " +
                                e.getMessage() + e.getClass().getName() +
                                "for HeuristicParticipant flag combination: " +
                                i1 + "," + i2 + "," + i3 + "," + i4  );
                        }
                      
                    }
                  }
                }
             }
             System.err.println ( "" );
             System.err.println ( "Testing heuristics: done!" );
      }
      
       /**
        *Test heuristic exceptions and states during prepare.
        *
        *@param uts The user transaction service.
        */
        
      private static void testHeuristicPrepare ( UserTransactionService uts )
      throws Exception
      {
           CompositeTransactionManager ctm = 
              uts.getCompositeTransactionManager();
           CompositeTransaction ct = null;
           HeuristicParticipant hPart = null ;
           VoteNoParticipant vnPart  = null;
           CompositeTerminator term = null;
           StringHeuristicMessage msg =
              new StringHeuristicMessage ( "Test" );
          
           System.err.println ( "Testing heuristic prepares" );
           
           //
          //CASE 1: test prepare with one no-voter 
          //and a Heuristic Hazard case
          //
          
          ct = ctm.createCompositeTransaction ( 10000 );
          term = ct.getTransactionControl().getTerminator();
          
          //adding a no-voter will make sure that prepare leads
          //to a rollback
          vnPart = new VoteNoParticipant(); 
          ct.addParticipant ( vnPart );
          
          //adding a heuristic participant will make sure that the rollback
          //fails
          hPart = new HeuristicParticipant ( msg );
          hPart.setFailMode ( HeuristicParticipant.FAIL_HEUR_HAZARD );
          ct.addParticipant ( hPart );
          
          try {
              ct.commit();
              throw new Exception ( "Commit should throw hazard?" );
          }
          catch ( HeurHazardException hh ) {
              //should happen
          }
          
          //
          //CASE 2: test prepare with a no-voter and a heuristic mixed case.
          //
          
          ct = ctm.createCompositeTransaction ( 10000 );
          term = ct.getTransactionControl().getTerminator();
          
          //adding a no-voter will make sure that prepare leads
          //to a rollback
          vnPart = new VoteNoParticipant(); 
          ct.addParticipant ( vnPart );
          
          //adding a heuristic participant will make sure that the rollback
          //fails
          hPart = new HeuristicParticipant ( msg );
          hPart.setFailMode ( HeuristicParticipant.FAIL_HEUR_MIXED );
          ct.addParticipant ( hPart );
          
          try {
              ct.commit();
              throw new Exception ( "Commit should throw mixed?" );
          }
          catch ( HeurMixedException hh ) {
              //should happen
          }
          
          //
          //CASE 3: test prepare with a no voter and a heuristic commit
          //
          
          ct = ctm.createCompositeTransaction ( 10000 );
          term = ct.getTransactionControl().getTerminator();
          
          //adding a no-voter will make sure that prepare leads
          //to a rollback
          vnPart = new VoteNoParticipant(); 
          ct.addParticipant ( vnPart );
          
          //adding a heuristic participant will make sure that the rollback
          //fails
          hPart = new HeuristicParticipant ( msg );
          hPart.setFailMode ( HeuristicParticipant.FAIL_HEUR_COMMIT );
          ct.addParticipant ( hPart );
          
          try {
              ct.commit();
              throw new Exception ( "Commit should throw mixed?" );
          }
          catch ( HeurMixedException hh ) {
              //should happen
          }
          
          //
          //CASE 4: prepare with a no voter and a heuristic rollback
          //
          
          ct = ctm.createCompositeTransaction ( 10000 );
          term = ct.getTransactionControl().getTerminator();
          
          //adding a no-voter will make sure that prepare leads
          //to a rollback
          vnPart = new VoteNoParticipant(); 
          ct.addParticipant ( vnPart );
          
          //adding a heuristic participant will make sure that the rollback
          //fails
          hPart = new HeuristicParticipant ( msg );
          hPart.setFailMode ( HeuristicParticipant.FAIL_HEUR_ROLLBACK );
          ct.addParticipant ( hPart );
          
          try {
            ct.commit();
          }
          catch (com.atomikos.icatch.RollbackException rb ) {
              //should happen: no vote and heur rollback corresponds to
              //global decision
          }
          catch ( Exception e ) {
              //should NOT happen: heuristic rollback corresponds with
              //global decision -> no exception expected
              throw new Exception ( 
              "Commit should succeed but fails with error: " + 
              e.getMessage() );
          }   
           
           //
           //CASE 5: add a no voter and a normal case
           //
           
          ct = ctm.createCompositeTransaction ( 10000 );
          term = ct.getTransactionControl().getTerminator();
          
          //adding a no-voter will make sure that prepare leads
          //to a rollback
          vnPart = new VoteNoParticipant(); 
          ct.addParticipant ( vnPart );
          
          //adding a heuristic participant will make sure that the rollback
          //fails
          hPart = new HeuristicParticipant ( msg );
          hPart.setFailMode ( HeuristicParticipant.NO_FAIL );
          ct.addParticipant ( hPart );
          
          try {
              ct.commit();
              
          }
          catch (com.atomikos.icatch.RollbackException rb ) {
              //should happen
          }
          
          System.err.println ( "Testing heuristic prepares: done!" );
      }
      
       /**
        *Test the subtx relationship and method thereto.
        *@param child The child.
        *@param parent The parent of the child.
        */
        
      private static void testSubTx ( 
      CompositeTransaction child, CompositeTransaction parent )
      throws Exception
      {
            if ( ! parent.isSameTransaction ( parent ) )
                throw new Exception ( "isSameTransaction() does not work?" );
            if ( ! child.isSameTransaction ( child ) )
                throw new Exception ( "isSameTransaction() does not work?" );
            if ( child.isSameTransaction ( parent ) )
                throw new Exception ( "isSameTransaction() does not work?" );

            if ( child.isRoot() )
                throw new Exception ( "isRoot() true for child?" );
            if ( ! ( child.isLocal() && parent.isLocal() ) )
                throw new Exception ( "isLocal() does not work?" );
            
            if ( ! parent.isAncestorOf ( child ) )
                throw new Exception ( "isAncestorOf() does not work?" );
            if ( ! child.isDescendantOf ( parent ) )
                throw new Exception ( "isDescendantOf() does not work?" );
            if ( ! child.isRelatedTransaction ( parent ) )
                throw new Exception ( "isRelatedTransaction() does not work?" );
            if ( child.isSameTransaction ( parent ) )
                throw new Exception ( "isSameTransaction() does not work?" );
                
            Stack lineage = child.getLineage();
            CompositeTransaction tester = 
                ( CompositeTransaction ) lineage.peek();
            if ( ! tester.getTid().equals ( parent.getTid() ) )
                throw new Exception ( "getTid() of lineage not same as parent's?" );
            
      }
    
     /**
      *Test if the administrative interfaces work as they should.
      */
      
    private static void testAdministration ( 
    UserTransactionService uts , TestLogAdministrator admin , 
    boolean propagation )
    throws Exception
    {
        System.err.println ( "Testing admin interfaces" );
        
        CompositeTransactionManager ctm = 
            uts.getCompositeTransactionManager();
        ExportingTransactionManager etm = 
            uts.getExportingTransactionManager();
        ImportingTransactionManager itm = 
            uts.getImportingTransactionManager();
        
        //
        //CASE 1: commit with heuristic participant
        //
        
        CompositeTransaction ct = 
            ctm.createCompositeTransaction ( 10000 );
        StringHeuristicMessage tag = new StringHeuristicMessage ( "Test" );
        ct.getTransactionControl().setTag ( tag );
        String tid = ct.getTid();
        
        StringHeuristicMessage msg = new StringHeuristicMessage ( "TestMsg" );
        HeuristicParticipant hPart = new HeuristicParticipant ( msg );
        ct.addParticipant ( hPart );
        
        
        StringHeuristicMessage msg2 = new StringHeuristicMessage ( "TestMsg2" );
        HeuristicParticipant hPart2 = new HeuristicParticipant ( msg2 );
        ct.addParticipant ( hPart2 );
        //add readonly participant : test for previous BUG: forget
        //would block if readonlyParticipant is present
        ReadOnlyParticipant roPart = new ReadOnlyParticipant();
        ct.addParticipant ( roPart );
        
        System.err.println ( "About to simulate heuristic hazard - this might take a while..." );
        try {
            ct.commit();
            throw new Exception ( "Heuristic participant not recognized?" );
        }
        catch ( HeurHazardException hh ) {
            //should happen
        }
        System.err.println ( "Heuristic hazard done!" );
        
        //assert that no tx association exists for the thread
        if ( ctm.getCompositeTransaction() != null )
            throw new Exception ( "Tx exists for thread AFTER termination?" );
        
        //test if the corresponding AdminTransaction exists
        String[] tids = new String[1];
        tids[0] = tid;
        AdminTransaction[] txs = admin.getLogControl().getAdminTransactions ( tids );
        
        //assert that returned list has exactly ONE value in it
        if ( txs == null || txs.length != 1 )
            throw new Exception ( "LogControl does not work?" );
        
        //asssert that the one value returns is the tx with our id
        if ( ! txs[0].getTid().equals ( tid ) )
            throw new Exception ( "LogControl returns wrong AdminTransaction?" );
        
        //assert that the transaction was intended to commit
        if ( ! txs[0].wasCommitted() )
            throw new Exception ( "wasCommitted() does not work for commit?" );
        
        //assert that the state of the returned tx is correct
        if (  txs[0].getState() != AdminTransaction.STATE_HEUR_HAZARD )
            throw new Exception ( "Wrong state for admintransaction?" );
        
        HeuristicMessage[] tags = txs[0].getTags();
        
        //assert that tags has one element
        if ( tags == null || tags.length != 1 )
            throw new Exception ( "getTags returns too many tags?" );
          
        //assert that the original tag is returned
        if ( ! tags[0].toString().equals ( tag.toString() ) )
            throw new Exception ( "getTags returns wrong tag?" );
        
        HeuristicMessage[] msgs = txs[0].getHeuristicMessages();
        
        //assert that two msgs are there
        if ( msgs == null || msgs.length < 2 )
            throw new Exception ( "getHeuristicMessages() does not return all?" );
        
        Hashtable testMsgTable = new Hashtable();
        for ( int i = 0 ; i < msgs.length ; i++ ) {
        	testMsgTable.put ( msgs[i].toString() , msgs[i].toString() );
        }
        
        //assert that both messages are returned as original
        if (  ! (  ( testMsgTable.containsKey ( msg.toString() ) 
                     ) 
                     &&
                     ( testMsgTable.containsKey ( msg2.toString() )
                     )
                  )  )
                  
              throw new Exception ( 
              "getHeuristicMessages returns different msgs?" );
              
        msgs = txs[0].getHeuristicMessages ( AdminTransaction.STATE_HEUR_HAZARD );
          
          //assert that two msgs are there
        if ( msgs == null || msgs.length != 2 )
            throw new Exception ( "getHeuristicMessages(state) does not return all?" );
        
        //assert that both messages are returned as original
        if (  ! (  ( msgs[0].toString().equals ( msg.toString() ) || 
                     msgs[1].toString().equals ( msg.toString() ) 
                     ) 
                     &&
                     ( msgs[0].toString().equals ( msg2.toString() ) || 
                     msgs[1].toString().equals ( msg2.toString() ) 
                     )
                  )  )
                  
              throw new Exception ( 
              "getHeuristicMessages(state) returns different msgs?" );
        
        
        txs[0].forceForget();
        
        //assert that the transaction is really forgotten by the log
        txs = admin.getLogControl().getAdminTransactions ( tids );
        if ( ! ( txs == null || txs.length == 0 ) ) {
                throw new Exception ( "forceForget() does not work?" );
        }
        
        //
        //FOLLOWING TESTS ARE ONLY POSSIBLE IF PROPAGATION IS IMPLEMENTED
        //
        
        if ( propagation ) {
            
            //
            //CASE 2: test if forceCommit works
            //
            
            //create a ct with a large timeout, to maintain indoubt state
            ct = ctm.createCompositeTransaction ( 10000 );
            tid = ct.getTid();
            
            //'export' the transaction, and import again (to obtain extent)
            Propagation prop = etm.getPropagation();
            CompositeTransaction child = itm.importTransaction ( prop , true , false );
            child.addParticipant ( new TestParticipant() );
            Extent extent = itm.terminated ( true );
            
            //retrieve the TM's participant from the extent, and bring it
            //to indoubt state
            Stack parts = extent.getParticipants();
            if ( parts == null || parts.empty() )
                throw new Exception ( "No participants in extent?" );
            
            Participant part = ( Participant ) parts.peek();
            part.setCascadeList ( new Hashtable() );
            part.setGlobalSiblingCount ( 1 );
            int vote = part.prepare();
            if ( vote == Participant.READ_ONLY ) 
                throw new Exception ( "Prepare should not return readonly?" );
            
            //the local transaction should now be prepared => test forceCommit
            //test if the corresponding AdminTransaction exists
            tids = new String[1];
            tids[0] = tid;
            txs = admin.getLogControl().getAdminTransactions ( tids );
            
            //assert that returned list has exactly ONE value in it
            if ( txs == null || txs.length != 1 )
                throw new Exception ( "LogControl does not return indoubt?" );
            
            //assert that state is really indoubt
            if (  txs[0].getState() != AdminTransaction.STATE_PREPARED )
                throw new Exception ( "AdminTransaction not PREPARED after prepare?" );
            
            //try if forceCommit works
            txs[0].forceCommit();
            
            //assert that the state is now heuristic commit
            if ( txs[0].getState() != AdminTransaction.STATE_HEUR_COMMITTED )
                throw new Exception ( 
                "getState() not HEUR_COMMITTED after forceCommit but rather " + txs[0].getState() );
            
            //assert that wasCommitted works
            if ( ! txs[0].wasCommitted() )
                throw new Exception ( "wasCommitted false after forceCommit?" );
            
                
            try {
                ct.rollback();
            }
            catch ( Exception e ) {
                //should happen: tx already terminated 
            }
            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( "Tx exists for thread AFTER termination?" );
            
            //
            //CASE 3: ASSERT THAT FORCEROLLBACK WORKS
            //
            
            //create a ct with a large timeout, to maintain indoubt state
            ct = ctm.createCompositeTransaction ( 10000 );
            tid = ct.getTid();
            
            //'export' the transaction, and import again (to obtain extent)
            prop = etm.getPropagation();
            child = itm.importTransaction ( prop , true , false );
            child.addParticipant ( new TestParticipant() );
            extent = itm.terminated ( true );
            
            //retrieve the TM's participant from the extent, and bring it
            //to indoubt state
            parts = extent.getParticipants();
            if ( parts == null || parts.empty() )
                throw new Exception ( "No participants in extent?" );
            
            part = ( Participant ) parts.peek();
            part.setCascadeList ( new Hashtable() );
            part.setGlobalSiblingCount ( 1 );
            vote = part.prepare();
            if ( vote == Participant.READ_ONLY ) 
                throw new Exception ( "Prepare should not return readonly?" );
            
            //the local transaction should now be prepared => test forceCommit
            //test if the corresponding AdminTransaction exists
            tids = new String[1];
            tids[0] = tid;
            txs = admin.getLogControl().getAdminTransactions ( tids );
            
            //assert that returned list has exactly ONE value in it
            if ( txs == null || txs.length != 1 )
                throw new Exception ( "LogControl does not return indoubt?" );
            
            //assert that state is really indoubt
            if (  txs[0].getState() != AdminTransaction.STATE_PREPARED )
                throw new Exception ( "AdminTransaction not PREPARED after prepare?" );
            
            //try if forceRollback works
            txs[0].forceRollback();
            
            //assert that the state is now heuristic commit
            if ( txs[0].getState() != AdminTransaction.STATE_HEUR_ABORTED )
                throw new Exception ( 
                "getState() not HEUR_ABORTED after forceRollback?" );
            
            //assert that wasCommitted works
            if ( txs[0].wasCommitted() )
                throw new Exception ( "wasCommitted true after forceRollback?" );
            
                
            try {
                ct.rollback();
            }
            catch ( Exception e ) {
                //should happen: tx already terminated 
            }
            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( "Tx exists for thread AFTER termination?" );
        }
        
        System.err.println ( "Testing admin interfaces: done!" );
    }
     
     /**
      *Test the native (icatch) interfaces for transaction management.
      */
      
      private static void testNative ( 
      UserTransactionService uts , TestLogAdministrator admin  )
      throws Exception
      {
            System.err.println ( "Testing native icatch interfaces" );
            
            //
            //FIRST, TEST THE TRANSACTION MANAGER
            //AND SOME GENERAL FUNCTIONS
            //
            
            CompositeTransactionManager ctm =
                uts.getCompositeTransactionManager();
            if ( ctm == null )
                throw new Exception ( "No composite transaction manager?" );
            
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( 
                "Comp. tx. mgr.: thread has tx before any was created?" );
            
            CompositeTransaction parent = 
                ctm.createCompositeTransaction ( 1000 );
            if ( ctm.getCompositeTransaction() != parent )
                throw new Exception ( "getCompositeTransaction() does not work?" );
            
            //create a subtx of the parent
            CompositeTransaction child = 
                                ctm.createCompositeTransaction ( 1000 );
            if ( ctm.getCompositeTransaction() != child )
                throw new Exception ( "getCompositeTransaction() does not work?" );
            
            if ( ! parent.isRoot() )
                throw new Exception ( "isRoot() false for root?" );
            
            testSubTx ( child, parent );    
            
            CompositeTransaction suspended = ctm.suspend();
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( "suspend() does not work?" );
            
            ctm.resume ( suspended );
            if ( ctm.getCompositeTransaction() != child ) 
                throw new Exception ( "resume() does not work?" );
            
            child.rollback();
            parent.rollback();
            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( "Tx exists for thread AFTER termination?" );
            
            //
            //SECONDLY, TEST THE COMPOSITE TRANSACTION ITSELF
            //
            
            //
            //CASE 1: TEST NORMAL 2PC + COMMIT AND CALLBACKS
            //
            
            CompositeTransaction ct = 
                ctm.createCompositeTransaction ( 1000 );
                
            TestParticipant tPart = new TestParticipant();
            ReadOnlyParticipant rPart = new ReadOnlyParticipant();
            TestSubTxAwareParticipant subPart = 
                new TestSubTxAwareParticipant();
           TestSynchronization sync = new TestSynchronization();
            
            //intermezzo: create subtx to test synchronization.beforeCompletion
            //at subtx commit time
            CompositeTransaction subtx = 
            ctm.createCompositeTransaction ( 1000 );
            TestSynchronization sync2 = new TestSynchronization ( subtx );
            subtx.registerSynchronization ( sync2 );
            subtx.commit();
            //assert that beforecompletion was called
            if ( !sync2.isCalledBefore() )
                throw new Exception ( 
                "beforeCompletion not called upon subtx commit?" );
            if ( sync2.isCalledAfter() )
                throw new Exception ( 
                "afterCompletion called for subtx commit?" );
            
            //next, continue with the top-level tx
            ct.addParticipant ( rPart );
            ct.addParticipant ( tPart );
            
            ct.addSubTxAwareParticipant ( subPart );
            ct.registerSynchronization ( sync );
            
            //first, register
            
            ct.commit();
            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( "Tx exists for thread AFTER termination?" );
            
            //a sync should have been notified
            if ( ! sync.isCalledBefore() || ! sync.isCalledAfter() )
                throw new Exception ( "Synchronization not called on commit?" );
            
            //a subtx aware participant should have been notified
            if ( ! subPart.isNotified() )
                throw new Exception ( "SubTxAware not called for commit?" );
            
            //a normal participant should have seen a commit
            if ( !tPart.isTerminated() )
                throw new Exception ( "2PC commit does not work?" );
                
            //a readonly participant should NOT have seen commit
            if ( rPart.isTerminated() )
                throw new Exception ( "2PC for readonly: commit called?" );
            
            //a new participant should not be able to register after commit
            try {
                TestParticipant tPart2 = new TestParticipant();
                ct.addParticipant ( tPart2 );
                throw new Exception ( "Participant added after commit?" );
            }
            catch ( IllegalStateException e ) {
                //should happen: added participant after commit!
            }
            
            //adding another subtx aware should fail after commit
            try {
                ct.addSubTxAwareParticipant ( subPart );
                throw new Exception ( "SubTxAware added after commit?"  );
            }
            catch ( IllegalStateException e )  {
                //should happen
            }
            
            //adding another sync should fail after commit
            try {
                ct.registerSynchronization ( sync );
                throw new Exception ( "Synchronization added after commit?"  );
            }
            catch ( IllegalStateException e )  {
                //should happen
            }
            
            //
            //CASE 2: TEST ROLLBACK AND CALLBACKS
            //
            
            ct = ctm.createCompositeTransaction ( 1000 );
            tPart = new TestParticipant();
            rPart = new ReadOnlyParticipant();
            subPart = new TestSubTxAwareParticipant();
            sync = new TestSynchronization();
            
            ct.addParticipant ( tPart );
            ct.addParticipant ( rPart );
            ct.addSubTxAwareParticipant ( subPart );
            ct.registerSynchronization ( sync );
            
            
            ct.rollback();
            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( "Tx exists for thread AFTER termination?" );
            
            //a sync should NOT have been notified
            if ( sync.isCalledBefore() || sync.isCalledAfter() )
                throw new Exception ( "Synchronization called on rollback?" );
            
            //a subtx aware participant should have been notified
            if ( ! subPart.isNotified() )
                throw new Exception ( "SubTxAware not called for rollback?" );
            
            //a normal participant should have seen a rollback
            if ( !tPart.isTerminated() )
                throw new Exception ( "Rollback does not work?" );
                
            
            //a new participant should not be able to register after rollback
            try {
                TestParticipant tPart2 = new TestParticipant();
                ct.addParticipant ( tPart2 );
                if ( !tPart2.isTerminated() )
               		 throw new Exception ( "Participant not rolledback?" );
            }
            catch ( IllegalStateException ill ) {
                //should happen: no longer active 
            }
            
            
            //adding another subtx aware should fail after rollback
            try {
            	subPart = new TestSubTxAwareParticipant();
                ct.addSubTxAwareParticipant ( subPart );
                if ( ! subPart.isNotified() )
                	throw new Exception ( "SubTxAware added after rollback and not notified?"  );
            }
            catch ( IllegalStateException e )  {
                //should happen
            }
            
            //adding another sync should fail after rollback
            try {
                ct.registerSynchronization ( sync );
                throw new Exception ( "Synchronization added after rollback?"  );
            }
            catch ( IllegalStateException ill ) {
                //should happen 
            }
            
            //
            //CASE 3: TEST COMPOSITE COORDINATOR HANDLE
            //
            
            ct = ctm.createCompositeTransaction ( 1000 );
            tPart = new TestParticipant();
            rPart = new ReadOnlyParticipant();
           
            
            RecoveryCoordinator rec_coord = ct.addParticipant ( tPart );
            ct.addParticipant ( rPart );
            StringHeuristicMessage msg =
                new StringHeuristicMessage ( "TestMsg" );
            ct.getTransactionControl().setTag ( msg );
            CompositeCoordinator coord = ct.getCompositeCoordinator();
            
            
            if ( ! rec_coord.equals ( coord.getRecoveryCoordinator() ) )
                throw new Exception ( 
                "getRecoveryCoordinator() does not work?" );
            
            if ( ! ct.getTid().equals ( coord.getCoordinatorId() ) )
                throw new Exception ( "getRootTid() does not work?" );
            
            ct.commit();
            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( "Tx exists for thread AFTER termination?" );
            
             if ( ! msg.toString().equals ( coord.getTags()[0].toString() ) )
                throw new Exception ( "setTag()/getTags() does not work?" );
            
            //
            //CASE 4: TEST REPLAY COMPLETION
            //
            
            ct = ctm.createCompositeTransaction ( 1000 );
            rPart = new ReadOnlyParticipant();
          
            HeuristicParticipant hPart = 
              new HeuristicParticipant ( 
              new StringHeuristicMessage ( "Test" ) );
            
            ct.addParticipant ( rPart );
            rec_coord = ct.addParticipant ( hPart );
            
            //assert that a participant asking for replay gets
            //an exception
            tPart = new TestParticipant();
            try {
                rec_coord.replayCompletion ( tPart );
                throw new Exception ( "Replay works before termination?" );
            }
            catch ( IllegalStateException ill ) {
                //should happen
            }
            
            System.err.println ( "About to simulate heuristic hazard - this might take a while..." );

            
            try {
                ct.commit();
            }
            catch ( HeurHazardException hh ) {
                //should happen due to heuristic participant
            }
            System.err.println ( "Heuristic hazard done!" );

            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( "Tx exists for thread AFTER termination?" );
            
            //now, set hPart to NOT fail next time, and test replay
            hPart.setFailMode ( HeuristicParticipant.NO_FAIL );
            Boolean commit = rec_coord.replayCompletion ( hPart );
            if ( commit == null || ! commit.booleanValue() )
                throw new Exception ( "replayCompletion() return is wrong?" );
                
            //sleep a while to make replay possible
            try {
                System.err.println ( 
                    "Test replay: about to sleep for 30 secs..." );
                Thread.currentThread().sleep ( 30000 );
                System.err.println ( "Woken up again!" );
            }
            catch ( InterruptedException inter ) {}
            
            //assert that tx state is now terminated
            if ( ! ct.getState().
                   equals ( TxState.TERMINATED ) )
                throw new Exception ( "State not TERMINATED after replay?" );
                
            //
            //CASE 5: TEST THE TRANSACTIONCONTROL HANDLE
            //
            
            parent = ctm.createCompositeTransaction ( 1000 );
            long timeout = parent.getTransactionControl().getTimeout();
            
            if ( timeout > 1000 )
                throw new Exception ( "getTimeout() does not work: " + timeout );
            
            parent.getTransactionControl().setSerial();
            
            tPart = new TestParticipant();
          
            child = parent.getTransactionControl().createSubTransaction();
            child.addParticipant ( tPart );
            testSubTx ( child , parent );
            
            //assert that child sees parent's serial tag
            if ( ! child.isSerial() )
                throw new Exception ( "setSerial()/getSerial() does not work?" );
                
            //assert that non-root tx can not call setSerial
            try {
                child.getTransactionControl().setSerial();
                throw new Exception ( "setSerial() works for non-root?" );
            }
            catch ( IllegalStateException ill ) {
                //should happen 
            }
              
            if ( parent.getTransactionControl().getLocalSubTxCount() != 1 )
                throw new Exception ( "getLocalSubTxCount() fails?" );
                
            child.commit();
            
            if ( parent.getTransactionControl().getLocalSubTxCount() != 0 )
                throw new Exception ( "getLocalSubTxCount() fails?" );
            
            //tPart should NOT yet be notified
            if ( tPart.isTerminated() )
                throw new Exception ( 
                "Participant terminated on SUBTX commit?" );
            
            //assert that creating a subtx of child fails: already terminated
            try {
                child.getTransactionControl().createSubTransaction();
                throw new Exception ( 
                "createSubTransaction() works on terminated tx?" );
            }
            catch ( IllegalStateException ill ) {
                //should happen 
            }
            
            CompositeTransaction child2 = 
                parent.getTransactionControl().createSubTransaction();
            TestParticipant tPart2 = new TestParticipant();
            child2.addParticipant ( tPart2 );
            testSubTx ( child2, parent );
            child2.rollback();
            
            //assert that tPart2 was terminated
            if ( ! tPart2.isTerminated() )
                throw new Exception ( 
                "Participant not terminated on SUBTX rollback?" );
            
            parent.rollback();
            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                throw new Exception ( "Tx exists for thread AFTER termination?" );
                
			//
			//CASE 6: Assert that extent participants are rolled back
			//when the parent ct is rolled back (BUG in TransactionsRMI
			//release 1.21!!!)
			//
			ct = ctm.createCompositeTransaction ( 1000 );
			Extent ext = ct.getTransactionControl().getExtent();
			tPart = new TestParticipant();
			ext.add ( tPart , 1 );
			ct.rollback();
			if ( ! tPart.isTerminated() )
				throw new Exception ( "Participants in Extent are not rolled back?");                
            
            System.err.println ( "Testing native icatch interfaces: done!" );
      }
      
       /** 
        *Test the import/export mechanism, the propagation interface,
        *the related transaction restrictions and the extent 
        *checking.
        */
        
      private static void testPropagation ( UserTransactionService uts )
      throws Exception
      {
          System.err.println ( "Testing propagation" );
          
          CompositeTransactionManager ctm =
              uts.getCompositeTransactionManager();
          ImportingTransactionManager itm =
              uts.getImportingTransactionManager();
          ExportingTransactionManager etm =
              uts.getExportingTransactionManager();
          TransactionManager tm = uts.getTransactionManager();
          Transaction tx = null;
          
          
          //
          //CASE 1: test if propagation data is correct for non-SERIAL root
          //
          
          CompositeTransaction ct =
              ctm.createCompositeTransaction ( 10000 );
          Propagation propagation = etm.getPropagation();
          
          //assert that serial is false
          if ( propagation.isSerial() )
              throw new Exception ( "Propagation is serial if tx is not?" );
          //assert timeout is correct; this will have decreased
          if ( propagation.getTimeOut() > 10000 )
              throw new Exception ( "Propagation timeout is wrong?" );
          Stack lineage = propagation.getLineage();
          
          //assert lineage is not empty
          if ( lineage == null || lineage.empty() )
              throw new Exception ( "No lineage in propagation?" );
              
          //the only tx in the propagation is the root
          CompositeTransaction root = 
              ( CompositeTransaction ) lineage.peek();  
          if ( !root.isRoot() )
              throw new Exception ( "Propagation has no root?" );
          
          ct.rollback();
          
          //
          //CASE 2: test if propagation data is correct, for SERIAL root
          //
          
          ct = ctm.createCompositeTransaction ( 10000 );
          ct.getTransactionControl().setSerial();
          propagation = etm.getPropagation();
          
          //assert serial is true
          if ( ! propagation.isSerial() )
              throw new Exception ( "Propagation not serial if tx is?" );
          
          ct.rollback();

          //
          //CASE 3: test if a non-SERIAL transaction can be exported and imported 
          //in the usual way.
          //
          
          ct = ctm.createCompositeTransaction ( 10000 );
          propagation = etm.getPropagation();
          CompositeTransaction child = itm.importTransaction ( propagation , true , true );
          TestParticipant tPart = new TestParticipant();
          child.addParticipant ( tPart );
          
          //assert that child is not serial
          if ( child.isSerial() )
              throw new Exception ( "Imported child is serial whereas parent is not?" );
              
          //assert that child behaves like a reasonable subtx
          testSubTx ( child , ct );
          
          Extent extent = itm.terminated ( true );
          
          etm.addExtent ( extent );
          ct.commit();
          
          //assert that 2PC was OK
          if ( ! tPart.isTerminated() )
              throw new Exception ( "Commit propagation does not work?" );
          
          //
          //CASE 4: test if a SERIAL transaction can be exported and imported 
          //the usual way
          //
          
          ct = ctm.createCompositeTransaction ( 10000 );
          ct.getTransactionControl().setSerial();
          propagation = etm.getPropagation();
          child = itm.importTransaction ( propagation , true , true );
          tPart = new TestParticipant();
          child.addParticipant ( tPart );
          
          //assert that child is serial
          if ( ! child.isSerial() )
              throw new Exception ( "Imported child is serial whereas parent is not?" );
              
          //assert that child behaves like a reasonable subtx
          testSubTx ( child , ct );
          
          extent = itm.terminated ( true );
          
          etm.addExtent ( extent );
          ct.commit();
          
          //assert that 2PC was OK
          if ( ! tPart.isTerminated() )
              throw new Exception ( "Commit propagation does not work?" );
          
          //
          //CASE 5: test if terminated fails when the tx was rolledback already
          //
          
          ct = ctm.createCompositeTransaction ( 10000 );
          ct.getTransactionControl().setSerial();
          propagation = etm.getPropagation();
          child = itm.importTransaction ( propagation , true , true );
          tPart = new TestParticipant();
          child.addParticipant ( tPart );
          
          //assert that child is serial
          if ( ! child.isSerial() )
              throw new Exception ( "Imported child is serial whereas parent is not?" );
              
          //assert that child behaves like a reasonable subtx
          testSubTx ( child , ct );
          
          //rollback  child, to test if later termination fails as expected
          child.rollback();
          if ( ctm.getCompositeTransaction().equals ( child ) ) {
              //ONLY DO THIS IF THREAD IS NOT THAT OF PARENT 
              //DUE TO ROLLBACK!
              try {
                  extent = itm.terminated ( true );
                  throw new Exception ( "terminated ( true ) works for rolledback import?" );
              }
              catch (com.atomikos.icatch.RollbackException rb ) {
                  //should happen
              }
          }
          ct.commit();
          
          //
          //CASE 6: test if sibling counts are  propagated as should
          //
         
          ct = ctm.createCompositeTransaction ( 10000 );
          ct.getTransactionControl().setSerial();
          propagation = etm.getPropagation();
          
          //add an extent participant to the child tx to test sibling counts
          ExtentParticipant ePart = new ExtentParticipant();
          extent = new ExtentImp();
          extent.add ( ePart , 3 );
          ePart.setLocalSiblingCount ( 3 );
          
          etm.addExtent ( extent );
          
          ct.commit();
          //assert that ePart was terminated
          if ( ! ePart.isTerminated() )
              throw new Exception ( "Extent propagation of 2PC does not work?" );
          
          //
          //CASE 7: test if orphan detection works fine
          //
          
          ct = ctm.createCompositeTransaction ( 10000 );
          ct.getTransactionControl().setSerial();
          propagation = etm.getPropagation();
          
          //add an extent participant to the child tx to test sibling counts
          ePart = new ExtentParticipant();
          extent = new ExtentImp();
          extent.add ( ePart , 2 );
          
          //add another participant to avoid having 1PC
          ct.addParticipant ( new ReadOnlyParticipant() );
          
          //simulate comm. failure by setting local count to a higher value
          ePart.setLocalSiblingCount ( 3 );
          
          etm.addExtent ( extent );
          try {
              ct.commit();
              throw new Exception ( "Orphan detection does not work?" );
          }
          catch (com.atomikos.icatch.RollbackException rb ) {
              //should happen due to orphan simulation
          }


          //
          //CASE 8: test if orphan detection works with 1PC in CLIENT TM
          //but 2PC in SERVER VM
          //
          
          CompositeTransactionStub stubtx = new CompositeTransactionStub ( "StubTransaction" );
          Stack stack = new Stack();
          stack.push ( stubtx );
          Propagation p = new PropagationImp ( stack , true , 1000 );
          itm.importTransaction ( p , true , true );
          ct = ctm.getCompositeTransaction();
          //add at least two participants to make sure that 2PC is done
          //even though incoming command is 1PC
          ReadOnlyParticipant p1 = new ReadOnlyParticipant();
          ReadOnlyParticipant p2 = new ReadOnlyParticipant();
          ct.addParticipant ( p1 );
          ct.addParticipant ( p2 );
          extent = itm.terminated ( true );
          //get the participant to call commit on
          stack = extent.getParticipants();
          Participant committer = ( Participant ) stack.peek();
          //simulate incoming 1PC request from client TM
          committer.setGlobalSiblingCount ( 1 );
          committer.commit ( true );

          //
          //CASE 9: test if no-orphan detection works with 1PC in CLIENT TM
          //but 2PC in SERVER VM
          //
          stubtx = new CompositeTransactionStub ( "StubTransaction2" );
          stack = new Stack();
          stack.push ( stubtx );
          p = new PropagationImp ( stack , true , 1000 );
          itm.importTransaction ( p , false , true );
          ct = ctm.getCompositeTransaction();
          //add at least two participants to make sure that 2PC is done
          //even though incoming command is 1PC
          p1 = new ReadOnlyParticipant();
          p2 = new ReadOnlyParticipant();
          ct.addParticipant ( p1 );
          ct.addParticipant ( p2 );
          extent = itm.terminated ( true );
          //get the participant to call commit on
          stack = extent.getParticipants();
          committer = ( Participant ) stack.peek();
          //simulate incoming 1PC request from client TM
          committer.setGlobalSiblingCount ( 1 );
          committer.commit ( true );

          //
          //CASE 10: test if no-orphan detection works with 1PC in CLIENT TM
          //but 2PC in SERVER VM, AND DO NOT set sibling count either...
          //
          stubtx = new CompositeTransactionStub ( "StubTransaction3" );
          stack = new Stack();
          stack.push ( stubtx );
          p = new PropagationImp ( stack , true , 1000 );
          itm.importTransaction ( p , false , true );
          ct = ctm.getCompositeTransaction();
          //add at least two participants to make sure that 2PC is done
          //even though incoming command is 1PC
          p1 = new ReadOnlyParticipant();
          p2 = new ReadOnlyParticipant();
          ct.addParticipant ( p1 );
          ct.addParticipant ( p2 );
          extent = itm.terminated ( true );
          //get the participant to call commit on
          stack = extent.getParticipants();
          committer = ( Participant ) stack.peek();
          //simulate incoming 1PC request from client TM
          committer.commit ( true );
          
          //
          //CASE 11: test if XAResource gets rollback on terminated(false)
          //
          stubtx = new CompositeTransactionStub ( "StubTransaction3" );
          xaRes_.reset();
          stack = new Stack();
          stack.push ( stubtx );
          p = new PropagationImp ( stack , true , 1000  );
          itm.importTransaction ( p , false , true );
          tx = tm.getTransaction();
          tx.enlistResource ( xaRes_ );
          tx.delistResource ( xaRes_ , XAResource.TMSUCCESS );
          extent = itm.terminated ( false );
          if ( xaRes_.getLastRolledback() == null )
              throw new Exception ( "XAResource not rolled back on terminated ( false )" );
          
          System.out.println ( "Testing propagation: done." );

      }

    /**
     *Test if READONLY transactions can be committed through the local terminator.
     *This has been a problem in past releases.
     */
    
    public static void testReadOnlyCommit ( UserTransactionService uts )
    throws Exception
    {
        CompositeTransactionManager ctm = uts.getCompositeTransactionManager();
        CompositeTransaction ct = null;
        ReadOnlyParticipant p1 = null , p2 = null;

        if ( ctm.getCompositeTransaction() != null ) {
            throw new Exception ( "Invalid precondition for test: existing tx for thread" );
        }

        ct = ctm.createCompositeTransaction ( 1000 );
        p1 = new ReadOnlyParticipant();
        p2 = new ReadOnlyParticipant();
        ct.addParticipant ( p1 );
        ct.addParticipant ( p2 );
        //if buggy then the following will issue commit on CoordinatorImp, even
        //though readonly -> Exception
        ct.commit();
        //assert that participants got NO commit
        if ( p1.isTerminated() ) throw new Exception ( "Commit called on readonly participant?" );
        if ( p2.isTerminated() ) throw new Exception ( "Commit called on readonly participant?" );
    }


     /**
      *Tests if recovery works for propagation cases.
      */
     
     public static void testRecovery ( UserTransactionService uts , TSInitInfo info ) throws Exception
     {
         CompositeTransactionManager ctm = null;
         ImportingTransactionManager itm = null;
         ExportingTransactionManager etm = null;
         CompositeTransaction ct = null, stubtx = null;
         Participant p1 = null , p2 = null;
         String tid = null;
         Stack stack = null;
         Extent extent = null;
         Propagation p = null;
         Participant subcoordinator = null;
         AdminTransaction admintx = null;
         AdminTransaction[] recoveredtxs = null;
         TestLogAdministrator admin =
         new TestLogAdministrator();
         info.registerLogAdministrator ( admin );
         Properties properties = info.getProperties();
         properties.setProperty (
                                AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TrmiTestTransactionManager" );
         properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
         properties.setProperty ( Context.PROVIDER_URL , "iiop://localhost:1050" );
         properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY ,
                                "com.sun.jndi.cosnaming.CNCtxFactory" );
         info.setProperties ( properties );
    
         uts.init ( info );

         ctm = uts.getCompositeTransactionManager();
         itm = uts.getImportingTransactionManager();
         etm = uts.getExportingTransactionManager();
         ct = null;

         //
         //CASE 1: assert that prepared subcoordinator is recovered 
         //

         tid = "TestRecoveryTransaction1";
         stubtx = new CompositeTransactionStub ( tid );
         stack = new Stack();
         stack.push ( stubtx );
         p = new PropagationImp ( stack , true , 5000  );
         itm.importTransaction ( p , true , true );
         ct = ctm.getCompositeTransaction();
         
         p1 = new ReadOnlyParticipant();
         p2 = new TestParticipant();
         ct.addParticipant ( p1 );
         ct.addParticipant ( p2 );
         extent = itm.terminated ( true );
         //get the participant to call prepare on
         stack = extent.getParticipants();
         subcoordinator = ( Participant ) stack.peek();
         subcoordinator.setGlobalSiblingCount ( 1 );
         subcoordinator.prepare();

         //shutdown and restart TM to simulate recovery
         uts.shutdown ( true );
         admin = new TestLogAdministrator();
         info.registerLogAdministrator ( admin );
         info.setProperties ( properties );
         uts.init ( info );
         ctm = uts.getCompositeTransactionManager();
         itm = uts.getImportingTransactionManager();
         etm = uts.getExportingTransactionManager();
         ct = null;
         
         //assert that the prepared instance is still there
         recoveredtxs = admin.getLogControl().getAdminTransactions();
         admintx = null;
         for ( int i = 0 ; i < recoveredtxs.length ; i++ ) {
             if ( recoveredtxs[i].getTid().equals ( tid ) ) admintx = recoveredtxs[i];
         }
         if ( admintx == null )
             throw new Exception ( "Prepared coordinator not recovered?" );
         if ( admintx.getState() != AdminTransaction.STATE_PREPARED )
             throw new Exception ( "Recovered coordinator not prepared?" );
         admintx.forceRollback();

         //
         //CASE 2: assert that readonly prepared subcoordinator is NOT recovered
         //
         tid = "TestRecoveryTransaction2";
         stubtx = new CompositeTransactionStub ( tid );
         stack = new Stack();
         stack.push ( stubtx );
         p = new PropagationImp ( stack , true , 1000  );
         itm.importTransaction ( p , true , true );
         ct = ctm.getCompositeTransaction();
         p1 = new ReadOnlyParticipant();
         p2 = new ReadOnlyParticipant();
         ct.addParticipant ( p1 );
         ct.addParticipant ( p2 );
         extent = itm.terminated ( true );
         //get the participant to call prepare on
         stack = extent.getParticipants();
         subcoordinator = ( Participant ) stack.peek();
         subcoordinator.setGlobalSiblingCount ( 1 );
         subcoordinator.prepare();
         //shutdown and restart TM to simulate recovery
         uts.shutdown ( true );
         admin = new TestLogAdministrator();
         info.registerLogAdministrator ( admin );
         info.setProperties ( properties );
         uts.init ( info );
         //assert that the prepared instance is still there
         recoveredtxs = admin.getLogControl().getAdminTransactions();
         admintx = null;
         for ( int i = 0 ; i < recoveredtxs.length ; i++ ) {
             if ( recoveredtxs[i].getTid().equals ( tid ) ) admintx = recoveredtxs[i];
         }
         if ( admintx != null )
             throw new Exception ( "Readonly prepared coordinator was recovered?" );
         
         uts.shutdown ( true );
     }
          

     /**
       *Perform a test run with the given transaction service and info.
       *@param uts The transactionservice to test.
       *@param info The init info instance to use. This instance should 
       *be 'empty': registration of resources and administrators will
       *be done during the test itself.
       *@param propagation If true, then the propagation (import/export) 
       *will also be tested.
       *@exception Exception If one of the test assertions fails.
       */
       
      public static void test ( UserTransactionService uts , 
                                        TSInitInfo info , boolean propagation )
      throws Exception
      {
          if ( propagation ) {
              testRecovery ( uts ,info );
          }
          
          TestRecoverableResource res = 
              new TestRecoverableResource ( "ReleaseTesterResource" );
          info.registerResource ( res );
          xaRes_ = new TestXAResource();
          info.registerResource ( new TestXATransactionalResource ( xaRes_ , "TestXA1"  ) );
          TestLogAdministrator admin = 
              new TestLogAdministrator();
          info.registerLogAdministrator ( admin );
		  Properties properties = info.getProperties();
		  properties.setProperty ( 
							AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TrmiTestTransactionManager" );
		  properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
          if ( propagation ) {
              
              properties.setProperty ( Context.PROVIDER_URL , "iiop://localhost:1050" );
              properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
                    "com.sun.jndi.cosnaming.CNCtxFactory" );
              
          }
		  info.setProperties ( properties );
          uts.init ( info );
          if ( ! res.isRecoveryEnded() )
              throw new Exception ( 
              "RecoverableResource.endRecovery() not called?" );
          if ( admin.getLogControl() == null )
              throw new Exception ( 
              "LogAdministrator did not get a log control?" );
          
          testReadOnlyCommit ( uts );
          if ( propagation )
              testPropagation ( uts );
          testAdministration ( uts , admin , propagation );
          testNative ( uts , admin );
          testHeuristicPrepare ( uts );
          testHeuristicCommit ( uts );
          
          
          
          uts.shutdown ( true );
          if ( ! res.isClosed() )
              throw new Exception ( 
              "RecoverableResource.close() not called?" );
          if ( admin.getLogControl() != null )
              throw new Exception ( 
              "LogAdministrator: deregister of LogControl not done?" );

          
      }
      
      public static void main ( String[] args )
      {
          try {
              UserTransactionService uts =
                      new com.atomikos.icatch.standalone.UserTransactionServiceFactory().
                      getUserTransactionService ( new Properties());
              test ( uts ,
              uts.createTSInitInfo() , false );
          }
          catch ( Exception e ) {
              e.printStackTrace();     
          }
      }
}		
