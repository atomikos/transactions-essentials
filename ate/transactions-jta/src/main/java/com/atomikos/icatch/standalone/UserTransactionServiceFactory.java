package com.atomikos.icatch.standalone;

import java.net.UnknownHostException;
import java.util.Properties;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;

/**
 * 
 * 
 * The configuration (facade) class for the standalone version of the
 * transaction manager. This version supports no import or export of
 * transactions.
 */

public final class UserTransactionServiceFactory extends AbstractUserTransactionServiceFactory implements
        com.atomikos.icatch.config.UserTransactionServiceFactory
{


    static String getDefaultName ()
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
     * Get the UserTransactionManager instance for the configuration.
     * 
     * @param properties
     *            The properties as specified by the client.
     * @return UserTransactionManager The UserTransactionManager
     */

    public UserTransactionService getUserTransactionService (
            Properties properties )
    {
        return new UserTransactionServiceImp ( properties );
    }
    //
    // /**
    // *Create a TSInitInfo object for this configuration.
    // *@return TSInitInfo The initialization object.
    // */
    //
    // public static final TSInitInfo createTSInitInfo()
    // {
    // return UserTransactionServiceImp.createTSInitInfo();
    // }
}
