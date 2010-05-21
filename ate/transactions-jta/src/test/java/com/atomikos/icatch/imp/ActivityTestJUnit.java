package com.atomikos.icatch.imp;

import java.util.Properties;

import javax.transaction.TransactionManager;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;

public class ActivityTestJUnit extends AbstractJUnitActivityTest
{

    private UserTransactionService uts;
    private CompositeTransactionManager ctm;
    private CompositeTransaction ct;
    private TransactionManager tm;
    
    private Properties properties;

    public ActivityTestJUnit ( String name )
    {
        super ( name );
    }


    
    public void testTransactionInActivityNotAllowed()
    throws Exception
    {
        ct = ctm.createCompositeTransaction ( 1000 );
        assertNull ( tm.getTransaction() );
        
        
        tm.begin();
        
        assertNotNull ( tm.getTransaction() );
        
        //assert that JTA tx is root tx (old tx is suspended)
        CompositeTransaction jtaTx = ctm.getCompositeTransaction();
        assertTrue ( jtaTx.isRoot() );
        jtaTx.commit();
        assertNull ( tm.getTransaction() );
        
        CompositeTransaction previous = ctm.getCompositeTransaction();
        assertTrue ( previous.isSameTransaction ( ct ));
        
        ct.rollback();
    }
    
 


	protected UserTransactionService startUp() 
	{
		uts = new UserTransactionServiceImp ();

        TSInitInfo info = uts.createTSInitInfo ();
        properties = info.getProperties ();
        properties.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME,
                "ActivityTestTransactionManager" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME,
                getTemporaryOutputDirAsAbsolutePath() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME,
                getTemporaryOutputDirAsAbsolutePath() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME,
                "DEBUG" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME, "25000" );
        uts.init ( info );
        ctm = uts.getCompositeTransactionManager();
        tm = uts.getTransactionManager();
        return uts;
	}

}
