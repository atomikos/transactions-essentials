
package com.atomikos.icatch.imp;

import java.util.Properties;

import javax.transaction.HeuristicMixedException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import junit.swingui.TestRunner;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.datasource.xa.XID;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeuristicParticipant;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.config.imp.TSInitInfoImp;

/**
 * 
 * 
 * 
 *
 * 
 */
public class XARecoveryTestJUnit extends TransactionServiceTestCase
{
    
    
    

    //instances vars to be set during each test
    private CompositeTransactionManager ctm;
    private TransactionManager tm;
    private CompositeTransaction ct;
    private Transaction tx;
    private CoordinatorImp coord;
    private Xid xid;
    private Xid[] xids;
    private XAResourceTransaction restx;
    private String tid;
    private String[] tids;
    
    //instance vars to be set in setUp
    private TestLogAdministrator admin;
    private TestXAResource xaRes1, xaRes2;
    private TestXATransactionalResource res1, res2;
    private Properties properties;
    private TSInitInfo info;
    private UserTransactionService uts;
    public XARecoveryTestJUnit ( String name )
    {
        super ( name );
    }
    
   
    protected void setUp()
    {
        super.setUp();
        xaRes1 = new TestXAResource();
        xaRes2 = new TestXAResource();
        res1 = new TestXATransactionalResource ( xaRes1 , "XARes1" );
        res2 = new TestXATransactionalResource ( xaRes2 , "XARes2" );
        admin = new TestLogAdministrator();
        
        
        
        uts =
            new UserTransactionServiceImp();
        
        info = uts.createTSInitInfo();
        properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "XARecoveryTestTransactionManager" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        uts.registerResource ( res1 );
        uts.registerResource ( res2 );
        
        
        
    }
    
    public void tearDown ( )
    {
        uts.shutdown ( true );
        super.tearDown();
        
    }
    
    
    
    public void testPresumedAbortInXAResource()
    throws Exception
    {
        uts.init ( info );
        if ( ! xaRes1.isRecoveryCalled () )
            failTest ( "No recovery for xa resource?" );
        
        //to simulate this, we first start a normal transaction
        //that completes, and then use the resulting (forgotten)
        //xid to simulate our test case
        
        ctm = uts.getCompositeTransactionManager();
        tm = uts.getTransactionManager();
        tm.begin();
        tx = tm.getTransaction();
        tx.enlistResource ( xaRes1 );
        xid = xaRes1.getLastStarted();
        tx.delistResource ( xaRes1 , XAResource.TMSUCCESS );
        tm.commit();
        
        //now, the previous tx will be forgotten by the logs, so it 
        //has an xid equivalent to a new one. Use this xid to 
        //simulate a recovered xid of a coordinator that was not
        //recoverable.
        
        uts.shutdown ( true );
        xids = new Xid[1];
        xids[0] = xid;
        //simulate recovery by setting the xids to be returned

        //recreate info
        info = uts.createTSInitInfo();
        info.setProperties ( properties );
        //re-register a NEW instance for XARes1. This is absolutely needed,
        //or the previous TestXATransactionalResource will not recover since it 
        //believes it already has been recovered
        res1 = new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info.registerResource ( res1 );
		xaRes1.reset();
		xaRes1.setRecoveryLoop ( true );
		xaRes1.setRecoverList ( xids );        
        uts.init ( info );
        if ( ! xaRes1.isRecoveryCalled() )
            failTest ( "Recovery not called on registered XAResource?" );
        if ( xaRes1.getLastRolledback() == null || 
            ! xaRes1.getLastRolledback().equals ( xid ) )
            failTest ( "Recovery of indoubt xid for voting coordinator fails for xid: " + xid );
        
        //lastly, shut down the transaction service for the next test case
        uts.shutdown ( true );
        
    }
    
    public void testPresumedAbortInOracleXAResource()
    throws Exception
    {

        //oracle 8.1.7 loops during recovery, always
        //returning the same Xid array at the end
        //of the recovery scan
        xaRes1.setRecoveryLoop ( true );
        xaRes2.setRecoveryLoop ( true );
        testPresumedAbortInXAResource();
    }
    
