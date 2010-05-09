package com.atomikos.icatch.jta.jetty;

import java.util.Iterator;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jndi.Util;

import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.jta.J2eeTransactionManager;
import com.atomikos.icatch.jta.J2eeUserTransaction;
import com.atomikos.jdbc.SimpleDataSourceBean;
import com.atomikos.jdbc.nonxa.NonXADataSourceBean;


/**
 * 
 * Copyright &copy; 2003-2004 Atomikos. All rights reserved.
 * 
 * An implementation of TMService for Jetty integration.
 * Use an instance of this class in the JettyPlus startup
 * configuration file. Any Atomikos datasource you need in your
 * JTA transactions needs to be configured and registered
 * with this TMService during JettyPlus startup. 
 * Refer to the Jetty docs on how to do this.
 *
 */


public class TMService extends org.mortbay.jetty.plus.TMService
{
	public static final String DEFAULT_SERVICE_NAME = "AtomikosTMService";
	
	private static final Log log = LogFactory.getLog ( TMService.class );
	
	private java.util.Map datasources_;
	
	private java.util.Map nonXaDatasources_;

	
	private UserTransactionService uts_;
	
	private boolean initOnStart_;
	//if true: also init TM upon start event

    /**
     * Creates a new instance. 
     */
    
    public TMService()
    {
        super();
        setName( DEFAULT_SERVICE_NAME );
        reset();
        
    }
    
    private void reset()
    {
    	uts_ = null;
    	datasources_ = new java.util.HashMap();
    	nonXaDatasources_ = new java.util.HashMap();
    	initOnStart_ = false;
    }
    
    /**
     * Set initialization on start on or off.
     * If enabled, then the TMService instance start()
     * will trigger startup of the transaction service 
     * as well. The default is false. 
     * @param value True if start should also trigger
     * transaction service startup. <b>Leave this to false
     * if you are using the Atomikos control panel 
     * web application.</b>
     */
    
    public void setInitOnStart ( boolean value )
    {
    	initOnStart_ = value;
    }

    /**
     * @see org.mortbay.jetty.plus.TMService#getTransactionManager()
     */
    
    public TransactionManager getTransactionManager()
    {
 		TransactionManager ret = null;
 		ret = new J2eeTransactionManager();
        return ret;
    }

    /**
     * @see org.mortbay.jetty.plus.TMService#getUserTransaction()
     */
    
    public UserTransaction getUserTransaction()
    {
        UserTransaction ret = null;
        ret = new J2eeUserTransaction();
        return ret;
    }
    
    /**
     * Register a data source bean instance for JNDI
     * and for recovery as well. 
     * @param dataSource The data source bean. This bean
     * will be bound in JNDI based on the uniqueResourceName
     * attribute, and will normally be recovered instantly
     * (unless the database server is not running at the 
     * time of Jetty startup).
     */
    
    public synchronized void registerSimpleDataSourceBean ( 
    SimpleDataSourceBean dataSource )
    {
    	if ( dataSource.getUniqueResourceName() == null )
    		log.warn( "IGNORING DATASOURCE: uniqueResourceName must not be NULL!!!");
    	else
    		datasources_.put( dataSource.getUniqueResourceName() , dataSource);
    }
    
    /**
     * Register a non-xa datasource bean for JNDI.
     * Since non-xa datasources are not recoverable
     * with respect to JTA transactions, there is no
     * explicit recovery in this case. This implies that
     * all pending database transactions will be 
     * rolled back by the database.
     * @param dataSource 
     */
    
    public synchronized void registerNonXADataSourceBean ( 
    NonXADataSourceBean dataSource )
    {
    	if ( dataSource.getUniqueResourceName() == null ) 
			log.warn( "IGNORING DATASOURCE: jndiName must not be NULL!!!");
    	else 
    		nonXaDatasources_.put ( dataSource.getUniqueResourceName() , dataSource ); 
    }
    
    
    public void start() throws Exception
    {
    	if ( isStarted() || uts_ != null ) {
			log.info ( getName() + 
			" is already running - ignoring start() request." );
    		return;
    	} 
    	
		log.info ( "Starting service: " + getName() );
    	
    	Context ctx = null;
    	
    	
		
		TSInitInfo info = null;
		
		try {
			String jettyHome = System.getProperty ( "jetty.home");
			if ( initOnStart_ ) {
			
				if ( jettyHome == null ) jettyHome = ".";
				if ( System.getProperty ( 
					UserTransactionServiceImp.FILE_PATH_PROPERTY_NAME ) == null )
					System.setProperty ( UserTransactionServiceImp.FILE_PATH_PROPERTY_NAME ,
							jettyHome +  java.io.File.separator + "etc" + 
							java.io.File.separator +
							"jta.properties" );
				Properties p = new Properties();
				p.setProperty ( "com.atomikos.icatch.service" , 
				"com.atomikos.icatch.trmi.UserTransactionServiceFactory");	
				p.setProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME , "none");
		
				if ( jettyHome != null ){
					p.setProperty ( AbstractUserTransactionServiceFactory.OUTPUT_DIR_PROPERTY_NAME ,  jettyHome + 
									java.io.File.separator + "logs" + java.io.File.separator);
					p.setProperty ( AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME , jettyHome + 
									java.io.File.separator + "logs" + java.io.File.separator );
				}
				uts_ = new UserTransactionServiceImp ( p );
				info = uts_.createTSInitInfo();
				
			}
		}
		catch ( Exception e ) {
			log.warn ( e );
			throw e;
		}
    	
