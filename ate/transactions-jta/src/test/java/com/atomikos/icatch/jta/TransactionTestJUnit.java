package com.atomikos.icatch.jta;

import java.util.Properties;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;

/**
 * 
 * 
 * 
 *
 * 
 */
public class TransactionTestJUnit extends TransactionServiceTestCase
{
    private UserTransactionService uts;
    
    private Xid xid1, xid2;
    
    private Transaction tx;
    
    private TransactionManagerImp tm;
    
    private TestXAResource xaRes1_ , xaRes2_;
    

    public TransactionTestJUnit ( String name )
    {
        super ( name );
    }
    
    protected void setUp()
    {
        super.setUp();
        uts =
            new UserTransactionServiceImp();
        
        TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TransactionTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME , "1000" );
        	xaRes1_ = new TestXAResource();
        	xaRes2_ = new TestXAResource();
        	uts.registerResource ( new TestXATransactionalResource ( xaRes1_ , "TestXA1"  ) );    
        uts.registerResource ( new TestXATransactionalResource ( xaRes2_ , "TestXA2" ) );
        
        	uts.init ( info );
        	tm = ( TransactionManagerImp ) uts.getTransactionManager();


    }
    
    protected void tearDown()
    {
        uts.shutdown ( true );
        super.tearDown();
        
    }
    
    public void testRollbackOfActiveTransaction()
    throws Exception
    {
        tm.begin();
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
    }
    
    public void testCommitWithOneHeuristicRollbackResourceAndOneNormalResource()
    throws Exception
    {
        tm.begin();
        

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
    }
    
    public void testCommitWithOneHeuristicRollbackResourceAndOneHeuristicCommitResource()
    throws Exception
    {
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
        
    }
    
    public void testCommitWithUniformHeuristicRollback() throws Exception
    {
        tm.begin();
        
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
    }
    
    public void testCommitWithNormalResources() throws Exception
    {
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
    }
    
    public void testCommitWithOneReadonlyResourceAndOneNormalResource() 
    throws Exception
    {
        tm.begin();
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
    }
    
    public void testCommitWithUnexpectedXAError() throws Exception
    {
        tm.begin();
        
        
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
    }
    
    public void testCommitOnePhase() throws Exception
    {
        tm.begin();
 
        tx = tm.getTransaction();
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
               
        tx.commit();
        
        if ( xaRes1_.getLastPrepared() != null )
            throw new Exception ( "Prepare on resource for 1PC?" );
    }
    
    
    public void testCommitOnePhaseWithRollbackException() throws Exception
    {
        tm.begin();
        
        
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
    }
    
    public void testSynchAfterCompletionRollbackForCommitOnePhaseWithRollbackException() throws Exception
    {
        tm.begin();
        
        
        xaRes1_.setFailureMode ( TestXAResource.FAIL_PREPARE , 
                    new XAException ( XAException.XA_RBTIMEOUT) );
                    
        tx = tm.getTransaction();
        TestSynchronization ts = new TestSynchronization();
        tx.registerSynchronization ( ts );
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        try {        
            tx.commit();
            throw new Exception ( "No commit exception for RBROLLBACK?" );
        }
        catch ( javax.transaction.RollbackException rb ) {
            //should happen
        }
        assertFalse ( ts.getCompletionStatus() == Status.STATUS_COMMITTED );
        //System.out.println ( ts.getCompletionStatus() );
    }    
    
    public void testCommitWithSetRollbackOnly() throws Exception
    {
        tm.begin();
       
                    
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
    }
    
    public void testEnlistAfterCommit() throws Exception
    {
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
    }
    
    public void testEnlistAfterRollback() throws Exception
    {
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
    }
    
    public void testEnlistSameResourceTwice() throws Exception
    {
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
    }
    
    public void testEnlistSameResourceTwiceWithoutDelistInBetween() throws Exception
    {
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
    }
    
    public void testEnlistAfterSetRollbackOnly() throws Exception
    {
        
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
		

    }
    
    public void testEnlistWithoutDelist() throws Exception
    {
		tm.begin();
		tx = tm.getTransaction();
		        
		tx.enlistResource ( xaRes1_ );
		xid1 = xaRes1_.getLastStarted();
		        
		tx.rollback();	
		//should trigger delist
		if ( !xid1.equals ( xaRes1_.getLastEnded() ) )
			throw new Exception ( "Auto-delist does not work?");
    }
    
    public void testDelistAfterCommit() throws Exception
    {
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
        
    }
    
    public void testDelistAfterRollback() throws Exception
    {
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
    }
    
    public void testSynchronizationWithCommit() throws Exception
    {
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
    }
    
    public void testSynchronizationWithRollback() throws Exception
    {
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
        
        tx.rollback();
        
        if ( sync.isCalledBefore() )
            throw new Exception ( "Synch: beforeCompletion() on rollback?" );
        
        //FOLLOWING COMMENTED OUT FOR 2.0 RELEASE: JBOSS RELIES ON THIS 
        //TO ACTUALLY HAPPEN   
//        if ( sync.isCalledAfter() )
//            throw new Exception ( "Synch: afterCompletion() on rollback?" );
    }
    
    public void testRegisterSynchronizationAfterCompletion()
    throws Exception
    {
        tm.begin();
        
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        
        
        
        tx.commit();
        
        try {
            TestSynchronization sync = new TestSynchronization();
            tx.registerSynchronization ( sync );
            throw new Exception ( "Synchronization added after commit?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen 
        }
        
    }
    
    public void testParallelSubTransactionIsolation() throws Exception
    {
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
    }
    
    public void testSerialSubTransactionIsolation() throws Exception
    {
        try {
            tm.begin();
            
            tx = tm.getTransaction();
            
            uts.getCompositeTransactionManager().getCompositeTransaction().
                    getTransactionControl().setSerial();
            
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
            
            //assert that both child transactions use the same
            //xid on the resource
            if ( !xid1.equals ( xid2 ) )
                throw new Exception ( "Serial siblings do not share same XID?" );
            
            tx.commit();
        } catch ( Exception e ) {
            e.printStackTrace();
            failTest ( e.getMessage() );
        }
    }
    
    public void testSubTransactionCommitAfterTimeoutRollback() throws Exception
    {
    	//simulate the case where a timeout/rollback interleaves with subtx commit by application
    	tm.begin();
    	tx = tm.getTransaction();
    	tm.begin();
    	Transaction subtx = tm.getTransaction();
    	//simulate timeout
    	subtx.rollback();
    	//now simulate commit by app
    	try {
    		subtx.commit();
    		failTest ( "Commit works after timeout/rollback" );
    	} catch ( Exception ok ) {
    		ok.printStackTrace();
    	}
    	tx.commit();
    }
    
    public void testSynchronizationWithAddParticipantInBeforeCompletion() throws Exception
    {
    	tm.begin();
    	tx = tm.getTransaction();
    	tx.registerSynchronization ( 
    			new Synchronization () {
    				
					public void afterCompletion (int arg ) {
						
						
					}

					public void beforeCompletion() {
						
						try {
							tx.enlistResource ( new TestXAResource() );
						} catch (Exception e) {
							throw new RuntimeException ( e );
						}
						
					}
    				
    			}
    	);
    	tx.commit();
    }
    
    public void testSynchronizationWithSetRollbackOnlyInBeforeCompletion() throws Exception
    {
    	tm.begin();
    	tx = tm.getTransaction();
    	tx.registerSynchronization ( 
    			new Synchronization() {

					public void afterCompletion(int arg) {
						
						
					}

					public void beforeCompletion() {
						try {
							tx.setRollbackOnly();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SystemException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
    				
    			}
    	);
    	try {
    		tx.commit();
    		failTest ( "Commit works if setRollbackOnly during beforeCompletion!" );
    	} catch ( RollbackException ok ) {}
    }
    
    public void testSynchronizationWithRuntimeExceptionInBeforeCompletionLeadsToRollback()
    throws Exception 
    {
    	//test for case 24246
    	tm.begin();
    	tx = tm.getTransaction();
    	tx.registerSynchronization(
    			new Synchronization() {

					public void afterCompletion(int state) {
						
						
					}

					public void beforeCompletion() {
						throw new RuntimeException ( "Simulated error" );
					}
    				
    			}
    	);
    	
    	try {
    		tx.commit();
    		fail ( "Commit works with errors in beforeCompletion" );
    	} catch ( RollbackException ok ) {
    		//this should be ok
    	} catch ( Exception e ) {
    		fail ( "commit does not propertly deal with runtime exceptions in synchronization" );
    	}
    	
    }
    
    public void testSynchronizationWithRuntimeExceptionInAfterCompletionStillAllowsCommit()
    throws Exception {
    	//test for case 24246
    	assertNull ( tm.getTransaction() );
    	tm.begin();
    	tx = tm.getTransaction();
    	tx.registerSynchronization(
    			new Synchronization() {

					public void afterCompletion(int state) {
						throw new RuntimeException ( "Simulated error" );
						
					}

					public void beforeCompletion() {
						
					}
    				
    			}
    	);
    	
    	try {
    		tx.commit();
    	}  catch ( Exception e ) {
    		e.printStackTrace();
    		fail ( "commit does not propertly deal with runtime exceptions in synchronization" );
    	}
    	
    }
    
    public void testDefaultTimeout() throws Exception
    {
    	assertNull ( tm.getTransaction() );
    	
    	tm.begin();
    	Thread.sleep(1200);
    	try {
			tm.commit();
			fail("transaction should have timed out");
		} catch (RollbackException ex) {
			assertTrue ( ex.getMessage().startsWith("Transaction set to rollback only") );
		}
    }
    
    public void testApplicationLevelRollbackAfterTimeout() throws Exception {
    	tm.begin();
    	Thread.sleep(1200);
    	tm.rollback();
    }
    
    public void testHashCode() throws Exception 
    {
    	tm.begin();
    	TransactionImp tx = ( TransactionImp ) tm.getTransaction();
    	String tid = tx.getCT().getTid();
    	int expectedHashCode = tid.hashCode();
    	assertEquals ( expectedHashCode , tx.hashCode() );
    	tm.rollback();
    }
    
    public void testSuspendTransactionSuspendsXAResourcesInAnIdempotentWay() throws Exception 
    {
    	tm.begin();
    	Transaction tx = tm.getTransaction();
    	tx.enlistResource ( xaRes1_ );
    	assertNull ( xaRes1_.getLastEnded() );
    	tm.suspend();
    	assertNotNull ( xaRes1_.getLastEnded() );
    	// assert idempotence: second delist/suspend should not do anything
    	xaRes1_.reset();
    	tx.delistResource ( xaRes1_ , XAResource.TMSUSPEND );
    	assertNull ( xaRes1_.getLastEnded() );
    }

        public void testSuspendResumeWorks() throws Exception 
    {
    	// test for bug 63145 
    	tm.begin();
    	tx = tm.getTransaction();
		tx.enlistResource ( xaRes1_ );
		Xid xid = xaRes1_.getLastStarted();
		tx.delistResource ( xaRes1_ , XAResource.TMSUSPEND );
		assertEquals ( xid , xaRes1_.getLastEnded() );
		xaRes1_.reset();
		tx.enlistResource ( xaRes1_ );
		assertEquals ( xid , xaRes1_.getLastStarted() );
    	tm.rollback();
    }
    
    
}