    public void testIndoubtRecovery() throws Exception
    {
        uts.init ( info );
        
        ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction ( TX_TIMEOUT );
        coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
        tid = coord.getCoordinatorId();
        //we can not use the regular JTA interfaces because then the
        //restx will not be added to the coordinator until commit, 
        //which will not be reached in this case.
        restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
        restx.setXAResource ( xaRes1 );
        restx.resume();
        restx.suspend();
        xid = xaRes1.getLastStarted();
        coord.addParticipant ( restx );
        coord.prepare();
        
        //assert that xa resource is now indoubt
        if ( xaRes1.getLastPrepared() == null ||
            !xaRes1.getLastPrepared().equals ( xid ) )
            failTest ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            failTest ( "Coordinator not indoubt after prepare?" );
            
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        xaRes1.setRecoverList ( xids );
		xaRes1.setRecoveryLoop ( true );
        info = uts.createTSInitInfo(); 
        info.setProperties ( properties );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
        uts.init ( info );
        
        //assert that coordinator is recovered
//        if ( admin.getLogControl().getAdminTransactions() == null ||
//             admin.getLogControl().getAdminTransactions().length == 0 )
//             failTest ( "Indoubt coordinator not recovered?" );
        
        //wait a while to allow rollback on timeout of the coordinator
        //System.err.println ( "About to sleep a while to allow recovery..." );
        sleep();
        
        tids = new String[1];
        tids[0] = tid;
        //assert that coordinator is rolled back
        if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
             admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
             AdminTransaction admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
             failTest ( "Indoubt coordinator not rolled back on recovery? " +
                                             admintx.getState()  );
        }
             
        if ( xaRes1.getLastRolledback() == null )
             failTest ( 
             "Indoubt xa tx not rolled back on recovery of indoubt coordinator?" );
        
