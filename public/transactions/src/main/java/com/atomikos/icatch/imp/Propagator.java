/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.icatch.imp;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import com.atomikos.icatch.imp.thread.TaskManager;
import com.atomikos.icatch.system.Configuration;

/**
 *
 *
 * A propagator sends PropagationMessages to participants.
 */

class Propagator
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.createLogger(Propagator.class);

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
                         if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Propagator: retrying "
                                            + "message: " + msg );
        				}
        			} while ( tryAgain );
        		}
        		catch ( Exception e ) {
        			LOGGER.logWarning ( "ERROR in propagator: "
                            + e.getMessage ()
                            + (msg != null ? " while sending message: " + msg : "")
                            , e );
        		}
    		}

    }
}
