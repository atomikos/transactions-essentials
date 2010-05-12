

package com.atomikos.icatch.trmi;
import java.util.Properties;

import javax.naming.Context;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.jta.TransactionManagerTest;

public class Test
{

	static void testJrmp() throws Exception {
		//assert that we can start the TM over the old RMI

		UserTransactionService uts =
				new com.atomikos.icatch.trmi.UserTransactionServiceFactory().
					getUserTransactionService ( new Properties() );
		TSInitInfo info = uts.createTSInitInfo();
		Properties properties = info.getProperties();
		properties.setProperty (
								com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME , new String() );
		properties.setProperty ( 
								AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TrmiTestTransactionManager" );
		properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		properties.setProperty ( Context.PROVIDER_URL , "rmi://localhost:1099" );
		properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
								 "com.sun.jndi.rmi.registry.RegistryContextFactory" );
		properties.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "UnicastRemoteObject" );
        
		
		uts.init ( info );
		//TransactionManagerTest.testBasic ( null );
		uts.shutdown ( true );
        
	}
	
	static void testNoExportMode() throws Exception
	{
		//assert that TM works even if no export mode is selected
		UserTransactionService uts = 
			new com.atomikos.icatch.trmi.UserTransactionServiceFactory().
			getUserTransactionService(new Properties());
		TSInitInfo info = uts.createTSInitInfo();
		Properties properties = info.getProperties();
		properties.setProperty (
								com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME , new String() );
		properties.setProperty ( 
								AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TrmiTestTransactionManager" );
		properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
		//deliberately make sure that the registry can not be found
		//needed to assert that startup works without registry
		properties.setProperty ( Context.PROVIDER_URL , "rmi://nocomputer:1099" );
		properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
								 "com.sun.jndi.rmi.registry.RegistryContextFactory" );
		properties.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "FakeClassToAvoidExport" );
        
		
		uts.init ( info );
		
		
		//assert that exportingTM is returned
		ExportingTransactionManager etm = uts.getExportingTransactionManager();
		if ( etm == null ) throw new Exception ( "No exporting tm");
		//assert that propagation can be gotten; needed for threaded txs
		CompositeTransactionManager ctm = uts.getCompositeTransactionManager();
		CompositeTransaction ct = ctm.createCompositeTransaction ( 1000 );
		Propagation p = etm.getPropagation();
		if ( p == null ) throw new Exception ( "No propagation");
		//assert that propagation can be imported; needed for threaded txs
		ImportingTransactionManager itm = uts.getImportingTransactionManager();
		if ( itm == null ) throw new Exception ( "No importing tm");
		itm.importTransaction ( p , false, false );
		//assert that addExtent doesn't work
		Extent extent = itm.terminated ( false );
		try {
			etm.addExtent ( extent );
			throw new Exception ( "addExtent works if not exported");
		}
		catch ( SysException e ) {
			//should happen
		}
		ct.getTransactionControl().getTerminator().rollback();
		uts.shutdown ( true );
		
	}
  
    static void test() throws Exception
    {
     
            
            UserTransactionService uts =
                new com.atomikos.icatch.trmi.UserTransactionServiceFactory().
                    getUserTransactionService ( new Properties() );
            TSInitInfo info = uts.createTSInitInfo();
            Properties properties = info.getProperties();
            properties.setProperty (
                com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME , new String() );
           
            properties.setProperty (
                AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TrmiTestTransactionManager" );
            properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
            properties.setProperty ( Context.PROVIDER_URL , "iiop://localhost:1050" );
            properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
                "com.sun.jndi.cosnaming.CNCtxFactory" );

            uts.init ( info );
            TransactionManagerTest.testBasic ( null );
            TransactionManagerTest.testLoad ( 
                uts.getCompositeTransactionManager() );
            uts.shutdown ( true );
            info = uts.createTSInitInfo();
            
            info = uts.createTSInitInfo();
		    properties = info.getProperties();
			properties.setProperty (
						AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "TrmiTestTransactionManager" );
			properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
			properties.setProperty ( Context.PROVIDER_URL , "iiop://localhost:1050" );
			properties.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
						"com.sun.jndi.cosnaming.CNCtxFactory" ); 
			info.setProperties(properties);           
			com.atomikos.icatch.jta.ReleaseTester.test ( uts , info );			
            com.atomikos.icatch.imp.ReleaseTester.test ( uts , info , true ); 
            uts.shutdown(true);     
              
        
        
    }
    
    public static void main ( String[] args ) 
    {
        try {
            System.err.println ( "Starting test..." );
            testJrmp();
            testNoExportMode();
            test();
          
        }
        catch ( SysException se ) {
            se.printStackTrace();
        }   
        catch ( Exception e ) {
            e.printStackTrace(); 
        } 
        finally {
            System.err.println ( "Test done." ); 
        }
    } 
}
