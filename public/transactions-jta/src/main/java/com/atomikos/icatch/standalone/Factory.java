package com.atomikos.icatch.standalone;

import java.io.File;
import java.io.IOException;

import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.dataserializable.FileLogStream;
import com.atomikos.persistence.dataserializable.StateRecoveryManagerImp;
import com.atomikos.persistence.dataserializable.StreamObjectLog;
import com.atomikos.persistence.imp.VolatileStateRecoveryManager;
import com.atomikos.util.IOHelper;

import java.util.Properties;
public class Factory {

	private static final Logger LOGGER = LoggerFactory.createLogger(Factory.class);
	public static StateRecoveryManager createLogSystem(String logname, String logdir, boolean enableRecovery, long chckpt) throws IOException {
		//the default...
		//TODO : lookup...
		LogStream logstream = new FileLogStream ( logdir, logname );
        StreamObjectLog slog = new StreamObjectLog ( logstream, chckpt );

        StateRecoveryManager recmgr = null;
        if ( enableRecovery )
            recmgr = new StateRecoveryManagerImp ( slog );
        else
            recmgr = new VolatileStateRecoveryManager ();
		return recmgr;
	}
	
	public static StateRecoveryManager createLogSystem(Properties p) throws IOException {
		//the default...
        long chckpt = Long.valueOf( getTrimmedProperty (
                AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME, p ) );

        String logdir = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME, p );
        String logname = getTrimmedProperty (
                AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME, p );
        logdir = findOrCreateFolder ( logdir );
		//TODO : lookup...
		LogStream logstream = new FileLogStream ( logdir, logname );
        StreamObjectLog slog = new StreamObjectLog ( logstream, chckpt );

        StateRecoveryManager recmgr = null;
//        if ( enableRecovery )
            recmgr = new StateRecoveryManagerImp ( slog );
//        else
//            recmgr = new VolatileStateRecoveryManager ();
		return recmgr;
	}
	
	 /**
     * Utility method to find or create a given folder
     *
     * @param path
     *            The folder path.
     * @return String The resulting file path, or a default if the given path is
     *         not valid as a folder.
     */

    private static String findOrCreateFolder ( String path )
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
                    LOGGER.logWarning( msg );
                }
            } else {
                // file does not exist; attempt to create
                String msg = path + " could not be created - using default";
                try {
                    if ( IOHelper.createPathTo ( tmp, true ) ) {
                        ret = tmp;
                    } else {
                    	LOGGER.logWarning ( msg );
                    }
                } catch ( IOException e ) {
                	LOGGER.logWarning ( msg );
                }
            }
        }

        String result = ret.getAbsolutePath ();
        if ( !result.endsWith ( File.separator ) )
            result = result + File.separator;
        return result;
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

}
