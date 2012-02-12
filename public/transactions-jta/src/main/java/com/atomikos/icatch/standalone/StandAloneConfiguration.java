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

package com.atomikos.icatch.standalone;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

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
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(StandAloneConfiguration.class);

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
