package com.atomikos.icatch.msg;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A message indicating a 2PC related error and carrying error information.
 */

public interface ErrorMessage extends TransactionMessage
{
    /**
     * Constant indicating RollbackException. For certain protocols, e.g. BTP,
     * such an error message may be translated by the transport into a CANCELLED
     * message.
     */

    public static final int ROLLBACK_ERROR = 0;

    /**
     * Constant indicating HeurHazardException.
     */

    public static final int HEUR_HAZARD_ERROR = 1;

    /**
     * Constant defining the beginning of the range of heuristic exceptions.
     */

    public static final int HEURISTIC_MIN = HEUR_HAZARD_ERROR;

    /**
     * Constant indicating HeurMixedException.
     */

    public static final int HEUR_MIXED_ERROR = 2;

    /**
     * Constant indicating HeurCommitException. For certain protocols, e.g. BTP,
     * such an error message may be translated by the transport into a CONFIRMED
     * message.
     */

    public static final int HEUR_COMMIT_ERROR = 3;

    /**
     * Constant indicating HeurRollbackException. For certain protocols, e.g.
     * BTP, such an error message may be translated by the transport into a
     * CANCELLED message.
     */

    public static final int HEUR_ROLLBACK_ERROR = 4;

    /**
     * Constant defining the end of the range of heuristic exceptions.
     */

    public static final int HEURISTIC_MAX = HEUR_ROLLBACK_ERROR;

    // /**
    // *Constant indicating that a participant that has sent a replay
    // *message should not have been prepared.
    // */
    //      
    // public static final int NO_PREPARE_SENT_ERROR = 5;
    //    
    /**
     * Constant indicating that an unknown error has happened.
     */

    public static final int UNKNOWN_ERROR = 5;

    /**
     * Get the error code.
     * 
     * @return int One of the predefined error constants.
     */

    public int getErrorCode ();

}
