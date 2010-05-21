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

package com.atomikos.icatch.jta;

import javax.transaction.xa.XAResource;

/**
 * 
 * 
 * A configuration entry consists of an XAResource and its name.
 */

class ConfigurationEntry
{
    private String name_;
    private XAResource xares_;

    ConfigurationEntry ( XAResource xares , String name )
    {
        xares_ = xares;
        name_ = name;
    }

    /**
     * Check for the name for the given resource.
     * 
     * @param xares
     *            The given XAResource
     * @return String The name, or null if wrong resource.
     */

    String getName ( XAResource xares )
    {
        try {
            if ( xares_.isSameRM ( xares ) )
                return name_;
            else
                return null;
        } catch ( Exception e ) {
            throw new RuntimeException ( e.getMessage () + " "
                    + e.getClass ().getName () );
        }
    }
}
