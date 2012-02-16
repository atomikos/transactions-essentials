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
