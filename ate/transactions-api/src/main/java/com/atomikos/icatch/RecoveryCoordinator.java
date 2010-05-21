package com.atomikos.icatch;

/**
 *
 *
 *A recoverycoordinator is contacted by an indoubt participant 
 *on timeout or restart.
 */

public interface RecoveryCoordinator extends java.io.Serializable
{

    /**
     *Ask for a repetition of completion.
     *@param participant The indoubt  participant asking for replay
     *@return Boolean Null if decision not know to coordinator yet;
     *True if commit is known, False if abort is known.
     *@exception IllegalStateException If no prepare was done for the 
     *participant asking the replay.
     */

    public Boolean replayCompletion ( Participant participant )
        throws IllegalStateException;
    
     /**
      *Gets the URI identifier for this coordinator.
      *@return String The URI identifier.
      */
      
    public String getURI();

}
