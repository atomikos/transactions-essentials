/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;


/**
 * A handle to contact by an indoubt participant 
 * on timeout or restart, to resolve the outcome.
 */

public interface RecoveryCoordinator extends java.io.Serializable
{

    /**
     * Asks for a repetition of completion.
     * @param participant The indoubt  participant asking for replay
     * @return Boolean Null if decision not know to coordinator yet;
     * True if commit is known, False if abort is known.
     * @exception IllegalStateException If no prepare was done for the 
     * participant asking the replay.
     */

     Boolean replayCompletion ( Participant participant )
        throws IllegalStateException;
    
     /**
      * Gets the URI identifier for this coordinator.
      * @return String The URI identifier.
      */
      
     String getURI();

}
