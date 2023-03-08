/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;


 /**
  * A plugin interface for transaction service extension modules.
  * Instances can register themselves via the ServiceLoader mechanism
  * in order to be notified about startup and shutdown events.
  */

public interface TransactionServicePlugin
{
     /**
      * Called before initialization of the transaction core.
      * 
      * <em>
      * DISCLAIMER: only implementations that register with the ServiceLoader
      * mechanism are sure of receiving this notification. Other implementations
      * should be aware that the transaction core may already be running by the 
      * time they register - in which case there will be no callback.
      * </em>
      */
      
    void beforeInit();
    
    /**
     * Called after initialization of the transaction core.
     */
    
    void afterInit();
    
     /** 
      * Called after shutdown of the transaction core.
      */
      
    void afterShutdown(); 
}
