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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.icatch.provider.TransactionServicePlugin;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

/**
 * This is the main class for creating a UserTransactionService instance.
 * This class is the client's main entry point into the transaction engine.
 *
 * The creation of a user transaction happens via a UserTransactionServiceFactory
 * which is looked up by this object behind the scenes.
 * Instances can be serialized to disk and re-read at a later time.
 * Note: deserialization will only work in the SAME configuration as the
 * one that did the streaming out. In particular, if no identical Atomikos
 * transaction service is present in the target VM then the process
 * of deserialization will be erroneous.
 */

public class UserTransactionServiceImp
        implements java.io.Serializable , UserTransactionService
{
	
	private static final Logger LOGGER = LoggerFactory.createLogger(UserTransactionServiceImp.class);

	private static final long serialVersionUID = -3374591336514451887L;

	/**
     * Constant denoting the system property name that suggest NOT to use
     * any configuration file for the transaction service's properties.
     * 
     * @deprecated No longer used since 4.0
     */
    public static final String NO_FILE_PROPERTY_NAME = "com.atomikos.icatch.no_file";

    /**
     * The name of the system property whose value corresponds to the path
     * towards the properties file for initialization of the transaction service.
     * If this system property is set then the corresponding file path will be
     * used to load the initialization parameters for the transaction service.
     * Otherwise, the transaction service will attempt to load the default
     * file from the classpath.
     */

    public static final String FILE_PATH_PROPERTY_NAME = ConfigProperties.FILE_PATH_PROPERTY_NAME;
    
    /**
     * The name of the system property to disable printing 'Using init file...' messages.
     * 
     * @deprecated No longer used since 4.0
     */
    
    public static final String HIDE_INIT_FILE_PATH_PROPERTY_NAME = "com.atomikos.icatch.hide_init_file_path";

   /**
    * @deprecated
    */
    public static final String DEFAULT_PROPERTIES_FILE_NAME = "transactions.properties";
    

    private Properties properties_;
    
    private List<TransactionServicePlugin> tsListeners_;
    private List<LogAdministrator> logAdministrators_;
    private List<RecoverableResource> resources_;

    
    

   


    /**
     * Default constructor.
     *
     */

    public UserTransactionServiceImp ()
    {
		tsListeners_ = new ArrayList<TransactionServicePlugin>();
		logAdministrators_ = new ArrayList<LogAdministrator>();
		resources_ = new ArrayList<RecoverableResource>();
		properties_ = new Properties();
       
    }

	/**
	 * Constructs a new instance and initializes it with the given properties.
	 * If this constructor is called, then file-based initialization is overridden.
	 * In particular, the given properties will take precedence over the file-based
	 * properties (if found).
	 * 
	 * @param properties The properties.
	 */
	
    public UserTransactionServiceImp ( Properties properties )
    {
    	this();
    	properties_ = properties;
    }


    /**
     *
     * @see UserTransactionService
     */

    public void shutdown ( boolean force )
            throws IllegalStateException
    {
    	Configuration.shutdown(force);
    }




	private void initialize() {
		Iterator it = resources_.iterator();
        while ( it.hasNext() ) {
        	RecoverableResource nxt = ( RecoverableResource ) it.next();
        	Configuration.addResource ( nxt );
        }
        it = logAdministrators_.iterator();
        while  ( it.hasNext() ) {
        	LogAdministrator nxt = ( LogAdministrator ) it.next();
        	Configuration.addLogAdministrator ( nxt );
        }
        it = tsListeners_.iterator();
        while ( it.hasNext() ) {
        	TransactionServicePlugin nxt = ( TransactionServicePlugin ) it.next();
        	Configuration.registerTransactionServicePlugin ( nxt );
        }
        ConfigProperties configProps = Configuration.getConfigProperties();
        configProps.applyUserSpecificProperties(properties_);
        Configuration.init();
	}

    /**
     *@see UserTransactionService
     */

    public CompositeTransactionManager
            getCompositeTransactionManager ()
    {
        return Configuration.getCompositeTransactionManager();
    }


    /**
     * @see com.atomikos.icatch.UserTransactionService#registerResource(com.atomikos.datasource.RecoverableResource)
     */
    public void registerResource(RecoverableResource res)
    {
        Configuration.addResource(res);
        
    }

    /**
     * @see com.atomikos.icatch.UserTransactionService#registerLogAdministrator(com.atomikos.icatch.admin.LogAdministrator)
     */
    public void registerLogAdministrator(LogAdministrator admin)
    {
        Configuration.addLogAdministrator(admin);
        
    }   

	public void removeResource ( RecoverableResource res ) 
	{
		Configuration.removeResource(res.getName());
		
	}

	public void removeLogAdministrator ( LogAdministrator admin ) 
	{
		Configuration.removeLogAdministrator(admin);
	}

	public void registerTransactionServicePlugin ( TransactionServicePlugin listener ) 
	{
		Configuration.registerTransactionServicePlugin(listener);
	}

	public void removeTransactionServicePlugin ( TransactionServicePlugin listener ) 
	{
		Configuration.unregisterTransactionServicePlugin(listener);
	}


	/**
	 * Convenience shutdown method for DI containers like Spring. 
	 * 
	 */
	
	public void shutdownForce() 
	{
		shutdown ( true );
		
	}

	/**
	 * Convenience shutdown method for DI containers like Spring. 
	 * 
	 */
	
	public void shutdownWait() 
	{
		shutdown ( false );
		
	}

	/**
	 * Dependency injection of all resources to be added during init.
	 * 
	 * @param resources
	 */
	public void setInitialRecoverableResources ( List<RecoverableResource> resources ) 
	{
		resources_ = resources;
		
	}

	/**
	 * Dependency injection of all administrators to be added during init.
	 * 
	 * @param administrators
	 */
	public void setInitialLogAdministrators ( List<LogAdministrator> administrators ) 
	{
		logAdministrators_ = administrators;
		
	}

	/**
	 * Dependency injection of extra plugins to be added during init.
	 * @param listeners
	 */
	public void setInitialTransactionServicePlugins ( List<TransactionServicePlugin> listeners ) 
	{
		tsListeners_ = listeners;
		
	}
	
	/**
	 * Convenience init method for DI containers like Spring. 
	 * 
	 */
	public void init()
	{
		initialize();
	}



	public void init ( Properties properties ) throws SysException {
		properties_ = properties;
		initialize();
	}


}
