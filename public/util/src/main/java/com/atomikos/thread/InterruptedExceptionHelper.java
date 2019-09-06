/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.thread;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class InterruptedExceptionHelper 
{
	private static final Logger LOGGER = LoggerFactory.createLogger(InterruptedExceptionHelper.class);

	public static void handleInterruptedException ( InterruptedException e ) 
	{
		LOGGER.logWarning ( "Thread interrupted " , e );
		// interrupt again - cf http://www.javaspecialists.co.za/archive/Issue056.html
		Thread.currentThread().interrupt();
	}
}
