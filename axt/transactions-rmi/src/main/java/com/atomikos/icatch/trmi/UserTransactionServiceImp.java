package com.atomikos.icatch.trmi;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;

import javax.naming.Context;
import javax.transaction.UserTransaction;

import com.atomikos.diagnostics.Console;
import com.atomikos.diagnostics.RotatingFileConsole;
import com.atomikos.diagnostics.Slf4jConsole;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.imp.LocalLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.TSMetaData;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.config.imp.TSInitInfoImp;
import com.atomikos.icatch.config.imp.TSMetaDataImp;
import com.atomikos.icatch.jca.XidLogAdministrator;
import com.atomikos.icatch.jta.AbstractJtaUserTransactionService;
import com.atomikos.icatch.jta.J2eeUserTransaction;
import com.atomikos.icatch.jta.JTA;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.jta.UserTransactionServerImp;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.license.License;
import com.atomikos.license.LicenseException;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.imp.FileLogStream;
import com.atomikos.persistence.imp.StateRecoveryManagerImp;
import com.atomikos.persistence.imp.StreamObjectLog;
import com.atomikos.persistence.imp.VolatileStateRecoveryManager;
import com.atomikos.util.ClassLoadingHelper;

class UserTransactionServiceImp
extends AbstractJtaUserTransactionService
{
    private static final String PRODUCT_NAME = "ExtremeTransactions";
    //the product name as it should be in the license

    
    private static final String VERSION = "3.7M1.0";
  
    
     private static Properties getDefaultProperties()
     {
         //
          Properties ret = new Properties ();
          ret.setProperty ( UserTransactionServiceFactory.TRUST_CLIENT_TM_PROPERTY_NAME , "false" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME , "tm.out" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME ,  "." + File.separator );
          ret.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , "." + File.separator );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , "WARN");
          ret.setProperty ( AbstractUserTransactionServiceFactory.SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME , "true" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME , "tmlog" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , "50" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME , "60000" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , getDefaultName() );
          ret.setProperty ( AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME , "500" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.CLIENT_DEMARCATION_PROPERTY_NAME , "true" );
          ret.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "none");
          ret.setProperty ( AbstractUserTransactionServiceFactory.THREADED_2PC_PROPERTY_NAME , "true");
          ret.setProperty ( AbstractUserTransactionServiceFactory.REGISTER_SHUTDOWN_HOOK_PROPERTY_NAME , "false" );
          ret.setProperty( AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME , "10000" );
		  ret.setProperty ( Context.INITIAL_CONTEXT_FACTORY , 
			 "com.sun.jndi.cosnaming.CNCtxFactory" );
		  ret.setProperty ( Context.PROVIDER_URL , "iiop://localhost:1050" );
		  
		  //
		  ret.setProperty ( AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME , "true" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.ENABLE_LOGGING_PROPERTY_NAME , "true" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_LIMIT_PROPERTY_NAME , "-1" );
		  ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_COUNT_PROPERTY_NAME , "1" );
		  
		  //comma-separated string of protocols to install: allows license-based
	      //limitation of soap capabilities
		  ret.setProperty ( UserTransactionServiceFactory.SOAP_COMMIT_PROTOCOLS_PROPERTY_NAME , "atomikos,wsat");
		  ret.setProperty ( UserTransactionServiceFactory.SOAP_HOST_ADDRESS_PROPERTY_NAME , getHostAddress() );
		  ret.setProperty ( UserTransactionServiceFactory.SOAP_PORT_PROPERTY_NAME , "8088");
		  ret.setProperty ( UserTransactionServiceFactory.SECURE_HTTP_PROPERTY_NAME , "false");
		  
          return ret;
      }

     
     private static String getDefaultProperty ( String name ) 
     {
    	 return getDefaultProperties().getProperty ( name );
     } 
     
     private static void warnIfEqualsDefaultValue ( String propertyName , Properties p )
     {
    	 String value = getDefaultProperty ( propertyName );
    	if ( value != null && value.equals ( p.getProperty ( propertyName )) ) {
    			Configuration.logWarning ( "Init property " + propertyName + ": using default - you may want to override this..." );
    		
    	}
     }
      
      //private TrmiTransactionManager tm_;
      
      private File lockfile_;
      //sets lock to prevent double startup
      
      private FileOutputStream lockfilestream_ = null;
      private FileLock lock_ = null;

      
      //private TSInitInfo info_;
      //the info object

      private Properties properties_;
      //the properties

      UserTransactionServiceImp ( Properties properties )
      {
    	  
          properties_ = getDefaultProperties();
          Enumeration enumm = properties.propertyNames();

          while ( enumm.hasMoreElements() ) {
               String name = ( String ) enumm.nextElement();
               String property = properties.getProperty ( name );
               properties_.setProperty ( name , property );
          }

          if ( System.getProperty (
                 com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME )
                 != null ) {

             enumm = properties_.propertyNames ();
             Stack names = new Stack ();
             while ( enumm.hasMoreElements () ) {
                 String name = ( String ) enumm.nextElement ();
                 names.push ( name );
             }
             while ( !names.empty () ) {
                 String name = ( String ) names.pop ();
                 if ( System.getProperty ( name ) != null ) {
                     properties_.setProperty ( name , System.getProperty ( name ) );
                 }
             }


         }

      }

      private Properties getProperties()
      {

            return properties_;
      }
      
    


       /**
        *Creates a default TM instance.
        *
        *@param p The properties to use.
        *@return TrmiTransactionManager The default instance.
        *@exception IOException On IO error.
        *@exception FileNotFoundException If the config file could not
        *be found.
        */
        
      private  TrmiTransactionManager createDefault ( Properties p )
      throws IOException, FileNotFoundException
      {
        TrmiTransactionManager ret = null;
        
        warnIfEqualsDefaultValue ( UserTransactionServiceFactory.SOAP_HOST_ADDRESS_PROPERTY_NAME , p );
       
		String consoleDir = getTrimmedProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME , p );
		consoleDir = findOrCreateFolder ( consoleDir );
		String consolePath = consoleDir + getTrimmedProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME, p);

		String limitString = getTrimmedProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_LIMIT_PROPERTY_NAME , p );
		String countString = getTrimmedProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_COUNT_PROPERTY_NAME , p );
		int limit = Integer.parseInt ( limitString );
		int count = Integer.parseInt ( countString );
		RotatingFileConsole console = new RotatingFileConsole ( consolePath , limit , count );
		String logLevel = getTrimmedProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME , p);
		if ( logLevel != null ) logLevel = logLevel.trim();
		int level = Console.WARN;
		if ( "INFO".equals ( logLevel ) ) level = Console.INFO;
		else if ( "DEBUG".equals ( logLevel ) ) level = Console.DEBUG;
		console.setLevel ( level );
		Configuration.addConsole ( console );         
		
		License.setConsole ( console );

		License license = null;
		try {
			license = License.createLicense ( License.PRODUCT_NAME_PROPERTY_NAME );
			license.checkLocalHost ( PRODUCT_NAME );
		} catch ( LicenseException e ) {
			String msg = "ERROR:\nATOMIKOS: NO VALID LICENSE FOUND!\n" +
			"PLEASE CONTACT sales@atomikos.com\n" + 
			"FOR A LICENSE TO USE THIS PRODUCT, OR DOWNGRADE TO\n" +
			"TRANSACTIONSESSENTIALS (WITHOUT PRODUCTION SUPPORT)";
			Configuration.logWarning ( msg , e );
			Stack errors = new Stack();
			errors.push ( e );
			throw new SysException ( 
					msg , errors );

		}
		
		license.filterProductFeatures ( p );
		
        try {
        	//use improved class loading
			ClassLoadingHelper.loadClass ( "org.slf4j.Logger" );
        	Slf4jConsole slf4jConsole = new Slf4jConsole();
        	slf4jConsole.setLevel ( level );
        	Configuration.addConsole ( slf4jConsole );
        } catch (ClassNotFoundException ex) {
        	Configuration.logDebug("cannot load SLF4J, skipping this console", ex);
		}
		
		
		  
          String logname = getTrimmedProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME,p );
          String logdir = getTrimmedProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , p );
          logdir = findOrCreateFolder ( logdir );
          String tmName = getTrimmedProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , p);
          //set system props for trmi transaction manager to use JNDI
          String provider = getTrimmedProperty ( Context.PROVIDER_URL , p );
          String factory = getTrimmedProperty ( Context.INITIAL_CONTEXT_FACTORY , p );
          
          if ( provider == null || provider.equals ( "" ) )
              throw new SysException ( "Context.PROVIDER_URL not set!" );
          if ( factory == null || factory.equals ( "" ) ) 
              throw new SysException ( 
              "Context.INITIAL_CONTEXT_FACTORY not set!" );
          
          //set JNDI parameters as system properties for 
          //TrmiTransactionManager
         
              
          if ( tmName == null || tmName.equals ( "" ) ) {
          	String msg = "For correct recovery, please set the startup property com.atomikos.icatch.tm_unique_name to a globally unique name!";
          	Configuration.logWarning ( msg );
          	tmName = getDefaultName();
          }
              
          String recoveryPrefs = getTrimmedProperty ( AbstractUserTransactionServiceFactory.ENABLE_LOGGING_PROPERTY_NAME , p );
          boolean enableRecovery = true;
          if ( "false".equals ( recoveryPrefs ) )
          	enableRecovery = false;
          
          //make sure that no other instance is running with the same log
          //by setting a lock file
          lockfile_ = new File ( logdir + logname + ".lck" );
          if ( enableRecovery ) {
         	 //ISSUE 10077: don't complain about lock file if no logging
         	try {
         		lockfilestream_ = new FileOutputStream ( lockfile_ );
         		lock_ = lockfilestream_.getChannel().tryLock();
         		lockfile_.deleteOnExit();
         	} catch ( OverlappingFileLockException failedToGetLock ) {
         		//happens on windows
         		lock_ = null;
         	} catch ( IOException failedToGetLock ) {
         		//happens on windows
         		lock_ = null;
         	}
         	if ( lock_ == null ) {
         		 System.err.println ( "ERROR: the specified log seems to be "
                          + "in use already. Make sure that no other instance is "
                          + "running, or kill any pending process if needed." );
                  throw new RuntimeException ( "Log already in use?" );
         	}
         }

          
          int max = 
              ( new Integer ( getTrimmedProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME , p ) ) ).intValue();
          long chckpt = 
              ( new Long ( getTrimmedProperty ( AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME , p ) ) ).longValue();

          FileLogStream logstream = 
              new FileLogStream ( logdir , logname , console );
          StreamObjectLog slog = 
              new StreamObjectLog ( logstream , chckpt , console );
          
         
         StateRecoveryManager recmgr = null;
         if ( enableRecovery )
              recmgr = new StateRecoveryManagerImp ( slog );
         else recmgr = new VolatileStateRecoveryManager();
          
          long timeout = 
              ( new Long ( getTrimmedProperty ( AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME , p ) ) ).longValue();
 

          Boolean trustClient = new Boolean ( getTrimmedProperty ( UserTransactionServiceFactory.TRUST_CLIENT_TM_PROPERTY_NAME , p ) );
          
          String threadedCommitPrefs = getTrimmedProperty ( AbstractUserTransactionServiceFactory.THREADED_2PC_PROPERTY_NAME , p );
          boolean threadedCommit = true;
          if ( "false".equals( threadedCommitPrefs )) threadedCommit = false;
          
          ret = new TrmiTransactionManager ( trustClient.booleanValue() , 
                                                          recmgr , tmName , console , 
                                                          logdir , timeout , max , threadedCommit );
          
          long defaultTimeoutInMillis = (new Long ( getTrimmedProperty (
                  AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME, p ) )).longValue ();
          int defaultTimeout = 0;

          defaultTimeout = (int) defaultTimeoutInMillis/1000;
          if ( defaultTimeout <= 0 ) {
          	Configuration.logWarning ( "WARNING: " + AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME + " should be more than 1000 milliseconds - resetting to 10000 milliseconds instead..." );
          	defaultTimeout = 10;
          }

          
          TransactionManagerImp.setDefaultTimeout(defaultTimeout);

		
          //set default serial mode for JTA txs.
          if ( new Boolean ( getTrimmedProperty ( 
                                    AbstractUserTransactionServiceFactory.SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME , p )
                                    ).booleanValue() )
                  TransactionManagerImp.setDefaultSerial ( true );

          return ret;
      }
      
      
   
      public void init ( TSInitInfo info )
      throws SysException
      {
      	
          Properties p = info.getProperties();
          TrmiTransactionManager tm = null;
          
          
          try {
              
			  //add default JCA log administrator
			  Configuration.addLogAdministrator ( XidLogAdministrator.getInstance() );              
              registerTSListener ( new TransportTSListener() );
              
              tm = createDefault ( p );
              tm.init ( p );
              String autoMode = getTrimmedProperty ( AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME , p );
              if ( autoMode != null ) autoMode = autoMode.trim();
              boolean autoRegister = "true".equals ( 
              	autoMode );
              Configuration.installCompositeTransactionManager ( tm );
              com.atomikos.icatch.jta.TransactionManagerImp.
                  installTransactionManager ( tm , autoRegister );
              Configuration.installExportingTransactionManager ( tm );
              Configuration.installImportingTransactionManager ( tm );
              Configuration.installRecoveryService ( tm.getTransactionService() );
              Configuration.installLogControl ( tm.getTransactionService().getLogControl() );
              Configuration.installTransactionService ( tm.getTransactionService() );
              
              Enumeration admins = info.getLogAdministrators();
              while ( admins.hasMoreElements() ) {
                  LogAdministrator admin = 
                      ( LogAdministrator ) admins.nextElement();
                  if ( admin instanceof LocalLogAdministrator ) {
                      LocalLogAdministrator ladmin =
                        (LocalLogAdministrator ) admin;
                      ladmin.init ( this );
                  }
                  
              }
              
              
              
              //lastly, set up UserTransactionServer if client demarcation 
              //is allowed
              boolean clientDemarcation = 
                  new Boolean ( getTrimmedProperty ( AbstractUserTransactionServiceFactory.CLIENT_DEMARCATION_PROPERTY_NAME , p ) ).
                  booleanValue();
                  
             
                  
              if ( clientDemarcation ) {
                  //get the local host name, needed for the user tx
                  String name = getTrimmedProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME , p );
                  String url = getTrimmedProperty ( Context.PROVIDER_URL , p );
                   if ( url == null ) {
                      throw new SysException ( "Property not set: " + 
                      Context.PROVIDER_URL );
                  }
                  
                  String factory = getTrimmedProperty ( Context.INITIAL_CONTEXT_FACTORY , p );
                  if ( name == null || name.equals ( "" ) )
                      throw new SysException ( "Property not set: com.atomikos.icatch.tm_unique_name" );
                  UserTransactionServerImp utxs = 
                      UserTransactionServerImp.getSingleton();
                  utxs.init ( name , p );
                 
              }
             
				
			  //supercall will add resources
			  //and recover each one	
			  super.init ( info );
          }
          catch ( Exception e ) {
          	  //e.printStackTrace();
          	  Configuration.logWarning ( "Error in init(): " + e.getMessage() , e );
              Stack errors = new Stack();
              errors.push ( e );
              throw new SysException ( "Error in init(): " + e.getMessage()  , errors ); 
          }
      }
      
      public void shutdown ( boolean force ) 
      throws IllegalStateException
      { 
      	  TrmiTransactionManager tm = ( TrmiTransactionManager )
      	  	Configuration.getCompositeTransactionManager();
          if ( tm == null ) 
              return;
 
            tm.shutdown ( force );
            tm = null;
            
           
            
            try {
            	if ( lock_ != null ) {
            		lock_.release();
            		//lock_.channel().close();
            	}
            	if ( lockfilestream_ != null ) lockfilestream_.close();
            } catch (IOException e) {
            	// error release lock or shutting down channel
            	System.err.println ( "Error releasing file lock: "
            			+ e.getMessage());
            } finally {
            	lock_ = null;
            }
			
            if ( lockfile_ != null ) {
                lockfile_.delete ();
                lockfile_ = null;
            }

           
            //super call to shut down resources
            super.shutdown ( force );
      }
      
       /**
        *@see TSMetaData
        */
        
      public TSMetaData getTSMetaData()
      {
           
           return new TSMetaDataImp ( JTA.version , 
              VERSION , PRODUCT_NAME , 
              true , true );
      }
      
      /**
       *@see UserTransactionService
       */
       
       public UserTransaction getUserTransaction()
       {
           UserTransaction ret = null;
           ret = UserTransactionServerImp.getSingleton().getUserTransaction();
           if ( ret == null ) {
           		//not exported
           		ret = new J2eeUserTransaction();
           }
           
           return ret;
       }

       public TSInitInfo createTSInitInfo()
      {
           TSInitInfoImp ret = new TSInitInfoImp();
           ret.setProperties ( getDefaultProperties() );
           return ret;
      }

}
