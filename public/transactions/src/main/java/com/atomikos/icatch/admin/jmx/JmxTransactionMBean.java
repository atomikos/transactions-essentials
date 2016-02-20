/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin.jmx;


/**
 * An MBean interface for administration of pending transactions.
 */

public interface JmxTransactionMBean
{

    /**
     * Gets the transaction identifier.
     * 
     * @return String The unique id.
     */

    public String getTid ();

    /**
     * Gets the transaction's state.
     * 
     * @return String The state, represented as by its name
     */

    public String getState ();
    
    /**
     * Retrieves the descriptive details for each participant involved in this transaction.
     */

    public String[] getParticipantDetails();


}
