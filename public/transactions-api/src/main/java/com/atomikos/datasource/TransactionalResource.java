/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource;

import com.atomikos.icatch.CompositeTransaction;

/**
 * Represents the abstraction of a data source that
 * supports transactions and recovery.
 */

public interface TransactionalResource extends RecoverableResource
{
    /**
     * Gets or creates a ResourceTransaction. This instructs the resource
     * to internally start a context for a new transaction.
     * If the resource decides to return a new instance, it should
     * also make sure that before returning, the new resource 
     * transaction is registered as a participant for the supplied
     * composite transaction.
     *
     */

     ResourceTransaction 
        getResourceTransaction ( CompositeTransaction compositeTransaction ) 
        throws IllegalStateException, ResourceException;



}
