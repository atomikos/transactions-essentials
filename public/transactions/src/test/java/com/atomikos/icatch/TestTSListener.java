package com.atomikos.icatch;

import java.util.Properties;

public class TestTSListener implements TSListener {

	private boolean initedBefore;
	private boolean initedAfter;
	private boolean shutdownBefore;
	private boolean shutdownAfter;
	
	private Properties properties;
	
	public TestTSListener()
	{
		reset();
	}
	
	public void init(boolean before, Properties properties) {
		if ( before ) initedBefore = true;
		else initedAfter = true;
		this.properties = properties;

	}

	public void shutdown(boolean before) {
		if ( before ) shutdownBefore = true;
		else shutdownAfter = true;

	}

	public boolean isInitedAfter() {
		return initedAfter;
	}

	public boolean isInitedBefore() {
		return initedBefore;
	}

	public boolean isShutdownAfter() {
		return shutdownAfter;
	}

	public boolean isShutdownBefore() {
		return shutdownBefore;
	}
	
	public boolean hasProperties()
	{
		return  properties != null;
	}
	
	public void reset()
	{
		initedAfter = false;
		initedBefore = false;
		shutdownAfter = false;
		shutdownBefore = false;
		properties = null;
	}

}
