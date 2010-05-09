package com.atomikos.icatch.trmi;

import java.util.Properties;

import javax.naming.Context;

import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.AbstractJUnitReleaseTest;

public class NoExportTestJUnit extends AbstractJUnitReleaseTest 
{

	public NoExportTestJUnit ( String name )
	{
		super ( name , false );
	}
	
	protected UserTransactionService onSetUp(TestLogAdministrator admin) throws Exception 
	{
		UserTransactionService uts =
            new com.atomikos.icatch.trmi.UserTransactionServiceFactory().
                getUserTransactionService ( new Properties() );
        TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();
        properties.setProperty (
            com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME , new String() );
        properties.setProperty ( UserTransactionServiceFactory.SOAP_COMMIT_PROTOCOLS_PROPERTY_NAME  , "none" );

        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
	    properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
		  
        properties.setProperty (
            AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "JrmpTestJUnit" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		properties.setProperty ( Context.PROVIDER_URL , "rmi://nocomputer:1099" );
		properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
								 "com.sun.jndi.rmi.registry.RegistryContextFactory" );
		properties.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "FakeClassToAvoidExport" );
        

        uts.registerLogAdministrator ( admin );
        uts.init ( info );
        return uts;
	}

	protected void onTearDown() throws Exception 
	{
		

	}

}
