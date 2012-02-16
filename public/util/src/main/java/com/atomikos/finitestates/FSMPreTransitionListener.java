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

package com.atomikos.finitestates;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import java.util.EventListener;

/**
*
*
*A listener interface for FSMTransition events, but one that wishes
*to be notified BEFORE the transition is exposed to other threads.
*
*
*/

public interface FSMPreTransitionListener extends EventListener
{
	/**
	*A method to be called BEFORE the specified transition takes place.
	*Since the transition still has to happen, no listener can be sure
	*that the event notification eventually leads to the transition.
	*This is because the state machine process can fail after the notice,
	*or the target state can be prevented somehow.
	*
	*@param e The transition that will be attempted.
	*@exception IllegalStateException on failure.
	*/

	public void beforeTransition(FSMTransitionEvent e) 
	    throws IllegalStateException;

}
