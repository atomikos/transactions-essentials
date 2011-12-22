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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TSListener;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.util.ClassLoadingHelper;

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

	private static final long serialVersionUID = -3374591336514451887L;

	/**
     * Constant denoting the system property name that suggest NOT to use
     * any configuration file for the transaction service's properties.
     * If this a system property with this name is set to an arbitrary value
     * then the transaction service will attempt initialization based on
     * system properties only. In that case, all the parameters that would
     * normally be set through the properties file have to be supplied as
     * system properties with the same name and corresponding value.
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

    public static final String FILE_PATH_PROPERTY_NAME = "com.atomikos.icatch.file";
    
    /**
     * The name of the system property to disable printing 'Using init file...' messages
     * to System.err - if this system property to an arbitrary value then no such message
     * will be shown on System.err during startup.
     */
    
    public static final String HIDE_INIT_FILE_PATH_PROPERTY_NAME = "com.atomikos.icatch.hide_init_file_path";

    /**
     * The default file name for the transaction service's init parameters.
     *
     */

    
    public static final String DEFAULT_PROPERTIES_FILE_NAME = "transactions.properties";
    
    private static final String PRE_3_0_DEFAULT_PROPERTIES_FILE_NAME = "jta.properties";

    private Properties properties_;
    
    private List tsListeners_;
    private List logAdministrators_;
    private List resources_;

    private transient UserTransactionService delegate_;
    //the instance to delegate to; obtained from factory
    
    /**
     * Replace ${...} sequence with the referenced value from the given properties or 
     * (if not found) the system properties -
     * contributed through Marian Kelc (marian.kelc@eplus.de)
     * E-Plus Mobilfunk GmbH &amp; Co. KG, Germany
     */
     private static String evaluateReference ( String value , Properties properties )
     {
         String result = value;
         //by default, the value as-is is returned
         
         int startIndex = value.indexOf ( '$' );
         if ( startIndex > -1 && value.charAt ( startIndex +1 ) == '{') {
        	 	//at least one reference is found
             int endIndex = value.indexOf ( '}' );
             if ( startIndex + 2 == endIndex )
                 throw new IllegalArgumentException ( "property ref cannot refer to an empty name: ${}" );
             if ( endIndex == -1 )
                 throw new IllegalArgumentException ( "unclosed property ref: ${" + value.substring ( startIndex + 2 ) );

             //strip-off reference characters -> get the referenced property name 
             String subPropertyKey = value.substring ( startIndex + 2, endIndex );
             //the properties take precedence -> try them first
             String subPropertyValue = properties.getProperty ( subPropertyKey );
            	if ( subPropertyValue == null ) {
            		//not found in properties -> try system property
            		subPropertyValue = System.getProperty ( subPropertyKey );
            	}
             
             if ( subPropertyValue != null ) {
            	    //in-line refs supported - result is prefix + value + suffix !!!
                 result = result.substring ( 0, startIndex ) + subPropertyValue + result.substring ( endIndex +1 );
                 //two or more refs supported - evaluate any remaining references in the value
                 result =  evaluateReference ( result , properties );
             }
             else {
            	 	//referenced value not found -> ignore any other references and return value as-is
            	    //NOTE: trying to resolve further references would lead to infinite recursion
             }
            	 
         }
         
         return result;
     }


     private static void logToStdErr ( String msg ) 
     {
 		if ( System.getProperty ( HIDE_INIT_FILE_PATH_PROPERTY_NAME ) == null ) {
 			System.err.println ( msg );
 		}
 	}
 
    
    private static Properties mergeProperties ( Properties first, Properties defaults )
    {
    	
    	Enumeration names = first.propertyNames();
    	while ( names.hasMoreElements() ) {
    		String name = ( String ) names.nextElement();
    		String value = first.getProperty ( name );
    		defaults.setProperty ( name , value );
    	}
    	return defaults;
    }
    
    private URL findPropertiesFileInClasspath ( String fileName )
    {
    		URL ret = null;
    		
    		// FIRST: look in application classpath (cf ISSUE 10091)
    		ret = ClassLoadingHelper.loadResourceFromClasspath( getClass() , fileName );
    		
    		if ( ret == null ) {
    			//if not found in app classpath: try what we always did:
    			//lookup as a system resource, without extra prefix
    			ret = getClass().getClassLoader().
			   	getSystemResource ( fileName );
    		}
    		
    		return ret;
    }

    private Properties findProperties()
    {
        Properties p = new Properties();

        
         //look for system property that specifies the transactions.properties file
         //if not found: look for properties file in classpath
         //if not found: use default properties

		  java.net.URL url = null;
          if ( System.getProperty ( NO_FILE_PROPERTY_NAME )  == null ) {

              String filename = System.getProperty ( FILE_PATH_PROPERTY_NAME );
              if ( filename == null ) {
                  filename = DEFAULT_PROPERTIES_FILE_NAME;
                  logToStdErr ( "No properties path set - looking for " +
                       DEFAULT_PROPERTIES_FILE_NAME +
                       " in classpath..." );
				   url = findPropertiesFileInClasspath ( filename );
				   if ( url == null ) {
					   filename = PRE_3_0_DEFAULT_PROPERTIES_FILE_NAME;
		               logToStdErr ( DEFAULT_PROPERTIES_FILE_NAME + " not found - looking for " +
		                       PRE_3_0_DEFAULT_PROPERTIES_FILE_NAME +
		                       " in classpath..." );
					   url = findPropertiesFileInClasspath ( filename );
				   }
              }
              else {
              		//a file was given
              		java.io.File file = new java.io.File ( filename );
              		try
                    {
                        url = file.toURL();
                    }
                    catch (MalformedURLException e)
                    {
                        //ignore: just leave url null
                        //and use default
                    }
              		
              }
              try {
                  
                  
                  if ( url == null ) throw new IOException();
                  logToStdErr ( "Using init file: " + url.getPath() );
                  InputStream in = url.openStream();
                  p.load ( in );
                  in.close();
              }
              catch ( IOException io ) {
              	 //io.printStackTrace();
              	 String msg = "Failed to open transactions properties file - using default values";
              	 //Configuration.logWarning ( msg , io );
                 logToStdErr ( msg );
                 //use the default standalone service
                 p.setProperty ( "com.atomikos.icatch.service" ,
                                 "com.atomikos.icatch.standalone.UserTransactionServiceFactory" );
              }
          }
          else {
              //NO property file should be used, so try system property
              //but ONLY for the factory class: each implementation should
              //get its own specific properties

              p.setProperty ( "com.atomikos.icatch.service" ,
                              System.getProperty ( "com.atomikos.icatch.service" ));
          }
          
          substitutePlaceHolderValues(p);

          return p;


    }


	private void substitutePlaceHolderValues(Properties p) {
		//resolve referenced values with ant-like ${...} syntax
          java.util.Enumeration allProps= p.propertyNames();
          while ( allProps.hasMoreElements() ) {
              String key = ( String ) allProps.nextElement();
              String raw = p.getProperty ( key );
              String value= evaluateReference ( raw , p );
              if ( !raw.equals ( value ) ) {
                p.setProperty ( key, value );
              }
          }
	}
    




	private String getOrFindProperty ( String name )
    {
    	
        if ( properties_ == null ) properties_ = findProperties();

        return properties_.getProperty ( name );
    }
    
    /**
     * Default constructor.
     *
     */

    public UserTransactionServiceImp ()
    {
		tsListeners_ = new ArrayList();
		logAdministrators_ = new ArrayList();
		resources_ = new ArrayList();
       
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
    	
    	properties_ = findProperties();
    		
    	//override defaults in file with specified properties   		
        properties_.putAll ( properties );
    }

    private void checkInit ()
    {

        if ( delegate_ != null ) return;

        String factoryClassName = getOrFindProperty ( "com.atomikos.icatch.service" );

        if ( factoryClassName == null )
            throw new SysException (
            "UserTransactionServiceImp: property not defined: com.atomikos.icatch.service" );

        try {
            Class factoryClass = ClassLoadingHelper.loadClass ( factoryClassName );
            UserTransactionServiceFactory factory =
                    ( UserTransactionServiceFactory ) factoryClass.newInstance ();
            delegate_ = factory.getUserTransactionService ( properties_ );
            //This should initialize the properties with whatever is specified and
            //use SYSTEM_DEPENDENT DEFAULT values for others
        }
        catch ( Exception e ) {
            Stack errors = new Stack ();
            errors.push ( e );
            throw new SysException ( "Error in init of UserTransactionServiceImp: " + e.getMessage() , errors );
        }

    }

    /**
     *
     * @see UserTransactionService
     */
    public TSInitInfo createTSInitInfo ()
    {
        TSInitInfo ret = null;
        checkInit ();
        ret = delegate_.createTSInitInfo ();
        Properties p = mergeProperties ( properties_ , ret.getProperties() );
        ret.setProperties ( p );
        return ret;
    }

    /**
     *
     * @see UserTransactionService
     */

    public void shutdown ( boolean force )
            throws IllegalStateException
    {
        checkInit ();
        delegate_.shutdown ( force );
    }


    /**
     *@see UserTransactionService
     */

    public void init ( TSInitInfo info )
            throws SysException
    {
        checkInit ();
        Iterator it = resources_.iterator();
        while ( it.hasNext() ) {
        	RecoverableResource nxt = ( RecoverableResource ) it.next();
        	registerResource ( nxt );
        }
        it = logAdministrators_.iterator();
        while  ( it.hasNext() ) {
        	LogAdministrator nxt = ( LogAdministrator ) it.next();
        	registerLogAdministrator ( nxt );
        }
        it = tsListeners_.iterator();
        while ( it.hasNext() ) {
        	TSListener nxt = ( TSListener ) it.next();
        	registerTSListener ( nxt );
        }
        delegate_.init ( info );
    }

    /**
     *@see UserTransactionService
     */

    public CompositeTransactionManager
            getCompositeTransactionManager ()
    {
        checkInit ();
        return delegate_.getCompositeTransactionManager ();
    }


   

    /**
     *@see UserTransactionService
     */

    public ImportingTransactionManager getImportingTransactionManager ()
    {
        checkInit ();
        return delegate_.getImportingTransactionManager ();
    }

    /**
     *@see UserTransactionService
     */

    public ExportingTransactionManager getExportingTransactionManager ()
    {
        checkInit ();
        return delegate_.getExportingTransactionManager ();
    }



    /**
     * @see com.atomikos.icatch.UserTransactionService#registerResource(com.atomikos.datasource.RecoverableResource)
     */
    public void registerResource(RecoverableResource res)
    {
        checkInit();
        delegate_.registerResource(res);
        
    }

    /**
     * @see com.atomikos.icatch.UserTransactionService#registerLogAdministrator(com.atomikos.icatch.admin.LogAdministrator)
     */
    public void registerLogAdministrator(LogAdministrator admin)
    {
        checkInit();
        delegate_.registerLogAdministrator ( admin );
        
    }

    /**
     * @see com.atomikos.icatch.UserTransactionService#getResources()
     */
    public Enumeration getResources()
    {
        checkInit();
        return delegate_.getResources();
    }

    /**
     * @see com.atomikos.icatch.UserTransactionService#getLogAdministrators()
     */
    public Enumeration getLogAdministrators()
    {
        checkInit();
        return delegate_.getLogAdministrators();
    }

	public void removeResource ( RecoverableResource res ) 
	{
		checkInit();
		delegate_.removeResource ( res );
		
	}

	public void removeLogAdministrator ( LogAdministrator admin ) 
	{
		checkInit();
		delegate_.removeLogAdministrator ( admin );
	}

	public void registerTSListener ( TSListener listener ) 
	{
		checkInit();
		delegate_.registerTSListener ( listener );
	}

	public void removeTSListener ( TSListener listener ) 
	{
		checkInit();
		delegate_.removeTSListener ( listener );
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
	public void setInitialRecoverableResources ( List resources ) 
	{
		resources_ = resources;
		
	}

	/**
	 * Dependency injection of all administrators to be added during init.
	 * 
	 * @param administrators
	 */
	public void setInitialLogAdministrators ( List administrators ) 
	{
		logAdministrators_ = administrators;
		
	}

	/**
	 * Dependency injection of all listeners to be added during init.
	 * @param listeners
	 */
	public void setInitialTSListeners ( List listeners ) 
	{
		tsListeners_ = listeners;
		
	}
	
	/**
	 * Convenience init method for DI containers like Spring. 
	 * 
	 */
	public void init()
	{
		TSInitInfo info = createTSInitInfo();
		init ( info );
	}



	public void init ( Properties properties ) throws SysException {
		TSInitInfo info = createTSInitInfo();
		mergeProperties ( properties , info.getProperties() );
		init ( info );
	}


}
