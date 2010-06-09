package com.atomikos.icatch.jta;

import java.util.Enumeration;
import java.util.Properties;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.TemporaryXATransactionalResource;
import com.atomikos.datasource.xa.TestXAResource;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * 
 *
 * 
 */
public abstract class AbstractJUnitTransactionManagerTest extends TransactionServiceTestCase
{

    private UserTransactionService uts;
    
    private Xid xid1, xid2;
    
    private Transaction tx;
    
    private TransactionManager tm;
    
    private TestXAResource xaRes1_ , xaRes2_;
 
    public AbstractJUnitTransactionManagerTest(String name)
    {
        super(name);
    }
    
    
    protected void setUp()
    {
        super.setUp();
        uts =
            new UserTransactionServiceImp();
        
        TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TransactionManagerTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDirAsAbsolutePath()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        	xaRes1_ = new TestXAResource();
        	xaRes2_ = new TestXAResource();
        	uts.registerResource ( new TestXATransactionalResource ( xaRes1_ , "TestXA1"  ) );    
        uts.registerResource ( new TestXATransactionalResource ( xaRes2_ , "TestXA2" ) );
        
        	uts.init ( info );
        	tm = getTransactionManager();

    }
    
    protected UserTransactionService getUserTransactionService()
    {
        return uts;
    }
    
    protected void tearDown()
    {
        uts.shutdown ( true );
        super.tearDown();
        
    }
    
    protected abstract TransactionManager getTransactionManager();
    
    public void testNormalTransaction() throws Exception
    {
        if ( tm.getTransaction() != null )
            failTest ( "Transaction for thread before begin?" );
        
        if ( tm.getStatus() != Status.STATUS_NO_TRANSACTION )
            failTest ( "tm.getStatus() wrong if no tx?" );
        
        tm.begin();
        
        if ( tm.getStatus() == Status.STATUS_NO_TRANSACTION )
            failTest ( "tm.getStatus() returns none after begin?" );
            
        if ( tm.getTransaction() == null )
            failTest ( "No transaction for thread after begin?" );
            
        Transaction tx = tm.getTransaction();
        
        //make sure that the underlying CT is marked as JTA transaction
        CompositeTransaction ct = Configuration.getCompositeTransactionManager().getCompositeTransaction();
        assertNotNull ( ct.getProperty (  TransactionManagerImp.JTA_PROPERTY_NAME )  );
        
        //test if the transaction instances can be compared
        if ( ! tx.equals ( tm.getTransaction() ) )
            failTest ( "Transactions can not be compared?" );
        
        //enlist some well-behaved resources
       
            
        tx.enlistResource ( xaRes2_ );
        tx.delistResource ( xaRes2_ , XAResource.TMSUCCESS );
        
        Xid xid2 = xaRes2_.getLastStarted();
        if ( xid2 == null )
            failTest ( "No enlist on second resource?" );
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        Xid xid1 = xaRes1_.getLastStarted();
        if ( xid1 == null )
            failTest ( "No enlist on first resource?" );
        
        
        tm.commit();
        
        
            
        if ( xaRes2_.getLastPrepared() == null || 
             ! xaRes2_.getLastPrepared().equals ( xid2 )) 
            failTest ( "No prepare on second resource?" );
        
        if ( xaRes1_.getLastCommitted() == null || 
             ! xaRes1_.getLastCommitted().equals ( xid1 ))
            failTest ( "No commit on first resource?" );
        
        if ( tm.getTransaction() != null || 
             tm.getStatus() != Status.STATUS_NO_TRANSACTION )
            failTest ( "Transaction association after commit?" );
    }
    
