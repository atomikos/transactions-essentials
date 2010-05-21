package com.atomikos.icatch.trmi;

import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;

import javax.naming.Context;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.AbstractJUnitReleaseTest;
import com.atomikos.icatch.system.Configuration;

public class JrmpTestJUnit extends AbstractJUnitReleaseTest 
{
	
	public JrmpTestJUnit ( String name )
	{
		super ( name , true );
	}

	protected UserTransactionService onSetUp(TestLogAdministrator admin) throws Exception 
	{
		try {
			LocateRegistry.createRegistry ( 1099 );
		}
		catch ( Exception e ) {
			 System.err.println ( "WARNING: failed to start RMI registry - already running?");
			
		}
		UserTransactionService uts =
            new com.atomikos.icatch.trmi.UserTransactionServiceFactory().
                getUserTransactionService ( new Properties() );
        TSInitInfo info = uts.createTSInitInfo();
        Properties properties = info.getProperties();
        properties.setProperty ( UserTransactionServiceFactory.SOAP_COMMIT_PROTOCOLS_PROPERTY_NAME  , "none" );

        properties.setProperty (
            com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME , new String() );
        
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
	    properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
		  
        properties.setProperty (
            AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "JrmpTestJUnit" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
        properties.setProperty ( Context.PROVIDER_URL , "rmi://localhost:1099" );
		properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
								 "com.sun.jndi.rmi.registry.RegistryContextFactory" );
		properties.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "UnicastRemoteObject" );
		properties.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "DEBUG");
        

        uts.registerLogAdministrator ( admin );
        uts.init ( info );
        return uts;
	}
	
	//test for bug 22436 in fogbugz
	public void testExportedProperties() throws Exception 
	{
		CompositeTransactionManager ctm = Configuration.getCompositeTransactionManager();
		CompositeTransaction ct = 
	            ctm.createCompositeTransaction ( 10000 );
		ct.setProperty( "key" , "value" );
		//just to make sure: is the property really set?
		assertEquals (  "value" , ct.getProperty("key") );
		ExportingTransactionManager etm = Configuration.getExportingTransactionManager();
		Propagation p = etm.getPropagation();
		Stack l = p.getLineage();
		Enumeration enumm = l.elements();
		while ( enumm.hasMoreElements() ) {
			ct = ( CompositeTransaction ) enumm.nextElement();
			//now, test for the observed behaviour in the bug
			assertEquals ( "value" , ct.getProperty("key") );
		}

	}

	protected void onTearDown() throws Exception 
	{

	}

	public void testHeuristicCommit() {
		//overridden to do nothing - is tested in core already and seems 
		//to cause problems in this build
	}
}
