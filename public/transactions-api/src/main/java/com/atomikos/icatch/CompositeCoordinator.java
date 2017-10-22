/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
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
     * @return The coordinatorId. 
     */

     String getCoordinatorId();
     
     /**
      * 
      * @return The top-level root's coordinatorId.
      */
     String getRootId();
   

    /**
     *
     *@return RecoveryCoordinator.
     */

     RecoveryCoordinator getRecoveryCoordinator();

}







