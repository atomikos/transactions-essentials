//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//$Log: AbstractUserTransactionService.java,v $
//Revision 1.3  2006/09/22 11:53:26  guy
//ADDED 1003
//
//Revision 1.2  2006/09/15 08:39:16  guy
//Merged-in changes from 3.0.1 release.
//
//Revision 1.1.1.1.2.1  2006/09/14 07:09:02  guy
//FIXED 10038
//
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.3  2006/04/11 15:05:28  guy
//Added removal methods and TSListener methods.
//
//Revision 1.2  2006/04/11 11:42:25  guy
//Extracted init properties as constants and replaced all literal references.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:09  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.23  2005/08/23 13:06:34  guy
//Updated SOAP init parameters.
//Moved CommitProtocol to msg package.
//
//Revision 1.22  2005/08/11 12:12:33  guy
//Debugged.
//
//Revision 1.21  2005/08/10 16:23:03  guy
//Debugged/adapted for compensation and dito testing.
//
//Revision 1.20  2005/08/06 07:37:17  guy
//Updated to include installTransactionService call.
//
//Revision 1.19  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.18  2005/02/19 14:05:50  guy
//Added echoing of init properties in tm.out
//
//Revision 1.17  2004/10/31 11:03:45  guy
//Added default name to TRMI.
//
//Revision 1.16  2004/10/27 09:40:23  guy
//Updated init to create output and log directories if required.
//
//Revision 1.15  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.14  2004/09/28 11:26:13  guy
//Updated startup/shutdown processing to make it independent of the UTS
//instance on which it is called. Moved register methods to UserTransactionService
//instead of TSInitInfo.
//
//Revision 1.13  2004/09/27 12:39:19  guy
//Removed deprecated code fragments.
//
//Revision 1.12  2004/09/21 10:10:44  guy
//Added AcceptAllXATransactionalResource registration if
//explicit registration is done.
//
//Revision 1.11  2004/09/20 14:49:19  guy
//Configuration resources are retrieved in order added.
//Added tests for new recovery.
//Added shutdown code in usertxservice.
//
//Revision 1.10  2004/09/18 11:41:32  guy
//Added shutdownhook for shutdown ( needed with new auto-init feature in 2.0 )
//
//Revision 1.9  2004/09/06 09:26:37  guy
//Redesigned recovery: can now be done at any time.
//Resources can now be added after init() and will be
//recovered immediately rather than on the next restart.
//
//Revision 1.8  2004/09/01 13:39:01  guy
//Merged changes from TransactionsRMI 1.22.
//Corrected bug in SysException.printStackTrace.
//Added log method to Configuration.
//
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//Revision 1.7  2004/08/30 07:16:23  guy
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//Added admin tx state: active.
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//Revision 1.6  2003/03/11 06:38:53  guy
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//$Log: AbstractUserTransactionService.java,v $
//Revision 1.3  2006/09/22 11:53:26  guy
//ADDED 1003
//
//Revision 1.2  2006/09/15 08:39:16  guy
//Merged-in changes from 3.0.1 release.
//
//Revision 1.1.1.1.2.1  2006/09/14 07:09:02  guy
//FIXED 10038
//
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.3  2006/04/11 15:05:28  guy
//Added removal methods and TSListener methods.
//
//Revision 1.2  2006/04/11 11:42:25  guy
//Extracted init properties as constants and replaced all literal references.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:09  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:16  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.23  2005/08/23 13:06:34  guy
//Updated SOAP init parameters.
//Moved CommitProtocol to msg package.
//
//Revision 1.22  2005/08/11 12:12:33  guy
//Debugged.
//
//Revision 1.21  2005/08/10 16:23:03  guy
//Debugged/adapted for compensation and dito testing.
//
//Revision 1.20  2005/08/06 07:37:17  guy
//Updated to include installTransactionService call.
//
//Revision 1.19  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.18  2005/02/19 14:05:50  guy
//Added echoing of init properties in tm.out
//
//Revision 1.17  2004/10/31 11:03:45  guy
//Added default name to TRMI.
//
//Revision 1.16  2004/10/27 09:40:23  guy
//Updated init to create output and log directories if required.
//
//Revision 1.15  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.14  2004/09/28 11:26:13  guy
//Updated startup/shutdown processing to make it independent of the UTS
//instance on which it is called. Moved register methods to UserTransactionService
//instead of TSInitInfo.
//
//Revision 1.13  2004/09/27 12:39:19  guy
//Removed deprecated code fragments.
//
//Revision 1.12  2004/09/21 10:10:44  guy
//Added AcceptAllXATransactionalResource registration if
//explicit registration is done.
//
//Revision 1.11  2004/09/20 14:49:19  guy
//Configuration resources are retrieved in order added.
//Added tests for new recovery.
//Added shutdown code in usertxservice.
//
//Revision 1.10  2004/09/18 11:41:32  guy
//Added shutdownhook for shutdown ( needed with new auto-init feature in 2.0 )
//
//Revision 1.9  2004/09/06 09:26:37  guy
//Redesigned recovery: can now be done at any time.
//Resources can now be added after init() and will be
//recovered immediately rather than on the next restart.
//
//Revision 1.8  2004/09/01 13:39:01  guy
//Merged changes from TransactionsRMI 1.22.
//Corrected bug in SysException.printStackTrace.
//Added log method to Configuration.
//
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//Revision 1.6.4.1  2004/04/30 14:33:00  guy
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//Included different log levels, and added immediate rollback for extent
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//participants.
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//Revision 1.6  2003/03/11 06:38:53  guy
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: AbstractUserTransactionService.java,v 1.3 2006/09/22 11:53:26 guy Exp $
//
//Revision 1.5.4.2  2002/09/18 13:39:09  guy
//Added check for at most one weakly comparing resource of the same vendor.
//
//Revision 1.5.4.1  2002/08/29 07:21:36  guy
//Added support for XAResource timeout, and corrected heuristic exception
//logic.
//
//Revision 1.5  2002/01/08 15:21:26  guy
//Updated to new LogAdministrator paradigm.
//
//Revision 1.4  2002/01/07 12:25:33  guy
//Updated AbstractUserTransactionService to shutdown resources as well,
//and make it resilient to multiple init/shutdown calls.
//
//Revision 1.3  2001/12/30 13:41:26  guy
//Updated to have XA transparency: no XA references needed in interface.
//
//Revision 1.2  2001/12/30 10:32:09  guy
//Simplified the initialization by abstraction into a TSInitInfo intf/object.
//

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
import com.atomikos.util.IOHelper;

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

	 
	private static void echoProperties ( Properties properties )
    {
		Configuration.logInfo ( "USING core version: " + Configuration.VERSION );
        if ( properties != null ) {
            Enumeration names = properties.propertyNames ();
            while ( names.hasMoreElements () ) {
                String name = (String) names.nextElement ();
                String value = properties.getProperty ( name );
                //log as INFO - see case 24689
                Configuration.logInfo ( "USING " + name + " = " + value );
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
        // System.err.println ( "getTrimmedProperty ( " + name + " ) returning:
        // " + ret );
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
        // current dir is default to return
        // System.out.println ( "Creating path: " + path );
        if ( path != null ) {
            File tmp = new File ( path );
            if ( tmp.exists () ) {
                if ( tmp.isDirectory () ) {
                    // System.err.println ( "Using existing directory: " + path
                    // );
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
        // System.out.println ( "Returning path: " + result );
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
        Configuration.removeConsoles ();
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
        // inspector_ = info.getTmAdminTool();
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
