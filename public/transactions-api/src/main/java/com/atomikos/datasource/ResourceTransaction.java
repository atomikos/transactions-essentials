/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource;


/**
 * The notion of a local transaction executed on a resource.
 * Serves as a handle towards the transaction management module.
 */
 
public interface ResourceTransaction 
{


    /**
     * Suspends the work, so that underlying resources can
     * be used for a next (sibling) invocation.
     *
     */

     void suspend() throws IllegalStateException,ResourceException;

    /**
     * Resumes a previously suspended tx.
     *
     */

     void resume() throws IllegalStateException,ResourceException;
       
}
