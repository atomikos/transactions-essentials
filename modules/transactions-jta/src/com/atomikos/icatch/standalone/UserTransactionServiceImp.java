package com.atomikos.icatch.standalone;

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
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.imp.LocalLogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.TSMetaData;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.config.imp.TSInitInfoImp;
import com.atomikos.icatch.config.imp.TSMetaDataImp;
import com.atomikos.icatch.imp.BaseTransactionManager;
import com.atomikos.icatch.imp.thread.TaskManager;
import com.atomikos.icatch.jta.AbstractJtaUserTransactionService;
import com.atomikos.icatch.jta.J2eeUserTransaction;
import com.atomikos.icatch.jta.JTA;
import com.atomikos.icatch.jta.TransactionManagerImp;
import com.atomikos.icatch.jta.UserTransactionServerImp;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.imp.FileLogStream;
import com.atomikos.persistence.imp.StateRecoveryManagerImp;
import com.atomikos.persistence.imp.StreamObjectLog;
import com.atomikos.persistence.imp.VolatileStateRecoveryManager;

/**
 * 
 * 
 * A standalone implementation of the UserTransactionManager interface.
 */

class UserTransactionServiceImp extends AbstractJtaUserTransactionService
{
    private static final String PRODUCT_NAME = "TransactionsEssentials";
    // the product name as it should be in the license.


    private static final String VERSION = Configuration.VERSION;
    // the current release number to be checked in license

    // private StandAloneTransactionManager tm_ = null;
    // the TM to use

    private File lockfile_ = null;
    // the lockfile to guard against double startup
	
	private FileOutputStream lockfilestream_ = null;
    private FileLock lock_ = null;

    private TSInitInfo info_ = null;
    // the info object that was used

    // private UserTransactionServerImp utxs_ = null;
    // not null if client demarcation allowed

    // REMOVED IN 2.0
    // private UserTransaction utx_ = null;
    // the user tx

    private Properties properties_;

   

    UserTransactionServiceImp ( Properties properties )
    {

        properties_ = getDefaultProperties ();
        Enumeration enumm = properties.propertyNames ();
        while ( enumm.hasMoreElements () ) {
            String name = (String) enumm.nextElement ();
            String property = properties.getProperty ( name );
            properties_.setProperty ( name, property );
        }

        if ( System
                .getProperty ( com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME ) != null ) {

            enumm = properties_.propertyNames ();
            Stack names = new Stack ();
            while ( enumm.hasMoreElements () ) {
                String name = (String) enumm.nextElement ();
                names.push ( name );
            }
            while ( !names.empty () ) {
                String name = (String) names.pop ();
                if ( System.getProperty ( name ) != null ) {
                    properties_
                            .setProperty ( name, System.getProperty ( name ) );
                }
            }

        }
    }

    /**
     * Get the properties supplied at init time.
     * 
     * @return Properties The properties.
     */

    private Properties getProperties ()
    {
        return properties_;

    }

    /**
     * Creates a default TM instance.
     * 
     * @param p
     *            The properties to use.
     * @return StandAloneTransactionManager The default instance.
     * @exception IOException
     *                On IO error.
     * @exception FileNotFoundException
     *                If the config file could not be found.
     */

    private StandAloneTransactionManager createDefault ( Properties p )
            throws IOException, FileNotFoundException
    {
        StandAloneTransactionManager ret = null;

        String consoleDir = AbstractUserTransactionService.getTrimmedProperty (
                AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME, p );
        consoleDir = findOrCreateFolder ( consoleDir );
        // System.err.println ( "ConsoleDir found to be: " + consoleDir );
        String consolePath = consoleDir
                + getTrimmedProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME,
                        p );
        // OutputStream out = new FileOutputStream ( consolePath , true );
        // PrintStream ps = new PrintStream ( out );
        // PrintStreamConsole console = new PrintStreamConsole ( ps );

