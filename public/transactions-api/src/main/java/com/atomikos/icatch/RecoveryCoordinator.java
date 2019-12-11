/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;


/**
 * The coordinator who knows the outcome for recovery purposes.
 */

public interface RecoveryCoordinator 
{

    
     /**
      * Gets the URI identifier for this coordinator.
      * @return String The URI identifier.
      */
      
     String getURI();
     
      /**
       * 
       * @return The recovery domain of this coordinator; a different one indicates a foreign transaction
       * (i.e., one whose commit decision is unknown in our logs).
       */
     String getRecoveryDomainName();

}
