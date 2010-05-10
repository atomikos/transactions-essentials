package com.atomikos.icatch.imp;

import com.atomikos.icatch.imp.thread.TaskManager;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * A propagator sends PropagationMessages to participants.
 */

class Propagator
{
	
    static long RETRY_INTERVAL = 10000;
    // how long do we wait for retriable messages


    private boolean threaded_ = true;
    //a thread per message or not?

    
    Propagator ( boolean threaded )
    {
    		threaded_ = threaded;
    }

    /**
     * Schedules a message for submit.
     * 
     * @param msg
     *            The message to add.
     */

    public synchronized void submitPropagationMessage ( PropagationMessage msg )
    {
    		PropagatorThread t = new PropagatorThread ( msg );
    		if ( threaded_ ) {
    			TaskManager.getInstance().executeTask ( t );
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
                         Configuration.logDebug ( "Propagator: retrying "
                                            + "message: " + msg );
        				}
        			} while ( tryAgain );
        		}
        		catch ( Exception e ) {
        			Configuration.logWarning ( "ERROR in propagator: "
                            + e.getMessage ()
                            + (msg != null ? " while sending message: " + msg : "")
                            , e );
        		}
    		}
    	
    }
}
