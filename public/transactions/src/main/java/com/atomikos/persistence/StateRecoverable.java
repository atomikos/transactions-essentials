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

package com.atomikos.persistence;

import com.atomikos.finitestates.FSMPreEnterEventSource;

/**
 * A type of stateful objects whose state is guaranteed to be recoverable. The
 * logging is done based on PreEnter events. The guarantee offered is the
 * following: IF a recoverable state is reached by the instance, then its image
 * is GUARANTEED to be recoverable. The inverse does NOT hold: the fact that an
 * object is recovered in some state does NOT mean that the state was reached.
 * Indeed, other PreEnter listeners may still have prevented the transition in
 * the last moment. However, this should not be a real problem; applications
 * should take this into account.
 */

public interface StateRecoverable<T> extends Recoverable, FSMPreEnterEventSource<T>
{

    /**
     * Get an object image for the given state.
     * 
     * @param state
     *            The state about to be reached. Because the instance is not yet
     *            IN the new state, this state is supplied as a parameter.
     * 
     * @return ObjectImage The image, or null to override the recoverability preference.
     *         In other words, if null is returned that logging is not done for the given
     *         state, even if the state was returned as one of the recoverable states.
     */

    public ObjectImage getObjectImage ( T state );
}
