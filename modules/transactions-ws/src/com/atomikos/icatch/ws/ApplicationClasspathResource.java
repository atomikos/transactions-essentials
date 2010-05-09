package com.atomikos.icatch.ws;

/**
 * Copyright &copy; 2006, Atomikos. All rights reserved.
 * 
 * Reusable superclass for application classpath-specific recovery.
 * 
 */

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.system.Configuration;
import com.atomikos.persistence.LogException;
import com.atomikos.persistence.ObjectLog;
import com.atomikos.persistence.imp.FileLogStream;
import com.atomikos.persistence.imp.StreamObjectLog;
import com.atomikos.util.UniqueIdMgr;

public abstract class ApplicationClasspathResource implements RecoverableResource 
{

	private static final Map classLoaderToResourceMap = new HashMap ();

	/**
	 * Registers a resource. 
	 * 
	 * @param resource
	 * @param cl
	 */
	public static void registerResource(ApplicationClasspathResource resource, ClassLoader cl) {
		   //This is needed to make sure that the file IO
	   //succeeds during registration: the core by itself doesn't know what
	   //resource to use, so it looks up the resource by the classloader of the
	   //activity participant (app-specific). This method also add the resource to the
	   //overall Configuration (needed for recovery).
	   //
	    Configuration.addResource ( resource );
	    classLoaderToResourceMap.put ( cl, resource );
	
	}
	
    protected static ApplicationClasspathResource getResourceForClassLoader (
            ClassLoader cl )
    {
        return (ApplicationClasspathResource) classLoaderToResourceMap.get ( cl );
    }

	protected String name;
	protected ObjectLog participantLog;
	protected Map idToParticipantMap;
	private boolean closed = false;
	protected UniqueIdMgr idMgr;
	
	protected ApplicationClasspathResource ( String name , String logBaseDir ,
            String logBaseName , long checkPointInterval , Console console )
    throws ResourceException
    {
    

    		this.name = name;
    		
		if ( ! logBaseDir.endsWith ( File.separator ) )
			logBaseDir += File.separator;
		try {
			idMgr = new UniqueIdMgr ( logBaseName , logBaseDir );
			FileLogStream ls = new FileLogStream ( logBaseDir, logBaseName,
					console );
			participantLog = new StreamObjectLog ( ls, checkPointInterval,
					console );

			participantLog.init();
		} catch ( Exception e ) {
			Configuration.logWarning ( "ApplicationClasspathResource: error in init", e );
			Stack errors = new Stack();
			errors.push(e);
			throw new ResourceException (
					"Error in init of ApplicationClasspathResource", errors );
		}
	}
    

	public void setRecoveryService(RecoveryService recoveryService) throws ResourceException {
	    // null during testing
	    if ( recoveryService != null ) {
	        closed = false;
	        recoveryService.recover ();
	    }
	
	}

	public void removeParticipant(String id) {
		try {
			participantLog.delete ( id );
		} catch ( LogException e ) {
			Configuration.logWarning ( "ApplicationClasspathResource: could not remove activity participant from log: " + id );
			//ignore error for the rest: activity will stay in logs, but that is OK
		}
	}

	public void endRecovery() throws ResourceException {
		
			
		//remove those participants that were in log but not in TM logs
		//since these were added prematurely (crash between flush and TM flush)
		
		recoverAllIfNeeded ();
	
	    Iterator it = idToParticipantMap.entrySet ().iterator ();
	    while ( it.hasNext () ) {
	    	   Map.Entry entry = ( Map.Entry ) it.next();
	        
	    	   //no cancel logic needed: complete was not called yet -> no permanent effects
	    	   //so merely delete the entry from the logs
	        try { 
	            participantLog.delete ( entry.getKey() );
	            it.remove ();
	
	        } catch ( Exception e ) {
	            Configuration.logWarning ( "ApplicationClasspathResource: error during endRecovery"  , e );
	        } 
	    }
	        
	    idToParticipantMap = null;
			
	
	}

	public void close() throws ResourceException {
		try {
	            participantLog.close ();
	    } catch ( LogException e ) {
	        Configuration.logDebug (
	                "ApplicationClasspathResource: error closing logs", e );
	        Stack errors = new Stack ();
	        errors.push ( e );
	        throw new ResourceException ( "Error in close", errors );
	    }
	    closed = true;
	
	}

	public String getName() {
		return this.name;
	}

	public boolean isSameRM(RecoverableResource res) throws ResourceException {
		return getName ().equals ( res.getName () );
	}

	public boolean isClosed() {
		return closed;
	}

	protected abstract void recoverAllIfNeeded();
}
