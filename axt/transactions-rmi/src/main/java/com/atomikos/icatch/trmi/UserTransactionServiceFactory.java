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
