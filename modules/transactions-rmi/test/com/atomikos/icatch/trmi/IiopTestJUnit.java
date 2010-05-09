package com.atomikos.icatch.trmi;

import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;

import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.AbstractJUnitReleaseTest;

public class IiopTestJUnit extends AbstractJUnitReleaseTest 
{
	private Process nameserver;
	
	public IiopTestJUnit ( String name )
	{
		super ( name , true );
	}

	protected UserTransactionService onSetUp(TestLogAdministrator admin) throws Exception 
	{
	
		Thread.sleep ( 1000 );
		nameserver = Runtime.getRuntime().exec ( 
				"orbd -ORBInitialPort 1050 -defaultdb " + getTemporaryOutputDir() );
		InputStream console = nameserver.getInputStream();
		while ( console.available() > 0 ) {
			System.err.write ( console.read() );
		}
		Thread.sleep(1000);
		
		UserTransactionService uts =
            new com.atomikos.icatch.trmi.UserTransactionServiceFactory().
                getUserTransactionService ( new Properties() );
        TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();
        properties.setProperty (
            com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME , new String() );
        
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
	    properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
		  
        properties.setProperty (
            AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "IiopTestJUnit" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        properties.setProperty ( Context.PROVIDER_URL , "iiop://localhost:1050" );

		properties.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "PortableRemoteObject" );
        properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
            "com.sun.jndi.cosnaming.CNCtxFactory" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG");
        properties.setProperty ( UserTransactionServiceFactory.SOAP_COMMIT_PROTOCOLS_PROPERTY_NAME  , "none" );
        
        uts.registerLogAdministrator ( admin );
        uts.init ( info );
        return uts;
	}

	protected void onTearDown() throws Exception 
	{
		
		nameserver.destroy();
	}
	
	public void testHeuristicCommit() {
		//overridden to do nothing - is tested in core already and seems 
		//to cause problems in this build
	}
	

}
