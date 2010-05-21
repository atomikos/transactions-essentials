package com.atomikos.icatch.standalone;

import java.util.Properties;

import com.atomikos.datasource.TestRecoverableResource;
import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;

/**
 * 
 * 
 * 
 *
 * 
 */
public class StandaloneTestJUnit extends 
com.atomikos.icatch.imp.AbstractJUnitReleaseTest
{

    
    private TestRecoverableResource  res;
    
    /**
     * @param name
     */
    public StandaloneTestJUnit(String name)
    {
        super ( name , false );
    }

    /**
     * @throws Exception
     * @see com.atomikos.icatch.imp.AbstractJUnitReleaseTest#onSetUp()
     */
    protected UserTransactionService onSetUp ( TestLogAdministrator admin ) throws Exception
    {
        
        UserTransactionService uts =
            new com.atomikos.icatch.standalone.UserTransactionServiceFactory().
            getUserTransactionService ( new Properties());
        TSInitInfo info = uts.createTSInitInfo();
    
        res = 
            new TestRecoverableResource ( getName() );
        info.registerResource ( res );
        info.registerLogAdministrator ( admin );
		  Properties properties = info.getProperties();
		  properties.setProperty ( 
							AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TrmiTestTransactionManager" );
		  properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
		  properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
		  properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
       
		  
		 info.setProperties ( properties );
        uts.init ( info );
        if ( ! res.isRecoveryEnded() )
            throw new Exception ( 
            "RecoverableResource.endRecovery() not called?" );
        if ( admin.getLogControl() == null )
            throw new Exception ( 
            "LogAdministrator did not get a log control?" );
        return uts;
    }

    /**
     * @throws Exception
     * @see com.atomikos.icatch.imp.AbstractJUnitReleaseTest#onTearDown()
     */
    protected void onTearDown() throws Exception
    {
       
        if ( ! res.isClosed() )
            throw new Exception ( 
            "RecoverableResource.close() not called?" );
        
    }
    
    

}
