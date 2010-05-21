package com.atomikos.icatch.imp;
import java.util.Properties;

import javax.transaction.HeuristicMixedException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.XAResourceTransaction;
import com.atomikos.datasource.xa.XID;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.TSInitInfoImp;

/**
  *
  *
  *A test class for testing recovery functionality.
  *This class should ONLY be called by package tests, because
  *it is part of the test source that is NOT to be distributed to 
  *customers. 
  */

public class XARecoveryTester
{
    
    
    //
    //test all combinations for XA recovery 
    //
    
    private static void testXARecovery (
        UserTransactionService uts , TSInitInfo info )
        throws Exception
    {
          //test each of the 5 recoverable XA states 
          //with each recoverable coordinator state
          
        TransactionManager tm = null;
        CompositeTransactionManager ctm = null;
        CompositeTransaction ct = null;
        Transaction tx = null;
        CoordinatorImp coord = null;
        Xid xid = null;
        Xid[] xids = null;
        //TSInitInfo info = new TSInitInfoImp();
        XAResourceTransaction restx = null;
        TestLogAdministrator admin = null;
        String tid = null;
        String[] tids = null;
        TestXAResource xaRes1 = null , xaRes2 = null;
        TestXATransactionalResource res1 = null , res2 = null;
        Properties props = info.getProperties();
        //
        //CASE 1: test recovery of indoubt XAresource of a
        //non-recoverable ( VOTING ) coordinator.
        //This should lead to rollback of the XA transaction.
        //
        
      
        xaRes1 = new TestXAResource();
        res1 = 
            new TestXATransactionalResource ( xaRes1 , "XARes1"  );
        info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        if ( ! xaRes1.isRecoveryCalled () )
            throw new Exception ( "No recovery for xa resource?" );
        
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
        info = new TSInitInfoImp();
        info.setProperties ( props );
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
            throw new Exception ( "Recovery not called on registered XAResource?" );
        if ( xaRes1.getLastRolledback() == null || 
            ! xaRes1.getLastRolledback().equals ( xid ) )
            throw new Exception ( "Recovery of indoubt xid for voting coordinator fails for xid: " + xid );
        
        //lastly, shut down the transaction service for the next test case
        uts.shutdown ( true );
        
         //
        //CASE 1 BIS: test recovery of indoubt ORACLE 8.1.7 XAResource of a
        //non-recoverable ( VOTING ) coordinator.
        //This should lead to rollback of the XA transaction.
        //
        
      
        xaRes1 = new TestXAResource();
        xaRes1.setRecoveryLoop ( true );
        info = new TSInitInfoImp();
        info.setProperties ( props );
        res1 = 
            new TestXATransactionalResource ( xaRes1 , "XARes1Bis"  );
        info.registerResource ( res1 );
        uts.init ( info );
        //here, we should not go into an infinite loop! (ora8.1.7 can 
        //provoke this!)
        if ( ! xaRes1.isRecoveryCalled () )
            throw new Exception ( "No recovery for xa resource?" );
        
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
        xaRes1.reset();
        xaRes1.setRecoverList ( xids );
        //recreate info
        info = new TSInitInfoImp(); info.setProperties ( props );
        //re-register a NEW instance for XARes1. This is absolutely needed,
        //or the previous TestXATransactionalResource will not recover since it 
        //believes it already has been recovered
        res1 = new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        if ( ! xaRes1.isRecoveryCalled() )
            throw new Exception ( "Recovery not called on registered XAResource?" );
        if ( xaRes1.getLastRolledback() == null || 
            ! xaRes1.getLastRolledback().equals ( xid ) )
            throw new Exception ( "Recovery of indoubt xid for voting coordinator fails?" );
        
        //lastly, shut down the transaction service for the next test case
        uts.shutdown ( true );
        
     
        //
        //CASE 2: test recovery of indoubt XA for indoubt coordinator.
        //Should lead to rollback, but only after coordinator timeout.
        //
        
        //make sure that the xa resource makes no fuzz on next 
        //startup
        xaRes1.reset();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
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
            throw new Exception ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            throw new Exception ( "Coordinator not indoubt after prepare?" );
            
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        xaRes1.setRecoverList ( xids );
		xaRes1.setRecoveryLoop ( true );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
        uts.init ( info );
        
        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             throw new Exception ( "Indoubt coordinator not recovered?" );
        
        //wait a while to allow rollback on timeout of the coordinator
        System.err.println ( "About to sleep a while to allow recovery..." );
        try {
            Thread.currentThread().sleep ( 5000 );
        }
        catch ( InterruptedException inter ) {}
        
        tids = new String[1];
        tids[0] = tid;
        //assert that coordinator is rolled back
        if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
             admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
             AdminTransaction admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
             throw new Exception ( "Indoubt coordinator not rolled back on recovery? " +
                                             admintx.getState()  );
        }
             
        if ( xaRes1.getLastRolledback() == null )
             throw new Exception ( 
             "Indoubt xa tx not rolled back on recovery of indoubt coordinator?" );
        