    public void testCommitAfterRollback() throws Exception
    {
        tm.begin();
        
        tx = tm.getTransaction();
        
        tx.rollback();
        
        try {
          tm.commit();
          failTest ( "Commit after rollback: no exception?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen because tm has no tx association any more
        }
        
        //test commit on the tx instance
        try {
            tx.commit();
            failTest ( "Commit after rollback: no exception?" );
        }
        catch ( javax.transaction.RollbackException rb ) {
            //should happen
        }
        catch ( IllegalStateException ill ) {
            //should happen 
        }
    }
    
    public void testSubTransactionThreadAssociation() throws Exception
    {
        tm.begin();
        tx = tm.getTransaction();
        
        //create first child
        tm.begin();
        if ( tm.getTransaction().equals ( tx ) )
            failTest ( "No thread association for subtx?" );
            
        //commit the child
        tm.commit();
        
        if ( ! tm.getTransaction().equals ( tx ) )
            failTest ( "No parent tx for thread after subtx commit?" );
        tm.rollback();
    }
    
    public void testSuspendAndResume() throws Exception
    {
        tm.begin();
        tx = tm.getTransaction();
        
        tx.enlistResource ( xaRes1_ );
        tx.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
        tx = tm.suspend();
        
        if ( tm.getStatus() != Status.STATUS_NO_TRANSACTION )
            failTest ( "getStatus() on tm is wrong after suspend?" );
        
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
            failTest ( "begin() after suspend() does not create independent tx?" );
        
        
        
        tm.resume ( tx );
        
        tm.rollback();
        
        //assert that resource 1 has really received rollback
        if ( xaRes1_.getLastRolledback() == null )
            failTest ( "Resume does not work properly?" );
    }
    
    public void testSubTransactionRollbackAndRootCommit() throws Exception
    {
    		
    	try {
     	TestSynchronization sync1 = new TestSynchronization();
      	TestSynchronization sync2 = new TestSynchronization();
      	
      	//TransactionManagerImp.setDefaultSerial ( false );
      	tm.begin();
      	Transaction tx0 = tm.getTransaction();
      	tx0.registerSynchronization ( sync1 );
      	
              //create a few nested tx
              tm.begin();
              Transaction tx1 = tm.getTransaction();
             
              tm.begin();
      	Transaction tx2 = tm.getTransaction();
              if ( tx2.equals ( tx1 ) ) 
                  failTest ( "parent equals subtx??" );
      	if ( !tx1.equals ( tx1 ) ) 
      	    failTest ( "Tx does not equal itself?" );
      	
      	//suspend entire subtx hierarchy
      	Transaction suspended = tm.suspend();
      	if ( tm.getTransaction() != null )
      	    failTest ( "thread has tx after suspend" );
      	tm.resume ( suspended );
      	if ( !tm.getTransaction().equals ( suspended ) )
      	  failTest ( "resume does not work" );
      	
      	tx2.registerSynchronization ( sync2 );
      	tx2.enlistResource ( xaRes2_ );
      	tx2.delistResource ( xaRes2_,  XAResource.TMFAIL );
      	tm.rollback();
              //rollback of subtx should not have affected parent
             
              //test enlisting/delisting of resource
              tx1.enlistResource ( xaRes1_ );
              tx1.delistResource ( xaRes1_, XAResource.TMSUCCESS );
        
              tx1.enlistResource ( xaRes1_ );
              tx1.delistResource ( xaRes1_ , XAResource.TMSUCCESS );
        
              tm.commit();
              tx0.commit();
              if ( tm.getTransaction() != null ) 
                  failTest ( "tx context after commit of root" );
              
              //since tx2 is rolled back, its sync should not have been 
              //called
              //CHANGED in 2.0: isCalledAfter is now required
              if ( sync2.isCalledBefore() ) 
                  failTest ( "synchronization called for rollback");
              
              //since tx0 is committed, and so is the coordinator,
              //sync1 should have been called.
              if ( ! sync1.isCalledAfter() || ! sync1.isCalledAfter() )
                  failTest ( "synchronization " + 
                                                   "not called after commit" );
    	}
    	catch ( Exception e ) {
    		e.printStackTrace();
    		failTest ( e.getMessage() );
    	}
    	finally {
    		//TransactionManagerImp.setDefaultSerial ( true );
    	}
    }
    
    
    public void testResourceOrdering() throws Exception
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
   		if ( res != res1 ) failTest ( "Error in resource order");
   		if ( ! res.usesXAResource(xares1) ) 
   			failTest ( "Error in usesXAResource");
   		
   		//second one should be res2
		res = ( XATransactionalResource ) enumm.nextElement();
		if ( res != res2 ) failTest ( "Error in resource order");
		if ( ! res.usesXAResource(xares2) ) 
			failTest ( "Error in usesXAResource" );  		
	   	
	   	//third one should be res3	
		res = ( XATransactionalResource ) enumm.nextElement();
		if ( res != res3 ) failTest ( "Error in resource order");
		if ( ! res.usesXAResource(xares3) ) 
			failTest ( "Error in usesXAResource" ); 
    }
    
