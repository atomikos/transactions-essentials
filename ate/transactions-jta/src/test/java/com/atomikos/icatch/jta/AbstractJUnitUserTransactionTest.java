package com.atomikos.icatch.jta;

import java.util.Properties;

import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

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
public abstract class AbstractJUnitUserTransactionTest 
extends TransactionServiceTestCase
{
    
    private UserTransactionService uts;

    private UserTransaction utx;
    
    private TestXAResource xaRes1 , xaRes2;
    
    public AbstractJUnitUserTransactionTest(String name)
    {
        super(name);
    }
    
    protected abstract UserTransaction getUserTransaction();

    protected UserTransactionService getUserTransactionService()
    {
        return uts;
    }
    
    protected void setUp()
    {
        super.setUp();
        uts =
            new UserTransactionServiceImp();
        
        TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();        
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "UserTransactionTestJUnit" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir()
        	        );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG" );
        	properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
       
        	xaRes1 = new TestXAResource();
        	xaRes2 = new TestXAResource();
        	uts.registerResource ( new TestXATransactionalResource ( xaRes1 , "TestXA1"  ) );    
        uts.registerResource ( new TestXATransactionalResource ( xaRes2 , "TestXA2" ) );
        
        	uts.init ( info );
        	utx = getUserTransaction();
    }
    
    protected void tearDown()
    {
        uts.shutdown ( true );
        super.tearDown();
        
    }
    
    public void testBegin()
    throws Exception
    {
        
        if ( utx.getStatus() != Status.STATUS_NO_TRANSACTION )
            fail ( "A transaction exists before begin()");
        utx.begin();
        if ( utx.getStatus() == Status.STATUS_NO_TRANSACTION )
            fail ( "No transaction after begin()");
        assertEquals ( utx.getStatus() , Status.STATUS_ACTIVE );
        
    }
    
    public void testCommit() throws Exception
    {
        utx.begin();
        
        UserTransactionManager tm = new UserTransactionManager();
        tm.getTransaction().enlistResource ( xaRes1 );
        utx.commit();
        assertEquals ( utx.getStatus() , Status.STATUS_NO_TRANSACTION );
        if ( xaRes1.getLastCommitted() == null )
            fail ( "No XA commit" );
    }
    
    
    public void testRollback() throws Exception
    {
        utx.begin();
        UserTransactionManager tm = new UserTransactionManager();
        tm.getTransaction().enlistResource ( xaRes1 );
        utx.rollback();
        assertEquals ( utx.getStatus() , Status.STATUS_NO_TRANSACTION );
        if ( xaRes1.getLastRolledback() == null )
            fail ( "No XA rollback" );
    }
    
    public void testSetRollbackOnly() throws Exception
    {
        utx.begin();
        utx.setRollbackOnly();
        
        try {
            utx.commit();
            fail ( "commit for setRollbackOnly");
        }
        catch ( RollbackException ok ) {}
        
    }
    
    
}
