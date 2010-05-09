//$Id: ErrorMessage.java,v 1.1.1.1 2006/10/02 15:21:15 guy Exp $
//$Log: ErrorMessage.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:15  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:48  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2005/08/05 15:03:46  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.2  2005/07/28 12:43:41  guy
//Completed Axis implementation of 2PC/SOAP.
//
//Revision 1.1.4.1  2004/06/14 08:09:18  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.2  2002/11/07 10:00:11  guy
//Tuned messaging framework.
//
//Revision 1.1.2.1  2002/10/31 16:06:48  guy
//Added basic message framework for 2PC over message systems.
//

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
