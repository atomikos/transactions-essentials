//$Id: ParticipantServer.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: ParticipantServer.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:32  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.9  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.8  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.7  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.6  2004/09/09 13:10:28  guy
//Regenerated stubs (marshal errros).
//Corrected bug in ParticipantProxy: lookup and cast in one go
//seemed to throw non-caught exception during recover()?
//
//Revision 1.5  2004/03/22 15:38:14  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.4.2.1  2003/06/20 16:31:53  guy
//*** empty log message ***
//
//Revision 1.4  2003/03/23 15:20:04  guy
//Corrected BUG in 1PC requested from remote TM in RMI. Added tests for this.
//
//Revision 1.3  2002/03/01 10:48:08  guy
//Updated to new prepare exception of HeurMixed.
//
//Revision 1.2  2001/10/29 16:38:11  guy
//Changed UniqueId for String.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//
//Revision 1.2  2001/03/23 17:00:37  pardon
//Lots of implementations for Terminator and proxies.
//
//Revision 1.1  2001/03/21 17:26:51  pardon
//Added proxies and server interfaces / classes.
//

package com.atomikos.icatch.trmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Dictionary;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

/**
 *Copyright &copy; 2001, Atomikos. All rights reserved.
 *
 *A participant server maps 2PC calls to the proper 
 *Participant instance.
 *NOTE: implementations should also implement the 
 *<code>equals</code> and <code>hashCode</code> methods correctly!
 */

public interface ParticipantServer extends Remote
{

    /**
     *Prepare the participant of the given root.
     *
     *@param root The root String of the participant.
     *@param cascadelist The list of participants to cascade to, if any.
     *@param siblings The count of siblings for the given participant.
     *
     *@see Participant For more information on the exceptions and return value.
     */

    public int prepare ( String root , int siblings  , Dictionary cascadelist )
        throws RollbackException ,
	     HeurHazardException ,
	     HeurMixedException,
	     SysException,
	     RemoteException ;
    
    /**
    *Commit the participant of the given root, after prepare was sent.
     *
     *@param root The String of the given root.
     *
     *@see Participant For more info on the exceptions and return value.
     */

    public HeuristicMessage[] commit ( String root )
        throws HeurRollbackException,
	     HeurHazardException,
	     HeurMixedException,
             RollbackException,
	     SysException,
	     RemoteException ;

    /**
     *Perform one-phase commit for the given root.
     *@param root The root id.
     *@param siblings The count of siblings for the given participant.
     *@param cascadeList The info needed for orphan detection.
     */
    public HeuristicMessage[] commitOnePhase ( String root , int siblings , Dictionary cascadeList )
        throws HeurRollbackException,
        HeurHazardException,
        HeurMixedException,
        RollbackException,
        SysException,
        RemoteException ;

    /**
     *Rollback the participant for the given root.
     *
     *@param root The given root.
     *
     *@see Participant for more exception info.
     */

    public HeuristicMessage[] rollback ( String root )
        throws HeurCommitException,
	     HeurMixedException,
	     HeurHazardException,
	     SysException,
	     RemoteException ;
    

    /**
     *Forget all about a given root.
     *
     *@param root The root to forget about.
     */

    public void forget ( String root ) throws SysException,RemoteException;

 
    
}
