package com.atomikos.icatch.config;

import java.util.Enumeration;
import java.util.Properties;


import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.admin.LogAdministrator;

/**
 * 
 * 
 * Initializer information for the UserTransactionService. Instances can be
 * passed to a UserTransactionService during initialization.
 * 
 * @deprecated This interface has been replaced by regular properties on the
 * UserTransactionService and UserTransactionServiceFactory classes.
 * 
 */

public interface TSInitInfo {

	

	
	/**
	 * Register a LogAdministrator instance for administration. This allows
	 * inspection of active transactions and manual intervention. Care should be
	 * taken if multiple instances are registered: the responsibility of taking
	 * conflicting manual decisions is entirely with the user!
	 * 
	 * @param admin
	 *            The instance.
	 *            
	 * @deprecated Do this through the UserTransactionService instead.
	 */

	public void registerLogAdministrator(LogAdministrator admin);

	/**
	 * Add a resource. The purpose of registering resources is mainly to be able
	 * to enable recovery of these resources. This is needed for those
	 * ResourceTransaction instances that do not encapsulate the full state
	 * themselves, such as in the XAResource case.
	 * 
	 * @param resource
	 *            The resource to add.
	 * @deprecated Do this through the UserTransactionService instead.
	 */

	public void registerResource(RecoverableResource resource);


	/**
	 * Get the resources registered.
	 * 
	 * @return Enumeration The resources, or empty if none.
	 * @deprecated Do this through the UserTransactionService instead.
	 * 
	 */

	public Enumeration getResources();

	/**
	 * Get the log administrators.
	 * 
	 * @return Enumeration The registered administrators.
	 * @deprecated Do this through the UserTransactionService instead.
	 */

	public Enumeration getLogAdministrators();


	/**
	 * Sets the properties that the transaction service should use.
	 * 
	 * @param properties
	 *            The properties. These values will override any properties
	 *            found in the server configuration file.
	 *            
	 */

	public void setProperties(Properties properties);

	/**
	 * Get the configuration properties for the transaction service.
	 * 
	 * @return Properties The configuration properties. Initially, these will
	 *         include any properties read from the server configuration file.
	 * 
	 */

	public Properties getProperties();

	/**
	 * Set the initialization property with the given name
	 * 
	 * @param name
	 *            The name of the property.
	 * @param value
	 *            The value.
	 */

	public void setProperty(String name, String value);

	/**
	 * Get the value of the given property.
	 * 
	 * @param name
	 *            The name of the property.
	 * @return String The property value, or null if not defined.
	 */

	public String getProperty(String name);

}
