package com.atomikos.icatch.imp;

import java.util.Properties;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.TestParticipant;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.system.Configuration;

public abstract class AbstractJUnitActivityTest extends TransactionServiceTestCase
{

    private UserTransactionService uts;
    private CompositeTransactionManager ctm;
    private CompositeTransaction ct;
    
    private Properties properties;

    public AbstractJUnitActivityTest ( String name )
    {
        super ( name );
    }

    protected abstract UserTransactionService startUp();
    
   
    
    protected void setUp ()
    {
        super.setUp ();
        uts = startUp();
        ctm = uts.getCompositeTransactionManager();
    }
    
    protected void tearDown()
    {
        uts.shutdown ( true );
        super.tearDown();
    }
    
    public void testCreateActivity()
    throws Exception
    {
        ct = ctm.createCompositeTransaction ( 1000 );
        assertNotNull ( ct.getCompositeCoordinator().isRecoverableWhileActive() );
        assertFalse ( ct.getCompositeCoordinator().isRecoverableWhileActive().booleanValue() );
        ct.getCompositeCoordinator().setRecoverableWhileActive();
        assertTrue ( ct.getCompositeCoordinator().isRecoverableWhileActive().booleanValue() );
        ct.commit();
    }
    
    public void testCreateSubActivity()
    throws Exception
    {
        ct = ctm.createCompositeTransaction ( 1000  );
        ct.getCompositeCoordinator().setRecoverableWhileActive();
        CompositeTransaction subtx = ct.createSubTransaction();
        assertTrue ( subtx.getCompositeCoordinator().isRecoverableWhileActive().booleanValue() );
        subtx.commit();
        subtx = ctm.createCompositeTransaction ( 1000  );
        assertTrue ( subtx.getCompositeCoordinator().isRecoverableWhileActive().booleanValue() );
        assertTrue ( subtx.isDescendantOf ( ct ) );
        subtx.commit();
        ct.rollback();
    }
    
    

    
 
    public void testActivityRecoverableWhileActive()
    throws Exception
    {
        ct = ctm.createCompositeTransaction ( 1000 );
        ct.getCompositeCoordinator().setRecoverableWhileActive();
        String coordId = ct.getCompositeCoordinator().getCoordinatorId();
        
        //add a test participant to make sure that the coordinator is flushed to the logs
        TestParticipant p = new TestParticipant();
        ct.addParticipant ( p );
        
        //shutdown while CT is ACTIVE
        uts.shutdown ( true );
        uts = startUp();
        
        //assert activity coordinator is still there
        CompositeCoordinator c = Configuration.getTransactionService().getCompositeCoordinator ( coordId );
        assertNotNull ( c );
        
        
    }

}
