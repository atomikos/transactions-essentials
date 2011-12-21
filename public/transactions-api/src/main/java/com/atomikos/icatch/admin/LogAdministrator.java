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
