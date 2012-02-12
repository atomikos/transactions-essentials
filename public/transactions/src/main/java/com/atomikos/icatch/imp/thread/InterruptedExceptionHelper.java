package com.atomikos.icatch.imp.thread;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import com.atomikos.icatch.system.Configuration;

public class InterruptedExceptionHelper 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(InterruptedExceptionHelper.class);

	public static void handleInterruptedException ( InterruptedException e ) 
	{
		Configuration.logWarning ( "Thread interrupted " , e );
		// interrupt again - cf http://www.javaspecialists.co.za/archive/Issue056.html
		Thread.currentThread().interrupt();
	}
}
