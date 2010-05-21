
package com.atomikos.icatch.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.datasource.TestRecoverableResource;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.TestLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * 
 *
 * 
 */
public class UserTransactionServiceTestJUnit extends TransactionServiceTestCase
{
    
    
    private UserTransactionService uts;
    
    

    
    public UserTransactionServiceTestJUnit(String arg0)
    {
        super(arg0);
    }

    private void addTestProperties ( Properties properties )
    {
        properties.setProperty ( 
				AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , "UserTransactionServiceTestTransactionManager" );
        properties.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , getTemporaryOutputDir() );
        properties.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "25000" );
    }
    
    protected void setUp()
    {
        super.setUp();
        System.setProperty ( UserTransactionServiceImp.FILE_PATH_PROPERTY_NAME ,  getTemporaryOutputDir() + "/jta.properties" );
    }
    
    protected void tearDown()
    {
        super.tearDown();
        System.getProperties().remove ( UserTransactionServiceImp.FILE_PATH_PROPERTY_NAME );
    }
    
    public void testSetProperties()
    throws Exception
    {
        uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		
		//set properties to some custom values
		Properties p = info.getProperties();
		
		
		if ( p == null ) throw new Exception ( "ERROR:  no default properties?" );
		
		String defaultTmName = p.getProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME);
		if ( defaultTmName == null )
					throw new Exception ( "ERROR: tm name has no default value???" );
		addTestProperties ( p );
		
		String myTmName = "TMTestSetProperties";
		p.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , myTmName );		
		info.setProperties(p);	
		p = info.getProperties();
		if ( !p.getProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME ).equals ( myTmName ))
			throw new Exception ( "ERROR: TSInitInfo does not remember custom property");
		
		String consoleFileName = "ConsoleFileTestSetProperties";
		p.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME , consoleFileName );
		File outputDir = new File ( getTemporaryOutputDir() );
		String consoleFilePath = outputDir + File.separator + consoleFileName;
		
		p.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , outputDir.getAbsolutePath() + File.separator );

		String logDirName = getTemporaryOutputDir() + File.separator;
		File logDir = new File ( logDirName );
		String logBaseName = "logFileTestSetProperties";
		
		p.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , logDirName );
		p.setProperty (AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME , logBaseName );
		
		
		uts.init ( info );
		
		File consoleFile = new File ( consoleFilePath );
		if ( !consoleFile.exists() ) 
			throw new Exception ( "ERROR: " + consoleFilePath + " does not exists?" );
		
		//check if logfile exists in log dir
		String[] names = logDir.list();
		boolean logFileFound = false;
		for ( int i = 0 ; i < names.length ; i++ ) {
			if ( names[i].indexOf ( logBaseName ) >= 0 ) logFileFound = true;
		}
		if ( ! logFileFound ) 
			throw new Exception ( "ERROR: Logfile " + logBaseName + " not found?"  );
		
		
		
		//Test if the TM name appears in the TIDs
		TransactionManager tm = uts.getTransactionManager();
		tm.begin();
		Transaction tx = tm.getTransaction();
		String tid = uts.getCompositeTransactionManager().getCompositeTransaction().getTid();
		if ( tid.indexOf ( myTmName ) < 0 ) 
			throw new Exception ( "ERROR: custom TM name not used: " + tid);
		
		tm.rollback();
		
		uts.shutdown ( true );
    }
    
    public void testSerializable()
    throws Exception
    {
		uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		Properties p = info.getProperties();
		addTestProperties ( p );
		String myTmName = "TMTestSerializable";
		p.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , myTmName );	
		
		//write out uts to file
		ObjectOutputStream out = new ObjectOutputStream ( new FileOutputStream ( getTemporaryOutputDir() + "/uts.ser"));
		out.writeObject ( uts );
		out.close();
		uts = null;
		
		ObjectInputStream in = new ObjectInputStream ( new FileInputStream ( getTemporaryOutputDir() + "/uts.ser"));
		uts = ( UserTransactionServiceImp ) in.readObject();
		in.close();
		
		uts.init ( info );
		//Test if the TM name appears in the TIDs
		TransactionManager tm = uts.getTransactionManager();
		tm.begin();
		Transaction tx = tm.getTransaction();
		String tid = uts.getCompositeTransactionManager().getCompositeTransaction().getTid();
		if ( tid.indexOf ( myTmName ) < 0 ) 
				  throw new Exception ( "ERROR: custom TM name not used: " + tid);
		
		tm.rollback();		
		uts.shutdown ( true );
    }
    
    public void testJNDI() throws Exception
    {
		java.rmi.registry.Registry registry = null;
		try {
			java.rmi.registry.LocateRegistry.createRegistry ( 1099 );
		}
		catch ( Exception e ) {
			//already runnings
		}
		
		
		
		uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		addTestProperties ( info.getProperties() );
		uts.init ( info );
		
		TransactionManager  tm = uts.getTransactionManager();
		UserTransaction utx = uts.getUserTransaction();
		Context ctx = new InitialContext ( info.getProperties() );
		ctx.rebind("TM" , tm);
		ctx.rebind ( "UTX", utx);
		
		tm = ( TransactionManager ) ctx.lookup ( "TM");
		utx = ( UserTransaction ) ctx.lookup ( "UTX");
		
		utx.begin();
		
		utx.rollback();
		
		uts.shutdown ( true );
    }
    
    public void testPropertyFile() throws Exception
    {
		Properties p = new Properties();
			
		addTestProperties ( p );
		
		String myTmName = "TMTestPropertyFile";
		p.setProperty ( "com.atomikos.icatch.service" , 
			"com.atomikos.icatch.standalone.UserTransactionServiceFactory");
		p.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , myTmName );	
		
		p.store ( new FileOutputStream ( getTemporaryOutputDir() + "/jta.properties" ) , "JTA PROPERTIES" );
		
		
		uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		//Do NOT set properties explicitly to use the ones stored in the file
		uts.init( info );
		
		TransactionManager tm = uts.getTransactionManager();
		tm.begin();
		Transaction tx = tm.getTransaction();
		String tid = uts.getCompositeTransactionManager().getCompositeTransaction().getTid();
		if ( tid.indexOf ( myTmName ) < 0 ) 
			throw new Exception ( "ERROR: JTA Property file not used?" );
		
		tm.rollback();		
		uts.shutdown ( true );	
    }
    
    public void testExplicitOverridesPropertyFile() throws Exception
    {

		Properties p = new Properties();
		addTestProperties ( p );
		String myTmFakeName = "TMExplicitOverridesPropertyFileFake";
		String myTmRealName = "TMExplicitOverridesPropertyFile";
		p.setProperty ( "com.atomikos.icatch.service" , 
			"com.atomikos.icatch.standalone.UserTransactionServiceFactory");
		p.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , myTmFakeName );		
		
		p.store ( new FileOutputStream ( getTemporaryOutputDir() + "/jta.properties" ) , "JTA PROPERTIES" );
		
		uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		addTestProperties ( info.getProperties() );
		info.getProperties().setProperty( 
			AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , myTmRealName);
		
		uts.init( info );
		
		TransactionManager tm = uts.getTransactionManager();
		tm.begin();
		Transaction tx = tm.getTransaction();
		String tid = uts.getCompositeTransactionManager().getCompositeTransaction().getTid();
		if ( tid.indexOf ( myTmRealName ) < 0 ) 
			throw new Exception ( "ERROR: Explicit sets do not override file property?" );
		
		tm.rollback();		
		uts.shutdown ( true );	
    }
    
    public void testNoPropertyFile() throws Exception
    {
		//write property file and assert that it is NOT used
		Properties p = new Properties();
		addTestProperties ( p );
		String myTmFakeName = "TMTestNoPropertyFileFakeName";
		p.setProperty ( "com.atomikos.icatch.service" , 
			"com.atomikos.icatch.standalone.UserTransactionServiceFactory");
		p.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , myTmFakeName );		
		
		p.store ( new FileOutputStream ( getTemporaryOutputDir() + "/jta.properties" ) , "JTA PROPERTIES" );
		
		String myTmRealName = "TMTestNoPropertyFile";
		addTestProperties ( System.getProperties() );
		System.setProperty ( UserTransactionServiceImp.NO_FILE_PROPERTY_NAME ,"true");
		System.setProperty ( "com.atomikos.icatch.service" , 
			"com.atomikos.icatch.standalone.UserTransactionServiceFactory");
		System.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , myTmRealName );
		
		
		UserTransactionServiceImp uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		//Do NOT set properties explicitly to use the ones stored in the file
		uts.init( info );
		
		TransactionManager tm = uts.getTransactionManager();
		tm.begin();
		Transaction tx = tm.getTransaction();
		String tid = uts.getCompositeTransactionManager().getCompositeTransaction().getTid();
		if ( tid.indexOf ( myTmRealName ) < 0 ) 
			throw new Exception ( "ERROR: No property file option fails?" );
		
		tm.rollback();		
		uts.shutdown ( true );
		
		//UNSET system property to reset VM state for the further tests!
		//System.setProperty ( UserTransactionServiceImp.NO_FILE_PROPERTY_NAME ,null);	
		System.getProperties().remove(UserTransactionServiceImp.NO_FILE_PROPERTY_NAME);
    }
    
    public void testCustomPathPropertyFile() throws Exception
    {
		Properties p = new Properties();
		addTestProperties ( p );
		String myTmName = "TMTestCustomPathPropertyFile";
		p.setProperty ( "com.atomikos.icatch.service" , 
			"com.atomikos.icatch.standalone.UserTransactionServiceFactory");
		p.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , myTmName );		
		
		String fileName = getTemporaryOutputDir() + File.separator + "customJta.properties";
		p.store ( new FileOutputStream (  fileName ) , "JTA PROPERTIES" );
		System.setProperty ( 
			UserTransactionServiceImp.FILE_PATH_PROPERTY_NAME , fileName );
		
		UserTransactionServiceImp uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		//Do NOT set properties explicitly to use the ones stored in the file
		uts.init( info );
		
		TransactionManager tm = uts.getTransactionManager();
		tm.begin();
		Transaction tx = tm.getTransaction();
		String tid = uts.getCompositeTransactionManager().getCompositeTransaction().getTid();
		if ( tid.indexOf ( myTmName ) < 0 ) 
			throw new Exception ( "ERROR: custom JTA Property file not used?" );
		
		tm.rollback();		
		uts.shutdown ( true );
    }
    
    public void testShutdownCleansConfiguration() throws Exception
    {
        
        
		String myTmRealName = "TMTestShutdownCleansConfiguration";
		addTestProperties ( System.getProperties() );
		System.setProperty ( UserTransactionServiceImp.NO_FILE_PROPERTY_NAME ,"true");
		System.setProperty ( "com.atomikos.icatch.service" , 
			"com.atomikos.icatch.standalone.UserTransactionServiceFactory");
		System.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , myTmRealName );
		
		
		UserTransactionServiceImp uts = new UserTransactionServiceImp();
		TSInitInfo info = uts.createTSInitInfo();
		uts.init( info );
		TestRecoverableResource testResource = new TestRecoverableResource( getName() );
		uts.registerResource( testResource );
		
		//assertTrue ( Configuration.getResources().hasMoreElements() );
		assertNotNull ( Configuration.getConsole() );
		assertNotNull ( Configuration.getCompositeTransactionManager() );
		assertNotNull ( Configuration.getTransactionService() );
		assertTrue (  Configuration.getResources().hasMoreElements() );
		assertFalse ( testResource.isClosed() );
		uts.shutdown ( true );
		
		assertNull ( Configuration.getConsole() );
		assertNull ( Configuration.getCompositeTransactionManager() );
		assertNull ( Configuration.getTransactionService() );
		assertFalse ( Configuration.getResources().hasMoreElements() );
		assertTrue ( testResource.isClosed() );
    }
    
    public void testRemoveResourceWorksOnUts() throws Exception
    {
    		TestRecoverableResource res = new TestRecoverableResource(getTemporaryOutputDir());
            
    		
    		UserTransactionServiceImp uts = new UserTransactionServiceImp();
    		uts.registerResource(res);
    		assertNotNull ( Configuration.getResource(res.getName()));
    		uts.removeResource(res);
    		assertNull ( Configuration.getResource(res.getName()));
    		
    }
    
    public void testRemoveLogAdminWorksOnUts() throws Exception
    {
    		UserTransactionServiceImp uts = new UserTransactionServiceImp();
		TestLogAdministrator admin = new TestLogAdministrator();
		uts.registerLogAdministrator(admin);
		Enumeration enumm = Configuration.getLogAdministrators();
		boolean found = false;
		while ( enumm.hasMoreElements() ) {
			LogAdministrator next = ( LogAdministrator ) enumm.nextElement();
			if ( next == admin ) found = true;
		}
		assertTrue ( "Registration of logadmin fails" , found );
		uts.removeLogAdministrator(admin);
		found = false;
		enumm = Configuration.getLogAdministrators();
		while ( enumm.hasMoreElements() ) {
			LogAdministrator next = ( LogAdministrator ) enumm.nextElement();
			if ( next == admin ) found = true;
		}
		assertFalse ( "Removal of logadmin fails" , found );
		
		
    }
    
}
