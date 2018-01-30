/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */



package com.atomikos.icatch.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionServicePlugin;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.provider.ConfigProperties;

/**
 * This is the main class for creating a UserTransactionService instance.
 * This class is the client's main entry point into the transaction engine.
 *
 * Instances can be serialized to disk and re-read at a later time.
 * Note: deserialization will only work in the SAME configuration as the
 * one that did the streaming out. In particular, if no identical Atomikos
 * transaction service is present in the target VM then the process
 * of deserialization will be erroneous.
 */

public final class UserTransactionServiceImp
        implements java.io.Serializable , UserTransactionService
{
	

	private static final long serialVersionUID = -3374591336514451887L;

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
		for (RecoverableResource resource : resources_) {
			Configuration.addResource ( resource );
		}
		for (LogAdministrator logAdministrator : logAdministrators_) {
			Configuration.addLogAdministrator ( logAdministrator );
		}
        for (TransactionServicePlugin nxt : tsListeners_) {
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
     * @see com.atomikos.icatch.config.UserTransactionService#registerResource(com.atomikos.datasource.RecoverableResource)
     */
    public void registerResource(RecoverableResource res)
    {
        Configuration.addResource(res);
        
    }

    /**
     * @see com.atomikos.icatch.config.UserTransactionService#registerLogAdministrator(com.atomikos.icatch.admin.LogAdministrator)
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


	/**
	 * Initializes with given properties.
	 */
	public void init ( Properties properties ) throws SysException {
		properties_ = properties;
		initialize();
	}

	@Override
	public void shutdown(long maxWaitTime) throws IllegalStateException {
		Configuration.shutdown(maxWaitTime);
	}


}
