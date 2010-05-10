//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//$Log: UserTransactionServiceFactory.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:11  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:30  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:56  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:13  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.10  2005/08/30 07:09:49  guy
//Made getDefaultName package scoped.
//
//Revision 1.9  2005/08/29 06:53:28  guy
//Added SOAP txs capability.
//
//Revision 1.8  2005/04/29 15:00:35  guy
//Updated to use new RotatingFileConsole.
//
//Revision 1.7  2004/10/25 09:45:53  guy
//Changed property name: enable_logging.
//
//Revision 1.6  2004/10/18 08:48:34  guy
//Added support for disabling recovery.
//
//Revision 1.5  2004/10/11 13:39:39  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.4  2004/10/04 06:31:00  guy
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Added default tm name based on IP.
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.3  2004/10/01 08:56:33  guy
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Moved getDefaultProperties to servicefactory
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.2  2004/03/22 15:38:03  guy
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.1.2.1  2003/07/09 09:14:41  guy
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//*** empty log message ***
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.2  2003/03/11 06:39:11  guy
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//Revision 1.1.4.1  2003/01/31 15:45:29  guy
//Adapted to set/get Properties in TSInitInfo.
//
//Revision 1.1  2002/01/23 11:40:02  guy
//Added standalone  package to CVS.
//

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
