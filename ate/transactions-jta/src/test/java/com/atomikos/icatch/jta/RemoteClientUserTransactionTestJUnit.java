package com.atomikos.icatch.jta;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
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
public class RemoteClientUserTransactionTestJUnit extends
        TransactionServiceTestCase
{
    
    private UserTransactionService uts;
    
    private UserTransaction rcut;
    
    private CompositeTransactionManager ctm;
    
    public RemoteClientUserTransactionTestJUnit ( String name )
    {
        super ( name );
    }
    
    protected void setUp()
    {
        super.setUp();
        
        //sleep between tests to allow cleanup
        //of temp files to complete
        sleep();
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
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CLIENT_DEMARCATION_PROPERTY_NAME , "true" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "UnicastRemoteObject" );
        
        	try {
                LocateRegistry.createRegistry ( 1099 );
            } catch (RemoteException e) {
                System.err.println( "WARNING: could not start RMI registry - already running?");
            }
        	
        	uts.init ( info );
        	rcut = uts.getUserTransaction();
        	ctm = uts.getCompositeTransactionManager();
    }
    
    protected void tearDown()
    {
        uts.shutdown ( false );
        super.tearDown();
    }
    
    public void testIsInstanceOfRightClass() 
    {
        assertTrue ( rcut instanceof RemoteClientUserTransaction );
    }
    
    public void testNoTxContextBeforeBegin() throws Exception
    {
//      assert that no tx exists at being
        if ( rcut.getStatus() != Status.STATUS_NO_TRANSACTION )
            fail ( "UserTx has tx before begin?" );
    }
    
    public void testNormalTransactionFlow() throws Exception
    {
        rcut.begin();
        
        if ( rcut.getStatus() == Status.STATUS_NO_TRANSACTION )
            throw new Exception ( "UserTx has NO tx after begin?" );
        
       
        //assert that server does no thread mapping
        CompositeTransaction ct = ctm.getCompositeTransaction();
        
        
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
        String oldTid = rcut.toString();
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
            
    }
    
    public void testRollback() throws Exception
    {
        
        rcut.begin();
        String oldTid = rcut.toString();
        rcut.rollback();
        if ( rcut.toString() != null )
            throw new Exception ( "User tx rollback() does not work OK?" );
        CompositeTransaction ct = ctm.getCompositeTransaction ( oldTid );
        if ( ct != null )
            throw new Exception ( "User tx rollback not delegated to server?" );
    }
    
    public void testSetRollbackOnly() throws Exception
    {
        
        rcut.begin();
        String oldTid = rcut.toString();
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
    }
    
    public void testReferencibility() throws Exception
    {
        		System.setProperty ( 
                Context.INITIAL_CONTEXT_FACTORY, 
                "com.sun.jndi.rmi.registry.RegistryContextFactory" );
            
            Context ctx = new InitialContext();
            ctx.bind ( "usertx" , rcut );
            rcut = ( UserTransaction ) ctx.lookup ( "usertx" );
            if ( rcut == null )
                throw new Exception ( "Usertx not referenceable?" );
            ctx.unbind ( "usertx" );
    }
    
    public void testAfterSerialization() throws Exception
    {
        rcut.begin();
        
        //assert that usertx can be shipped (Serialized) and 
        //reconstructed on the other side
        UserTransaction old = rcut;
        String oldTid = rcut.toString();
        String dir = getTemporaryOutputDir();
        if ( ! dir.endsWith ( "/" ) ) dir = dir + "/";
        FileOutputStream out = new FileOutputStream ( dir + "rcut.out" );
        ObjectOutputStream oout = new ObjectOutputStream ( out );
        oout.writeObject ( rcut );
        oout.close();
        
        FileInputStream in = new FileInputStream ( dir + "rcut.out" );
        ObjectInputStream oin = new ObjectInputStream ( in );
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
    
}
