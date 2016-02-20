/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;


/**
 * Represents the per-server work done
 * as part of the same global (root) transaction scope.
 */

public interface CompositeCoordinator extends java.io.Serializable 
{

    /**
     * @return String The coordinatorId. For imported transactions, this will be the
     * id of the top-level or root transaction. 
     * For subtransactions, this will be an independent id.
     */

     String getCoordinatorId();
   

    /**
     *
     *@return RecoveryCoordinator.
     */

     RecoveryCoordinator getRecoveryCoordinator();

}







