/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.provider;

import java.util.Properties;

 /**
  * A plugin interface for transaction service extension modules.
  * Instances can register themselves in order to be notified about
  * recovery and shutdown events.
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
      
    public void beforeInit ( Properties properties );
    
    /**
     * Called after initialization of the transaction core.
     */
    
    public void afterInit();
    
     /** 
      * Called after shutdown of the transaction core.
      */
      
    public void afterShutdown(); 
}
