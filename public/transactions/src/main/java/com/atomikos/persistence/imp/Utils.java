package com.atomikos.persistence.imp;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.IOHelper;

public class Utils {

	private static final Logger LOGGER = LoggerFactory.createLogger(Utils.class);
	 /**
     * Utility method to find or create a given folder
     *
     * @param path
     *            The folder path.
     * @return String The resulting file path, or a default if the given path is
     *         not valid as a folder.
     */

    public static String findOrCreateFolder ( String path )
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
