
package com.atomikos.datasource.xa;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.imp.TransactionServiceTestCase;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.util.UniqueIdMgr;

/**
 * 
 * 
 * 
 *
 * 
 */

public class XATestJUnit 
extends TransactionServiceTestCase
{
    private static final String ID_MGR_NAME = "XATester";
    
    private UniqueIdMgr tidmgr;
    
    public XATestJUnit ( String name )
    {
        super ( name );
    }
    
    protected void setUp()
    {
        super.setUp();
        
        tidmgr = new UniqueIdMgr ( ID_MGR_NAME , getTemporaryOutputDir() + "/" );
    }
    
    
    public void test2PC()
    throws Exception
    {
      
        RMSimulator xares1 = new RMSimulator();
        TestXATransactionalResource res1 = 
        new TestXATransactionalResource ( xares1 , "TestXAResource1");
        String root1 = tidmgr.get();
        String tid1 = tidmgr.get();
      
        CompositeCoordinator coord1 = 
          new TestCompositeCoordinator ( root1 );
        CompositeTransaction ct1 = 
          new CompositeTransactionStub ( tid1 , true  , coord1 );     
        XAResourceTransaction restx1 = 
          ( XAResourceTransaction ) res1.getResourceTransaction ( ct1 );
        restx1.setXAResource ( xares1 );
        restx1.resume();
        try {
            restx1.prepare();	
            //following disabled: auto-delist makes this work in 2.0!!!
            //System.out.println ( "ERROR: restx prepare should fail if active");
        }
        catch ( Exception e ) {
        	    //OK
        }
        
        String tid2 = tidmgr.get();
        CompositeTransaction ct2 = 
          new CompositeTransactionStub ( tid2 , false  , coord1 );     
        XAResourceTransaction restx2 =
           ( XAResourceTransaction ) res1.getResourceTransaction ( ct2 );
        
         restx2.setXAResource ( xares1 );
         restx2.resume();
         restx2.suspend();
         restx2.prepare();
         restx2.commit ( false );
         
        
        String tid3 = tidmgr.get();
        CompositeTransaction ct3 = 
          new CompositeTransactionStub ( tid3 , true  , coord1 );     
        XAResourceTransaction restx3 =
            ( XAResourceTransaction ) res1.getResourceTransaction ( ct3 );
         restx3.setXAResource ( xares1 );
         restx3.resume();
         restx3.suspend();
         restx3.prepare();
         restx3.commit ( false );
         
         String tid4 = tidmgr.get();
         CompositeTransaction ct4 = 
            new CompositeTransactionStub ( tid4 , false  , coord1 );     
         XAResourceTransaction restx4 =
            ( XAResourceTransaction ) res1.getResourceTransaction ( ct4 );
         restx4.setXAResource ( xares1 );
         restx4.resume();
         restx4.suspend();
         restx4.rollback();
         
         String tid5 = tidmgr.get();
         CompositeTransaction ct5 = 
            new CompositeTransactionStub ( tid5 , false  , coord1 );     
         XAResourceTransaction restx5 =
            ( XAResourceTransaction ) res1.getResourceTransaction ( ct5 );
         
         restx5.setXAResource ( xares1 ); 
         restx5.resume();
         restx5.suspend();
         restx5.commit ( true );
       
    }
    
    public void testAcceptAllXAResource()
    throws Exception
    {
  		String name = "AcceptAllResource";
  		XATransactionalResource res = 
  			new AcceptAllXATransactionalResource( name );
  		
  		if ( !res.getName().equals ( name ) )
  			throw new Exception ( "getName() fails");
  		
  		if ( !res.acceptsAllXAResources() ) 
  			throw new Exception ( "acceptsAllXAResources() fails");
  		
  		if ( res.isClosed() )
  			throw new Exception ( "isClosed() fails");
  		
  		if ( ! res.isSameRM ( res ) )
  			throw new Exception ( "isSameRM() fails");
  		
  		XAResource xares = new TestXAResource();
  		if ( ! res.usesXAResource ( xares ) )
  			throw new Exception ( "usesXAResource() fails");
  			
  		
  		res.recover();
  		res.endRecovery();
  		
  		res.recover();
  		res.endRecovery();
  		
  		res.recover();
  		res.endRecovery();
  		
  		CompositeCoordinator c = new TestCompositeCoordinator ("root1");
  		CompositeTransactionStub ct = new CompositeTransactionStub ( "tid1" , true, c );
  		
  		ResourceTransaction restx1 = res.getResourceTransaction(ct);
  		ResourceTransaction restx2 = res.getResourceTransaction(ct);
  		
  		//this resource should return a different restx each time
  		//since every request can be for a different underlying RM
  		//meaning the RM may not know the previous branch
  		if ( restx1 ==  restx2 || restx1.equals ( restx2 ) )
  			throw new Exception ( "getResourceTransaction() fails" );
  		
  		
  		res.close();
  		if ( !res.isClosed() )
  			throw new Exception ( "close() fails");
    }
    
    public void testHeuristicCommit()
    throws Exception
    {
        RMSimulator xares1 = new RMSimulator();
        TestXATransactionalResource res1 = 
          new TestXATransactionalResource ( xares1 , "TestXAResource1");
      
        String root1 = tidmgr.get();
        String tid1 = tidmgr.get();
      
        CompositeCoordinator coord1 = 
          new TestCompositeCoordinator ( root1 );
        CompositeTransaction ct1 = 
          new CompositeTransactionStub ( tid1 , true  , coord1 );     
        XAResourceTransaction restx1 = 
          ( XAResourceTransaction ) res1.getResourceTransaction ( ct1 );
        Xid xid1 = new XID ( tid1 , res1.getName()  );
        Xid xid2 = new XID ( xid1 );
        if ( ! xid1.equals ( xid2 ) )
            throw new Exception ( " XID.equals() does not work ");
        if (  xid1.hashCode() != xid2.hashCode() )
            throw new Exception ( "XID hashCode() does not work");
        HeuristicMessage msg1 = new StringHeuristicMessage ("Hello");
        restx1.setXAResource ( xares1 );
        restx1.resume();
        restx1.suspend();
        restx1.addHeuristicMessage ( msg1 );
        restx1.prepare();
        //make sure that heuristic exception happens on rollback
        xares1.setHeuristicallyCommitted( restx1.getXid() ) ;
        try {
        	  restx1.rollback();
        	  throw new Exception ( "Heuristic abort is not dealt with?" );
        }
        catch ( HeurCommitException hc ) {
        	   HeuristicMessage[] msgs = hc.getHeuristicMessages();
        	   if ( msgs.length == 0 )
        	      throw new Exception ( "Heur. msg not returned right");
            	
        }
    }
    	
    
    public  void testHeuristicRollback ()
    throws Exception
    {
        RMSimulator xares1 = new RMSimulator();
        TestXATransactionalResource res1 = 
          new TestXATransactionalResource ( xares1 , "TestXAResource1");
      
        String root1 = tidmgr.get();
        String tid1 = tidmgr.get();
      
        CompositeCoordinator coord1 = 
          new TestCompositeCoordinator ( root1 );
        CompositeTransaction ct1 = 
          new CompositeTransactionStub ( tid1 , true  , coord1 );     
        XAResourceTransaction restx1 = 
          ( XAResourceTransaction ) res1.getResourceTransaction ( ct1 );
        Xid xid1 = new XID ( tid1 , res1.getName()  );
        Xid xid2 = new XID ( xid1 );
        if ( ! xid1.equals ( xid2 ) )
            throw new Exception ( " XID.equals() does not work ");
        if (  xid1.hashCode() != xid2.hashCode() )
            throw new Exception ( "XID hashCode() does not work");
        HeuristicMessage msg1 = new StringHeuristicMessage ("Hello");
        restx1.setXAResource ( xares1 );
        restx1.resume();
        restx1.suspend();
        restx1.addHeuristicMessage ( msg1 );
        restx1.prepare();
        //make sure that heuristic exception happens on commit
        xares1.setRolledBack( restx1.getXid() ); 
            //throw new Exception ( "RMSimulator.setRolledBack failure???"+
//                                              "for xid "+ xid1 );
        try {
        	  restx1.commit ( false );
        	  throw new Exception ( "Heuristic abort is not dealt with?" );
        }
        catch ( HeurRollbackException hr ) {
        	   HeuristicMessage[] msgs = hr.getHeuristicMessages();
        	   if ( msgs.length == 0 )
        	      throw new Exception ( "Heur. msg not returned right");
            	
        }
    }
    
    public void testMapping()
    throws Exception
    {
        RMSimulator xares1 = new RMSimulator();
        
        TestXATransactionalResource res1 = 
          new TestXATransactionalResource ( xares1 , "TestXAResource1");
        if ( !res1.getName().equals ( "TestXAResource1" ) )
            throw new Exception ( "TestXATransactionalResource.getName is wrong");
        if ( !res1.isSameRM ( res1 ) )
            throw new Exception ( "TestXATransactionalResource.isSameRM is wrong");
          
        String root1 = tidmgr.get();
        String tid1 = tidmgr.get();
        
        CompositeCoordinator coord1 = 
            new TestCompositeCoordinator ( root1 );
        CompositeTransaction ct1 = 
            new CompositeTransactionStub ( tid1 , true  , coord1 );
            
        XAResourceTransaction restx1 = 
             ( XAResourceTransaction ) res1.getResourceTransaction ( ct1 );
         
         if ( res1.recover ( restx1 ) ) 
            throw new Exception ( "TestXATransactionalResource.recover (ct) " +
                                              "wrong: true for new restx ! " );
          
          if ( !restx1.getTid().equals ( tid1 ) )
            throw new Exception ( "XAResourceTransaction.geTid() not ok" );
          
          restx1.setXAResource ( xares1 );
          restx1.resume();
          restx1.suspend();
                                              
         //get new ResTx for same sibling -> should be the same instance      
         ResourceTransaction restx2 =
            res1.getResourceTransaction ( ct1 );
         if ( ! restx1.equals ( restx2 ) ) 
            throw new Exception ( "Same sibling should get same restx" +
                                             "when calling getResourceTx twice" );
         
         //since restx2 is same as restx1, the following test if repeated resume 
         //works
         restx2.resume();
         restx2.suspend();
         
         //test if a sibling with serial mode returns same restx
         CompositeTransaction ct2 = 
            new CompositeTransactionStub ( tid1 , true  , coord1 );
          
          ResourceTransaction restx3 =
              res1.getResourceTransaction ( ct2 );
          if ( ! restx3.equals ( restx2 ) )
              throw new Exception ("Serial sibling should get same restx" );
          
          //test if non-serial sibling gets different restx
          String tid2 = tidmgr.get();
          CompositeTransaction ct3 = 
            new CompositeTransactionStub ( tid2 , false  , coord1 );
          
          XAResourceTransaction restx4 =
               ( XAResourceTransaction ) res1.getResourceTransaction ( ct3 );
          if (  restx4.equals ( restx3 ) )
              throw new Exception ("Nonserial sibling should NOT " +
                                              "get same restx" );
          restx4.setXAResource ( xares1 );
          restx4.resume();
          restx4.suspend();
    }
    
    public void testRecovery()
    throws Exception
    {
        RMSimulator xares1 = new RMSimulator();
        TestXATransactionalResource res1 = 
          new TestXATransactionalResource ( xares1 , "TestXAResource1");
       
        
        //CASE 1: basic prepare, test if can be recovered.
        String root1 = tidmgr.get();
        String tid1 = tidmgr.get();
      
        CompositeCoordinator coord1 = 
          new TestCompositeCoordinator ( root1 );
        CompositeTransaction ct1 = 
          new CompositeTransactionStub ( tid1 , true  , coord1 );     
        XAResourceTransaction restx1 = 
          ( XAResourceTransaction ) res1.getResourceTransaction ( ct1 );
        HeuristicMessage msg1 = new StringHeuristicMessage ("Hello");
        restx1.setXAResource ( xares1 );
        restx1.resume();
        restx1.suspend();
        restx1.addHeuristicMessage ( msg1 );
        restx1.prepare();
        
        //simulate second prepare, but who does NOT get logged 
        //->endRecovery call should abort this one.
        String tid2 = tidmgr.get();
        CompositeTransaction ct2 = 
          new CompositeTransactionStub ( tid2 , true  , coord1 );     
        XAResourceTransaction restx2 = 
          ( XAResourceTransaction ) res1.getResourceTransaction ( ct2 );
        HeuristicMessage msg2 = new StringHeuristicMessage ("Hello");
        restx2.setXAResource ( xares1 );
        restx2.resume();
        restx2.suspend();
        restx2.addHeuristicMessage ( msg2 );
        restx2.prepare();
        
        //now write to file
         //make sure that recovery can find the resource.
         //even if it is a diff. instance, only the XA under it counts
         TestXATransactionalResource res2 = 
          new TestXATransactionalResource ( xares1 , "TestXAResource1");
         Configuration.addResource ( res2 );
         FileOutputStream fout = new FileOutputStream ( "XARecoveryTestFile.tmp");
        ObjectOutputStream out = new ObjectOutputStream ( fout );
        out.writeObject ( restx1 );
        out.close();
        fout.close();
        
       //read in again
       FileInputStream fin = new FileInputStream ("XARecoveryTestFile.tmp");
       ObjectInputStream in = new ObjectInputStream ( fin );
       restx1 = ( XAResourceTransaction ) in.readObject();
       HeuristicMessage[] msgs = restx1.getHeuristicMessages();
       restx1.recover();
       restx1.commit( false );
       
       //now, test if endRecovery rolls back restx2
       if ( xares1.getState ( restx2.getXid() ) == null )
          throw new Exception ( "RMSimulator forgets indoubt?" );
       res2.endRecovery();
       if ( xares1.getState ( restx2.getXid() ) != null )
          throw new Exception ( "endRecovery() does not cause rollback: " + restx2.getState() );
    }
    
    public void testRollback()
    throws Exception
    {
        RMSimulator xares1 = new RMSimulator();
        TestXATransactionalResource res1 = 
        new TestXATransactionalResource ( xares1 , "TestXAResource1");
        String root1 = tidmgr.get();
        String tid1 = tidmgr.get();
        //case1 
        CompositeCoordinator coord1 = 
          new TestCompositeCoordinator ( root1 );
        CompositeTransaction ct1 = 
          new CompositeTransactionStub ( tid1 , true  , coord1 );     
        XAResourceTransaction restx1 = 
          ( XAResourceTransaction ) res1.getResourceTransaction ( ct1 );
        restx1.setXAResource ( xares1 );
        restx1.resume();
        try {
        	  restx1.rollback();
        }
        catch ( Exception e ) {
            throw new Exception ("active state restx makes rollback fail");	
        }
        
        //case2
        String tid2 = tidmgr.get();
        CompositeTransaction ct2 = 
          new CompositeTransactionStub ( tid2 , true  , coord1 );     
        XAResourceTransaction restx2 = 
          ( XAResourceTransaction ) res1.getResourceTransaction ( ct2 );
        
        restx2.setXAResource ( xares1 );
        restx2.resume();
        restx2.suspend();
        try {
        	  restx2.rollback();
        }
        catch ( Exception e ) {
            throw new Exception ( "inactive state restx makes rollback fail" );	
        }
        
        //case3
        String tid3 = tidmgr.get();
        CompositeTransaction ct3 = 
          new CompositeTransactionStub ( tid3 , true  , coord1 );     
        XAResourceTransaction restx3 = 
          ( XAResourceTransaction ) res1.getResourceTransaction ( ct3 );
        restx3.setXAResource ( xares1 );
        try {
        	  restx3.rollback();
        }
        catch ( Exception e ) {
            throw new Exception ( "initial state restx makes rollback fail" );	
        }
    }
    
    public void testTemporaryXATransactionsResource()
    throws Exception
    {
  		TestXAResource xares = new TestXAResource();
  		
  		TemporaryXATransactionalResource res = 
  			new TemporaryXATransactionalResource ( xares );
  		
  		if ( res.isClosed() )
			throw new Exception ( "isClosed() fails");
		
		if ( res.acceptsAllXAResources() )
			throw new Exception ( "acceptsAllXAResources() fails");
		
		if ( ! res.usesXAResource ( xares ) )
			throw new Exception ( "usesXAResource() fails");
		
		TestXAResource xares2 = new TestXAResource();
		if ( res.usesXAResource ( xares2 ) )
			throw new Exception ( "usesXAResource() fails");
		
		if ( !res.isSameRM ( res ) )
			throw new Exception ( "isSameRM() fails ");
		
		//make sure recovery is repeatable
		res.recover();
		res.endRecovery();
		res.recover();
		res.endRecovery();
		res.recover();
		res.endRecovery();
		
	
		
		
		xares.setFailureMode( TestXAResource.FAIL_IS_SAME_RM , new XAException() );	
		//following is vital to avoid memory exhaustion in Configuration
		if ( ! res.isClosed() ) 
			throw new Exception ( "isClosed() fails"); 
    }
    
    
}
