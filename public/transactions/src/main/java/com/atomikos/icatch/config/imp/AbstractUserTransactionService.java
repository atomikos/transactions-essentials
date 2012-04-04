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

package com.atomikos.icatch.config.imp;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Properties;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TSListener;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.IOHelper;
import static com.atomikos.util.Atomikos.VERSION;
/**
 *
 *
 * The user's (client program) view of the transaction manager's configuration,
 * a compact facade with all the information the client program needs. This base
 * class should be overridden for each module (CORBA, trmi,...).
 *
 */

public abstract class AbstractUserTransactionService implements
        UserTransactionService
{
	private static final Logger LOGGER = LoggerFactory.createLogger(AbstractUserTransactionService.class);


	private static void echoProperties ( Properties properties )
    {
		if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( "USING core version: " + VERSION );
        if ( properties != null ) {
            Enumeration names = properties.propertyNames ();
            while ( names.hasMoreElements () ) {
                String name = (String) names.nextElement ();
                String value = properties.getProperty ( name );
                //log as INFO - see case 24689
                if ( LOGGER.isInfoEnabled() ) LOGGER.logInfo ( "USING " + name + " = " + value );
            }
        }
    }

    /**
     * Utility method to get and trim properties.
     *
     * @param name
     *            The name of the property to get.
     * @param p
     *            The properties to look in.
     * @return String The property without leading or trailing spaces, or null
     *         if not found.
     */
    public static String getTrimmedProperty ( String name , Properties p )
    {
        String ret = null;
        ret = p.getProperty ( name );
        if ( ret != null )
            ret = ret.trim ();
        return ret;
    }

    /**
     * Utility method to get a default TM name.
     *
     * @return A default TM name based on the local IP.
     */
    protected static String getDefaultName ()
    {

        String ret = "tm";
        try {
            ret = java.net.InetAddress.getLocalHost ().getHostAddress ()
                    + ".tm";
        } catch ( UnknownHostException e ) {
            // ignore: use short default
        }

        return ret;
    }

    /**
     * Utility method to get the local host address.
     *
     * @return
     */
    protected static String getHostAddress ()
    {
        String ret = "localhost";
        try {
            ret = java.net.InetAddress.getLocalHost ().getHostAddress ();
        } catch ( UnknownHostException e ) {
            // ignore: use short default
        }

        return ret;
    }

    /**
     * Utility method to find or create a given folder
     *
     * @param path
     *            The folder path.
     * @return String The resulting file path, or a default if the given path is
     *         not valid as a folder.
     */

    protected static String findOrCreateFolder ( String path )
    {
        File ret = new File ( "." );
        if ( path != null ) {
            File tmp = new File ( path );
            if ( tmp.exists () ) {
                if ( tmp.isDirectory () ) {
                    ret = tmp;
                } else {
                    // if exists but not a directory: use default
                    String msg = path + " is not a directory - using default";
                    System.err.println ( msg );
                }
            } else {
                // file does not exist; attempt to create
                String msg = path + " could not be created - using default";
                try {
                    if ( IOHelper.createPathTo ( tmp, true ) ) {
                        ret = tmp;
                    } else {
                        System.err.println ( msg );
                    }
                } catch ( IOException e ) {
                    System.err.println ( msg );
                }
            }
        }

        String result = ret.getAbsolutePath ();
        if ( !result.endsWith ( File.separator ) )
            result = result + File.separator;
        return result;
    }

    private TSInitInfo info_;
    // the info object, needed at shutdown to remove resources
    // so that re-init calls work fine.

    /**
     * @see UserTransactionService
     */

    public void shutdown ( boolean force ) throws IllegalStateException
    {
        boolean errors = false;

        //remove shutdown hooks to avoid case 21519
        Configuration.removeShutdownHooks();

        // Ask the Configuration for all current resources, since
        // newly added ones are not known at init.
        Enumeration resources = Configuration.getResources ();
        while ( resources.hasMoreElements () ) {
            RecoverableResource res = (RecoverableResource) resources
                    .nextElement ();

            Configuration.removeResource ( res.getName () );
            try {
                res.close ();
            } catch ( ResourceException re ) {

         	   //Issue 10038:
         	   //Ignore errors in force mode: force is most likely
         	   //during VM exit; in that case interleaving of shutdown hooks
         	   //means that resource connectors may have closed already
         	   //by the time the TM hook runs. We don't want useless
         	   //reports in that case.
         	   //NOTE: any invalid states will be detected during the next
         	   //(re)init so they can be ignored here (if force mode)

                if ( !force ) {
                   //log to System.err because console file
             	   //is closed already!!!
             	   String msg = "WARNING: error closing resource: " +
             	   			re.getMessage ();
                	   System.err.println ( msg );
                	   re.printStackTrace();
                }

                errors = true;
            }

        }
        Enumeration logAdmins = Configuration.getLogAdministrators ();
        while ( logAdmins.hasMoreElements () ) {
            LogAdministrator admin = (LogAdministrator) logAdmins
                    .nextElement ();
            Configuration.removeLogAdministrator ( admin );
        }

        // remove TM handles to enable restarts
        Configuration.installCompositeTransactionManager ( null );
        Configuration.installExportingTransactionManager ( null );
        Configuration.installImportingTransactionManager ( null );
        Configuration.installRecoveryService ( null );
        Configuration.installTransactionService ( null );
        Configuration.installLogControl ( null );

        if ( errors && ! force ) {
        	   //Issue 10038:
        	   //Ignore errors in force mode: force is most likely
        	   //during VM exit; in that case interleaving of shutdown hooks
        	   //means that resource connectors may have closed already
        	   //by the time the TM hook runs. We don't want useless
        	   //exceptions in that case.
        	   //NOTE: any invalid states will be detected during the next
        	   //(re)init so they can be ignored here
            throw new RuntimeException ( "Error(s) during shutdown." );
        }
    }

    /**
     * @see UserTransactionService
     */

    public void init ( TSInitInfo info ) throws SysException
    {
        info_ = info;

        // NOTE: adding resources is no longer done here,
        // since they are directly added to the Configuration.
        // Also, in 2.0 useWeakCompare has been deprecated,
        // so we don't have to check for duplicate vendor
        // additions like before.

        // ADDED IN 2.0: if one or more resources were added before init,
        // then assume that dynamic registration is NOT being used.
        // In that case, also add an AcceptAllXATransactionalResource
        // to avoid that users have to use weakCompare modes (which
        // are restricted to one instance per vendor class).
        // NOTE: to avoid lack of recovery, this should only
        // be done if automatic resource registration is NOT set
        // Also note: this is done ONLY if resources are
        // added explicitly, to ensure that the acceptAll is LAST
        // otherwise custom XidFactory settings would be impossible.

        echoProperties ( info.getProperties () );


        String hookAsString = getTrimmedProperty ( AbstractUserTransactionServiceFactory.REGISTER_SHUTDOWN_HOOK_PROPERTY_NAME , info.getProperties() );
        boolean register = false;
        if ( hookAsString != null ) register = "true".equals ( hookAsString.toLowerCase() );

        // ADDED IN 2.0 for automatic initialization: shutdown hook needed
        if ( register ) Configuration.addShutdownHook ( new ShutdownHook ( this ) );

    }

    /**
     * @see UserTransactionService
     */

    public CompositeTransactionManager getCompositeTransactionManager ()
    {
        return Configuration.getCompositeTransactionManager ();
    }



    /**
     * @see UserTransactionService
     */

    public ImportingTransactionManager getImportingTransactionManager ()
    {
        return Configuration.getImportingTransactionManager ();
    }

    /**
     * @see UserTransactionService
     */

    public ExportingTransactionManager getExportingTransactionManager ()
    {
        return Configuration.getExportingTransactionManager ();
    }

    public void registerResource ( RecoverableResource res )
    {
        Configuration.addResource ( res );
    }

    public void registerLogAdministrator ( LogAdministrator admin )
    {
        Configuration.addLogAdministrator ( admin );
    }

    public Enumeration getResources ()
    {
        return Configuration.getResources ();
    }

    public Enumeration getLogAdministrators ()
    {
        return Configuration.getLogAdministrators ();
    }

	public void removeResource ( RecoverableResource res )
	{
		if ( res == null ) throw new IllegalArgumentException ( "Null not allowed" );
		Configuration.removeResource ( res.getName() );

	}

	public void removeLogAdministrator ( LogAdministrator admin )
	{
		if ( admin == null ) throw new IllegalArgumentException ( "Null not allowed" );
		Configuration.removeLogAdministrator ( admin );
	}

	public void registerTSListener ( TSListener listener )
	{
		if ( listener == null ) throw new IllegalArgumentException ( "Null not allowed");
		Configuration.addTSListener ( listener );
	}

	public void removeTSListener ( TSListener listener )
	{
		if ( listener == null ) throw new IllegalArgumentException ( "Null not allowed");
		Configuration.removeTSListener ( listener );
	}


	public void init ( Properties properties ) throws SysException {
		TSInitInfo info = createTSInitInfo();
		info.setProperties(properties);
		init ( info );
	}

    //
    //
    // NESTED SHUTDOWNHOOK CLASS
    //
    //

    private static class ShutdownHook extends Thread
    {
        private UserTransactionService uts_;

        private ShutdownHook ( UserTransactionService uts )
        {
            super ();
            uts_ = uts;
        }

        public void run ()
        {
            // this code executes during VM shutdown
            // so force the uts to shutdown too
            uts_.shutdown ( true );

        }
    }


}
