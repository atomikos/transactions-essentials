package com.atomikos.icatch.ws.j2ee;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.atomikos.icatch.TSListener;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.icatch.ws.ApplicationClasspathResourceTSListener;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A context listener for recovery of application-specific participants. By adding
 * a listener to your J2EE web application you can avoid having to put your
 * custom participant classes in the core server classpath of your application
 * server.
 * <p>
 * A listener should be configured with the following context parameters:
 * <p>
 * <table border="1">
 * <tr>
 * <td><b>com.atomikos.icatch.log_base_dir</b></td>
 * <td> (Full) path to an <b>existing</b> folder where the log files for the
 * J2EE web application's participants should be put. If not specified then the
 * full path of the installed application's WEB-INF folder will be taken.
 * <b>Please make sure that you use a unique log folder for each deployed
 * application!</b> </td>
 * </tr>
 * <tr>
 * <td><b>com.atomikos.icatch.log_base_name</b></td>
 * <td> Base name of the log files generated and maintained for the J2EE web
 * application. If not specified then the default name 'participants' will be
 * taken. </td>
 * </tr>
 * <tr>
 * <td><b>com.atomikos.icatch.checkpoint_interval</b></td>
 * <td> The interval (in number of log writes) after which purging of old log
 * entries should happen. Defaults to 500. </td>
 * </tr>
 * </table>
 */

public abstract class ApplicationClasspathRecoveryListener implements
		ServletContextListener {

	/**
	 * Name of the context parameter that indicates the location of the logging
	 * folder where activities of this application are logged.
	 */
	public static final String LOG_DIR_PARAMETER_NAME = AbstractUserTransactionServiceFactory.LOG_BASE_DIR_PROPERTY_NAME;
	/**
	 * Name of the context parameter that indicates the base name of the log
	 * files for this application.
	 * 
	 */
	public static final String LOG_FILE_PARAMETER_NAME = AbstractUserTransactionServiceFactory.LOG_BASE_NAME_PROPERTY_NAME;
	/**
	 * Name of the context parameter that indicates the checkpoint interval for
	 * purging old log entries. The interval is expressed as a number of log
	 * writes after which purging happens.
	 */
	public static final String LOG_CHECKPOINT_PARAMETER_NAME = AbstractUserTransactionServiceFactory.CHECKPOINT_INTERVAL_PROPERTY_NAME;
	protected static boolean listenerRegistered = false;

	protected static String getLogDir(ServletContext context) {
	    String ret = "WEB-INF";
	
	    ret = context.getRealPath ( "WEB-INF" );
	    if ( ret == null )
	        System.err
	                .println ( "WARNING: ACTIVITIES WILL ONLY WORK IF YOU UNZIP THE WAR FILE!!!" );
	    if ( context.getInitParameter ( LOG_DIR_PARAMETER_NAME ) != null ) {
	        ret = context.getInitParameter ( LOG_DIR_PARAMETER_NAME ).trim ();
	    }
	
	    if ( !ret.endsWith ( File.separator ) )
	        ret = ret + File.separator;
	
	    return ret;
	}

	protected static String getLogFile(ServletContext context) {
	    String ret = "participants";
	    if ( context.getInitParameter ( LOG_FILE_PARAMETER_NAME ) != null ) {
	        ret = context.getInitParameter ( LOG_FILE_PARAMETER_NAME ).trim ();
	    }
	    return ret;
	}

	protected static long getCheckpointInterval(ServletContext context) {
	    long ret = 500;
	    String valAsString = context
	            .getInitParameter ( LOG_CHECKPOINT_PARAMETER_NAME );
	    if ( valAsString != null ) {
	        try {
	            ret = Long.parseLong ( valAsString );
	        } catch ( Exception notANumber ) {
	            System.err.println ( "ERROR: CONTEXT PARAMETER "
	                    + LOG_CHECKPOINT_PARAMETER_NAME
	                    + " MUST BE A VALID LONG NUMBER!" );
	            System.err.println ( "USING DEFAULT VALUE INSTEAD" );
	            ret = 500;
	        }
	    }
	    return ret;
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent context) {
	
	
	}
	
	protected abstract ApplicationClasspathResourceTSListener 
	createApplicationClasspathResourceTSListener ( 
			String resourceName ,
			String logDir , String fileName , long interval
	);
	
	protected abstract void createAndRegisterResourceIfNotRegisteredAlready (
			String logDir , String fileName , long interval 
	);

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent context) {
	    ServletContext sContext = context.getServletContext ();
	    String logDir = getLogDir ( sContext );
	    String fileName = getLogFile ( sContext );
	    long interval = getCheckpointInterval ( sContext );
	    
	    //add a TS listener to survive TS restarts
	    registerStartupListener ( logDir , logDir , fileName , interval );
	
	    //add a resource just in case the TS is already running
	    //but don't re-register on restart of this webapp!
	    createAndRegisterResourceIfNotRegisteredAlready(
	    		logDir , fileName , interval );
	    
	
	}

	synchronized void registerStartupListener(String resourceName, String logDir, String fileName, long interval) {
			if ( ! listenerRegistered ) {
				
				TSListener l = createApplicationClasspathResourceTSListener (
						resourceName ,logDir , 
						fileName ,  interval
				);
				
				
				Configuration.addTSListener ( l );
				
				listenerRegistered = true;
			}
	}

}
