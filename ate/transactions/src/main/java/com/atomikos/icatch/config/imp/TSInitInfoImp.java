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
