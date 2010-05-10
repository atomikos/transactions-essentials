//$Id: StandAloneConfiguration.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//$Log: StandAloneConfiguration.java,v $
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
//Revision 1.6  2004/10/12 13:03:49  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.5  2004/10/11 13:39:39  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: StandAloneConfiguration.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.4  2004/09/28 11:49:36  guy
//$Id: StandAloneConfiguration.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: StandAloneConfiguration.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Re-added this class for backward compatibility.
//$Id: StandAloneConfiguration.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//$Id: StandAloneConfiguration.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Revision 1.2  2003/03/11 06:39:11  guy
//$Id: StandAloneConfiguration.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: StandAloneConfiguration.java,v 1.1.1.1 2006/08/29 10:01:11 guy Exp $
//
//Revision 1.1.4.1  2003/01/31 15:45:29  guy
//Adapted to set/get Properties in TSInitInfo.
//
//Revision 1.1  2002/01/23 11:40:02  guy
//Added standalone  package to CVS.
//

package com.atomikos.icatch.standalone;

import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;

/**
 * 
 * 
 * The configuration (facade) class for the standalone version of the
 * transaction manager. This version supports no import or export of
 * transactions.
 * 
 * @deprecated As from release 1.30, all of this is done through an instance of
 *             the bean class com.atomikos.icatch.UserTransactionServiceImp.
 */

public final class StandAloneConfiguration
{
    private static final UserTransactionService uts_ = new com.atomikos.icatch.config.UserTransactionServiceImp ();

    /**
     * The System property name that holds the path to the standalone properties
     * file, needed for default initialization of the standalone config.
     * 
     */

    public static final String FILE_PATH_PROPERTY_NAME = com.atomikos.icatch.config.UserTransactionServiceImp.FILE_PATH_PROPERTY_NAME;

    /**
     * The system property name that indicates that NO configuration file should
     * be used. In this case, default properties will be used, and overridden by
     * any system-set properties. To use this option, just set the System
     * property with this name to an arbitrary value.
     */

    public static final String NO_FILE_PROPERTY_NAME = com.atomikos.icatch.config.UserTransactionServiceImp.NO_FILE_PROPERTY_NAME;

    /**
     * The default standalone properties file name, assumed to be in the current
     * directory and to be used in case the property <b>FILE_PATH_PROPERTY_NAME</b>
     * is not set.
     */

    public static final String DEFAULT_PROPERTIES_FILE_NAME = com.atomikos.icatch.config.UserTransactionServiceImp.DEFAULT_PROPERTIES_FILE_NAME;

    /**
     * Get the UserTransactionManager instance for the configuration.
     * 
     * @return UserTransactionManager The UserTransactionManager
     */

    public static final UserTransactionService getUserTransactionService ()
    {
        return uts_;
    }

    /**
     * Create a TSInitInfo object for this configuration.
     * 
     * @return TSInitInfo The initialization object.
     */

    public static final TSInitInfo createTSInitInfo ()
    {
        return uts_.createTSInitInfo ();
    }
}
