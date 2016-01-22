/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
