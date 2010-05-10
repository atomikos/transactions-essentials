//$Id: TSInitInfoImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: TSInitInfoImp.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
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
//Revision 1.9  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.8  2004/10/14 13:08:45  guy
//Added methods: setProperty and getProperty.
//
//Revision 1.7  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: TSInitInfoImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.6  2004/09/28 11:26:13  guy
//$Id: TSInitInfoImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Updated startup/shutdown processing to make it independent of the UTS
//$Id: TSInitInfoImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//instance on which it is called. Moved register methods to UserTransactionService
//$Id: TSInitInfoImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//instead of TSInitInfo.
//$Id: TSInitInfoImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//$Id: TSInitInfoImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Revision 1.5  2003/03/11 06:38:54  guy
//$Id: TSInitInfoImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: TSInitInfoImp.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//
//Revision 1.4.4.1  2003/01/31 15:45:01  guy
//Added get/set Properties functionality, to allow extensible JNDI settings.
//
//Revision 1.4  2002/01/08 15:21:26  guy
//Updated to new LogAdministrator paradigm.
//
//Revision 1.3  2001/12/30 13:41:26  guy
//Updated to have XA transparency: no XA references needed in interface.
//
//Revision 1.2  2001/12/30 12:35:13  guy
//Some minor changes.
//
//Revision 1.1  2001/12/30 10:32:09  guy
//Simplified the initialization by abstraction into a TSInitInfo intf/object.
//

package com.atomikos.icatch.config.imp;

import java.util.Enumeration;
import java.util.Properties;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * implementation of TSInitInfo.
 */

public class TSInitInfoImp implements TSInitInfo
{

 
    private Properties properties_;

    public TSInitInfoImp ()
    {

        properties_ = new Properties();
    }

    /**
     * @see TSInitInfo
     */

    public void setProperties ( Properties properties )
    {
        properties_ = properties;
    }

    /**
     * @see TSInitInfo
     */

    public Properties getProperties ()
    {
        return properties_;
    }

    /**
     * @see TSInitInfo
     */

    public Enumeration getResources ()
    {
        return Configuration.getResources ();
    }

    /**
     * @see TSInitInfo
     */

    public void registerLogAdministrator ( LogAdministrator admin )
    {
        Configuration.addLogAdministrator ( admin );
    }

    /**
     * @see TSInitInfo
     */

    public Enumeration getLogAdministrators ()
    {
        return Configuration.getLogAdministrators ();
    }

    /**
     * @see TSInitInfo
     */

    public void registerResource ( RecoverableResource resource )
    {
        Configuration.addResource ( resource );
    }

    /**
     * @see com.atomikos.icatch.TSInitInfo#setProperty(java.lang.String,
     *      java.lang.String)
     */
    public void setProperty ( String name , String value )
    {
        properties_.setProperty ( name, value );
    }

    /**
     * @see com.atomikos.icatch.TSInitInfo#getProperty(java.lang.String)
     */
    public String getProperty ( String name )
    {
        return properties_.getProperty ( name );
    }

}
