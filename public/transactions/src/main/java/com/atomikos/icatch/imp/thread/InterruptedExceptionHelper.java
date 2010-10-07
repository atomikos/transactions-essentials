package com.atomikos.icatch.imp.thread;

import com.atomikos.icatch.system.Configuration;

public class InterruptedExceptionHelper 
{
	public static void handleInterruptedException ( InterruptedException e ) 
	{
		Configuration.logWarning ( "Thread interrupted " , e );
		// interrupt again - cf http://www.javaspecialists.co.za/archive/Issue056.html
		Thread.currentThread().interrupt();
	}
}
