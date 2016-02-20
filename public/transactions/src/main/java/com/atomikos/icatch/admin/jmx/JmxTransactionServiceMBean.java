/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin.jmx;

import javax.management.ObjectName;

/**
 * An MBean interface for the administration of the transaction service.
 */

public interface JmxTransactionServiceMBean
{
    /**
     * Gets the pending transactions from the Transaction Service.
     * 
     * @return ObjectName[] An array of all MBean object names (one MBean is
     *         created for each transaction).
     */

    public ObjectName[] getTransactions ();

    /**
     * Sets whether ONLY heuristic transactions should be returned.
     * Default is false.
     * 
     * @param heuristicsOnly
     */
    
    public void setHeuristicsOnly ( boolean heuristicsOnly );
    
    /**
     * Gets the heuristic mode.
     * 
     * @return
     */
    public boolean getHeuristicsOnly();
}
