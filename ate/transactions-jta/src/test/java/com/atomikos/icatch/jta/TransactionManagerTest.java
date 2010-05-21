package com.atomikos.icatch.jta;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.xa.RMSimulator;
import com.atomikos.datasource.xa.TestXATransactionalResource;
import com.atomikos.datasource.xa.XATransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.TestParticipant;
import com.atomikos.icatch.system.Configuration;

/**
 *
 *
 *Provides a test for the JTA transaction manager.
 */
 
public class TransactionManagerTest
{
      //static TrmiTransactionManager ctmgr_ , ctmgr2_ ;
  
//      /**
//       *Setup TM instance
//       */
//       
//      static void setup() throws Exception
//      {
//              System.setProperty ( "java.naming.factory.initial" , 
//                        "com.sun.jndi.rmi.registry.RegistryContextFactory" );
//              
//      	LocateRegistry.createRegistry ( 1099 );
//      	Console console = new PrintStreamConsole ( System.out );
//      	LogStream logstream = 
//      	    new FileLogStream ( "./" , "TransactionManagerTest" , console );
//          	StreamObjectLog log = 
//          	  new StreamObjectLog (logstream , 100 , console );
//          	//log.setOutput ( System.out );
//          	StateRecoveryManager recmgr =
//          	  new StateRecoveryManagerImp ( log );
//          	
//          	ctmgr_ =
//          	  new TrmiTransactionManager ( false,recmgr,
//          	                                                "TestTransactionManager",
//          	                                                console,
//          	                                                "./" , 60000, 1099 );
//          	System.out.println ( "init of first TM" );
//          	ctmgr_.init();
//              System.out.println ( "returned from init of first TM" );
//              TransactionManagerImp.installTransactionManager ( ctmgr_ );
//              
//              //create a second tm for testing import and export
//              //NOTE: doing this inside ONE tm would be trouble since  importing 
//              //thread would already have tx context on import
//              //because the importing thread = exporting thread!
//              
//              LogStream logstream2 = 
//                  new FileLogStream ( "./" , "SecondTransactionManagerTest" , console );
//              ObjectLog log2 = 
//                new StreamObjectLog ( logstream2 , 100 , console );
//              StateRecoveryManager recmgr2 =
//          	  new StateRecoveryManagerImp ( log2 );
//          	Console console2 = new PrintStreamConsole ( System.out );
//          	ctmgr2_ =
//          	  new TrmiTransactionManager ( false,recmgr2,
//          	                                                "TestTransactionManager2",
//          	                                                console2,
//          	                                                "./", 60000 , 1099 );
//          	System.out.println ( "init of second TM" );
//          	ctmgr2_.init();
//          	System.out.println ( "returned from init of second TM " );
//      }
//      
//      static void shutdown() throws Exception {
//            	if ( ctmgr_ != null )
//            	    ctmgr_.shutdown( true );
//            	if ( ctmgr2_ != null )
//            	    ctmgr2_.shutdown ( true );
//      }
//      
      /**
       *Basic tests: get tx., try commit, abort etc.
       */
       
      public static void testBasic ( XAResource xares ) throws Exception
      {
        if ( xares == null ) {
		xares = new RMSimulator();
              XATransactionalResource res = new TestXATransactionalResource ( xares, "TestResource" );
              //XAResourceConfiguration.addResource ( res );
              Configuration.addResource ( res );
	}
      	TestSynchronization sync1 = new TestSynchronization();
      	TestSynchronization sync2 = new TestSynchronization();
      	
      	TransactionManager tm = TransactionManagerImp.getTransactionManager();
      	tm.begin();
      	Transaction tx0 = tm.getTransaction();
      	tx0.registerSynchronization ( sync1 );
      	
              //create a few nested tx
              tm.begin();
              Transaction tx1 = tm.getTransaction();
             
              tm.begin();
      	Transaction tx2 = tm.getTransaction();
              if ( tx2.equals ( tx1 ) ) 
                  throw new Exception ( "parent equals subtx??" );
      	if ( !tx1.equals ( tx1 ) ) 
      	    throw new Exception ( "Tx does not equal itself?" );
      	
      	//suspend entire subtx hierarchy
      	Transaction suspended = tm.suspend();
      	if ( tm.getTransaction() != null )
      	    throw new Exception ( "thread has tx after suspend" );
      	tm.resume ( suspended );
      	if ( !tm.getTransaction().equals ( suspended ) )
      	  throw new Exception ( "resume does not work" );
      	
      	tx2.registerSynchronization ( sync2 );
      	tx2.enlistResource ( xares );
      	tx2.delistResource ( xares,  XAResource.TMFAIL );
      	tm.rollback();
              //rollback of subtx should not have affected parent
             
              //test enlisting/delisting of resource
              tx1.enlistResource ( xares );
              tx1.delistResource ( xares, XAResource.TMSUCCESS );
        
              tx1.enlistResource ( xares );
              tx1.delistResource ( xares, XAResource.TMSUCCESS );
        
              tm.commit();
              tx0.commit();
              if ( tm.getTransaction() != null ) 
                  throw new Exception ( "tx context after commit of root" );
              
              //since tx2 is rolled back, its sync should not have been 
              //called
              //CHANGED in 2.0: isCalledAfter is now required
              if ( sync2.isCalledBefore() ) 
                  throw new Exception ( "synchronization called for rollback");
              
              //since tx0 is committed, and so is the coordinator,
              //sync1 should have been called.
              if ( ! sync1.isCalledAfter() || ! sync1.isCalledAfter() )
                  throw new Exception ( "synchronization " + 
                                                   "not called after commit" );
      }
      
      public static void testLoad ( CompositeTransactionManager ctm ) throws Exception
      {
          //start many txs to see if memory GC problems 	
          TransactionManager tm = TransactionManagerImp.getTransactionManager();
          for ( int count = 0 ; count < 10000 ; count++ ) {
              tm.begin();
              Transaction tx = tm.getTransaction();
              //next, add 2 test participants to test overhead of 2pc and logging
              CompositeTransaction ct = ctm.getCompositeTransaction();
              ct.addParticipant ( new TestParticipant() );
              ct.addParticipant ( new TestParticipant() );
              if ( count % 2 == 0 )
                  tx.rollback();
              else 
                  tx.commit();
              if ( count % 100 == 0 )
                  System.out.print ( "." );
          }
          System.out.println();
      }
      
      
  
//      static void test() throws Exception
//      {
//          	try {
//			
//                  System.out.println ( "calling testBasic" );
//          	    testBasic();
//                  System.out.println ( "calling testPropagation" );
//          	    testPropagation();
//                  System.out.println ( "calling testLoad" );
//          	    testLoad ( ctmgr_ );
//          	}
//          	catch ( Exception e ) {
//          	    e.printStackTrace();
//          	}
//          	finally {
//          	    shutdown();	
//          	}
//      } 
      
       
}
 
