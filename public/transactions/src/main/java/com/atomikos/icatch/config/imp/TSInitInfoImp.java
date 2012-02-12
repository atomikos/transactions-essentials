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

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(TSInitInfoImp.class);

 
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
