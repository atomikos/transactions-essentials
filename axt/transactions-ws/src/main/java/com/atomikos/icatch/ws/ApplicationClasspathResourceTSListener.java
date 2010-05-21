package com.atomikos.icatch.ws;

import java.util.Properties;

import com.atomikos.icatch.TSListener;
import com.atomikos.icatch.system.Configuration;

public abstract class ApplicationClasspathResourceTSListener implements
		TSListener {

	protected String resourceName;
	protected String logDir;
	protected String fileName;
	protected long interval;
	protected boolean stayRegistered;

	protected ApplicationClasspathResourceTSListener ( String resourceName ,
			String logDir , String fileName , 
			long interval , boolean stayRegistered )
	{
		this.resourceName = resourceName;
		this.logDir = logDir;
		this.fileName = fileName;
		this.interval = interval;
		this.stayRegistered = stayRegistered;
	}
	
	public void shutdown(boolean before) {
		if ( ! stayRegistered && ! before ) 
			Configuration.removeTSListener ( this );
		
	}
	
	public void init (
			boolean before, Properties properties) 
	{
		if ( before && 
				Configuration.getResource ( resourceName ) == null ) {
			
		   //re-register resource if needed, to 
		   //allow independent restart of TM
		   	
		   createAndRegisterResource();
			
		}
		
	}

	protected abstract void createAndRegisterResource();
	
}
