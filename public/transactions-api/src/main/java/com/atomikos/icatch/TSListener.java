/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch;

import java.util.Properties;

 /**
  * A listener interface for transaction service startup and shutdown events.
  * Instances can register themselves in order to be notified about
  * recovery and shutdown events.
  */

public interface TSListener
{
     /**
      * Called before and after initialization.
      * @param before True indicates that  initialization is about to start.
      * False indicates that initialization has finished. This means that 
      * recovery has been done and the transaction service is now 
      * ready to start new transactions.
      * @param properties The initialization properties.
      */
      
    public void init ( boolean before , Properties properties );
    
     /** 
      * Called before and after shutdown.
      * @param before True if shutdown is about to start.
      * False if shutdown has finished.
      */
      
    public void shutdown ( boolean before ); 
}
