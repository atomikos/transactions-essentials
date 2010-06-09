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

package com.atomikos.datasource.xa.jmx;

/**
 * 
 * 
 * 
 * 
 * 
 * JMX interface for managing installed XATransactionalResources.
 */

public interface JmxTransactionalResourceMBean
{

    /**
     * Get the weak comparison mode.
     * 
     * @return boolean True iff weak comparison.
     */

    public boolean getUseWeakCompare ();

    /**
     * Set the weak comparison mode.
     * 
     * @param value
     *            The mode.
     */

    public void setUseWeakCompare ( boolean value );

    /**
     * Test if all XAResources should be accepted.
     * 
     * @return boolean True iff all xaresources are accepted.
     */

    public boolean getAcceptAllXAResources ();

    /**
     * Instruct this resource to accept all XAResource instances.This will
     * practically disable shared branches! The instance uses this feature to
     * determine the exact XID to be supplied during enlistment.
     * 
     * @param val
     */
    public void setAcceptAllXAResources ( boolean val );

}
