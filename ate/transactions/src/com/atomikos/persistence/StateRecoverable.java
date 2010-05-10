//
//  StateRecoverable.java
//
//  Created by guy on Fri Mar 30 2001.
//  
//

package com.atomikos.persistence;

import com.atomikos.finitestates.FSMPreEnterEventSource;

/**
 * 
 * 
 * A type of stateful objects whose state is guaranteed to be recoverable. The
 * logging is done based on PreEnter events. The guarantee offered is the
 * following: IF a recoverable state is reached by the instance, then its image
 * is GUARANTEED to be recoverable. The inverse does NOT hold: the fact that an
 * object is recovered in some state does NOT mean that the state was reached.
 * Indeed, other PreEnter listeners may still have prevented the transition in
 * the last moment. However, this should not be a real problem; applications
 * should take this into account.
 */

public interface StateRecoverable extends Recoverable, FSMPreEnterEventSource
{

    /**
     * Get the states that should be recoverable.
     * 
     * @return Object[] An array of states that are meant to be recoverable. For
     *         efficiency, this should also include a state where the logimage
     *         is forgettable!
     * 
     */

    public Object[] getRecoverableStates ();

    /**
     * Needed by the Recovery system to determine when a logged state can be
     * forgotten. If the instance reaches one of these states, then it will no
     * longer be recoverable.
     * 
     * @return Object[] The list of final states.
     */

    public Object[] getFinalStates ();

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

    public ObjectImage getObjectImage ( Object state );
}
