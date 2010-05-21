package com.atomikos.icatch.jta;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.TransactionalResource;
import com.atomikos.datasource.xa.TemporaryXATransactionalResource;
import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.standalone.UserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;

 /**
  *
  *
  *A release test class for JTA functionality.
  */

public class ReleaseTester 
{
    
  private static  TestXAResource xaRes1_ , xaRes2_;
  
  private static Properties getDefaultProperties()
     {
		Properties ret = new Properties ();
		ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME , "tm.out" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME ,  "." + File.separator );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , "." + File.separator );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME , "tmlog" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "50" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME , "300000" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME , "500" );
		  ret.setProperty ( UserTransactionServiceFactory.SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME , "true" );
		  //add unique tm name for remote usertx support
		  ret.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "tm" );
		  //indication of whether client tx demarcation is allowed
		  ret.setProperty ( AbstractUserTransactionServiceFactory.CLIENT_DEMARCATION_PROPERTY_NAME , "false" );
		  ret.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
			"com.sun.jndi.cosnaming.CNCtxFactory" );
		  ret.setProperty ( Context.PROVIDER_URL , "" );
	
		  return ret;
 
      }

  
  private static void testTransaction ( UserTransactionService uts )
  throws Exception
  {
  		Xid xid1 = null , xid2 = null;
        Transaction tx = null;
        TransactionManagerImp tm = 
            ( TransactionManagerImp ) uts.getTransactionManager();

        //
        //CASE ROLLBACK-1: test rollback of an active transaction
        //
		
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        xaRes1_.reset();
        xaRes2_.reset();

        tx = tm.getTransaction();
        tx.enlistResource ( xaRes1_ );
        tx.enlistResource ( xaRes2_ );
        
        //assert that XIDs are different for each resource
        //which is crucial for correct recovery
        xid1 = xaRes1_.getLastStarted();
        xid2 = xaRes2_.getLastStarted();
        if  ( xid1.equals ( xid2 ) ) 
        	throw new Exception ( "XIDs are the same for different resources???");

        tx.rollback();
        if ( xaRes1_.getLastRolledback() == null )
            throw new Exception ( "Rollback of active not propagated to XAResource?" );
        if ( xaRes2_.getLastRolledback() == null )
            throw new Exception ( "Rollback of active not propagated to XAResource?" );
        
        //
        //CASE COMMIT-1: test commit with heuristic rollback set of resources
        //
        
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        xaRes1_.reset();
        xaRes2_.reset();
        

        tx = tm.getTransaction();
        if ( tx.getStatus() != Status.STATUS_ACTIVE )
            throw new Exception ( "Transaction not active?" );
        
        //make the resource fail on the next commit call
        xaRes1_.setFailureMode ( 
            TestXAResource.FAIL_COMMIT , 
            new XAException ( XAException.XA_HEURRB ) );
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );

		//assert that XIDs are different for each resource
		//which is crucial for correct recovery
		xid1 = xaRes1_.getLastStarted();
		xid2 = xaRes2_.getLastStarted();
		if  ( xid1.equals ( xid2 ) ) 
			throw new Exception ( "XIDs are the same for different resources???");        
        
        try {
            tx.commit();
            throw new Exception ( "No heuristic exception on heuristic commit?" );
        }
        catch ( HeuristicMixedException hm ) {
            //should happen 
        }
        
        //
        //CASE COMMIT-2: test commit with heuristic mixed set of resources
        //
        
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        xaRes1_.reset();
        xaRes2_.reset();
        
        tx = tm.getTransaction();
        
        //make the resources fail in different heuristic ways
        xaRes1_.setFailureMode ( 
            TestXAResource.FAIL_COMMIT , 
            new XAException ( XAException.XA_HEURRB ) );
        xaRes2_.setFailureMode ( 
            TestXAResource.FAIL_COMMIT , 
            new XAException ( XAException.XA_HEURCOM ) );
            
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        
        try {
            tx.commit();
            throw new Exception ( "No heuristic exception on heuristic commit?" );
        }
        catch ( HeuristicMixedException hm ) {
            //should happen 
        }
        
        //
        //CASE COMMIT-3: test commit with entire heuristic rollback
        //
        
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        xaRes1_.reset();
        xaRes2_.reset();
        
        tx = tm.getTransaction();
        
         xaRes1_.setFailureMode ( 
            TestXAResource.FAIL_COMMIT , 
            new XAException ( XAException.XA_HEURRB ) );
        xaRes2_.setFailureMode ( 
            TestXAResource.FAIL_COMMIT , 
            new XAException ( XAException.XA_HEURRB ) );
            
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );

		//assert that XIDs are different for each resource
		//which is crucial for correct recovery
		xid1 = xaRes1_.getLastStarted();
		xid2 = xaRes2_.getLastStarted();
		if  ( xid1.equals ( xid2 ) ) 
			throw new Exception ( "XIDs are the same for different resources???");        
        
        try {
            tx.commit();
            throw new Exception ( "No heuristic exception on heuristic commit?" );
        }
        catch ( HeuristicRollbackException hm ) {
            //should happen 
        } 
        
        //
        //CASE COMMIT-4: test commit with normal resources
        //
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        xaRes1_.reset();
        xaRes2_.reset();
        
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        xid1 = xaRes1_.getLastStarted();
        
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        xid2 = xaRes2_.getLastStarted();
        
        tx.commit();
        
        if ( xaRes1_.getLastCommitted() == null || 
           ! xaRes1_.getLastCommitted().equals ( xid1 ) ) {
           throw new Exception ( "No commit on first resource?" );     
        }
        if ( xaRes2_.getLastCommitted() == null || 
           ! xaRes2_.getLastCommitted().equals ( xid2 ) ) {
           throw new Exception ( "No commit on second resource?" );     
        }
        
        //assert that rollback fails as expected
        try {
            tx.rollback();
            throw new Exception ( "No exception on rollback after commit?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen 
        }
        
        //
        //CASE COMMIT-5: test commit with a readonly resource
        //
        
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        xaRes1_.reset();
        xaRes2_.reset();
        xaRes1_.setReadOnly ( true );
        
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        xid1 = xaRes1_.getLastStarted();
        
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        xid2 = xaRes2_.getLastStarted();
        
        tx.commit();
        
        if ( xaRes1_.getLastCommitted() != null ) {
           throw new Exception ( "Commit on readonly resource?" );     
        }
        if ( xaRes2_.getLastCommitted() == null || 
           ! xaRes2_.getLastCommitted().equals ( xid2 ) ) {
           throw new Exception ( "No commit on second resource?" );     
        }
        
        //
        //CASE COMMIT-6: test commit with an unexpected XA error
        //
        
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        
        xaRes1_.reset();
        xaRes2_.reset();
        
        xaRes1_.setFailureMode ( TestXAResource.FAIL_COMMIT , 
                    new XAException ( XAException.XAER_RMERR ) );
                    
        tx = tm.getTransaction();
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        
        try {        
            tx.commit();
            throw new Exception ( "No commit exception for RMERR?" );
        }
        catch ( HeuristicMixedException se ) {
            //should happen: JTA converts hazard to mixed on commit
            //and the hazard state is induced due to repeated failures
            //of commit in the participant instance.
        }
        
        //
        //CASE COMMIT-7: test commit with one-phase 
        //
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        
        xaRes1_.reset();
        xaRes2_.reset();
                    
        tx = tm.getTransaction();
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
               
        tx.commit();
        
        if ( xaRes1_.getLastPrepared() != null )
            throw new Exception ( "Prepare on resource for 1PC?" );
        
        //
        //CASE COMMIT-8: rollback exception on 1PC
        //
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        
        xaRes1_.reset();
        xaRes2_.reset();
        
        xaRes1_.setFailureMode ( TestXAResource.FAIL_COMMIT , 
                    new XAException ( XAException.XA_RBROLLBACK ) );
                    
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        try {        
            tx.commit();
            throw new Exception ( "No commit exception for RBROLLBACK?" );
        }
        catch ( javax.transaction.RollbackException rb ) {
            //should happen
        }
        
        //
        //CASE COMMIT-9 test commit with setRollbackOnly
        //
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        
        xaRes1_.reset();
        xaRes2_.reset();
                    
        tx = tm.getTransaction();
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        tx.setRollbackOnly();
        
        try {
            tx.commit();
            throw new Exception ( "Commit succeeds after setRollbackOnly?" );
        }
        catch ( javax.transaction.RollbackException rb ) {
            //should happen
        }
        
        //
        //CASE ENLIST-1: test enlistment after commit
        //
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        tx = tm.getTransaction();
        
        tx.commit();
        
        try {
        	//System.out.println ("TEST SHOULD NOT EXIT HERE ");
            tx.enlistResource ( xaRes1_ );
            throw new Exception ( "Enlistment works after commit?" );
        }
        catch ( java.lang.IllegalStateException ill ) {
            //should happen  
            
        }
        
        //
        //CASE ENLIST-2: test enlistment after rollback
        //
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        tx = tm.getTransaction();
        
        tx.rollback();
        
        try {
            tx.enlistResource ( xaRes1_ );
            throw new Exception ( "Enlistment works after rollback?" );
        }
        catch ( java.lang.IllegalStateException ill ) {
            //should happen 
        }
        catch ( javax.transaction.RollbackException rb ) {
        	//should happen
        }
        
        //
        //CASE ENLIST-3: test double enlistment of same resource
        //
        
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        xaRes1_.reset();
        tm.begin();
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        xid1 = xaRes1_.getLastStarted();
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource. TMSUCCESS );
        xid2 = xaRes1_.getLastStarted();
        
        tx.rollback();
        
        //assert that both enlistments of SAME resource
        //also use the SAME xid to allow data sharing
        
        if ( ! xid1.equals ( xid2 ) )
            throw new Exception ( "Double enlistment of SAME resource with different XID?" );
        
        //
        //CASE ENLIST-4: test double enlistment of SAME resource BEFORE delistment
        //
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        xaRes1_.reset();
        tm.begin();
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        xid1 = xaRes1_.getLastStarted();
        try {
            tx.enlistResource ( xaRes1_ );
            throw new Exception ( "Second enlistment of enlisted resource before delist works?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen; not according to JTA specifically
            //but rather because of the context being re-used at
            //the wrong time?
        }
        
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        xid2 = xaRes1_.getLastStarted();
        
        tx.rollback();
        
		//
		//CASE ENLIST-5: test enlistment after setRollbackOnly
		//
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
		tm.begin();
		tx = tm.getTransaction();
	        
		tx.setRollbackOnly();
	        
		try {
			tx.enlistResource ( xaRes1_ );
			throw new Exception ( "Enlistment works after setRollbackOnly?" );
		}
		catch ( javax.transaction.RollbackException rb ) {
			//should happen
		}
		
		tx.rollback();
		

		//
		//CASE ENLIST-6: test enlist without delist
		//
		
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
		xaRes1_.reset();
		tm.begin();
		tx = tm.getTransaction();
		        
		tx.enlistResource ( xaRes1_ );
		xid1 = xaRes1_.getLastStarted();
		        
		tx.rollback();	
		//should trigger delist
		if ( !xid1.equals ( xaRes1_.getLastEnded() ) )
			throw new Exception ( "Auto-delist does not work?");
						
        
        //
        //CASE DELIST-1: test delist after commit
        //
        
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        tx = tm.getTransaction();
        
        tx.commit();
        
        try {
            tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
            throw new Exception ( "Delistment works after commit?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen 
        }
        
        //
        //CASE DELIST-2: test delist after rollback
        //
        
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        tx = tm.getTransaction();
        
        tx.rollback();
        
        try {
            tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
            throw new Exception ( "Delistment works after rollback?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen 
        }
        
        //
        //CASE SYNC-1: test synchronization in normal commit case
        //
        
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        
        xaRes1_.reset();
        xaRes2_.reset();
        
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        
        TestSynchronization sync = new TestSynchronization();
        tx.registerSynchronization ( sync );
        
        tx.commit();
        
        if ( ! sync.isCalledBefore() )
            throw new Exception ( "Synch: no beforeCompletion()?" );
        if ( ! sync.isCalledAfter() )
            throw new Exception ( "Synch: no afterCompletion()?" );
        if ( sync.getCompletionStatus() != Status.STATUS_COMMITTED )
        	throw new Exception ( "Synch: not committed status?");
            
        //
        //CASE SYNC-2: test synchronization in normal rollback
        //
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        tm.begin();
        
        xaRes1_.reset();
        xaRes2_.reset();
        
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        
        sync = new TestSynchronization();
        tx.registerSynchronization ( sync );
        
        tx.rollback();
        
        if ( sync.isCalledBefore() )
            throw new Exception ( "Synch: beforeCompletion() on rollback?" );
         
        //FOLLOWING COMMENTED OUT FOR 2.0 RELEASE: JBOSS RELIES ON THIS 
        //TO ACTUALLY HAPPEN   
//        if ( sync.isCalledAfter() )
//            throw new Exception ( "Synch: afterCompletion() on rollback?" );
            
        
		            
        //
        //CASE SYNC-3: add sync after completion
        //
		if ( tm.getTransaction() != null ) throw new Exception ("Pending txs?");
        xaRes1_.reset();
        xaRes2_.reset();
        
        tm.begin();
        
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        
        
        
        tx.commit();
        
        try {
            sync = new TestSynchronization();
            tx.registerSynchronization ( sync );
            throw new Exception ( "Synchronization added after commit?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen 
        }
        
          
        //
        //CASE SUBTX-1: test non-serial subtx for isolation
        //
        
        xaRes1_.reset();
        xaRes2_.reset();
        
        tm.setDefaultSerial ( false );
        
        tm.begin();
        
        tx = tm.getTransaction();
        
        
        //create first child
        tm.begin();
        Transaction child1 = tm.getTransaction();
        
        child1.enlistResource ( xaRes1_ );
        xid1 = xaRes1_.getLastStarted();
        child1.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        child1.commit();
        
        //create second child
        tm.begin();
        Transaction child2 = tm.getTransaction();
        
        child2.enlistResource ( xaRes1_ );
        xid2 = xaRes1_.getLastStarted();
        child2.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        child2.commit();
        
        //assert that both child transactions did NOT use the same
        //xid on the resource
        if ( xid1.equals ( xid2 ) && !tm.getDefaultSerial() )
            throw new Exception ( "Non-serial siblings share same XID?" );
        
        tx.commit();
        
        //
        //CASE SUBTX-2: test serial subtxs for non-isolation
        //
        
        xaRes1_.reset();
        xaRes2_.reset();
        
        tm.begin();
        
        tx = tm.getTransaction();
        
        uts.getCompositeTransactionManager().getCompositeTransaction().
                getTransactionControl().setSerial();
        
        //create first child
        tm.begin();
        child1 = tm.getTransaction();
        
        child1.enlistResource ( xaRes1_ );
        xid1 = xaRes1_.getLastStarted();
        child1.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        child1.commit();
        
        //create second child
        tm.begin();
        child2 = tm.getTransaction();
        
        child2.enlistResource ( xaRes1_ );
        xid2 = xaRes1_.getLastStarted();
        child2.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        child2.commit();
        
        //assert that both child transactions use the same
        //xid on the resource
        if ( !xid1.equals ( xid2 ) )
            throw new Exception ( "Serial siblings do not share same XID?" );
        
        tx.commit();
  }
  
    /**
     *Test the transaction manager functions.
     *@param uts The transaction service.
     */
     
   private static  void testTransactionManager ( UserTransactionService uts )
   throws Exception 
   {
        TransactionManager tm = uts.getTransactionManager();
        
        tm.setTransactionTimeout ( 10 );
        
        //clear the xa resources from previous tests
        xaRes1_.reset();
        xaRes2_.reset();
        
        //
        //CASE 1: test a well-behaved transaction case
        //
        
        if ( tm.getTransaction() != null )
            throw new Exception ( "Transaction for thread before begin?" );
        
        if ( tm.getStatus() != Status.STATUS_NO_TRANSACTION )
            throw new Exception ( "tm.getStatus() wrong if no tx?" );
        
        tm.begin();
        
        if ( tm.getStatus() == Status.STATUS_NO_TRANSACTION )
            throw new Exception ( "tm.getStatus() returns none after begin?" );
            
        if ( tm.getTransaction() == null )
            throw new Exception ( "No transaction for thread after begin?" );
            
        Transaction tx = tm.getTransaction();
        
        //test if the transaction instances can be compared
        if ( ! tx.equals ( tm.getTransaction() ) )
            throw new Exception ( "Transactions can not be compared?" );
        
        //enlist some well-behaved resources
       
            
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        
        Xid xid2 = xaRes2_.getLastStarted();
        if ( xid2 == null )
            throw new Exception ( "No enlist on second resource?" );
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        Xid xid1 = xaRes1_.getLastStarted();
        if ( xid1 == null )
            throw new Exception ( "No enlist on first resource?" );
        
        
        tm.commit();
        
        
            
        if ( xaRes2_.getLastPrepared() == null || 
             ! xaRes2_.getLastPrepared().equals ( xid2 )) 
            throw new Exception ( "No prepare on second resource?" );
        
        if ( xaRes1_.getLastCommitted() == null || 
             ! xaRes1_.getLastCommitted().equals ( xid1 ))
            throw new Exception ( "No commit on first resource?" );
        
        if ( tm.getTransaction() != null || 
             tm.getStatus() != Status.STATUS_NO_TRANSACTION )
            throw new Exception ( "Transaction association after commit?" );
        
        //
        //CASE 2: test commit after rollback: should generate exception
        //
        
        xaRes1_.reset();
        xaRes2_.reset();
        
        tm.begin();
        
        tx = tm.getTransaction();
        
        tx.rollback();
        
        try {
          tm.commit();
          throw new Exception ( "Commit after rollback: no exception?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen because tm has no tx association any more
        }
        
        //test commit on the tx instance
        try {
            tx.commit();
            throw new Exception ( "Commit after rollback: no exception?" );
        }
        catch ( javax.transaction.RollbackException rb ) {
            //should happen
        }
        catch ( IllegalStateException ill ) {
            //should happen 
        }
        
        //
        //CASE 3: test subtransactions and thread association
        //
        
        tm.begin();
        tx = tm.getTransaction();
        
        //create first child
        tm.begin();
        if ( tm.getTransaction().equals ( tx ) )
            throw new Exception ( "No thread association for subtx?" );
            
        //commit the child
        tm.commit();
        
        if ( ! tm.getTransaction().equals ( tx ) )
            throw new Exception ( "No parent tx for thread after subtx commit?" );
        tm.rollback();
        
        //
        //CASE 4: test suspend and resume
        //
        
        xaRes1_.reset();
        xaRes2_.reset();
        
        tm.begin();
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        tx = tm.suspend();
        
        if ( tm.getStatus() != Status.STATUS_NO_TRANSACTION )
            throw new Exception ( "getStatus() on tm is wrong after suspend?" );
        
        //create another tx
        tm.begin();
        Transaction other = tm.getTransaction();
        other.enlistResource ( xaRes2_ );
        Xid xid = xaRes2_.getLastStarted();
        other.delistResource ( xaRes2_ , XAResource.TMSUCCESS);
        
        tm.commit();
        
        //assert that other was really another tx, by checking of resource
        //has received commit.
        if ( xaRes2_.getLastCommitted() == null ||
             ! xaRes2_.getLastCommitted().equals ( xid ) )
            throw new Exception ( "begin() after suspend() does not create independent tx?" );
        
        
        
        tm.resume ( tx );
        
        tm.rollback();
        
        //assert that resource 1 has really received rollback
        if ( xaRes1_.getLastRolledback() == null )
            throw new Exception ( "Resume does not work properly?" );
        
   }
   
    /**
     *Perform a test for client demarcation functionality.
     *@param uts The transaction service handle.
     *@exception Exception On error.
     */
     
   public static void testClientDemarcation ( UserTransactionService uts )
   throws Exception
   {
        CompositeTransactionManager ctm = 
          uts.getCompositeTransactionManager();
        CompositeTransaction ct = null;
        FileOutputStream out = null;
        ObjectOutputStream oout = null;
        FileInputStream in = null;
        ObjectInputStream oin = null;
        String oldTid = null;
        
        //ensure that no previous transaction exists
        ct = ctm.getCompositeTransaction();
        if ( ct != null ) {
            ct.getTransactionControl().getTerminator().rollback();
        }
        //assert that server has no tx
        ct = ctm.getCompositeTransaction();
        if ( ct != null ) 
            throw new Exception ( 
            "Invalid precondition: existing tx" );

        
        UserTransaction rcut =
            uts.getUserTransaction();
        
        //Following makes sense only for remote demarcation
        if ( ! ( rcut instanceof RemoteClientUserTransaction ) ) 
            return;
        
        //assert that no tx exists at being
        if ( rcut.getStatus() != Status.STATUS_NO_TRANSACTION )
            throw new Exception ( "UserTx has tx before begin?" );
        
        rcut.begin();
        
        if ( rcut.getStatus() == Status.STATUS_NO_TRANSACTION )
            throw new Exception ( "UserTx has NO tx after begin?" );
        
        //assert that server does no thread mapping
        ct = ctm.getCompositeTransaction();
        
        
        //CHANGED TO USE THREAD ASSOCIATION
        if ( ct == null  ) 
            throw new Exception ( 
            "Remote usertx doesn't cause thread association when used in server?" );
        
        //assert that server tx exists
        ct = ctm.getCompositeTransaction ( rcut.toString() );
        if ( ct == null ) 
            throw new Exception ( "Server does not know tx started by client?" );
        
        
        
        //
        //CHECK COMMIT
        //
        oldTid = rcut.toString();
        //assert that commit works
        rcut.commit();
        //if commit worked, then tx is no longer at server
        ct = ctm.getCompositeTransaction ( oldTid );
        if ( ct != null ) 
            throw new Exception ( "User tx commit does not work?" );
        if ( rcut.toString() != null )
            throw new Exception ( "User tx toString() does not work OK?" );
        ct = ctm.getCompositeTransaction ( oldTid );
        if ( ct != null )
            throw new Exception ( "User tx commit not delegated to server?" );
            
        //
        //CHECK ROLLBACK
        //
        
        rcut.begin();
        oldTid = rcut.toString();
        rcut.rollback();
        if ( rcut.toString() != null )
            throw new Exception ( "User tx rollback() does not work OK?" );
        ct = ctm.getCompositeTransaction ( oldTid );
        if ( ct != null )
            throw new Exception ( "User tx rollback not delegated to server?" );
        
        //
        //CHECK SETROLLBACKONLY
        //
        
        rcut.begin();
        oldTid = rcut.toString();
        rcut.setRollbackOnly();
        try {
            rcut.commit();
            throw new Exception ( 
                "User tx commit works after setRollbackOnly?" );
        }
        catch ( javax.transaction.RollbackException e ) {
            //should happen 
        }
        
        //assert no tx for thread
        if ( rcut.toString() != null )
            throw new Exception ( "Commit did not remove tx for thread?" );
        
        //
        //ENSURE NO NESTED TXS ARE ALLOWED
        //REMOVED TO SUPPORT DUAL MODE
        
//        rcut.begin();
//        try {
//            rcut.begin();
//            throw new Exception ( "User tx allows nested txs?" );
//        }
//        catch ( NotSupportedException ns ) {
//            //required
//        }
//        rcut.rollback();
        
        //
        //ASSERT THAT USERTX IS REFERENCEABLE
        //
        
        System.setProperty ( 
            Context.INITIAL_CONTEXT_FACTORY, 
            "com.sun.jndi.rmi.registry.RegistryContextFactory" );
        
        Context ctx = new InitialContext();
        ctx.bind ( "usertx" , rcut );
        rcut = ( UserTransaction ) ctx.lookup ( "usertx" );
        if ( rcut == null )
            throw new Exception ( "Usertx not referenceable?" );
        ctx.unbind ( "usertx" );
        
        //
        //ASSERT THAT USERTX SHIPPING BEHAVES AS EXPECTED
        //
        
        rcut.begin();
        
        //assert that usertx can be shipped (Serialized) and 
        //reconstructed on the other side
        UserTransaction old = rcut;
        oldTid = rcut.toString();
        out = new FileOutputStream ( "rcut.out" );
        oout = new ObjectOutputStream ( out );
        oout.writeObject ( rcut );
        oout.close();
        
        in = new FileInputStream ( "rcut.out" );
        oin = new ObjectInputStream ( in );
        rcut = ( UserTransaction ) oin.readObject();
        
        //assert that tx is still there after serialization
        if ( rcut.getStatus() == Status.STATUS_NO_TRANSACTION )
            throw new Exception ( "Serialization loses tx?" );
        
        //assert that tx is still the same
        if ( !rcut.toString().equals ( oldTid ) )
            throw new Exception ( "Serialization not OK" );
     
     //FOLLOWING TEST REMOVED: FAIL WITH NEW DUAL MODE   
//        //assert that rollback will not work on a shipped instance
//        try {
//            rcut.rollback();
//            throw new Exception ( "Rollback works on a shipped usertx?" );
//        }
//        catch ( SecurityException se ) {
//            //expected
//        }
//        
//        //assert that commit will not work on a shipped instance
//        try {
//            rcut.commit();
//            throw new Exception ( "Commit works on a shipped usertx?" );
//        }
//        catch ( SecurityException se ) {
//            //expected
//        }
        
        old.commit();
        
        
   }
   
   public static void testUserTransaction ( UserTransactionService uts ) throws Exception
   {
   		TestXAResource xares = new TestXAResource();
   		TestXAResource xares2 = new TestXAResource();
   		UserTransactionImp utx1 = null;
   		UserTransactionImp utx2 = null;
   		UserTransactionManager utm = null;
   		
   		//First make sure TM is shutdown
   		uts.shutdown ( true );
   		
   		
   		uts.init ( uts.createTSInitInfo() );	
   	
   		utx1 = new UserTransactionImp();
   		
   		
   		//starting a tx should work since TM is running
   		utx1.begin();
   		
   		
   		
   		//second instance should NOT init TM
   		utx2 = new UserTransactionImp();
   		
   		utm = new UserTransactionManager();
   		utm.getTransaction().enlistResource(xares);
   		
		//	rollback on the second instance should work too
		utx2.rollback();
   		
		if ( utx1.getStatus() != Status.STATUS_NO_TRANSACTION )
			  throw new Exception ( 
			  "UserTransaction instances not equivalent for rollback?");   		
   		
   		//shutdown should work 
   		uts.shutdown ( true );
   		
   		if ( uts.getTransactionManager() != null )
   			throw new Exception ( "TM present after shutdown?");
   		
		
   		
   		
   		
   }
   
   public static void testUserTransactionManager ( UserTransactionService uts )
   throws Exception
   {
   		UserTransactionManager utm1 = null , utm2 = null;
   		boolean error = false;
   		
   		//assert TM not running
   		uts.shutdown ( true );
   		
		uts.init ( uts.createTSInitInfo() );
   		
   		
   		utm1 = new UserTransactionManager();
		//begin a tx on utm1;
		//this is the actual trigger
		utm1.begin();
   		
   		
   		
   		
   		//second utm should work
   		utm2 = new UserTransactionManager();
   		
   		
   		//and rollback on second
   		utm2.rollback();
   		
   		
   		uts.shutdown ( true );
   	
   		if ( Configuration.getCompositeTransactionManager() != null )
   			throw new Exception ( "TM present after shutdown");
   		
   		
   		
   }
   
   public static void testAutomaticRegistrationMode ( UserTransactionService uts )
   throws Exception
   {
   		uts.shutdown ( true );	
   	
		TransactionManager tm = null;
		UserTransaction utx = null;
   		TestXAResource xares = new TestXAResource();
   		TSInitInfo info = uts.createTSInitInfo();
   		
   		Properties p = info.getProperties();
   		p.setProperty (  AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME , "false");
   		uts.init ( info );
   		
   		utx = uts.getUserTransaction();
   		tm = uts.getTransactionManager();
   		if ( tm == null ) throw new Exception ( "No TM?");
   		utx.begin();
   		if ( tm.getTransaction() == null ) throw new Exception ( "No tx for usertx " + utx.getClass().getName()  );
		try {
			tm.getTransaction().enlistResource ( xares );
			throw new Exception ( "Unknown resource enlist works?");
		}
		catch ( SystemException ok ) {}   		
   		
   		utx.rollback();
   		
		uts.shutdown ( true );
		
		//repeat similar test, but now with explicit
		//resource registration: this should add
		//acceptAllResource during init, and unknown
		//resource enlist should work now
		xares = new TestXAResource();
		TransactionalResource res = 
			new TestXATransactionalResource (  xares , "TESTRESOURCE" );
		info.registerResource ( res );
		uts.init ( info );
	   		
	   		
		utx = uts.getUserTransaction();
		tm = uts.getTransactionManager();
		utx.begin();
		
		xares = new TestXAResource();
		tm.getTransaction().enlistResource ( xares );
				
	   		
		utx.rollback();
	   		
		uts.shutdown ( true );		
		
		
		info = uts.createTSInitInfo();
		p = info.getProperties();
		p.setProperty ( AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME , "true");
		xares.reset();
		uts.init ( info );
		utx = uts.getUserTransaction();
		utx.begin();
		tm = uts.getTransactionManager();
		tm.getTransaction().enlistResource(xares);
		tm.rollback();
		uts.shutdown ( true );   	
   }
   
   /**
    * Test if the order in which resources are returned from the 
    * Configuration is the same as the order of registration.
    * Needed to make sure that AcceptAllXATransactionalResource is last.
    * @param uts
    * @throws Exception
    */
   public static void testResourceOrdering ( UserTransactionService uts )
   throws Exception
   {
   		TestXAResource xares1 = new TestXAResource();
   		TestXAResource xares2 = new TestXAResource();
   		TestXAResource xares3 = new TestXAResource();
   		TemporaryXATransactionalResource res1 = null, res2 = null, res3 = null;
   		
   		res1 = new TemporaryXATransactionalResource ( xares1 );
   		res2 = new TemporaryXATransactionalResource ( xares2 );
   		res3 = new TemporaryXATransactionalResource ( xares3 );
   		
   		
   		uts.shutdown ( true );
   		TSInitInfo info = uts.createTSInitInfo();
   		info.registerResource ( res1 );
   		info.registerResource ( res2 );
   		info.registerResource ( res3 );
   		uts.init ( info );
   		
   		Enumeration enumm = Configuration.getResources();
   		XATransactionalResource res = null;
   		
   		//first one should be res1
   		res = ( XATransactionalResource ) enumm.nextElement();
   		if ( res != res1 ) throw new Exception ( "Error in resource order");
   		if ( ! res.usesXAResource(xares1) ) 
   			throw new Exception ( "Error in usesXAResource");
   		
   		//second one should be res2
		res = ( XATransactionalResource ) enumm.nextElement();
		if ( res != res2 ) throw new Exception ( "Error in resource order");
		if ( ! res.usesXAResource(xares2) ) 
			throw new Exception ( "Error in usesXAResource" );  		
	   	
	   	//third one should be res3	
		res = ( XATransactionalResource ) enumm.nextElement();
		if ( res != res3 ) throw new Exception ( "Error in resource order");
		if ( ! res.usesXAResource(xares3) ) 
			throw new Exception ( "Error in usesXAResource" ); 	   		
	   	uts.shutdown ( true );
   }
   
   
   /**
    * Test if the JTA along with AcceptAllXATransactionalResource
    * generates the right XIDs.
    * @param uts
    * @throws Exception
    */
   
   public static void testAcceptAllXAResource ( UserTransactionService uts )
   throws Exception
   {	
   		
   		
   		uts.shutdown(true);
   		
		String dummyName = "TestXARes";
   		TSInitInfo info = uts.createTSInitInfo();
   		TestXAResource xares = new TestXAResource();
   		TransactionManager tm = null;
   		Transaction tx = null;
   		Xid xid = null;
   		TestXATransactionalResource dummyRes = 
   			new TestXATransactionalResource ( xares , dummyName );
   		
   		//register dummy resource to trigger addition of 
   		//default resource
   		info.registerResource ( dummyRes );
   		uts.init ( info );
   		tm = uts.getTransactionManager();
   		
   		//create new resource to make sure dummyres doesn't
   		//accept it
   		xares = new TestXAResource();
   		
   		//FOLLOWING REMOVED: WE DON'T ACCEPT CONCURRENT ENLISTS
   		//FOR SAME XARESOURCE 
//   		tm.begin();
//   		tx = tm.getTransaction();
//   		tx.enlistResource(xares);
//   		xid = xares.getLastStarted();
//   		if ( xid.toString().indexOf ( dummyName ) >= 0 )
//   			throw new Exception ( "Wrong xid");
//   		
//   		//enlist second time without delist in between
//   		xares = ( TestXAResource ) xares.clone();
//   		tx.enlistResource ( xares );
//   		
//   		if ( xid.equals ( xares.getLastStarted() ) )
//   			throw new Exception ( "Same branch for concurrent enlists");
//   			
//   		
//   		tm.rollback();
   		
   		
   		//Now repeat test, but with delist in between;
   		//should reuse same XID
		xares = new TestXAResource();
		tm.begin();
		tx = tm.getTransaction();
		tx.enlistResource(xares);
		xid = xares.getLastStarted();
		if ( xid.toString().indexOf ( dummyName ) >= 0 )
			throw new Exception ( "Wrong xid");
	   		
		tx.delistResource ( xares , XAResource.TMSUCCESS );
		
		xares = ( TestXAResource ) xares.clone();
		tx.enlistResource ( xares );
	   		
		if ( !xid.equals ( xares.getLastStarted() ) )
			throw new Exception ( "Different branch for serial enlists");
	   			
	   		
		tm.rollback();   		
   		
   		
   		
   		uts.shutdown(true);
   }
   
   public static void testJ2eeUserTransaction ( 
   		UserTransactionService uts ) throws Exception
   {
   		uts.shutdown ( true );
   		
   		J2eeUserTransaction utx = new J2eeUserTransaction();
   		
   		//assert referencibility
   		Reference ref = utx.getReference();
   		if ( ref == null ) throw new Exception ( "getReference fails" );
   		Class clazz = Class.forName ( ref.getFactoryClassName() );
   		ObjectFactory fact = ( ObjectFactory ) clazz.newInstance();
   		utx = ( J2eeUserTransaction ) fact.getObjectInstance ( ref , null , null , null );
   		
   		try {
   			utx.begin();
   		}
   		catch ( Exception normal ) {}
   		
   		//VITAL: assert that TM is not running due to this
   		if ( Configuration.getCompositeTransactionManager() != null ) 
   			throw new Exception ( "Auto startup for J2eeUserTransaction" );
   		
   		TSInitInfo info = uts.createTSInitInfo();
   		uts.init ( info );
   		
   		utx.begin();
   		if ( utx.getStatus() == Status.STATUS_NO_TRANSACTION ) 
   			throw new Exception ( "No tx started" );
   		utx.rollback();
   		
   		uts.shutdown ( true );
   		
   }
   
   public static void testJ2eeTransactionManager ( 
   		UserTransactionService uts ) throws Exception
   {
   		uts.shutdown ( true );
   		
   		J2eeTransactionManager tm = new J2eeTransactionManager();
   		
   		//assert referencibility
   		Reference ref = tm.getReference();
		Class clazz = Class.forName ( ref.getFactoryClassName() );
		ObjectFactory fact = ( ObjectFactory ) clazz.newInstance();
		tm = ( J2eeTransactionManager ) fact.getObjectInstance ( ref , null , null , null );
		
		try {
			tm.begin();
		}
		catch ( Exception normal ) {}
		
		//VITAL: assert TM did not startup due to this
		if ( Configuration.getCompositeTransactionManager() != null )
			throw new Exception ( "Auto startup for J2eeTransactionManager" );

   		
		TSInitInfo info = uts.createTSInitInfo();
		uts.init ( info );
   		
		tm.begin();
		if ( tm.getStatus() == Status.STATUS_NO_TRANSACTION ) 
			throw new Exception ( "No tx started" );
		tm.rollback();
   		   		
   		uts.shutdown  ( true );
   }
    
     /**
       *Perform the test for a given UserTransactionService implementation
       *and a given init info object.
       *
       *@param uts The transaction service handle.
       *@param info The TSInitInfo instance. This should be an <b>empty</b>
       *instance, and all resource registering will be done in the test.
       *
       *@exception Exception On error.
       */
       
    public static void test ( UserTransactionService uts , TSInitInfo info )
    throws Exception 
    {
         xaRes1_ = new TestXAResource();
         info.registerResource ( new TestXATransactionalResource ( xaRes1_ , "TestXA1"  ) );
         
         xaRes2_ = new TestXAResource();
         info.registerResource ( new TestXATransactionalResource ( xaRes2_ , "TestXA2" ) );
         Properties p = info.getProperties();
         if ( p == null ) 
            p = getDefaultProperties();
         p.setProperty ( 
                AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TrmiTestTransactionManager" );
         p.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		p.setProperty ( Context.PROVIDER_URL , "iiop://localhost:1050" );
		p.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
			   "com.sun.jndi.cosnaming.CNCtxFactory" );
         info.setProperties ( p );
         uts.init ( info );
         //first testTransaction, to have maximum likelyhood of detecting
         //duplicate Xids
		 
		 testTransaction ( uts );
		 
		
         testClientDemarcation ( uts );
         testTransactionManager ( uts );
        
		         
		 uts.shutdown ( true );
		
		//the following tests will restart the TM 
		testJ2eeUserTransaction ( uts );
		testJ2eeTransactionManager ( uts );
		 
		testAutomaticRegistrationMode ( uts );
		testUserTransaction(uts);
		testUserTransactionManager ( uts );
		testResourceOrdering ( uts );
		testAcceptAllXAResource ( uts );
		 
		
			 
    }
    

    
    public static void main ( String[] args ) 
    {
        try {
            System.err.println ( "Starting JTA release test with the standalone version..." );
            UserTransactionService uts =
                new UserTransactionServiceImp();
            TSInitInfo info = uts.createTSInitInfo();
            test ( uts , info );
        }
        catch ( Exception e ) {
            e.printStackTrace(); 
        } 
        finally {
            System.err.println ( "Done!" ); 
        }
    }
  
   
}
