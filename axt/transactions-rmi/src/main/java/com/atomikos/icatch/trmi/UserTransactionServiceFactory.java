//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: UserTransactionServiceFactory.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.2  2004/03/22 15:38:14  guy
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.1.2.2  2003/05/30 15:18:59  guy
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Changed UserTransactionFactory.getUserTransaction: added Properties arg.
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.1.2.1  2003/05/22 15:24:53  guy
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Renamed Configuration to UserTransactionServiceFactory.
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.8  2003/03/11 06:39:16  guy
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//Revision 1.7.4.1  2003/01/31 15:45:34  guy
//Adapted to set/get Properties in AbstractUserTransactionServiceFactory.
//
//Revision 1.7  2002/01/07 12:26:39  guy
//Updated UserTransactionImp to check license, and to allow system propery
//settings.
//
//Revision 1.6  2001/12/30 12:35:41  guy
//Updated to use UserTransactionService interface.
//
//Revision 1.5  2001/12/20 10:16:42  guy
//Updated console file to be opened in append mode.
//
//Revision 1.4  2001/12/20 10:10:30  guy
//Changed shutdown to static method.
//
//Revision 1.3  2001/12/20 09:02:07  guy
//Updated UserTransactionServiceFactory to call init() on tm.
//
//Revision 1.2  2001/12/06 15:41:58  guy
//Added installation of Importing and Exporting TM in Configuration class.
//

package com.atomikos.icatch.trmi;
import java.util.Properties;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;

 /**
  *Copyright &copy; 2001, Atomikos. All rights reserved.
  *
  *A facade class for the Trmi TM framework. Allows easy integration 
  *of the trmi TM (standalone) in applications.
  */
  
public final class UserTransactionServiceFactory
		 extends AbstractUserTransactionServiceFactory
         implements com.atomikos.icatch.config.UserTransactionServiceFactory
{
      
	/**
	 * The name of the property indicating what SOAP commit protocols to enable.
	 * 
	 * Expands to {@value}.
	 */
	public static final String SOAP_COMMIT_PROTOCOLS_PROPERTY_NAME = "com.atomikos.icatch.soap_commit_protocols";

	/**
	 * The name of the property indicating the SOAP host address where this
	 * transaction manager is listening.
	 * 
	 * Expands to {@value}.
	 */
	public static final String SOAP_HOST_ADDRESS_PROPERTY_NAME = "com.atomikos.icatch.soap_host_address";
	
	/**
	 * The name of the property indicating what TCP port the SOAP service is
	 * listening on. If you are using some form of tunneling then you will need to set this
	 * to the publicly visible port on the machine that forwards request to the
	 * actual service.
	 * 
	 * Expands to {@value}.
	 */
	public static final String SOAP_PORT_PROPERTY_NAME = "com.atomikos.icatch.soap_port";
	
	/**
	 * The name of the property indicating what local port the SOAP endpoints are to be exported on.
	 * If you are using some form of tunneling then you will need to set this to the local
	 * port on the machine where the service is running.
	 * 
	 * Expands to {@value}. 
	 */
	public static final String LOCAL_ENDPOINTS_PORT_PROPERTY_NAME = "com.atomikos.icatch.local_endpoints_port";
	
	/**
	 * The name of the property that specifies whether or not a client
	 * transaction manager can be trusted to terminate heuristic problems.
	 * 
	 * Expands to {@value}.
	 */
	public static final String TRUST_CLIENT_TM_PROPERTY_NAME = "com.atomikos.icatch.trust_client_tm";

	/**
	 * The name of the property that specifies whether or not HTTPS should be used.
	 * 
	 * Expands to {@value}.
	 */
	public static final String SECURE_HTTP_PROPERTY_NAME = "com.atomikos.icatch.https";
	

      /**
        *Get the UserTransactionManager instance for the configuration.
        *@param properties The specified properties.
        *@return UserTransactionManager The UserTransactionManager 
        */    
      
      public UserTransactionService
      getUserTransactionService ( Properties properties )
      {
          return new UserTransactionServiceImp ( properties );
      }


      
 
}