        String limitString = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.CONSOLE_FILE_LIMIT_PROPERTY_NAME, p );
        String countString = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.CONSOLE_FILE_COUNT_PROPERTY_NAME, p );
        int limit = Integer.parseInt ( limitString );
        int count = Integer.parseInt ( countString );
        RotatingFileConsole console = new RotatingFileConsole ( consolePath,
                limit, count );

        String logLevel = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME, p );
        int level = Console.WARN;
        if ( "INFO".equalsIgnoreCase ( logLevel ) )
            level = Console.INFO;
        else if ( "DEBUG".equalsIgnoreCase ( logLevel ) )
            level = Console.DEBUG;
        console.setLevel ( level );

        Configuration.addConsole ( console );
        
        try {
			Class.forName("org.slf4j.Logger");
        	Slf4jConsole slf4jConsole = new Slf4jConsole();
        	slf4jConsole.setLevel ( level );
        	Configuration.addConsole ( slf4jConsole );
        } catch (ClassNotFoundException ex) {
        	Configuration.logDebug("cannot load SLF4J, skipping this console", ex);
		}


        String logname = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME, p );
        String logdir = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME, p );
        logdir = findOrCreateFolder ( logdir );
        
        String recoveryPrefs = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.ENABLE_LOGGING_PROPERTY_NAME, p );
        boolean enableRecovery = true;
        if ( "false".equals ( recoveryPrefs ) )
            enableRecovery = false;
        
        String threadedCommitPrefs = getTrimmedProperty ( AbstractUserTransactionServiceFactory.THREADED_2PC_PROPERTY_NAME , p );
        boolean threadedCommit = true;
        if ( "false".equals( threadedCommitPrefs )) threadedCommit = false;

        // make sure that no other instance is running with the same log
        // by setting a lock file
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

        int max = (new Integer ( getTrimmedProperty (
                AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME, p ) )).intValue ();
        long chckpt = (new Long ( getTrimmedProperty (
                AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME, p ) )).longValue ();
        FileLogStream logstream = new FileLogStream ( logdir, logname, console );
        StreamObjectLog slog = new StreamObjectLog ( logstream, chckpt, console );
        
        StateRecoveryManager recmgr = null;
        if ( enableRecovery )
            recmgr = new StateRecoveryManagerImp ( slog );
        else
            recmgr = new VolatileStateRecoveryManager ();

        long maxTimeout = (new Long ( getTrimmedProperty (
                AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME, p ) )).longValue ();

        long defaultTimeoutInMillis = (new Long ( getTrimmedProperty (
                AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME, p ) )).longValue ();
        int defaultTimeout = 0;

        defaultTimeout = (int) defaultTimeoutInMillis/1000;
        if ( defaultTimeout <= 0 ) {
        	Configuration.logWarning ( "WARNING: " + AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME + " should be more than 1000 milliseconds - resetting to 10000 milliseconds instead..." );
        	defaultTimeout = 10;
        }

        
        TransactionManagerImp.setDefaultTimeout(defaultTimeout);


        String name = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME, p );
        if ( name == null || name.equals ( "" ) )
            throw new SysException (
                    "Property not set: com.atomikos.icatch.tm_unique_name" );
        ret = new StandAloneTransactionManager ( name, recmgr, console,
                logdir, maxTimeout, max, !threadedCommit );
        
        // set default serial mode for JTA txs.
        if ( new Boolean ( getTrimmedProperty (
                UserTransactionServiceFactory.SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME, p ) )
                .booleanValue () )
            TransactionManagerImp.setDefaultSerial ( true );

        return ret;
    }

    public void init ( TSInitInfo info ) throws SysException
    {
        // SUPERCALL MOVED TO END IN NEW RECOVERY!!!
        // super.init ( info );
        info_ = info;
        Properties p = info.getProperties ();
        StandAloneTransactionManager tm = null;
        try {

            tm = createDefault ( p );
            tm.init ( info.getProperties() );
            // install composite manager
            Configuration.installCompositeTransactionManager ( tm );
            Configuration.installRecoveryService ( tm.getTransactionService () );
            Configuration
                    .installImportingTransactionManager ( new ImportingTransactionManagerImp (
                            tm.getTransactionService () ) );
            Configuration
                    .installExportingTransactionManager ( new ExportingTransactionManagerImp () );
            Configuration.installLogControl ( tm.getTransactionService ()
                    .getLogControl () );
            Configuration.installTransactionService ( tm
                    .getTransactionService () );

            String autoMode = getTrimmedProperty (
                    AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME, p );
            if ( autoMode != null )
                autoMode = autoMode.trim ();
            boolean autoRegister = "true".equals ( autoMode );
            com.atomikos.icatch.jta.TransactionManagerImp
                    .installTransactionManager ( tm, autoRegister );
            Enumeration admins = info_.getLogAdministrators ();
            while ( admins.hasMoreElements () ) {
                LogAdministrator admin = (LogAdministrator) admins
                        .nextElement ();
                if ( admin instanceof LocalLogAdministrator ) {
                    LocalLogAdministrator ladmin = (LocalLogAdministrator) admin;
                    ladmin.init ( this );
                }
               

            }

            // lastly, set up UserTransactionServer if client demarcation
            // is allowed
            boolean clientDemarcation = new Boolean ( getTrimmedProperty (
                    AbstractUserTransactionServiceFactory.CLIENT_DEMARCATION_PROPERTY_NAME, p ) )
                    .booleanValue ();

            if ( clientDemarcation ) {

                String name = getTrimmedProperty (
                        AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME, p );
                if ( name == null || name.equals ( "" ) )
                    throw new SysException (
                            "Property not set: com.atomikos.icatch.tm_unique_name" );
                UserTransactionServerImp utxs = UserTransactionServerImp
                        .getSingleton ();
                utxs.init ( name, p );

                String factory = getTrimmedProperty (
                        Context.INITIAL_CONTEXT_FACTORY, p );
                String url = getTrimmedProperty ( Context.PROVIDER_URL, p );
                if ( url == null || url.equals ( "" ) ) {
                    throw new SysException ( "Property not set: "
                            + Context.PROVIDER_URL );
                }

            }

            // this will add all resources and recover them
            super.init ( info );

        } catch ( Exception e ) {
            e.printStackTrace ();
            Stack errors = new Stack ();
            errors.push ( e );
            throw (SysException) new SysException ( "Error in init(): " + e.getMessage (),
                    errors ).initCause(e);
        }
    }

    public void shutdown ( boolean force ) throws IllegalStateException
    {
        BaseTransactionManager tm = (BaseTransactionManager) Configuration
                .getCompositeTransactionManager ();
        if ( tm == null )
            return;

       

        try {
            tm.shutdown ( force );
            tm = null;
            // shutdown system executors
            TaskManager exec = TaskManager.getInstance();
            if ( exec != null ) {
            		exec.shutdown();
            }
            // delegate to superclass to ensure resources are delisted.
            super.shutdown ( force );
			
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

            info_ = null;

        } catch ( IllegalStateException il ) {
            // happens if active txs and not force
            throw il;
        }
    }

    /**
     * @see UserTransactionService
     */

    public ImportingTransactionManager getImportingTransactionManager ()
    {
        // make sure that the SubTxThread instance is NOT returned.
        return null;
    }

    /**
     * @see UserTransactionService
     */

    public ExportingTransactionManager getExportingTransactionManager ()
    {
        // make sure that the SubTxThread instance is NOT returned.

        return null;
    }

    /**
     * @see UserTransactionService
     */

    public TSInitInfo createTSInitInfo ()
    {

        TSInitInfoImp ret = new TSInitInfoImp ();
        ret.setProperties ( getProperties () );
        return ret;

    }

    /**
     * @see UserTransactionService
     */

    public TSMetaData getTSMetaData ()
    {

        
        return new TSMetaDataImp ( JTA.version, VERSION
                , PRODUCT_NAME, false, false );
    }

    /**
     * @see UserTransactionService
     */

    public UserTransaction getUserTransaction ()
    {
        UserTransaction ret = null;
        ret = UserTransactionServerImp.getSingleton ().getUserTransaction ();
        if ( ret == null ) {
            // not exported
            ret = new J2eeUserTransaction ();
        }

        return ret;
    }

    public static Properties getDefaultProperties ()
    {
        Properties ret = new Properties ();
        ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_NAME_PROPERTY_NAME, "tm.out" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME, "."
                + File.separator );
        ret.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME, "."
                + File.separator );
        ret.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME, "tmlog" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME, "50" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME, "300000" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.DEFAULT_JTA_TIMEOUT_PROPERTY_NAME, "10000" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME, "500" );
        ret
                .setProperty ( UserTransactionServiceFactory.SERIAL_JTA_TRANSACTIONS_PROPERTY_NAME,
                        "true" );
        // add unique tm name for remote usertx support
        ret.setProperty ( AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME,
                UserTransactionServiceFactory.getDefaultName () );
        // indication of whether client tx demarcation is allowed
        ret.setProperty ( AbstractUserTransactionServiceFactory.CLIENT_DEMARCATION_PROPERTY_NAME, "false" );
        ret.setProperty ( Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.rmi.registry.RegistryContextFactory" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_LOG_LEVEL_PROPERTY_NAME, "WARN" );
        ret.setProperty ( Context.PROVIDER_URL, "rmi://localhost:1099" );

        // ADDED IN 2.0: automatic registration
        ret.setProperty (
                AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME, "true" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.ENABLE_LOGGING_PROPERTY_NAME, "true" );

        ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_LIMIT_PROPERTY_NAME, "-1" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.CONSOLE_FILE_COUNT_PROPERTY_NAME, "1" );


        ret.setProperty ( AbstractUserTransactionServiceFactory.THREADED_2PC_PROPERTY_NAME , "true" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.REGISTER_SHUTDOWN_HOOK_PROPERTY_NAME , "false" );
        return ret;
    }

}
