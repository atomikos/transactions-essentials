/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch.standalone;

import static com.atomikos.util.Atomikos.VERSION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;

import javax.naming.Context;

import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.imp.LocalLogAdministrator;
import com.atomikos.icatch.config.Configuration;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.TSMetaData;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.config.imp.TSInitInfoImp;
import com.atomikos.icatch.config.imp.TSMetaDataImp;
import com.atomikos.icatch.imp.CompositeTransactionManagerImp;
import com.atomikos.icatch.imp.thread.TaskManager;
import com.atomikos.icatch.jta.AbstractJtaUserTransactionService;
import com.atomikos.icatch.jta.JTA;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.imp.StateRecoveryManagerImp;
import com.atomikos.persistence.imp.VolatileStateRecoveryManager;

/**
 *
 *
 * A standalone implementation of the UserTransactionManager interface.
 */

class UserTransactionServiceImp extends AbstractJtaUserTransactionService
{
	private static final Logger LOGGER = LoggerFactory.createLogger(UserTransactionServiceImp.class);

    private static final String PRODUCT_NAME = "TransactionsEssentials";
    // the product name as it should be in the license.

    private TSInitInfo info_ = null;
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

        
        StateRecoveryManager recmgr = null;
        if ( enableRecovery ) {
        	recmgr = new StateRecoveryManagerImp();
        } else {
        	recmgr = new VolatileStateRecoveryManager();
        }

        int max = Integer.valueOf( getTrimmedProperty (
                AbstractUserTransactionServiceFactory.MAX_ACTIVES_PROPERTY_NAME, p ) );

        long maxTimeout = Long.valueOf( getTrimmedProperty (
                AbstractUserTransactionServiceFactory.MAX_TIMEOUT_PROPERTY_NAME, p ) );

        String name = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.TM_UNIQUE_NAME_PROPERTY_NAME, p );
        if ( name == null || name.equals ( "" ) )
            throw new SysException (
                    "Property not set: com.atomikos.icatch.tm_unique_name" );
        ret = new StandAloneTransactionManager ( name, recmgr,
                logdir, maxTimeout, max, !threadedCommit );

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

      
            // this will add all resources and recover them
            super.init ( info );

        } catch ( Exception e ) {
            e.printStackTrace ();
            Stack errors = new Stack ();
            errors.push ( e );
            throw (SysException) new SysException ( "Error in init(): " + e.getMessage (),
                    e ).initCause(e);
        }
    }

    public void shutdown ( boolean force ) throws IllegalStateException
    {
        CompositeTransactionManagerImp tm = (CompositeTransactionManagerImp) Configuration.
                getCompositeTransactionManager ();
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


        return new TSMetaDataImp ( JTA.version, VERSION, PRODUCT_NAME, false, false );
    }

 

    public static Properties getDefaultProperties ()
    {
        Properties ret = new Properties ();
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
        ret.setProperty ( Context.PROVIDER_URL, "rmi://localhost:1099" );

        // ADDED IN 2.0: automatic registration
        ret.setProperty (
                AbstractUserTransactionServiceFactory.AUTOMATIC_RESOURCE_REGISTRATION_PROPERTY_NAME, "true" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.ENABLE_LOGGING_PROPERTY_NAME, "true" );



        ret.setProperty ( AbstractUserTransactionServiceFactory.THREADED_2PC_PROPERTY_NAME , "false" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.REGISTER_SHUTDOWN_HOOK_PROPERTY_NAME , "false" );
        ret.setProperty ( AbstractUserTransactionServiceFactory.SERIALIZABLE_LOGGING_PROPERTY_NAME , "true" );
        return ret;
    }

}
