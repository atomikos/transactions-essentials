/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin;



 /**
  * Representation of an administrator facility for 
  * log inspection and termination of active transactions.
  * Different implementations are possible, local OR distributed.
  * This way, administration can be done from one and the same machine, 
  * if needed.
  */

public interface LogAdministrator 
{
     /**
      * Registers (adds) a LogControl instance to the administrator.
      * This method is typically called right after initialization of the 
      * transaction service.
      *
      * @param control The LogControl instance.
      */
      
    public void registerLogControl ( LogControl control );
    
     /**
      * De-registers (removes) a LogControl instance from the
      * administrator. 
      * This method is typically called at shutdown of the transaction
      * service.
      *
      * @param control The LogControl instance to remove. Does nothing
      * if the control is not registered.
      */
      
    public void deregisterLogControl ( LogControl control ); 
}
