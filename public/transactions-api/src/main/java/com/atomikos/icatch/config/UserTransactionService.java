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

package com.atomikos.icatch.config;
import java.util.Enumeration;
import java.util.Properties;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionServicePlugin;
import com.atomikos.icatch.admin.LogAdministrator;

 /**
  *
  * The user's (client program) view of the transaction manager's configuration, 
  * with all the information the client program needs.
  *
  */

public interface UserTransactionService
{
    
      
      /**
       * Gets the ImportingTransactionManager instance.
       *
       * @return ImportingTransactionManager The instance.
       */
       
     public ImportingTransactionManager getImportingTransactionManager();
     
      /**
       * Gets the ExportingTransactionManager instance
       *
       * @return ExportingTransactionManager The instance.
       */
       
     public ExportingTransactionManager getExportingTransactionManager();
     
      
      
     /**
      * Create a TSInitInfo for this transaction service.
      * @return  TSInitInfo The init info instance.
      * @deprecated Use the properties-based init method instead.
      */

     public TSInitInfo createTSInitInfo();

	/**
	     * Shuts down the core.
	     * It is <b>highly recommended</b> that this method be called 
	     * <b>before the VM exits</b>, in order to ensure proper log closing.
	     * After this method completes, all resources will have been removed 
	     * from the configuration, as well as all logadministrators.
	     * To re-initialize, everything should be registered again.
	     *
	     * @param force If true, then shutdown will succeed even if
	     * some transactions are still active. If false, then the calling thread
	     * will block until any active transactions are terminated. A heuristic
	     * transaction is also considered to be active.
	     */
	     
	   public void shutdown ( boolean force ) 
	   throws IllegalStateException;

	/**
	 * Registers a new resource for recovery.
	 * 
	 * @param resource The resource to be added.
	 * 
	 */
	
	public void registerResource ( RecoverableResource resource );
	
	/**
	 * Removes the given resource. This method should be 
	 * used with extreme care, because removing resources
	 * can endanger recovery!
	 * @param The resource to remove.
	 * If the resource is not found then this method does 
	 * nothing.
	 */
	
	public void removeResource ( RecoverableResource res );

		/**
		 * Registers a LogAdministrator instance for administration.
		 * This allows inspection of active transactions and manual
		 * intervention.
		 * Care should be taken if multiple instances are registered:
		 * the responsibility of taking conflicting manual decisions is
		 * entirely with the user!
		 * @param admin The instance.
		 */
	      
	  public void registerLogAdministrator ( LogAdministrator admin );
	  
	  /**
	   * Removes the given log administrator. Does nothing if 
	   * the instance is not found.
	   * @param admin
	   */
	  
	  public void removeLogAdministrator ( LogAdministrator admin );
	  
	  /**
	   * Registers a listener with the transaction service.
	   * @param listener
	   */
	  public void registerTSListener ( TransactionServicePlugin listener );
	  
	  /**
	   * Removes a listener from the transaction service.
	   * After this method is called, the listener in question
	   * will no longer receive callback notifications from the
	   * transaction service.
	   * This method does nothing if the listener is not found.
	   * @param listener 
	   */
	  public void removeTSListener ( TransactionServicePlugin listener );

	/**
	     *Initializes the intra-VM transaction manager.
	     *
	     *@param info The TSInitInfo with details for initialization.
	     *@deprecated Use the property-based init method instead.
	     */
	       
	   public void init ( TSInitInfo info )
	   throws SysException;
	   
	   public void init ( Properties properties ) throws SysException;

	/**
	  * Gets the resources registered.
	  * @return Enumeration The resources, or empty if none.
	  *
	  */
	
	public Enumeration getResources();

	/**
	  * Gets the log administrators.
	  * @return Enumeration The registered administrators.
	  *
	  */
	
	public Enumeration getLogAdministrators();

	/**
	     *Gets the composite transaction manager for the config.
	     *
	     * @return CompositeTransactionManager The composite
	     * transaction manager.
	     */
	     
	   public CompositeTransactionManager 
	   getCompositeTransactionManager();
     

}