        System.err.println ( "Woken up, recovery ok." );
        uts.shutdown ( true );
        
        //
        //CASE 3: test recovery of indoubt XA for COMMITTING coordinator.
        //Should lead to commit replay.
        //
        
        //make sure that the xa resource makes no fuzz on next 
        //startup
        xaRes1.reset();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
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
            throw new Exception ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            throw new Exception ( "Coordinator not indoubt after prepare?" );
            
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
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        
        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             throw new Exception ( "Committing coordinator not recovered?" );
        
        //wait a while to allow rollback on timeout of the coordinator
        System.err.println ( "About to sleep a while to allow recovery..." );
        try {
            Thread.currentThread().sleep ( 5000 );
        }
        catch ( InterruptedException inter ) {}
        
        tids = new String[1];
        tids[0] = tid;
        
        //assert that coordinator is terminated
        if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
             admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
             AdminTransaction admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
             throw new Exception ( "Committing coordinator not terminated on recovery? " +
                                             admintx.getState()  );
             }
             
        if ( xaRes1.getLastCommitted() == null )
             throw new Exception ( 
             "No commit replay on recovery of committing coordinator?" );
        
        System.err.println ( "Woken up, recovery ok." );
        uts.shutdown ( true );
        
        //
        //CASE 4: test recovery of indoubt XA for HAZARD coordinator.
        //Should NOT be rolled back during recovery (but not committed
        //either).
        //
        
        //make sure that the xa resource makes no fuzz on next 
        //startup
        
        //System.err.println ( "CASE 4" );
        xaRes1.reset();
        xaRes2 = new TestXAResource();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        res2 = new TestXATransactionalResource ( xaRes2 , "XARes2" );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        info.registerResource ( res2 );
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
            throw new Exception ( "XAResource not indoubt after failed commit?" );
        
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xaRes2.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 = new TestXATransactionalResource ( xaRes1 , "XARes1" );
        res2 =  new TestXATransactionalResource ( xaRes2 , "XARes2" );
        xaRes2.setRecoverList ( xids );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        info.registerResource ( res2 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
        uts.init ( info );
        
        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             throw new Exception ( "Hazard coordinator not recovered?" );
        
        //wait a while to allow rollback on timeout of the coordinator
        System.err.println ( "About to sleep a while to allow recovery..." );
        try {
            Thread.currentThread().sleep ( 6000 );
        }
        catch ( InterruptedException inter ) {}
        
        tids = new String[1];
        tids[0] = tid;
             
        if ( xaRes2.getLastCommitted() == null )
             throw new Exception ( 
             "Indoubt XAResource not committed after recovery of hazard coordinator?" );
        if ( xaRes2.getLastRolledback() != null )
            throw new Exception ( 
            "Indoubt XAResource rolledback after recovery of hazard coordinator?" );
            
        //assert that coordinator can be terminated
        if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
             admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
             AdminTransaction admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
             admintx.forceForget();
             }
        
        System.err.println ( "Woken up, recovery ok." );
        System.err.println ( "Shutting down..." );
        uts.shutdown ( true );
        System.err.println ( "Shut down." );

        //
        //CASE 5: test recovery of hazard XA for indoubt coordinator.
        //Should lead to rollback, but only after coordinator timeout.
        //After that, coordinator should be hazard.
        //
        
        //make sure that the xa resource makes no fuzz on next 
        //startup
        xaRes1.reset();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        System.err.println ( "Restarting transaction service..." );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        System.err.println ( "Started." );
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
        System.err.println ( "Preparing coordinator..." );
        coord.prepare();
        System.err.println ( "Prepared." );

        //assert that xa resource is now indoubt
        if ( xaRes1.getLastPrepared() == null ||
            !xaRes1.getLastPrepared().equals ( xid ) )
            throw new Exception ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            throw new Exception ( "Coordinator not indoubt after prepare?" );
        System.err.println ( "Shutting down transaction service..." );
        uts.shutdown ( true );
        System.err.println ( "Shut down." );
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        xaRes1.setRecoverList ( xids );
        //make xa resource fail with heur hazard on rollback
        xaRes1.setFailureMode ( TestXAResource.FAIL_ROLLBACK ,
              new XAException ( XAException.XA_HEURHAZ ) );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
        System.err.println ( "Restart for recovery..." );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        System.err.println ( "Started" );

        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             throw new Exception ( "Indoubt coordinator not recovered?" );
        
        //wait a while to allow rollback on timeout of the coordinator
        System.err.println ( "About to sleep a while to allow recovery..." );
        try {
            //sleep long enough, since hazard rollback will be repeated
            //a number of times before coordinator changes state
            Thread.currentThread().sleep ( 60000 );
        }
        catch ( InterruptedException inter ) {}
        
        tids = new String[1];
        tids[0] = tid;
        //assert that coordinator is hazard
       
        AdminTransaction admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
        if ( admintx.getState() != AdminTransaction.STATE_HEUR_HAZARD )
            throw new Exception ( 
            "Indoubt coordinator not hazard after recovery of hazard XA?" );
        
         //assert that coordinator can be terminated
         admintx.forceForget();
             
             
        if ( xaRes1.getLastRolledback() == null )
             throw new Exception ( 
             "Indoubt xa tx not rolled back on recovery of indoubt coordinator?" );
        
        System.err.println ( "Woken up, recovery ok." );
        uts.shutdown ( true );
        
        //
        //CASE 6: test recovery of hazard XA for COMMITTING coordinator.
        //Should lead to commit replay, and coordinator in hazard state.
        //
        
        //make sure that the xa resource makes no fuzz on next 
        //startup
        xaRes1.reset();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
            throw new Exception ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            throw new Exception ( "Coordinator not indoubt after prepare?" );
            
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
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        
        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             throw new Exception ( "Committing coordinator not recovered?" );
        
        //wait a while to allow rollback on timeout of the coordinator
        System.err.println ( "About to sleep a while to allow recovery..." );
        try {
            Thread.currentThread().sleep ( 60000 );
        }
        catch ( InterruptedException inter ) {}
        
        tids = new String[1];
        tids[0] = tid;
        
        //assert that coordinator is hazard
       
         admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
        if ( admintx.getState() != AdminTransaction.STATE_HEUR_HAZARD )
            throw new Exception ( 
            "Committing coordinator not hazard after recovery of hazard XA?" );
        
        //assert that coordinator can be terminated
        admintx.forceForget();
             
        //wait for forget propagation
        Thread.currentThread().sleep ( 2000 );
        
        if ( xaRes1.getLastCommitted() == null )
             throw new Exception ( 
             "No commit replay on recovery of committing coordinator?" );
        
        if ( xaRes1.getLastForgotten() == null )
            throw new Exception ( "Forget not propagated to XAResource?" );
        
        System.err.println ( "Woken up, recovery ok." );
        uts.shutdown ( true );
        
        //
        //CASE 7: test recovery of mixed XA for indoubt coordinator.
        //Should lead to rollback, but only after coordinator timeout.
        //After that, coordinator should be mixed.
        //
        
        //make sure that the xa resource makes no fuzz on next 
        //startup
        xaRes1.reset();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
            throw new Exception ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            throw new Exception ( "Coordinator not indoubt after prepare?" );
            
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        xaRes1.setRecoverList ( xids );
        //make xa resource fail with heur hazard on rollback
        xaRes1.setFailureMode ( TestXAResource.FAIL_ROLLBACK ,
              new XAException ( XAException.XA_HEURMIX ) );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        
        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             throw new Exception ( "Indoubt coordinator not recovered?" );
        
        //wait a while to allow rollback on timeout of the coordinator
        System.err.println ( "About to sleep a while to allow recovery..." );
        try {
            
            Thread.currentThread().sleep ( 5000 );
        }
        catch ( InterruptedException inter ) {}
        
        tids = new String[1];
        tids[0] = tid;
        //assert that coordinator is hazard
       
        admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
        if ( admintx.getState() != AdminTransaction.STATE_HEUR_MIXED )
            throw new Exception ( 
            "Indoubt coordinator not mixed after recovery of mixed XA?" );
        
         //assert that coordinator can be terminated
         admintx.forceForget();
             
             
        if ( xaRes1.getLastRolledback() == null )
             throw new Exception ( 
             "Indoubt xa tx not rolled back on recovery of indoubt coordinator?" );
        
        System.err.println ( "Woken up, recovery ok." );
        uts.shutdown ( true );
        
        //
        //CASE 8: test recovery of mixed XA for COMMITTING coordinator.
        //Should lead to commit replay, and coordinator in mixed state.
        //
        
        //make sure that the xa resource makes no fuzz on next 
        //startup
        xaRes1.reset();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
            throw new Exception ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            throw new Exception ( "Coordinator not indoubt after prepare?" );
            
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
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        
        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             throw new Exception ( "Committing coordinator not recovered?" );
        
        //wait a while to allow rollback on timeout of the coordinator
        System.err.println ( "About to sleep a while to allow recovery..." );
        try {
            Thread.currentThread().sleep ( 30000 );
        }
        catch ( InterruptedException inter ) {}
        
        tids = new String[1];
        tids[0] = tid;
        
        //assert that coordinator is hazard
       
         admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
                
        //NOTE: now, admin state should be HAZARD, not mixed.
        //this is because the coordinator is recovered and set into
        //hazard (the state before replay) and this state is maintained
        //if commit replay fails.
        if ( admintx.getState() != AdminTransaction.STATE_HEUR_HAZARD )
            throw new Exception ( 
            "Committing coordinator not hazard after recovery of mixed XA " + admintx.getState() );
        
        //assert that coordinator can be terminated
        admintx.forceForget();
             
        //wait for forget propagation
        Thread.currentThread().sleep ( 2000 );
        
        if ( xaRes1.getLastCommitted() == null )
             throw new Exception ( 
             "No commit replay on recovery of committing coordinator?" );
        
        if ( xaRes1.getLastForgotten() == null )
            throw new Exception ( "Forget not propagated to XAResource?" );
        
        System.err.println ( "Woken up, recovery ok." );
        uts.shutdown ( true );
        
        //
        //CASE 9: test recovery of heuraborted XA for indoubt coordinator.
        //Should lead to rollback, but only after coordinator timeout.
        //After that, coordinator should be terminated.
        //
        
        //make sure that the xa resource makes no fuzz on next 
        //startup
        xaRes1.reset();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
            throw new Exception ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            throw new Exception ( "Coordinator not indoubt after prepare?" );
            
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
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        
        //assert that coordinator is recovered
        if ( admin.getLogControl().getAdminTransactions() == null ||
             admin.getLogControl().getAdminTransactions().length == 0 )
             throw new Exception ( "Indoubt coordinator not recovered?" );
        
        //wait a while to allow rollback on timeout of the coordinator
        System.err.println ( "About to sleep a while to allow recovery..." );
        try {
            
            Thread.currentThread().sleep ( 5000 );
        }
        catch ( InterruptedException inter ) {}
        
        tids = new String[1];
        tids[0] = tid;
        //assert that coordinator is terminated
       
        if ( ! ( admin.getLogControl().getAdminTransactions( tids )  == null  ||
                admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
            
            admintx =  admin.getLogControl().getAdminTransactions()[0];
            throw new Exception ( 
            "Indoubt coordinator not terminated after recovery with heurAborted xa? " +
            admintx.getState() );
            
                }
             
        if ( xaRes1.getLastRolledback() == null )
             throw new Exception ( 
             "Indoubt xa tx not rolled back on recovery of heurAborted coordinator?" );
        
        System.err.println ( "Woken up, recovery ok." );
        uts.shutdown ( true );
        
        //
        //CASE 10: test recovery of heur committed XA for indoubt coordinator.
        //Should lead to rollback, but only after coordinator timeout.
        //After that, coordinator should be heur committed.
        //
        
        //make sure that the xa resource makes no fuzz on next 
        //startup
        xaRes1.reset();
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
            throw new Exception ( "XAResource not indoubt after prepare?" );
        
        //assert that coordinator is indoubt
        if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
            throw new Exception ( "Coordinator not indoubt after prepare?" );
            
        uts.shutdown ( true );
        
        //restart and make resource return the indoubt xid on recover
        xaRes1.reset();
        xids = new Xid[1];
        xids[0] = xid;
        res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
        xaRes1.setRecoverList ( xids );
        //make xa resource fail with heur commit on rollback
        xaRes1.setFailureMode ( TestXAResource.FAIL_ROLLBACK ,
              new XAException ( XAException.XA_HEURCOM ) );
        info = new TSInitInfoImp(); info.setProperties ( props );
        info.registerResource ( res1 );
        admin = new TestLogAdministrator();
        info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
        uts.init ( info );
        
        
        //wait a while to allow rollback on timeout of the coordinator
        System.err.println ( "About to sleep a while to allow recovery..." );
        try {
            
            Thread.currentThread().sleep ( 5000 );
        }
        catch ( InterruptedException inter ) {}
        
        tids = new String[1];
        tids[0] = tid;
        //assert that coordinator is hazard
       
        admintx = 
                admin.getLogControl().getAdminTransactions ( tids )[0];
        if ( admintx.getState() != AdminTransaction.STATE_HEUR_COMMITTED )
            throw new Exception ( 
            "Indoubt coordinator not heur.committed after recovery of heurCommitted XA?" );
        
         //assert that coordinator can be terminated
         admintx.forceForget();
             
             
        if ( xaRes1.getLastRolledback() == null )
             throw new Exception ( 
             "Indoubt xa tx not rolled back on recovery of indoubt coordinator?" );
        
        System.err.println ( "Woken up, recovery ok." );
        uts.shutdown ( true );
        
        //
        //
        //CASE 11: Assert that recovery doesn't crash TM in case of unavailable DBMS
        //
        //
        
		//construct a test resource with null as the xaresource
		res1 =  new TestXATransactionalResource ( null , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
		try { 
			uts.init ( info );
		}
		catch ( Exception e ) {
			e.printStackTrace();
			throw new Exception ( "Recovery fails for broken DB connection");
		}
		uts.shutdown ( true );
        
        
        //
        //
        //CASE 12: NEW RECOVERY IN 2.0: assert that each Xid is unique
        //among different resources in the same tx
        //
        //
        
        xaRes1.reset();
        xaRes2.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		res2 = new TestXATransactionalResource ( xaRes2 , "XARes2" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		info.registerResource ( res2 );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
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
			throw new Exception ( "Same XID for different resources in same tx?");
		
		ct.getTransactionControl().getTerminator().rollback();	
		
		uts.shutdown ( true );        
 
		//
		//
		//CASE 13: NEW RECOVERY IN 2.0: assert that each Xid is unique
		//among same resource in the different tx
		//
		//
        
		xaRes1.reset();
		
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
			throw new Exception ( "Same XID across different txs?");
		
		ct.getTransactionControl().getTerminator().rollback();	
		
		uts.shutdown ( true );  

		//
		//
		//CASE 14: NEW RECOVERY IN 2.0: assert that late
		//registration also does recovery
		//
		//
        
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		
		info = new TSInitInfoImp(); info.setProperties ( props );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
		
	
		xids[0] = xid;
		xaRes1.setRecoverList ( xids );		
		
		uts.registerResource ( res1 );
	

		if ( ! xaRes1.getLastRolledback().equals ( xid ) )
			throw new Exception ( "Late registration does not do presumed abort?");
		
		uts.shutdown ( true );
		
		
		//
		//
		//CASE 15: NEW RECOVERY IN 2.0: assert that another TMs
		//XIDs are NOT affected during recovery.
		//
		//
        
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
		



		xids = new Xid [1];
		xid = new XID ("anotherTid" , "anotherTM");
		xids[0] = xid;
		xaRes1.setRecoverList ( xids );		
		
		uts.registerResource ( res1 );


		if ( xid.equals ( xaRes1.getLastRolledback() ) )
			throw new Exception ( "Recovery rolls back another TM's XID?");
		if ( xid.equals ( xaRes1.getLastCommitted() ) )
			throw new Exception ( "Recovery commits another TM's XID?");
		if ( xid.equals( xaRes1.getLastPrepared() ) )
			throw new Exception ( "Recovery prepares another TM's XID");
		if ( xid.equals ( xaRes1.getLastEnded() ))
			throw new Exception ( "Recovery ends another TM's XID?");
		if ( xid.equals ( xaRes1.getLastForgotten() ))
			throw new Exception ( "Recovery forgets another TM's XID?");
		if ( xid.equals ( xaRes1.getLastStarted() ))
			throw new Exception ( "Recovery starts another TM's XID?");
		
		uts.shutdown ( true );

		//
		//CASE 16: test recovery of indoubt XA for indoubt coordinator,
		//with late registration.
		//Should lead to rollback, but only after coordinator timeout.
		//
        
		//make sure that the xa resource makes no fuzz on next 
		//startup
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
			throw new Exception ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			throw new Exception ( "Coordinator not indoubt after prepare?" );
            
		uts.shutdown ( true );
        
		//restart and make resource return the indoubt xid on recover
		xaRes1.reset();
		xids = new Xid[1];
		xids[0] = xid;
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		xaRes1.setRecoverList ( xids );
		info = new TSInitInfoImp(); info.setProperties ( props );

		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 throw new Exception ( "Indoubt coordinator not recovered?" );
		
		uts.registerResource(res1);
				 
        
		//wait a while to allow rollback on timeout of the coordinator
		System.err.println ( "About to sleep a while to allow recovery..." );
		try {
			Thread.currentThread().sleep ( 15000 );
		}
		catch ( InterruptedException inter ) {}
        
		tids = new String[1];
		tids[0] = tid;
		//assert that coordinator is rolled back
		if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
			 admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
			 admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
			 throw new Exception ( "Indoubt coordinator not rolled back on recovery? " +
											 admintx.getState()  );
		}
             
		if ( xaRes1.getLastRolledback() == null )
			 throw new Exception ( 
			 "Indoubt xa tx not rolled back on recovery of indoubt coordinator?" );
        
		System.err.println ( "Woken up, recovery ok." );
		uts.shutdown ( true );		

		//
		//CASE 17: test recovery of indoubt XA for COMMITTING coordinator.
		//Should lead to commit replay, even with late registration.
		//
        
		//make sure that the xa resource makes no fuzz on next 
		//startup
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
			throw new Exception ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			throw new Exception ( "Coordinator not indoubt after prepare?" );
            
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
		info = new TSInitInfoImp(); info.setProperties ( props );
		
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 throw new Exception ( "Committing coordinator not recovered?" );

		uts.registerResource ( res1 );
		
		//wait a while to allow rollback on timeout of the coordinator
		System.err.println ( "About to sleep a while to allow recovery..." );
		try {
			Thread.currentThread().sleep ( 15000 );
		}
		catch ( InterruptedException inter ) {}
        
		tids = new String[1];
		tids[0] = tid;
        
		//assert that coordinator is terminated
		if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
			 admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
			 admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
			 throw new Exception ( "Committing coordinator not terminated on recovery? " +
											 admintx.getState()  );
			 }
             
		if ( xaRes1.getLastCommitted() == null )
			 throw new Exception ( 
			 "No commit replay on recovery of committing coordinator?" );
        
		System.err.println ( "Woken up, recovery ok." );
		uts.shutdown ( true );


        
		//
		//CASE 18: test recovery of indoubt XA for HAZARD coordinator
		//with late registration of XAResource.
		//Should NOT be rolled back during recovery (but not committed
		//either).
		//
        
		//make sure that the xa resource makes no fuzz on next 
		//startup
        
		
		xaRes1.reset();
		xaRes2 = new TestXAResource();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		res2 = new TestXATransactionalResource ( xaRes2 , "XARes2" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		info.registerResource ( res2 );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
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
			throw new Exception ( "XAResource not indoubt after failed commit?" );
        
		uts.shutdown ( true );
        
		//restart and make resource return the indoubt xid on recover
		xaRes1.reset();
		xaRes2.reset();
		xids = new Xid[1];
		xids[0] = xid;
		res1 = new TestXATransactionalResource ( xaRes1 , "XARes1" );
		res2 =  new TestXATransactionalResource ( xaRes2 , "XARes2" );
		xaRes2.setRecoverList ( xids );
		info = new TSInitInfoImp(); info.setProperties ( props );
		
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 throw new Exception ( "Hazard coordinator not recovered?" );


		uts.registerResource ( res1 );
		uts.registerResource ( res2 );
        
		//wait a while to allow rollback on timeout of the coordinator
		System.err.println ( "About to sleep a while to allow recovery..." );
		try {
			Thread.currentThread().sleep ( 15000 );
		}
		catch ( InterruptedException inter ) {}
        
		tids = new String[1];
		tids[0] = tid;
             
		if ( xaRes2.getLastCommitted() == null )
			 throw new Exception ( 
			 "Indoubt XAResource not committed after recovery of hazard coordinator?" );
		if ( xaRes2.getLastRolledback() != null )
			throw new Exception ( 
			"Indoubt XAResource rolledback after recovery of hazard coordinator?" );
            
		//assert that coordinator can be terminated
		if ( ! ( admin.getLogControl().getAdminTransactions ( tids ) == null ||
			 admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
			 admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
			 admintx.forceForget();
			 }
        
		System.err.println ( "Woken up, recovery ok." );
		System.err.println ( "Shutting down..." );
		uts.shutdown ( true );
		System.err.println ( "Shut down." );
		
		//
		//CASE 19: test recovery of hazard XA for COMMITTING coordinator
		//with late registration of XAResource.
		//Should lead to commit replay, and coordinator in hazard state.
		//
        
		//make sure that the xa resource makes no fuzz on next 
		//startup
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
			throw new Exception ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			throw new Exception ( "Coordinator not indoubt after prepare?" );
            
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
		info = new TSInitInfoImp(); info.setProperties ( props );
		//info.registerResource ( res1 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 throw new Exception ( "Committing coordinator not recovered?" );



		uts.registerResource ( res1 );
	
        
		//wait a while to allow rollback on timeout of the coordinator
		System.err.println ( "About to sleep a while to allow recovery..." );
		try {
			Thread.currentThread().sleep ( 60000 );
		}
		catch ( InterruptedException inter ) {}
        
		tids = new String[1];
		tids[0] = tid;
        
		//assert that coordinator is hazard
       
		 admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
		if ( admintx.getState() != AdminTransaction.STATE_HEUR_HAZARD )
			throw new Exception ( 
			"Committing coordinator not hazard after recovery of hazard XA?" );
        
		//assert that coordinator can be terminated
		admintx.forceForget();
             
		//wait for forget propagation
		Thread.currentThread().sleep ( 2000 );
        
		if ( xaRes1.getLastCommitted() == null )
			 throw new Exception ( 
			 "No commit replay on recovery of committing coordinator?" );
        
		if ( xaRes1.getLastForgotten() == null )
			throw new Exception ( "Forget not propagated to XAResource?" );
        
		System.err.println ( "Woken up, recovery ok." );
		uts.shutdown ( true );		

		//
		//CASE 20: test recovery of mixed XA for indoubt coordinator
		//with late registration.
		//Should lead to rollback, but only after coordinator timeout.
		//After that, coordinator should be mixed.
		//
        
		//make sure that the xa resource makes no fuzz on next 
		//startup
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
			throw new Exception ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			throw new Exception ( "Coordinator not indoubt after prepare?" );
            
		uts.shutdown ( true );
        
		//restart and make resource return the indoubt xid on recover
		xaRes1.reset();
		xids = new Xid[1];
		xids[0] = xid;
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		xaRes1.setRecoverList ( xids );
		//make xa resource fail with heur hazard on rollback
		xaRes1.setFailureMode ( TestXAResource.FAIL_ROLLBACK ,
			  new XAException ( XAException.XA_HEURMIX ) );
		info = new TSInitInfoImp(); info.setProperties ( props );
		//info.registerResource ( res1 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 throw new Exception ( "Indoubt coordinator not recovered?" );

			
		//late registration should trigger recovery
		uts.registerResource ( res1 );			 
        
		//wait a while to allow rollback on timeout of the coordinator
		System.err.println ( "About to sleep a while to allow recovery..." );
		try {
            
			Thread.currentThread().sleep ( 5000 );
		}
		catch ( InterruptedException inter ) {}
        
		tids = new String[1];
		tids[0] = tid;
		//assert that coordinator is hazard
       
		admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
		if ( admintx.getState() != AdminTransaction.STATE_HEUR_MIXED )
			throw new Exception ( 
			"Indoubt coordinator not mixed after recovery of mixed XA?" );

		
		
		 //assert that coordinator can be terminated
		 admintx.forceForget();
             
             
		if ( xaRes1.getLastRolledback() == null )
			 throw new Exception ( 
			 "Indoubt xa tx not rolled back on recovery of indoubt coordinator?" );
        
		System.err.println ( "Woken up, recovery ok." );
		uts.shutdown ( true );

        
		//
		//CASE 21: test recovery of mixed XA for COMMITTING coordinator
		//with late XAResource registration;
		//Should lead to commit replay, and coordinator in mixed state.
		//
        
		//make sure that the xa resource makes no fuzz on next 
		//startup
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
			throw new Exception ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			throw new Exception ( "Coordinator not indoubt after prepare?" );
            
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
		info = new TSInitInfoImp(); info.setProperties ( props );
		//info.registerResource ( res1 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 throw new Exception ( "Committing coordinator not recovered?" );
			 
		//late registration triggers recovery
		uts.registerResource ( res1 );	 
        
		//wait a while to allow rollback on timeout of the coordinator
		System.err.println ( "About to sleep a while to allow recovery..." );
		try {
			Thread.currentThread().sleep ( 30000 );
		}
		catch ( InterruptedException inter ) {}
        
		tids = new String[1];
		tids[0] = tid;
        
		//assert that coordinator is hazard
       
		 admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
                
		//NOTE: now, admin state should be HAZARD, not mixed.
		//this is because the coordinator is recovered and set into
		//hazard (the state before replay) and this state is maintained
		//if commit replay fails.
		if ( admintx.getState() != AdminTransaction.STATE_HEUR_HAZARD )
			throw new Exception ( 
			"Committing coordinator not hazard after recovery of mixed XA " + admintx.getState() );


		

        
        
		//assert that coordinator can be terminated
		admintx.forceForget();
             
		//wait for forget propagation
		Thread.currentThread().sleep ( 2000 );
        
		if ( xaRes1.getLastCommitted() == null )
			 throw new Exception ( 
			 "No commit replay on recovery of committing coordinator?" );
        
		if ( xaRes1.getLastForgotten() == null )
			throw new Exception ( "Forget not propagated to XAResource?" );
        
		System.err.println ( "Woken up, recovery ok." );
		uts.shutdown ( true );  

		//
		//CASE 22: test recovery of heuraborted XA for indoubt coordinator
		//with late registration of XAResource.
		//Should lead to rollback, but only after coordinator timeout.
		//After that, coordinator should be terminated.
		//
        
		//make sure that the xa resource makes no fuzz on next 
		//startup
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
			throw new Exception ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			throw new Exception ( "Coordinator not indoubt after prepare?" );
            
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
		info = new TSInitInfoImp(); info.setProperties ( props );
		//info.registerResource ( res1 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 throw new Exception ( "Indoubt coordinator not recovered?" );

//		late registration triggers recovery
		uts.registerResource ( res1 );
        
		//wait a while to allow rollback on timeout of the coordinator
		System.err.println ( "About to sleep a while to allow recovery..." );
		try {
            
			Thread.currentThread().sleep ( 5000 );
		}
		catch ( InterruptedException inter ) {}
        
		tids = new String[1];
		tids[0] = tid;
		//assert that coordinator is terminated
       
		if ( ! ( admin.getLogControl().getAdminTransactions( tids )  == null  ||
				admin.getLogControl().getAdminTransactions ( tids ).length == 0 ) ) {
            
			admintx =  admin.getLogControl().getAdminTransactions()[0];
			throw new Exception ( 
			"Indoubt coordinator not terminated after recovery with heurAborted xa? " +
			admintx.getState() );
            
				}
             
		if ( xaRes1.getLastRolledback() == null )
			 throw new Exception ( 
			 "Indoubt xa tx not rolled back on recovery of heurAborted coordinator?" );
        
		System.err.println ( "Woken up, recovery ok." );
		uts.shutdown ( true );


		//
		//CASE 23: test recovery of heur committed XA for indoubt coordinator.
		//Should lead to rollback, but only after coordinator timeout.
		//After that, coordinator should be heur committed.
		//
        
		//make sure that the xa resource makes no fuzz on next 
		//startup
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
			throw new Exception ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			throw new Exception ( "Coordinator not indoubt after prepare?" );
            
		uts.shutdown ( true );
        
		//restart and make resource return the indoubt xid on recover
		xaRes1.reset();
		xids = new Xid[1];
		xids[0] = xid;
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		xaRes1.setRecoverList ( xids );
		//make xa resource fail with heur commit on rollback
		xaRes1.setFailureMode ( TestXAResource.FAIL_ROLLBACK ,
			  new XAException ( XAException.XA_HEURCOM ) );
		info = new TSInitInfoImp(); info.setProperties ( props );
		//info.registerResource ( res1 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		uts.init ( info );

//		late registration triggers recovery
		uts.registerResource ( res1 );        
        
		//wait a while to allow rollback on timeout of the coordinator
		System.err.println ( "About to sleep a while to allow recovery..." );
		try {
            
			Thread.currentThread().sleep ( 5000 );
		}
		catch ( InterruptedException inter ) {}
        
		tids = new String[1];
		tids[0] = tid;
		//assert that coordinator is hazard
       
		admintx = 
				admin.getLogControl().getAdminTransactions ( tids )[0];
		if ( admintx.getState() != AdminTransaction.STATE_HEUR_COMMITTED )
			throw new Exception ( 
			"Indoubt coordinator not heur.committed after recovery of heurCommitted XA?" );
 
 
        
		 //assert that coordinator can be terminated
		 admintx.forceForget();
             
             
		if ( xaRes1.getLastRolledback() == null )
			 throw new Exception ( 
			 "Indoubt xa tx not rolled back on recovery of indoubt coordinator?" );
        
		System.err.println ( "Woken up, recovery ok." );
		uts.shutdown ( true );	


		//
		//
		//CASE 24: NEW RECOVERY IN 2.0: 2 XATransactionalResources
		//may point to the same underlying EIS. 
		//In that case, a recovered Coordinator's XID should not be rolled back
		//at endRecovery, in ANY of the resources.
		//
		//
        
		
		
		xaRes1.reset();
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		xaRes1.setRecoveryLoop ( true );
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
			throw new Exception ( "XAResource not indoubt after prepare?" );
        
		//assert that coordinator is indoubt
		if ( !coord.getState().equals ( TxState.IN_DOUBT ) )
			throw new Exception ( "Coordinator not indoubt after prepare?" );
            
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
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		info.registerResource ( res2 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
		uts.init ( info );
        
		//assert that coordinator is recovered
		if ( admin.getLogControl().getAdminTransactions() == null ||
			 admin.getLogControl().getAdminTransactions().length == 0 )
			 throw new Exception ( "Committing coordinator not recovered?" );



		//wait a while to allow rollback on timeout of the coordinator
		System.err.println ( "About to sleep a while to allow recovery..." );
		try {
			Thread.currentThread().sleep ( 60000 );
		}
		catch ( InterruptedException inter ) {}
        
		tids = new String[1];
		tids[0] = tid;
        
		
		//rollback is forbidden for both xaRes instances
		if ( xaRes1.getLastRolledback() != null || xaRes2.getLastRolledback() != null)
			throw new Exception ( "Recovered coordinator XID is rolled back by presumed abort!!!");
        
        //commit must be received by at least one xaRes instance
		if ( xaRes1.getLastCommitted() == null && xaRes2.getLastCommitted() == null )
			 throw new Exception ( 
			 "No commit replay on recovery of committing coordinator?" );
        

        
		System.err.println ( "Woken up, recovery ok." );
		uts.shutdown ( true );	
		
		//
		//
		//CASE 25: NEW IN RECOVERY IN 2.0: if two resources point to the 
		//same underlying EIS then the presumed abort should be consistently
		//the same on both.
		//
		//	

		xaRes1.reset();
		xaRes2.reset();
		xids = new Xid[1];
		xids[0] = xid; //should be rolled back on both
		
		res1 =  new TestXATransactionalResource ( xaRes1 , "XARes1" );
		xaRes1.setRecoverList ( xids );
		res2 = new TestXATransactionalResource ( xaRes2 , "XARes2");
		xaRes2.setRecoverList ( xids );
		info = new TSInitInfoImp(); info.setProperties ( props );
		info.registerResource ( res1 );
		info.registerResource ( res2 );
		admin = new TestLogAdministrator();
		info.registerLogAdministrator ( admin );
		xaRes1.setRecoveryLoop ( true );
		xaRes2.setRecoveryLoop ( true );
		uts.init ( info );
        

		if ( !xaRes1.getLastRolledback().equals(xid)  || !xaRes2.getLastRolledback().equals ( xid )  )
			throw new Exception ( "Inconsistent Presumed Abort for equivalent resources");
        
		
		uts.shutdown ( true );				
	
		
			      		
		
    }
    
     /**
      *Perform the test for a particular implementation.
      *@param uts The user transaction service.
      *@exception Exception On error.
      */
      
    public static void test ( UserTransactionService uts , TSInitInfo info )
    throws Exception
    {
          testXARecovery ( uts , info );  
          
    }
    
     /**
      *Default test method.
      *This method does the tests with a default user transaction service.
      *The default instance is the standalone implementation.
      *@exception Exception On error.
      */
      
    public static void test() throws Exception
    {
        UserTransactionService uts =
                new UserTransactionServiceImp();
        test ( uts , uts.createTSInitInfo() );
    }
    
    public static void main ( String[] args )
    {
        try {
            System.err.println ( "Starting: RecoveryTester" );
            test();
        }
        catch ( SysException se ) {
            se.printStackTrace();
        }
        catch ( Exception e ) {
            e.printStackTrace(); 
        } 
        finally {
            System.err.println ( "Done: RecoveryTester" ); 
        }
    }
    
}