        System.err.println ( "Woken up, recovery ok." );
        uts.shutdown ( true );
    }
    
    public void testRecoveredIndoubtXAForCommittingCoordinator()
    throws Exception
    {
        uts.init ( info );
        
        ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction (1000 );
        coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
        tid = coord.getCoordinatorId();
        //we can not use the regular JTA interfaces because then the
        //restx will not be added to the coordinator until terminator commit, 
        //which will not be reached in this case.
        restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
        restx.setXAResource ( xaRes1 );
        restx.resume();
        restx.suspend();
        xid = xaRes1.getLastStarted();
        coord.addParticipant ( restx );
        coord.prepare();
        
        //assert that xa resource is now indoubt
        if ( xaRes1.getLastPrepared() == null ||
            !xaRes1.getLastPrepared().equals ( xid ) )
            failTest ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            failTest ( "Coordinator not indoubt after prepare?" );
            
        //force coordinator into committing state, and make sure that log entry will
        //see the commit decision
        coord.setCommitted();
        coord.setState ( TxState.COMMITTING );
        
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        xaRes1.setRecoverList ( xids );
        info = new TSInitInfoImp(); info.setProperties ( properties );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
       
        
        //wait a while to allow timeout of the coordinator
        sleep();
        
        tids = new String[1];
        tids[0] = tid;
        
        //assert that coordinator is terminated
        if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
             admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
             AdminTransaction admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
             failTest ( "Committing coordinator not terminated on recovery? " +
                                             admintx.getState()  );
             }
             
        if ( xaRes1.getLastCommitted() == null )
             failTest ( 
             "No commit replay on recovery of committing coordinator?" );
        
        
        uts.shutdown ( true );
    }
    
    public void testRecoveredIndoubtXAForHazardCoordinator()
    throws Exception
    {
        uts.init ( info );
        
        tm = uts.getTransactionManager();
        tm.begin();
        tx = tm.getTransaction();
        
        //set xaRes2 to go into heuristic hazard mode
        xaRes2.setFailureMode ( 
            TestXAResource.FAIL_COMMIT , 
            new XAException ( XAException.XA_HEURHAZ ) );
            
        tx.enlistResource ( xaRes1 );
        tx.delistResource ( xaRes1 , XAResource.TMSUCCESS );
        tx.enlistResource ( xaRes2 );
        tx.delistResource ( xaRes2 , XAResource.TMSUCCESS );
        
        try {
            //System.err.println ( "Simulating heuristic hazard - this will take some time" );
            tm.commit();
        }
        catch ( HeuristicMixedException hm ) {
            //should happen: JTA maps hazard to mixed for user 
        }
       
        xid = xaRes2.getLastStarted();
        
        //assert that xa resource is now indoubt
        if ( xaRes2.getLastPrepared() == null ||
            !xaRes2.getLastPrepared().equals ( xid ) )
            failTest ( "XAResource not indoubt after failed commit?" );
        
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xaRes2.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 = new TestXATransactionalResource ( xaRes1 , "XARes1" );
        res2 =  new TestXATransactionalResource ( xaRes2 , "XARes2" );
        xaRes2.setRecoverList ( xids );
        info = uts.createTSInitInfo(); 
        info.setProperties ( properties );
        uts.registerResource ( res1 );
        uts.registerResource ( res2 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
        uts.init ( info );
        
        
        //wait a while to allow rollback on timeout of the coordinator
        sleep();
     
             
        if ( xaRes2.getLastCommitted() == null )
             failTest ( 
             "Indoubt XAResource not committed after recovery of hazard coordinator?" );
       
            
        
        
        uts.shutdown ( true );
    }
    
    public void testRecoveryOfHazardAfterCommit() throws Exception
    {
        
        uts.init ( info );
        
        tm = uts.getTransactionManager();
        tm.begin();
       
        tx = tm.getTransaction();
        tid = uts.getCompositeTransactionManager().getCompositeTransaction().getCompositeCoordinator().getCoordinatorId();
        
        //set xaRes2 to go into heuristic hazard mode
        xaRes2.setFailureMode ( 
            TestXAResource.FAIL_COMMIT , 
            new XAException ( XAException.XA_HEURHAZ ) );
            
        tx.enlistResource ( xaRes1 );
        tx.delistResource ( xaRes1 , XAResource.TMSUCCESS );
        tx.enlistResource ( xaRes2 );
        tx.delistResource ( xaRes2 , XAResource.TMSUCCESS );
        
        try {
            //System.err.println ( "Simulating heuristic hazard - this will take some time" );
            tm.commit();
        }
        catch ( HeuristicMixedException hm ) {
            //should happen: JTA maps hazard to mixed for user 
        }
       
        xid = xaRes2.getLastStarted();
        
        //assert that xa resource is now indoubt
        if ( xaRes2.getLastPrepared() == null ||
            !xaRes2.getLastPrepared().equals ( xid ) )
            failTest ( "XAResource not indoubt after failed commit?" );
        
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        //make xaRes2 fail upon commit
        xaRes2.setFailureMode ( 
                TestXAResource.FAIL_COMMIT , 
                new XAException ( XAException.XA_HEURHAZ ) );
        xids = new Xid[1];
        xids[0] = xid;
        res1 = new TestXATransactionalResource ( xaRes1 , "XARes1" );
        res2 =  new TestXATransactionalResource ( xaRes2 , "XARes2" );
        xaRes2.setRecoverList ( xids );
        info = uts.createTSInitInfo(); 
        info.setProperties ( properties );
        uts.registerResource ( res1 );
        uts.registerResource ( res2 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
        uts.init ( info );
        
        
        //wait a while to allow timeout of the coordinator
        sleep();
     
             
        
        tids = new String[1];
        tids[0] = tid;
        
        //assert that coordinator is NOT terminated -cf BUG 10029
        AdminTransaction[] admintxs = admin.getLogControl().getAdminTransactions ( tids );
        if (  ( admintxs == null || admintxs.length == 0 ) ) {
             
             failTest ( "Committing coordinator terminated on recovery? " );
        }
        
        //terminate the tx
        admintxs[0].forceForget();
        
        
        
        uts.shutdown ( true );
    }
    
    public void testRecoveredHazardXAForCommittingCoordinator()
    throws Exception
    {
        
        uts.init ( info );
        
        ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction (1000 );
        coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
        tid = coord.getCoordinatorId();
        //we can not use the regular JTA interfaces because then the
        //restx will not be added to the coordinator until terminator commit, 
        //which will not be reached in this case.
        restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
        restx.setXAResource ( xaRes1 );
        restx.resume();
        restx.suspend();
        xid = xaRes1.getLastStarted();
        coord.addParticipant ( restx );
        coord.prepare();
        
        //assert that xa resource is now indoubt
        if ( xaRes1.getLastPrepared() == null ||
            !xaRes1.getLastPrepared().equals ( xid ) )
            failTest ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            failTest ( "Coordinator not indoubt after prepare?" );
            
        //force coordinator into committing state, and make sure that log entry will
        //see the commit decision
        coord.setCommitted();
        coord.setState ( TxState.COMMITTING );
        
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        xaRes1.setFailureMode ( TestXAResource.FAIL_COMMIT , 
                                            new XAException ( XAException.XA_HEURHAZ ) );
        xaRes1.setRecoverList ( xids );
        info = new TSInitInfoImp(); info.setProperties ( properties );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        
        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             failTest ( "Committing coordinator not recovered?" );
        
        sleep();
        
        tids = new String[1];
        tids[0] = tid;
        
        //assert that coordinator is hazard
       
         AdminTransaction admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
        if ( admintx.getState() != AdminTransaction.STATE_HEUR_HAZARD )
            failTest ( 
            "Committing coordinator not hazard after recovery of hazard XA?" );
        
        //assert that coordinator can be terminated
        admintx.forceForget();
             
        sleep();
        
        if ( xaRes1.getLastCommitted() == null )
             failTest ( 
             "No commit replay on recovery of committing coordinator?" );
        
        if ( xaRes1.getLastForgotten() == null )
            failTest ( "Forget not propagated to XAResource?" );

        uts.shutdown ( true );
    }
    
    //test case for bug 21552
    public void testRecoveredCommittingCoordinatorForCommittedXA()
    throws Exception
    {
        
        uts.init ( info );
        
        ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction (1000 );
        coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
        tid = coord.getCoordinatorId();
        //we can not use the regular JTA interfaces because then the
        //restx will not be added to the coordinator until terminator commit, 
        //which will not be reached in this case.
        restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
        restx.setXAResource ( xaRes1 );
        restx.resume();
        restx.suspend();
        xid = xaRes1.getLastStarted();
        coord.addParticipant ( restx );
        coord.prepare();
        
        //assert that xa resource is now indoubt
        if ( xaRes1.getLastPrepared() == null ||
            !xaRes1.getLastPrepared().equals ( xid ) )
            failTest ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            failTest ( "Coordinator not indoubt after prepare?" );
            
        //force coordinator into committing state, and make sure that log entry will
        //see the commit decision
        coord.setCommitted();
        coord.setState ( TxState.COMMITTING );
        
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        
        xaRes1.setFailureMode ( TestXAResource.FAIL_COMMIT , 
                                            new XAException ( XAException.XAER_NOTA ) );
       
        info = new TSInitInfoImp(); info.setProperties ( properties );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
       
        
        sleep();
        
        tids = new String[1];
        tids[0] = tid;
        
        //assert that coordinator is terminated
      
             
        sleep();
        
        if ( admin.getLogControl().getAdminTransactions() != null &&
                admin.getLogControl().getAdminTransactions().length != 0 )
                failTest ( "Committing coordinator not terminated?" );
        
        if ( xaRes1.getLastCommitted() == null )
             failTest ( 
             "No commit replay on recovery of committing coordinator?" );


        uts.shutdown ( true );
    }
    
 
    
    public void testRecoveredMixedXAForCommittingCoordinator() throws Exception
    {
        
        uts.init ( info );
        
        ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction (1000 );
        coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
        tid = coord.getCoordinatorId();
        //we can not use the regular JTA interfaces because then the
        //restx will not be added to the coordinator until terminator commit, 
        //which will not be reached in this case.
        restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
        restx.setXAResource ( xaRes1 );
        restx.resume();
        restx.suspend();
        xid = xaRes1.getLastStarted();
        coord.addParticipant ( restx );
        coord.prepare();
        
        //assert that xa resource is now indoubt
        if ( xaRes1.getLastPrepared() == null ||
            !xaRes1.getLastPrepared().equals ( xid ) )
            failTest ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            failTest ( "Coordinator not indoubt after prepare?" );
            
        //force coordinator into committing state, and make sure that log entry will
        //see the commit decision
        coord.setCommitted();
        coord.setState ( TxState.COMMITTING );
        
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        xaRes1.setFailureMode ( TestXAResource.FAIL_COMMIT , 
                                            new XAException ( XAException.XA_HEURMIX ) );
        xaRes1.setRecoverList ( xids );
        info = new TSInitInfoImp(); info.setProperties ( properties );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        
        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             failTest ( "Committing coordinator not recovered?" );
        
        sleep();
        
        tids = new String[1];
        tids[0] = tid;
        
        //assert that coordinator is hazard
       
         AdminTransaction admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
                
        //NOTE: now, admin state should be HAZARD, not mixed.
        //this is because the coordinator is recovered and set into
        //hazard (the state before replay) and this state is maintained
        //if commit replay fails.
        if ( admintx.getState() != AdminTransaction.STATE_HEUR_HAZARD )
            failTest ( 
            "Committing coordinator not hazard after recovery of mixed XA " + admintx.getState() );
        
        //assert that coordinator can be terminated
        admintx.forceForget();
             
         
        if ( xaRes1.getLastCommitted() == null )
             failTest ( 
             "No commit replay on recovery of committing coordinator?" );
        
        if ( xaRes1.getLastForgotten() == null )
            failTest ( "Forget not propagated to XAResource?" );
        
        
        uts.shutdown ( true );
    }
    
    public void testRecoveredHeurAbortedXAForIndoubtCoordinator() throws Exception
    {
        uts.init ( info );
        
        ctm = uts.getCompositeTransactionManager();
        ct = ctm.createCompositeTransaction (1000 );
        coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
        tid = coord.getCoordinatorId();
        //we can not use the regular JTA interfaces because then the
        //restx will not be added to the coordinator until commit, 
        //which will not be reached in this case.
        restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
        restx.setXAResource ( xaRes1 );
        restx.resume();
        restx.suspend();
        xid = xaRes1.getLastStarted();
        coord.addParticipant ( restx );
        coord.prepare();
        
        //assert that xa resource is now indoubt
        if ( xaRes1.getLastPrepared() == null ||
            !xaRes1.getLastPrepared().equals ( xid ) )
            failTest ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            failTest ( "Coordinator not indoubt after prepare?" );
            
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        xaRes1.setRecoverList ( xids );
        //make xa resource fail with heur hazard on rollback
        xaRes1.setFailureMode ( TestXAResource.FAIL_ROLLBACK ,
              new XAException ( XAException.XA_HEURRB ) );
        info = new TSInitInfoImp(); info.setProperties ( properties );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        
        //assert that coordinator is recovered
//        if ( admin.getLogControl().getAdminTransactions() == null ||
//             admin.getLogControl().getAdminTransactions().length == 0 )
//             failTest ( "Indoubt coordinator not recovered?" );
        
        
        sleep();
        
        tids = new String[1];
        tids[0] = tid;
        //assert that coordinator is terminated
       
        if ( ! ( admin.getLogControl().getAdminTransactions( tids )  == null  ||
                admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
            
            AdminTransaction admintx =  admin.getLogControl().getAdminTransactions()[0];
            failTest ( 
            "Indoubt coordinator not terminated after recovery with heurAborted xa? " +
            admintx.getState() );
            
                }
             
        if ( xaRes1.getLastRolledback() == null )
             failTest ( 
             "Indoubt xa tx not rolled back on recovery of heurAborted coordinator?" );
        
        
        uts.shutdown ( true );
        
    }
    
   
    public void testNoCrashIfXAResourceUnavailable() throws Exception
    {
        //construct resource with NULL XAResource
         res1 =  new TestXATransactionalResource ( null , "XARes3" );
		info = new TSInitInfoImp(); 
		info.setProperties ( properties );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
		try { 
			uts.init ( info );
		}
		catch ( Exception e ) {
			e.printStackTrace();
			failTest ( "Recovery fails for broken DB connection");
		}
		uts.shutdown ( true );
    }
    
    public void testDifferentXidForDifferentResourcesInSameTransaction()
    throws Exception
    {
		uts.init ( info );
		
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();	
		
		restx = ( XAResourceTransaction ) res2.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes2 );
		restx.resume();
		restx.suspend();
		
		if ( xid.equals ( xaRes2.getLastStarted() ) ) 
			failTest ( "Same XID for different resources in same tx?");
		
		ct.rollback();	
		
		uts.shutdown ( true );        
    }
    
    public void testDifferentXidForSameResourceInDifferentTransactions()
    throws Exception
    {
		uts.init ( info );
		
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();	
		ct.getTransactionControl().getTerminator().rollback();	

		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();		
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		
		if ( xid.equals ( xaRes1.getLastStarted() ) ) 
			failTest ( "Same XID across different txs?");
		
		ct.rollback();	
		
		uts.shutdown ( true );  

    }
    
    public void testLateRegistrationIsRecovered() throws Exception
    {
		uts.init ( info );
		
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();	
		ct.rollback();	
		
		
		
		TestXAResource xaRes3 = new TestXAResource();
		TestXATransactionalResource res3 = 
		    new TestXATransactionalResource ( xaRes3 , "XARes3" );
		
	    Xid[] xids = new Xid[1];
 		xids[0] = xid;
		xaRes3.setRecoverList ( xids );		
		
		uts.registerResource ( res3 );
	

		Xid last = xaRes3.getLastRolledback();
		if ( last == null || ! last.equals ( xid ) )
			failTest ( "Late registration does not do presumed abort?");
		
		
		uts.shutdown ( true );
		
    }
    
    public void testXidOfOtherTmIsNotAffectedByRecovery() throws Exception
    {
		
        xids = new Xid [1];
		xid = new XID ("anotherTid" , "anotherTM");
		xids[0] = xid;
		xaRes1.setRecoverList ( xids );		
		
        
        uts.init ( info );
		
		
		if ( xid.equals ( xaRes1.getLastRolledback() ) )
			failTest ( "Recovery rolls back another TM's XID?");
		if ( xid.equals ( xaRes1.getLastCommitted() ) )
			failTest ( "Recovery commits another TM's XID?");
		if ( xid.equals( xaRes1.getLastPrepared() ) )
			failTest ( "Recovery prepares another TM's XID");
		if ( xid.equals ( xaRes1.getLastEnded() ))
			failTest ( "Recovery ends another TM's XID?");
		if ( xid.equals ( xaRes1.getLastForgotten() ))
			failTest ( "Recovery forgets another TM's XID?");
		if ( xid.equals ( xaRes1.getLastStarted() ))
			failTest ( "Recovery starts another TM's XID?");
		
		uts.shutdown ( true );

        
    }
    
    
    public void testRecoveredIndoubtXAForCommittingCoordinatorWithLateRegistration()
    throws Exception
    {
		uts.init ( info );
        
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		tid = coord.getCoordinatorId();
		//we can not use the regular JTA interfaces because then the
		//restx will not be added to the coordinator until terminator commit, 
		//which will not be reached in this case.
		
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();
		coord.addParticipant ( restx );
		coord.prepare();
        
		//assert that xa resource is now indoubt
		if ( xaRes1.getLastPrepared() == null ||
			!xaRes1.getLastPrepared().equals ( xid ) )
			failTest ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			failTest ( "Coordinator not indoubt after prepare?" );
            
		//force coordinator into committing state, and make sure that log entry will
		//see the commit decision
		coord.setCommitted();
		coord.setState ( TxState.COMMITTING );
        
		uts.shutdown ( true );
        
		//restart and make resource return the indoubt xid on recover
		xaRes1.reset();
		xids = new Xid[1];
		xids[0] = xid;
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		xaRes1.setRecoverList ( xids );
		info = new TSInitInfoImp(); info.setProperties ( properties );
		
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 failTest ( "Committing coordinator not recovered?" );

		uts.registerResource ( res1 );
		
		sleep();
        
		tids = new String[1];
		tids[0] = tid;
        
		//assert that coordinator is terminated
		if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
			 admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
			 AdminTransaction admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
			 failTest ( "Committing coordinator not terminated on recovery? " +
											 admintx.getState()  );
			 }
             
		if ( xaRes1.getLastCommitted() == null )
			 failTest ( 
			 "No commit replay on recovery of committing coordinator?" );
        
		
		uts.shutdown ( true );
    }
    
    public void testRecoveredIndoubtXAForHazardCoordinatorWithLateRegistration() throws Exception
    {
		uts.init ( info );
        
		tm = uts.getTransactionManager();
		tm.begin();
		tx = tm.getTransaction();
		tid = tx.toString();
        
		//set xaRes2 to go into heuristic hazard mode
		xaRes2.setFailureMode ( 
			TestXAResource.FAIL_COMMIT , 
			new XAException ( XAException.XA_HEURHAZ ) );
            
		tx.enlistResource ( xaRes1 );
		tx.delistResource ( xaRes1 , XAResource.TMSUCCESS );
		tx.enlistResource ( xaRes2 );
		tx.delistResource ( xaRes2 , XAResource.TMSUCCESS );
        
		try {
			System.err.println ( "Simulating heuristic hazard - this will take some time" );
			tm.commit();
		}
		catch ( HeuristicMixedException hm ) {
			//should happen: JTA maps hazard to mixed for user 
		}
       
		xid = xaRes2.getLastStarted();
        
		//assert that xa resource is now indoubt
		if ( xaRes2.getLastPrepared() == null ||
			!xaRes2.getLastPrepared().equals ( xid ) )
			failTest ( "XAResource not indoubt after failed commit?" );
        
		uts.shutdown ( true );
        
		//restart and make resource return the indoubt xid on recover
		xaRes1.reset();
		xaRes2.reset();
		xids = new Xid[1];
		xids[0] = xid;
		res1 = new TestXATransactionalResource ( xaRes1 , "XARes1" );
		res2 =  new TestXATransactionalResource ( xaRes2 , "XARes2" );
		xaRes2.setRecoverList ( xids );
		info = new TSInitInfoImp(); info.setProperties ( properties );
		
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 failTest ( "Hazard coordinator not recovered?" );


		uts.registerResource ( res1 );
		uts.registerResource ( res2 );
        
		sleep();
        
		tids = new String[1];
		tids[0] = tid;
             
		if ( xaRes2.getLastCommitted() == null )
			 failTest ( 
			 "Indoubt XAResource not committed after recovery of hazard coordinator?" );
		if ( xaRes2.getLastRolledback() != null )
			failTest ( 
			"Indoubt XAResource rolledback after recovery of hazard coordinator?" );
            
		//assert that coordinator can be terminated
		if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
			 admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
			 AdminTransaction admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
			 admintx.forceForget();
			 }
        
		
		uts.shutdown ( true );
    }
    
    public void testRecoveredHazardXAForCommittingCoordinatorWithLateRegistration()
    throws Exception
    {
        uts.init ( info );
        
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		tid = coord.getCoordinatorId();
		//we can not use the regular JTA interfaces because then the
		//restx will not be added to the coordinator until terminator commit, 
		//which will not be reached in this case.
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();
		coord.addParticipant ( restx );
		coord.prepare();
        
		//assert that xa resource is now indoubt
		if ( xaRes1.getLastPrepared() == null ||
			!xaRes1.getLastPrepared().equals ( xid ) )
			failTest ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			failTest ( "Coordinator not indoubt after prepare?" );
            
		//force coordinator into committing state, and make sure that log entry will
		//see the commit decision
		coord.setCommitted();
		coord.setState ( TxState.COMMITTING );
        
		uts.shutdown ( true );
        
		//restart and make resource return the indoubt xid on recover
		xaRes1.reset();
		xids = new Xid[1];
		xids[0] = xid;
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		xaRes1.setFailureMode ( TestXAResource.FAIL_COMMIT , 
											new XAException ( XAException.XA_HEURHAZ ) );
		xaRes1.setRecoverList ( xids );
		info = new TSInitInfoImp(); info.setProperties ( properties );
		//info.registerResource ( res1 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 failTest ( "Committing coordinator not recovered?" );



		uts.registerResource ( res1 );
	
        
		sleep();
        
		tids = new String[1];
		tids[0] = tid;
        
		//assert that coordinator is hazard
       
		 AdminTransaction admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
		if ( admintx.getState() != AdminTransaction.STATE_HEUR_HAZARD )
			failTest ( 
			"Committing coordinator not hazard after recovery of hazard XA?" );
        
		//assert that coordinator can be terminated
		admintx.forceForget();
             
		sleep();
        
		if ( xaRes1.getLastCommitted() == null )
			 failTest ( 
			 "No commit replay on recovery of committing coordinator?" );
        
		if ( xaRes1.getLastForgotten() == null )
			failTest ( "Forget not propagated to XAResource?" );
        
		
		uts.shutdown ( true );		

    }
    
  
    public void testRecoveredMixedXAForCommittingCoordinatorWithLateRegistration()
    throws Exception
    {
        uts.init ( info );
        
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		tid = coord.getCoordinatorId();
		//we can not use the regular JTA interfaces because then the
		//restx will not be added to the coordinator until terminator commit, 
		//which will not be reached in this case.
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();
		coord.addParticipant ( restx );
		coord.prepare();
        
		//assert that xa resource is now indoubt
		if ( xaRes1.getLastPrepared() == null ||
			!xaRes1.getLastPrepared().equals ( xid ) )
			failTest ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			failTest ( "Coordinator not indoubt after prepare?" );
            
		//force coordinator into committing state, and make sure that log entry will
		//see the commit decision
		coord.setCommitted();
		coord.setState ( TxState.COMMITTING );
        
		uts.shutdown ( true );
        
		//restart and make resource return the indoubt xid on recover
		xaRes1.reset();
		xids = new Xid[1];
		xids[0] = xid;
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		xaRes1.setFailureMode ( TestXAResource.FAIL_COMMIT , 
											new XAException ( XAException.XA_HEURMIX ) );
		xaRes1.setRecoverList ( xids );
		info = new TSInitInfoImp(); info.setProperties ( properties );
		//info.registerResource ( res1 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 failTest ( "Committing coordinator not recovered?" );
			 
		//late registration triggers recovery
		uts.registerResource ( res1 );	 
        
		//wait a while to allow rollback on timeout of the coordinator
		sleep();
        
		tids = new String[1];
		tids[0] = tid;
        
		//assert that coordinator is hazard
       
		 AdminTransaction admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
                
		//NOTE: now, admin state should be HAZARD, not mixed.
		//this is because the coordinator is recovered and set into
		//hazard (the state before replay) and this state is maintained
		//if commit replay fails.
		if ( admintx.getState() != AdminTransaction.STATE_HEUR_HAZARD )
			failTest ( 
			"Committing coordinator not hazard after recovery of mixed XA " + admintx.getState() );


		

        
        
		//assert that coordinator can be terminated
		admintx.forceForget();
         
        
		if ( xaRes1.getLastCommitted() == null )
			 failTest ( 
			 "No commit replay on recovery of committing coordinator?" );
        
		if ( xaRes1.getLastForgotten() == null )
			failTest ( "Forget not propagated to XAResource?" );
        
		
		uts.shutdown ( true );  
    }
    
   
    
 
    public void testNoPresumedAbortForRecoveredXidIfMultipleResourcesForSameEIS()
    throws Exception
    {
        uts.init ( info );
        
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		tid = coord.getCoordinatorId();
		//we can not use the regular JTA interfaces because then the
		//restx will not be added to the coordinator until terminator commit, 
		//which will not be reached in this case.
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();
		xaRes1.setFailureMode ( TestXAResource.FAIL_COMMIT , 
				new XAException ( XAException.XA_HEURHAZ ) );
		coord.addParticipant ( restx );
		coord.prepare();
        
		//assert that xa resource is now indoubt
		if ( xaRes1.getLastPrepared() == null ||
			!xaRes1.getLastPrepared().equals ( xid ) )
			failTest ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			failTest ( "Coordinator not indoubt after prepare?" );
            
		//force coordinator into committing state, and make sure that log entry will
		//see the commit decision
		coord.setCommitted();
		coord.setState ( TxState.COMMITTING );
        
		uts.shutdown ( true );
        
		//restart and make resource return the indoubt xid on recover
		xaRes1.reset();
		xaRes2.reset();
		xids = new Xid[1];
		xids[0] = xid; //should be committed on one xaRes and not touched on the other
		
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		xaRes1.setRecoverList ( xids );
		res2 = new TestXATransactionalResource ( xaRes2 , "XARes2");
		xaRes2.setRecoverList ( xids );
		info = new TSInitInfoImp(); info.setProperties ( properties );
		info.registerResource ( res1 );
		info.registerResource ( res2 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
		uts.init ( info );
        
		

		sleep();
        
		tids = new String[1];
		tids[0] = tid;
        
		
		
        //commit must be received by at least one xaRes instance
		if ( ! xid.equals( xaRes1.getLastCommitted() ) && ! xid.equals ( xaRes2.getLastCommitted() ) )
			 failTest ( 
			 "No commit replay on recovery of committing coordinator?" );
        

		uts.shutdown ( true );	
    }
    
    public void testConsistentPresumedAbortForMultipleResourcesToSameEIS()
    throws Exception
    {
         
        
        uts.init ( info );
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();	
		ct.rollback();	
		
        xids = new Xid[1];
		xids[0] = xid; //should be rolled back on both
		TestXAResource xaRes3 = new TestXAResource();
		TestXATransactionalResource res3 = new TestXATransactionalResource ( xaRes3 , "XARes3" );
		TestXAResource xaRes4 = new TestXAResource();
		TestXATransactionalResource res4 = new TestXATransactionalResource ( xaRes4 , "XARes4" );
		xaRes3.setRecoverList ( xids );
		xaRes4.setRecoverList ( xids );
        uts.registerResource ( res3 );
        uts.registerResource ( res4 );
        
          
		if ( !xaRes3.getLastRolledback().equals(xid)  || !xaRes4.getLastRolledback().equals ( xid )  )
			failTest ( "Inconsistent Presumed Abort for equivalent resources");
        
		
		uts.shutdown ( true );				
	
    }
    
    public void testTransitionToHazardStateRetriesCommit() throws Exception 
    {
    	//test for issue 26911
    	
        uts.init ( info );
        
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		tid = coord.getCoordinatorId();
		//we can not use the regular JTA interfaces because then the
		//restx will not be added to the coordinator until terminator commit, 
		//which will not be reached in this case.
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();
		xaRes1.setFailureMode ( TestXAResource.FAIL_COMMIT , 
				new XAException ( XAException.XA_HEURHAZ ) );
		coord.addParticipant ( restx );
		coord.prepare();
		
        
		//assert that xa resource is now indoubt
		if ( xaRes1.getLastPrepared() == null ||
			!xaRes1.getLastPrepared().equals ( xid ) )
			failTest ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			failTest ( "Coordinator not indoubt after prepare?" );
		
		try {
			coord.commit ( false );
			failTest ( "No hazard errors on commit?" );
		} catch ( HeurHazardException ok ) {}
		
		//wait to allow for commit to retry (heuristic hazard exceptions require this)
		sleep();
		
		//assert we are in hazard state
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.HEUR_HAZARD ) )
			failTest ( "Coordinator not hazard after hazard exceptions on commit?" );
		
		//reset xa resource to detect commit reply
		xaRes1.reset();
		
		//wait for replay to happen
		sleep();
		sleep();
		sleep();
		
		if ( !coord.getState().equals ( TxState.TERMINATED ) )
			failTest ( "Coordinator not terminated after hazard is gone: " + coord.getState() );
		
		if ( xaRes1.getLastCommitted() == null ) failTest ( "Issue 26911: no retry for hazard coordinator?" );
		
	
		
		uts.shutdown ( true );
    }
    
    public void testTransitionToMixedStateRetriesCommit() throws Exception 
    {
    	//test for issue 26911
    	
        uts.init ( info );
        
		ctm = uts.getCompositeTransactionManager();
		ct = ctm.createCompositeTransaction (1000 );
		coord = ( CoordinatorImp ) ct.getCompositeCoordinator();
		tid = coord.getCoordinatorId();
		//we can not use the regular JTA interfaces because then the
		//restx will not be added to the coordinator until terminator commit, 
		//which will not be reached in this case.
		restx = ( XAResourceTransaction ) res1.getResourceTransaction ( ct );
		restx.setXAResource ( xaRes1 );
		restx.resume();
		restx.suspend();
		xid = xaRes1.getLastStarted();
		xaRes1.setFailureMode ( TestXAResource.FAIL_COMMIT , 
				new XAException ( XAException.XA_HEURHAZ) );
		HeuristicParticipant p = new HeuristicParticipant ( null );
		p.setFailMode ( HeuristicParticipant.FAIL_HEUR_MIXED );
		coord.addParticipant ( p );
		coord.addParticipant ( restx );
		coord.prepare();
		
        
		//assert that xa resource is now indoubt
		if ( xaRes1.getLastPrepared() == null ||
			!xaRes1.getLastPrepared().equals ( xid ) )
			failTest ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			failTest ( "Coordinator not indoubt after prepare?" );
		
		try {
			coord.commit ( false );
			failTest ( "No mixed errors on commit?" );
		} catch ( HeurMixedException ok ) {}
		
		//wait to allow for commit to retry (heuristic hazard exceptions require this)
		sleep();
		
		//assert we are in hazard state
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.HEUR_MIXED ) )
			failTest ( "Coordinator not hazard after heur mixed exceptions on commit?" );
	
		
		//reset xa resource to detect commit reply
		xaRes1.reset();
		
		//wait for reply to happen
		sleep();
		if ( xaRes1.getLastCommitted() == null ) failTest ( "Issue 26911: no retry for mixed coordinator?" );
		
	
		
		uts.shutdown ( true );
    }
    
    public static void main ( String[] args )
    throws Exception
    {
        TestRunner.run ( XARecoveryTestJUnit.class );
    }
}
