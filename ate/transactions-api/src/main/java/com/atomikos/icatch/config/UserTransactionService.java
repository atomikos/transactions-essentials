//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//$Log: UserTransactionService.java,v $
//Revision 1.2  2006/09/22 11:53:26  guy
//ADDED 1003
//
//Revision 1.1.1.1  2006/08/29 10:01:08  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.2  2006/04/11 15:05:30  guy
//Added removal methods and TSListener methods.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:58  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:23  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.9  2004/10/14 13:00:15  guy
//Corrected javadoc formatting.
//
//Revision 1.8  2004/10/11 13:39:29  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Revision 1.7  2004/09/28 11:26:14  guy
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Updated startup/shutdown processing to make it independent of the UTS
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//instance on which it is called. Moved register methods to UserTransactionService
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//instead of TSInitInfo.
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Revision 1.6  2004/09/06 09:26:37  guy
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Redesigned recovery: can now be done at any time.
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Resources can now be added after init() and will be
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//recovered immediately rather than on the next restart.
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Revision 1.5  2004/03/22 15:36:53  guy
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Revision 1.4.2.1  2003/05/22 15:23:15  guy
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Added UserTransactionServiceFactory interface, according to JNDI context pattern.
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Revision 1.4  2003/03/11 06:38:54  guy
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: UserTransactionService.java,v 1.2 2006/09/22 11:53:26 guy Exp $
//
//Revision 1.3.4.2  2002/11/16 16:26:11  guy
//Added getUserTransation() method.
//
//Revision 1.3.4.1  2002/10/09 17:13:16  guy
//Updated for shutdown with wait option.
//
//Revision 1.3  2002/03/11 01:36:57  guy
//Added MetaData for the UserTransactionService.
//
//Revision 1.2  2001/12/30 10:32:09  guy
//Simplified the initialization by abstraction into a TSInitInfo intf/object.
//

package com.atomikos.icatch.config;
import java.util.Enumeration;
import java.util.Properties;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TSListener;
import com.atomikos.icatch.admin.LogAdministrator;

 /**
  *
  *
  *The user's (client program) view of the transaction manager's configuration, 
  *with all the information the client program needs.
  *
  */

public interface UserTransactionService
{
    
 
     
      /**
       *Get the JTA transaction manager handle for the  config.
       *
       *@deprecated Use the UserTransactionManager and
       *J2eeTransactionManager classes instead.
       *
       *@return TransactionManager The JTA transaction manager.
       */
       
     public TransactionManager getTransactionManager();
     
      /**
       *Get the JTA user transaction, for client-demarcated 
       *transactions.
       *
       *@deprecated Use the classes in 
       *package <b>com.atomikos.icatch.jta</b> instead.
       *
       *@return UserTransaction The JTA user transaction.
       *<b>IMPORTANT</b>: the returned instance can be
       *bound in JNDI. In addition, <b>remote</b> clients 
       *can use this instance <b>only if</b> the configuration
       *parameters allow remote client transaction demarcation!
       *
       */
       
     public UserTransaction getUserTransaction();
      
      /**
       *Get the ImportingTransactionManager instance.
       *
       *@return ImportingTransactionManager The instance.
       */
       
     public ImportingTransactionManager getImportingTransactionManager();
     
      /**
       *Get the ExportingTransactionManager instance
       *
       *@return ExportingTransactionManager The instance.
       */
       
     public ExportingTransactionManager getExportingTransactionManager();
     
      
       /**
        *Get the meta data for the transaction service.
        *@return TSMetaData The meta data.
        *@deprecated 
        */
        
     public TSMetaData getTSMetaData();

     /**
      * Create a TSInitInfo for this transaction service.
      * @return  TSInitInfo The init info instance.
      * @deprecated Use the properties-based init method instead.
      */

     public TSInitInfo createTSInitInfo();

	/**
	     *Shuts down the TM.
	     *It is <b>highly recommended</b> that this method be called 
	     *<b>before the VM exits</b>, in order to ensure proper log closing.
	     *After this method completes, all resources will have been removed 
	     *from the configuration, as well as all logadministrators.
	     *To re-initialize, everything should be registered again.
	     *
	     *@param force If true, then shutdown will succeed even if
	     *some transactions are still active. If false, then the calling thread
	     *will block until any active transactions are terminated. A heuristic
	     *transaction is also considered to be active.
	     */
	     
	   public void shutdown ( boolean force ) 
	   throws IllegalStateException;

	/**
	 * Register a new resource for recovery.
	 * 
	 * @param res The resource to be added.
	 * 
	 */
	
	public void registerResource ( RecoverableResource res );
	
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
		 *Register a LogAdministrator instance for administration.
		 *This allows inspection of active transactions and manual
		 *intervention.
		 *Care should be taken if multiple instances are registered:
		 *the responsibility of taking conflicting manual decisions is
		 *entirely with the user!
		 *@param admin The instance.
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
	  public void registerTSListener ( TSListener listener );
	  
	  /**
	   * Removes a listener from the transaction service.
	   * After this method is called, the listener in question
	   * will no longer receive callback notifications from the
	   * transaction service.
	   * This method does nothing if the listener is not found.
	   * @param listener 
	   */
	  public void removeTSListener ( TSListener listener );

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
	  *Get the resources registered.
	  *@return Enumeration The resources, or empty if none.
	  *
	  */
	
	public Enumeration getResources();

	/**
	  *Get the log administrators.
	  *@return Enumeration The registered administrators.
	  *
	  */
	
	public Enumeration getLogAdministrators();

	/**
	     *Get the composite transaction manager for the config.
	     *
	     *@return CompositeTransactionManager The composite
	     *transaction manager.
	     */
	     
	   public CompositeTransactionManager 
	   getCompositeTransactionManager();
     

}
