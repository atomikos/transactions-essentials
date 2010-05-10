//$Id: RecoveryCoordinator.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: RecoveryCoordinator.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.4  2005/08/09 07:18:07  guy
//Correted javadoc comments.
//
//Revision 1.3  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.2  2001/03/23 17:00:30  pardon
//Lots of implementations for Terminator and proxies.
//
//Revision 1.1  2001/02/26 19:36:27  pardon
//Redesigned again.
//OTS like...
//


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
