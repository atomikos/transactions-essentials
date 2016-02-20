/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
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
