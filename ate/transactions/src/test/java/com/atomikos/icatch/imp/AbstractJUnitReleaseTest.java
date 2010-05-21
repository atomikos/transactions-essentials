package com.atomikos.icatch.imp;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Properties;

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
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TestParticipant;
import com.atomikos.icatch.TestSubTxAwareParticipant;
import com.atomikos.icatch.TestSynchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.VoteNoParticipant;
import com.atomikos.icatch.admin.AdminTransaction;
import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * 
 *
 * 
 */
public abstract class AbstractJUnitReleaseTest extends 
TransactionServiceTestCase
{

  
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
     
   private void testCombination ( 
         int[] combination )
        throws HeurRollbackException, 
        HeurHazardException, HeurMixedException ,
        Exception
    {
        CompositeTransactionManager ctm =
            com.atomikos.icatch.system.Configuration.
            getCompositeTransactionManager();
        
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
                    failTest ( 
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
    *Test the subtx relationship and method thereto.
    *@param child The child.
    *@param parent The parent of the child.
    */
    
  private void testSubTx ( 
  CompositeTransaction child, CompositeTransaction parent )
  throws Exception
  {
        if ( ! parent.isSameTransaction ( parent ) )
            failTest ( "isSameTransaction() does not work?" );
        if ( ! child.isSameTransaction ( child ) )
            failTest ( "isSameTransaction() does not work?" );
        if ( child.isSameTransaction ( parent ) )
            failTest ( "isSameTransaction() does not work?" );

        if ( child.isRoot() )
            failTest ( "isRoot() true for child?" );
        if ( ! ( child.isLocal() && parent.isLocal() ) )
            failTest ( "isLocal() does not work?" );
        
        if ( ! parent.isAncestorOf ( child ) )
            failTest ( "isAncestorOf() does not work?" );
        if ( ! child.isDescendantOf ( parent ) )
            failTest ( "isDescendantOf() does not work?" );
        if ( ! child.isRelatedTransaction ( parent ) )
            failTest ( "isRelatedTransaction() does not work?" );
        if ( child.isSameTransaction ( parent ) )
            failTest ( "isSameTransaction() does not work?" );
            
        Stack lineage = child.getLineage();
        CompositeTransaction tester = 
            ( CompositeTransaction ) lineage.peek();
        if ( ! tester.getTid().equals ( parent.getTid() ) )
            failTest ( "getTid() of lineage not same as parent's?" );
		
		Properties properties = parent.getProperties();
		Enumeration enumm = properties.propertyNames();
		while ( enumm.hasMoreElements() ) {
			String key = ( String ) enumm.nextElement();
			String value = ( String ) properties.getProperty ( key );
			if ( ! value.equals ( child.getProperty ( key ) ) ) fail ( "property not passed onto child transaction: " + key );
		}
        
  }
    
   
    private UserTransactionService us;
    
    private TestLogAdministrator admin;
    
    private boolean propagation;
    
    
    public AbstractJUnitReleaseTest ( String name , 
            boolean propagation )
    {
        super ( name );
        this.propagation = propagation;
    }
    
    /**
     * Callback during setUp.
     * @param admin 
     * @return The configured and initialized service.
     */
    protected abstract UserTransactionService 
    onSetUp ( TestLogAdministrator admin )
    throws Exception;
    
    
    /**
     * Removes all test output files to 
     * guarantee a clean startup for the next test.
     *
     */
    protected abstract void onTearDown()
    throws Exception;
    

    
    protected final void setUp()
    {
    		super.setUp();

        admin = new TestLogAdministrator();
        try {
			this.us = onSetUp ( admin );
		} catch (Exception e) {
			e.printStackTrace();
			failTest ( e.getMessage() );
		}
    }
    
    protected final void tearDown()
    {
        
        if ( us != null ) us.shutdown ( true );
        if ( admin.getLogControl() != null )
            failTest ( 
            "LogAdministrator: deregister of LogControl not done?" );
        us = null;
        try {
			onTearDown();
		} catch (Exception e) {
			e.printStackTrace();
			failTest ( e.getMessage() );
		}
		
		//clear configuration's consoles to avoid excessive output like in case 27230
		Configuration.removeConsoles();
		
		super.tearDown();
    }
    
    public void testHeuristicCommit()
    throws Exception
    {
     
        
        //test all combinations in the range 0-3
        //but NOT including 4 (heuristic commit)
        //since this exception does not occur
        //on global commit
        
        //System.err.println ( "Testing heuristics, this will take some time!" );
        for ( int i1 = 0 ; i1 <= 3 ; i1++ ) {
           for ( int i2 = 0 ; i2 <= 3 ; i2++ ) {
             
             for ( int i3 = 0 ; i3 <= 3 ; i3++ ) {
               for ( int i4 = 0 ; i4 <= 3 ; i4++ ) {
                   //System.err.print ( "." );
                   try {
                     int[] combination = { i1 , i2 , i3 , i4 };
                     testCombination ( combination );
                   }
                   catch ( Exception e ) {
                   	e.printStackTrace();
                       failTest ( 
                           "Heuristic state error: " +
                           e.getMessage() + e.getClass().getName() +
                           "for HeuristicParticipant flag combination: " +
                           i1 + "," + i2 + "," + i3 + "," + i4  );
                   }
                 
               }
             }
           }
        }
        //System.err.println ( "" );
        //System.err.println ( "Testing heuristics: done!" );
    }
    
    
    public void testHeuristicPrepare() throws Exception
    {
        CompositeTransactionManager ctm = 
            Configuration.getCompositeTransactionManager();
         CompositeTransaction ct = null;
         HeuristicParticipant hPart = null ;
         VoteNoParticipant vnPart  = null;
         CompositeTerminator term = null;
         StringHeuristicMessage msg =
            new StringHeuristicMessage ( "Test" );
        
         //System.err.println ( "Testing heuristic prepares" );
         
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
            failTest ( "Commit should throw hazard?" );
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
            failTest ( "Commit should throw mixed?" );
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
            failTest ( "Commit should throw mixed?" );
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
            failTest ( 
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
        
    }
    
    public void testAdministration()
    throws Exception
    {
        CompositeTransactionManager ctm = 
             Configuration.getCompositeTransactionManager();
        ExportingTransactionManager etm = 
            Configuration.getExportingTransactionManager();
        ImportingTransactionManager itm = 
             Configuration.getImportingTransactionManager();
        
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
        
        //System.err.println ( "About to simulate heuristic hazard - this might take a while..." );
        try {
            ct.commit();
            failTest ( "Heuristic participant not recognized?" );
        }
        catch ( HeurHazardException hh ) {
            //should happen
        }
        //System.err.println ( "Heuristic hazard done!" );
        
        //assert that no tx association exists for the thread
        if ( ctm.getCompositeTransaction() != null )
            failTest ( "Tx exists for thread AFTER termination?" );
        
        //test if the corresponding AdminTransaction exists
        String[] tids = new String[1];
        tids[0] = tid;
        AdminTransaction[] txs = admin.getLogControl().getAdminTransactions ( tids );
        
        //assert that returned list has exactly ONE value in it
        if ( txs == null || txs.length != 1 )
            failTest ( "LogControl does not work?" );
        
        //asssert that the one value returns is the tx with our id
        if ( ! txs[0].getTid().equals ( tid ) )
            failTest ( "LogControl returns wrong AdminTransaction?" );
        
        //assert that the transaction was intended to commit
        if ( ! txs[0].wasCommitted() )
            failTest ( "wasCommitted() does not work for commit?" );
        
        //assert that the state of the returned tx is correct
        if (  txs[0].getState() != AdminTransaction.STATE_HEUR_HAZARD )
            failTest ( "Wrong state for admintransaction?" );
        
        HeuristicMessage[] tags = txs[0].getTags();
        
        //assert that tags has one element
        if ( tags == null || tags.length != 1 )
            failTest ( "getTags returns too many tags?" );
          
        //assert that the original tag is returned
        if ( ! tags[0].toString().equals ( tag.toString() ) )
            failTest ( "getTags returns wrong tag?" );
        
        HeuristicMessage[] msgs = txs[0].getHeuristicMessages();
        
        //assert that two msgs are there
        if ( msgs == null || msgs.length < 2 )
            failTest ( "getHeuristicMessages() does not return all?" );
        
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
                  
              failTest ( 
              "getHeuristicMessages returns different msgs?" );
              
        msgs = txs[0].getHeuristicMessages ( AdminTransaction.STATE_HEUR_HAZARD );
          
          //assert that two msgs are there
        if ( msgs == null || msgs.length != 2 )
            failTest ( "getHeuristicMessages(state) does not return all?" );
        
        //assert that both messages are returned as original
        if (  ! (  ( msgs[0].toString().equals ( msg.toString() ) || 
                     msgs[1].toString().equals ( msg.toString() ) 
                     ) 
                     &&
                     ( msgs[0].toString().equals ( msg2.toString() ) || 
                     msgs[1].toString().equals ( msg2.toString() ) 
                     )
                  )  )
                  
              failTest ( 
              "getHeuristicMessages(state) returns different msgs?" );
        
        
        txs[0].forceForget();
        
        //assert that the transaction is really forgotten by the log
        txs = admin.getLogControl().getAdminTransactions ( tids );
        if ( ! ( txs == null || txs.length == 0 ) ) {
                failTest ( "forceForget() does not work?" );
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
                failTest ( "No participants in extent?" );
            
            Participant part = ( Participant ) parts.peek();
            part.setCascadeList ( new Hashtable() );
            part.setGlobalSiblingCount ( 1 );
            int vote = part.prepare();
            if ( vote == Participant.READ_ONLY ) 
                failTest ( "Prepare should not return readonly?" );
            
            //the local transaction should now be prepared => test forceCommit
            //test if the corresponding AdminTransaction exists
            tids = new String[1];
            tids[0] = tid;
            txs = admin.getLogControl().getAdminTransactions ( tids );
            
            //assert that returned list has exactly ONE value in it
            if ( txs == null || txs.length != 1 )
                failTest ( "LogControl does not return indoubt?" );
            
            //assert that state is really indoubt
            if (  txs[0].getState() != AdminTransaction.STATE_PREPARED )
                failTest ( "AdminTransaction not PREPARED after prepare?" );
            
            //try if forceCommit works
            txs[0].forceCommit();
            
            //assert that the state is now heuristic commit
            if ( txs[0].getState() != AdminTransaction.STATE_HEUR_COMMITTED )
                failTest ( 
                "getState() not HEUR_COMMITTED after forceCommit but rather " + txs[0].getState() );
            
            //assert that wasCommitted works
            if ( ! txs[0].wasCommitted() )
                failTest ( "wasCommitted false after forceCommit?" );
            
                
            try {
                ct.rollback();
            }
            catch ( Exception e ) {
                //should happen: tx already terminated 
            }
            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                failTest ( "Tx exists for thread AFTER termination?" );
            
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
                failTest ( "No participants in extent?" );
            
            part = ( Participant ) parts.peek();
            part.setCascadeList ( new Hashtable() );
            part.setGlobalSiblingCount ( 1 );
            vote = part.prepare();
            if ( vote == Participant.READ_ONLY ) 
                failTest ( "Prepare should not return readonly?" );
            
            //the local transaction should now be prepared => test forceCommit
            //test if the corresponding AdminTransaction exists
            tids = new String[1];
            tids[0] = tid;
            txs = admin.getLogControl().getAdminTransactions ( tids );
            
            //assert that returned list has exactly ONE value in it
            if ( txs == null || txs.length != 1 )
                failTest ( "LogControl does not return indoubt?" );
            
            //assert that state is really indoubt
            if (  txs[0].getState() != AdminTransaction.STATE_PREPARED )
                failTest ( "AdminTransaction not PREPARED after prepare?" );
            
            //try if forceRollback works
            txs[0].forceRollback();
            
            //assert that the state is now heuristic commit
            if ( txs[0].getState() != AdminTransaction.STATE_HEUR_ABORTED )
                failTest ( 
                "getState() not HEUR_ABORTED after forceRollback?" );
            
            //assert that wasCommitted works
            if ( txs[0].wasCommitted() )
                failTest ( "wasCommitted true after forceRollback?" );
            
                
            try {
                ct.rollback();
            }
            catch ( Exception e ) {
                //should happen: tx already terminated 
            }
            
            //assert that no tx association exists for the thread
            if ( ctm.getCompositeTransaction() != null )
                failTest ( "Tx exists for thread AFTER termination?" );
        }
        
    }
    
    public void testNative()
    throws Exception
    {
        //
        //FIRST, TEST THE TRANSACTION MANAGER
        //AND SOME GENERAL FUNCTIONS
        //
        
        CompositeTransactionManager ctm =
            Configuration.getCompositeTransactionManager();
        if ( ctm == null )
            failTest ( "No composite transaction manager?" );
        
        if ( ctm.getCompositeTransaction() != null )
            failTest ( 
            "Comp. tx. mgr.: thread has tx before any was created?" );
        
        CompositeTransaction parent = 
            ctm.createCompositeTransaction ( 1000 );
        if ( ctm.getCompositeTransaction() != parent )
            failTest ( "getCompositeTransaction() does not work?" );
        
        //create a subtx of the parent
        CompositeTransaction child = 
                            ctm.createCompositeTransaction ( 1000 );
        if ( ctm.getCompositeTransaction() != child )
            failTest ( "getCompositeTransaction() does not work?" );
        
        if ( ! parent.isRoot() )
            failTest ( "isRoot() false for root?" );
        
        testSubTx ( child, parent );    
        
        CompositeTransaction suspended = ctm.suspend();
        if ( ctm.getCompositeTransaction() != null )
            failTest ( "suspend() does not work?" );
        
        ctm.resume ( suspended );
        if ( ctm.getCompositeTransaction() != child ) 
            failTest ( "resume() does not work?" );
        
        child.rollback();
        parent.rollback();
        
        //assert that no tx association exists for the thread
        if ( ctm.getCompositeTransaction() != null )
            failTest ( "Tx exists for thread AFTER termination?" );
        
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
            failTest ( 
            "beforeCompletion not called upon subtx commit?" );
        if ( sync2.isCalledAfter() )
            failTest ( 
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
            failTest ( "Tx exists for thread AFTER termination?" );
        
        //a sync should have been notified
        if ( ! sync.isCalledBefore() || ! sync.isCalledAfter() )
            failTest ( "Synchronization not called on commit?" );
        
        //a subtx aware participant should have been notified
        if ( ! subPart.isNotified() )
            failTest ( "SubTxAware not called for commit?" );
        
        //a normal participant should have seen a commit
        if ( !tPart.isTerminated() )
            failTest ( "2PC commit does not work?" );
            
        //a readonly participant should NOT have seen commit
        if ( rPart.isTerminated() )
            failTest ( "2PC for readonly: commit called?" );
        
        //a new participant should not be able to register after commit
        try {
            TestParticipant tPart2 = new TestParticipant();
            ct.addParticipant ( tPart2 );
            failTest ( "Participant added after commit?" );
        }
        catch ( IllegalStateException e ) {
            //should happen: added participant after commit!
        }
        
        //adding another subtx aware should failTestafter commit
        try {
            ct.addSubTxAwareParticipant ( subPart );
            failTest ( "SubTxAware added after commit?"  );
        }
        catch ( IllegalStateException e )  {
            //should happen
        }
        
        //adding another sync should failTestafter commit
        try {
            ct.registerSynchronization ( sync );
            failTest ( "Synchronization added after commit?"  );
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
            failTest ( "Tx exists for thread AFTER termination?" );
        
        if ( !sync.isCalledAfter() )
            failTest ( "Synchronization afterCompletion not called for rollback" );
        //a subtx aware participant should have been notified
        if ( ! subPart.isNotified() )
            failTest ( "SubTxAware not called for rollback?" );
        
        //a normal participant should have seen a rollback
        if ( !tPart.isTerminated() )
            failTest ( "Rollback does not work?" );
            
        
        //a new participant should not be able to register after rollback
        try {
            TestParticipant tPart2 = new TestParticipant();
            ct.addParticipant ( tPart2 );
            if ( !tPart2.isTerminated() )
           		 failTest ( "Participant not rolledback?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen: no longer active 
        }
        
        
        //adding another subtx aware should failTestafter rollback
        try {
        	subPart = new TestSubTxAwareParticipant();
            ct.addSubTxAwareParticipant ( subPart );
            if ( ! subPart.isNotified() )
            	failTest ( "SubTxAware added after rollback and not notified?"  );
        }
        catch ( IllegalStateException e )  {
            //should happen
        }
        
        //adding another sync should failTestafter rollback
        try {
            ct.registerSynchronization ( sync );
            failTest ( "Synchronization added after rollback?"  );
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
            failTest ( 
            "getRecoveryCoordinator() does not work?" );
        
        if ( ! ct.getTid().equals ( coord.getCoordinatorId() ) )
            failTest ( "getRootTid() does not work?" );
        
        ct.commit();
        
        //assert that no tx association exists for the thread
        if ( ctm.getCompositeTransaction() != null )
            failTest ( "Tx exists for thread AFTER termination?" );
        
         if ( ! msg.toString().equals ( coord.getTags()[0].toString() ) )
            failTest ( "setTag()/getTags() does not work?" );
        
        //
        //CASE 4: TEST REPLAY COMPLETION
        //
        
        ct = ctm.createCompositeTransaction ( 100 );
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
            failTest ( "Replay works before termination?" );
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
            failTest ( "Tx exists for thread AFTER termination?" );
        
        //now, set hPart to NOT failTestnext time, and test replay
        hPart.setFailMode ( HeuristicParticipant.NO_FAIL );
        Boolean commit = rec_coord.replayCompletion ( hPart );
        if ( commit == null || ! commit.booleanValue() )
            failTest ( "replayCompletion() return is wrong?" );
            
        //sleep a while to make replay possible
        try {
            System.err.println ( 
                "Test replay: about to sleep..." );
            Thread.currentThread().sleep ( 300 );
            System.err.println ( "Woken up again!" );
        }
        catch ( InterruptedException inter ) {}
        
        //assert that tx state is now terminated
        if ( ! ct.getState().
               equals ( TxState.TERMINATED ) )
            failTest ( "State not TERMINATED after replay?" );
            
        //
        //CASE 5: TEST THE TRANSACTIONCONTROL HANDLE
        //
        
        parent = ctm.createCompositeTransaction ( 1000 );
        long timeout = parent.getTransactionControl().getTimeout();
        
        if ( timeout > 1000 )
            failTest ( "getTimeout() does not work: " + timeout );
        
        parent.getTransactionControl().setSerial();
        
        tPart = new TestParticipant();
      
        child = parent.getTransactionControl().createSubTransaction();
        child.addParticipant ( tPart );
        testSubTx ( child , parent );
        
        //assert that child sees parent's serial tag
        if ( ! child.isSerial() )
            failTest ( "setSerial()/getSerial() does not work?" );
            
        //assert that non-root tx can not call setSerial
        try {
            child.getTransactionControl().setSerial();
            failTest ( "setSerial() works for non-root?" );
        }
        catch ( IllegalStateException ill ) {
            //should happen 
        }
          
        if ( parent.getTransactionControl().getLocalSubTxCount() != 1 )
            failTest ( "getLocalSubTxCount() fails?" );
            
        child.commit();
        
        if ( parent.getTransactionControl().getLocalSubTxCount() != 0 )
            failTest ( "getLocalSubTxCount() fails?" );
        
        //tPart should NOT yet be notified
        if ( tPart.isTerminated() )
            failTest ( 
            "Participant terminated on SUBTX commit?" );
        
        //assert that creating a subtx of child fails: already terminated
        try {
            child.getTransactionControl().createSubTransaction();
            failTest ( 
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
            failTest ( 
            "Participant not terminated on SUBTX rollback?" );
        
        parent.rollback();
        
        //assert that no tx association exists for the thread
        if ( ctm.getCompositeTransaction() != null )
            failTest ( "Tx exists for thread AFTER termination?" );
            
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
			failTest ( "Participants in Extent are not rolled back?");         
        
    }
    
    public void testPropagation() 
    throws Exception
    {
        if ( ! propagation ) return;
        
        CompositeTransactionManager ctm =
            Configuration.getCompositeTransactionManager();
        ImportingTransactionManager itm =
            Configuration.getImportingTransactionManager();
        ExportingTransactionManager etm =
            Configuration.getExportingTransactionManager();
        
        
        //
        //CASE 1: test if propagation data is correct for non-SERIAL root
        //
        
        CompositeTransaction ct =
            ctm.createCompositeTransaction ( 10000 );
        Propagation propagation = etm.getPropagation();
        
        //assert that serial is false
        if ( propagation.isSerial() )
            failTest ( "Propagation is serial if tx is not?" );
        //assert timeout is correct; this will have decreased
        if ( propagation.getTimeOut() > 10000 )
            failTest ( "Propagation timeout is wrong?" );
        Stack lineage = propagation.getLineage();
        
        //assert lineage is not empty
        if ( lineage == null || lineage.empty() )
            failTest ( "No lineage in propagation?" );
            
        //the only tx in the propagation is the root
        CompositeTransaction root = 
            ( CompositeTransaction ) lineage.peek();  
        if ( !root.isRoot() )
            failTest ( "Propagation has no root?" );
        
        ct.rollback();
        
        //
        //CASE 2: test if propagation data is correct, for SERIAL root
        //
        
        ct = ctm.createCompositeTransaction ( 10000 );
        ct.getTransactionControl().setSerial();
        propagation = etm.getPropagation();
        
        //assert serial is true
        if ( ! propagation.isSerial() )
            failTest ( "Propagation not serial if tx is?" );
        
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
            failTest ( "Imported child is serial whereas parent is not?" );
            
        //assert that child behaves like a reasonable subtx
        testSubTx ( child , ct );
        
        Extent extent = itm.terminated ( true );
        
        etm.addExtent ( extent );
        ct.commit();
        
        //assert that 2PC was OK
        if ( ! tPart.isTerminated() )
            failTest ( "Commit propagation does not work?" );
        
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
            failTest ( "Imported child is serial whereas parent is not?" );
            
        //assert that child behaves like a reasonable subtx
        testSubTx ( child , ct );
        
        extent = itm.terminated ( true );
        
        etm.addExtent ( extent );
        ct.commit();
        
        //assert that 2PC was OK
        if ( ! tPart.isTerminated() )
            failTest ( "Commit propagation does not work?" );
        
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
            failTest ( "Imported child is serial whereas parent is not?" );
            
        //assert that child behaves like a reasonable subtx
        testSubTx ( child , ct );
        
        //rollback  child, to test if later termination fails as expected
        Configuration.logDebug ( "ROLLBACK OF CHILD..." );
        child.rollback();
        if ( ! child.equals ( ctm.getCompositeTransaction() ) ) {
            //ONLY DO THIS IF THREAD IS NOT THAT OF PARENT 
            //DUE TO ROLLBACK!
            try {
            	   Configuration.logDebug ( "TERMINATION OF CHILD..." );
                extent = itm.terminated ( true );
                failTest ( "terminated ( true ) works for rolledback import?" );
            }
            catch (RollbackException rb ) {
                //should happen
            }
        }
        try {
        	Configuration.logDebug ( "COMMIT OF PARENT..." );
        	ct.commit();
        }
        catch ( Exception e ) {
        		//ok: coordinator of child is also that of parent -> rollback will have been done!!!
        }
        
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
            failTest ( "Extent propagation of 2PC does not work?" );
        
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
            failTest ( "Orphan detection does not work?" );
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
        p = new PropagationImp ( stack , true , 1000  );
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
        //CASE 11: test if Participant gets rollback on terminated(false)
        //
        TestParticipant tp = new TestParticipant();
        
        
        stubtx = new CompositeTransactionStub ( "StubTransaction3" );
        
        stack = new Stack();
        stack.push ( stubtx );
        p = new PropagationImp ( stack , true , 1000  );
        itm.importTransaction ( p , false , true );
        
        ct = ctm.getCompositeTransaction();
        ct.addParticipant ( tp );
        extent = itm.terminated ( false );
        if ( ! tp.isTerminated()  )
            failTest ( "Participant not rolled back on terminated ( false )" );
        
    }
    
    public void testReadOnlyCommit()
    throws Exception
    {
        CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
        CompositeTransaction ct = null;
        ReadOnlyParticipant p1 = null , p2 = null;

        if ( ctm.getCompositeTransaction() != null ) {
            failTest ( "Invalid precondition for test: existing tx for thread" );
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
        if ( p1.isTerminated() ) failTest ( "Commit called on readonly participant?" );
        if ( p2.isTerminated() ) failTest ( "Commit called on readonly participant?" );
    }
    
    public void testDistributedRecovery()
    throws Exception
    {
        if ( ! propagation ) return;
        
        
        try {
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


			ctm = Configuration.getCompositeTransactionManager();
			itm = Configuration.getImportingTransactionManager();
			etm = Configuration.getExportingTransactionManager();
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
			us.shutdown ( true );
			
			us = onSetUp ( admin );
			
			ctm = us.getCompositeTransactionManager();
			itm = Configuration.getImportingTransactionManager();
			etm = Configuration.getExportingTransactionManager();
			ct = null;
			
			//assert that the prepared instance is still there
			recoveredtxs = admin.getLogControl().getAdminTransactions();
			admintx = null;
			for ( int i = 0 ; i < recoveredtxs.length ; i++ ) {
			    if ( recoveredtxs[i].getTid().equals ( tid ) ) admintx = recoveredtxs[i];
			}
			if ( admintx == null )
			    failTest ( "Prepared coordinator not recovered?" );
			if ( admintx.getState() != AdminTransaction.STATE_PREPARED )
			    failTest ( "Recovered coordinator not prepared?" );
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
			us.shutdown ( true );
			
      
			
			us = onSetUp ( admin );
			
			//assert that the prepared instance is still there
			recoveredtxs = admin.getLogControl().getAdminTransactions();
			admintx = null;
			for ( int i = 0 ; i < recoveredtxs.length ; i++ ) {
			    if ( recoveredtxs[i].getTid().equals ( tid ) ) admintx = recoveredtxs[i];
			}
			if ( admintx != null )
			    failTest ( "Readonly prepared coordinator was recovered?" );
			
			us.shutdown ( true );
		
		} catch ( Exception e ) {
			e.printStackTrace();
			failTest ( e.getMessage() );
		}
    }
    
}