    public void testAcceptAllResource()
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
   		
   
   		
   	
		xares = new TestXAResource();
		tm.begin();
		tx = tm.getTransaction();
		tx.enlistResource(xares);
		xid = xares.getLastStarted();
		if ( xid.toString().indexOf ( dummyName ) >= 0 )
			failTest ( "Wrong xid");
	   		
		tx.delistResource ( xares , XAResource.TMSUCCESS );
		
		xares = ( TestXAResource ) xares.clone();
		tx.enlistResource ( xares );
	   		
		if ( !xid.equals ( xares.getLastStarted() ) )
			failTest ( "Different branch for serial enlists");
	   			
	   		
		tm.rollback();   		
   		
   		
   		
    }

    public void testNoAutomaticRegistrationMode() throws Exception
    {
  			uts.shutdown ( true );	
  			if ( Configuration.getResources().hasMoreElements() ) fail ( "Shutdown does not remove all resources");
  			
  			TransactionManager tm = null;
  			UserTransaction utx = null;
  	   		TestXAResource xares = new TestXAResource();
  	   		TSInitInfo info = uts.createTSInitInfo();
  	   		
  	   		
  	   		info.setProperty (  AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME , "false");
  	   		uts.init ( info );
	   		
  	   		utx = uts.getUserTransaction();
  	   		tm = uts.getTransactionManager();
  	   		if ( tm == null ) failTest ( "No TM?");
  	   		utx.begin();
  	   		if ( tm.getTransaction() == null ) failTest ( "No tx for usertx " + utx.getClass().getName()  );
  			try {
  				tm.getTransaction().enlistResource ( xares );
  				failTest ( "Unknown resource enlist works?");
  			}
  			catch ( SystemException ok ) {}   		
  	   		
  	   		utx.rollback();
  	   		
    }
    
    public void testAutomaticRegistrationMode() throws Exception
    {

			uts.shutdown ( true );	
	   	
			TransactionManager tm = null;
			UserTransaction utx = null;
	   		TestXAResource xares = new TestXAResource();
	   		TSInitInfo info = uts.createTSInitInfo();
	   		
	   		Properties p = info.getProperties();
	   		p.setProperty (  AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME , "true");
	   		uts.init ( info );

	   		utx = uts.getUserTransaction();
	   		tm = uts.getTransactionManager();
	   		if ( tm == null ) failTest ( "No TM?");
	   		utx.begin();
	   		if ( tm.getTransaction() == null ) failTest ( "No tx for usertx " + utx.getClass().getName()  );
			try {
				tm.getTransaction().enlistResource ( xares );
			}
			catch ( SystemException error ) {
			    fail ( "Automatic registration does not work" );
			}   		
	   		
	   		utx.rollback();
    }
    
    public void testNoResourcesAfterShutdown()
    throws Exception
    {
        uts.shutdown ( true );
        if ( Configuration.getResources().hasMoreElements() ) fail ( "Shutdown does not remove all resources");
    }
    
    public void testResumeForMarkedAbort() throws Exception 
    {
    	//test for case 26398
    	tm.begin();
    	tm.setRollbackOnly();
    	Transaction tx = tm.suspend();
    	tx.setRollbackOnly();
    	assertEquals ( Status.STATUS_MARKED_ROLLBACK , tx.getStatus() );
    	tm.resume ( tx );
    	assertNotNull ( "resume fails for marked rollback" , tm.getTransaction() );
    }
    
}
