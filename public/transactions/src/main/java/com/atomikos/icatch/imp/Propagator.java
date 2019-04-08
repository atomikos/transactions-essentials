/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.thread.TaskManager;

/**
 * A propagator sends PropagationMessages to participants.
 */

class Propagator
{
	private static final Logger LOGGER = LoggerFactory.createLogger(Propagator.class);
	
    static long RETRY_INTERVAL = Configuration.getConfigProperties().getOltpRetryInterval();


    private boolean threaded_ = true;

    
    Propagator ( boolean threaded )
    {
    		threaded_ = threaded;
    }

    
    public synchronized void submitPropagationMessage ( PropagationMessage msg )
    {
    		PropagatorThread t = new PropagatorThread ( msg );
    		if ( threaded_ ) {
    			TaskManager.SINGLETON.executeTask ( t );
    		} else {
    			t.run();
    		}
    
    }


    
    private static class PropagatorThread implements Runnable
    {
    		private PropagationMessage msg;
    		
    		PropagatorThread ( PropagationMessage msg ) 
    		{
    			this.msg = msg;
    		}
    		
    		public void run() 
    		{
        		try {
        			boolean tryAgain = true;
        			do {
        				tryAgain = msg.submit();
        				if ( tryAgain  ) {
        				  //wait a little before retrying
        				  Thread.sleep ( RETRY_INTERVAL );
                          if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Propagator: retrying " + "message: " + msg );
        				}
        			} while ( tryAgain );
        		}
        		catch ( Exception e ) {
        			LOGGER.logWarning ( "ERROR in propagator: " + e.getMessage () +
                            (msg != null ? " while sending message: " + msg : "") , e );
        		}
    		}
    	
    }
}
