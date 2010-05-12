//$Id: RecoveryServer.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: RecoveryServer.java,v $
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
//Revision 1.4  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.3  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2001/10/29 16:38:11  guy
//Changed UniqueId for String.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//
//Revision 1.1  2001/03/23 17:02:01  pardon
//Added some files to repository.
//

package com.atomikos.icatch.trmi;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.SysException;

/**
 *Copyright &copy; 2001, Atomikos. All rights reserved.
 *
 *A server for recovery.
 */

public interface RecoveryServer extends Remote
{

    /**
     *Replay completion for given root, on given participant.
     *
     *@param root The root.
     *@param participant The participant.
     *@exception RemoteException On remote failure.
     */

    public Boolean replayCompletion ( String root , Participant participant )
        throws RemoteException, SysException;
}