    	try {
    		ctx = new InitialContext();
			log.debug ( "TMService: using initial context of class: " + 
    			ctx.getClass().getName() );
			log.debug ( "TMService: using initial context factory: " + 
    			System.getProperty ( "java.naming.factory.initial") );
			
    	}
		catch ( Exception e ) {
			log.warn ( e );
			throw new Exception ( "Error initializing JNDI context:+ " + e.getMessage() );
		}
		
		try {
			//iterate over datasources
			Iterator names = datasources_.keySet().iterator();
			while ( names.hasNext() ) {
				//register each in info
				String name = ( String ) names.next();
				SimpleDataSourceBean bean = (SimpleDataSourceBean ) datasources_.get ( name );
				//call getTransactionalResource to trigger setup, which
				//will also do registration
				try {
					bean.getLoginTimeout();
				
				//info.registerResource ( bean.getTransactionalResource() );
				//register each in JNDI
				
		
				Util.bind ( ctx , bean.getUniqueResourceName() , bean );	
				log.debug ( "Registered resource with name: " + bean.getUniqueResourceName() );
				}
				catch ( Exception notAvailable ) {
					//catch exception, or TM fails if datasource
					//is unavailable
					log.warn ( "DataSource initialization failure: " + 
						bean.getUniqueResourceName() , notAvailable );
				}
			}
			names = nonXaDatasources_.keySet().iterator();
			while ( names.hasNext() ) {
				//register each in info
				String name = ( String ) names.next();
				NonXADataSourceBean bean = (NonXADataSourceBean ) nonXaDatasources_.get ( name );
				//register each in JNDI
				try {
				
					Util.bind ( ctx , bean.getUniqueResourceName() , bean );	
					log.debug ( "Registered resource with name: " + bean.getUniqueResourceName() );
				}
				catch ( Exception notAvailable ) {
					//catch exception, or TM fails if datasource
					//is unavailable
					log.warn ( "DataSource initialization failure: " + 
						bean.getUniqueResourceName() , notAvailable );
				}
			}			
			
		}
		catch ( Exception e ) {
			log.warn ( e );
			throw new Exception ( "Error registering DataSources: " + e.getMessage() );
		}

	    if ( initOnStart_ ) {		
	   
			try {
				uts_.init ( info );
			}
			catch ( SysException e ) {
				e.printStackTrace();
				log.warn ( e );
				throw new Exception ( "Error initializing transaction service: " + e.getMessage() );
			}
		}
		
		
		try {
			//bind UserTx in JNDI	
			Util.bind ( ctx , getJNDI() , getUserTransaction());
			log.debug ( "UserTransaction bound with name: " + getJNDI() );
		}
		catch ( Exception e ) {
			log.warn ( e );
			throw new Exception ( "Error binding UserTransaction in JNDI: " + e.getMessage() );
		}
		
		try {
			//bind TM in JNDI
			Util.bind ( ctx , getTransactionManagerJNDI() , getTransactionManager() );
			log.debug ( "TransactionManager bound with name: " + getTransactionManagerJNDI() );
		}
		catch ( Exception e ) {
			log.warn ( e );
			throw new Exception ( "Error binding TransactionManager in JNDI: " + e.getMessage() );
		}
    	
    	super.start();
		log.info ( getName() + " started.");
    }
    
    public void stop() throws InterruptedException
    {
    	if ( isStarted() ) {
			log.info ( "Stopping service " + getName() + "..." );
    		//uts_.shutdown ( true );
    		reset();
    		super.stop();
			log.info ( "Stopped service: " + getName() );
    	}
    	else {
			log.debug ( "Stop: no transaction service is running.");
    	}
    	
    }

}
