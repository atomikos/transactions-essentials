//$Id: CompositeTransactionServer.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: CompositeTransactionServer.java,v $
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
//Revision 1.5  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.4  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2002/02/22 17:28:50  guy
//Updated: no RollbackException in addParticipant and registerSynch.
//
//Revision 1.2  2001/10/29 16:38:11  guy
//Changed UniqueId for String.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//
//Revision 1.3  2001/03/26 16:01:27  pardon
//Updated Proxy to use serial for SubTxAware notification.
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

import com.atomikos.icatch.Participant;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;

/**
 *Copyright &copy; 2001, Atomikos. All rights reserved.
 *
 *A server for composite transactions maps requests to the right instance.
 */

public interface CompositeTransactionServer extends Remote
{

    /**
     *Add a subtx aware participant to the given tx.
     *
     *@param subtxaware The participant  to add, will be notified on end().
     *@param txid The ID of the transaction to which the participant must be 
     *added.
     *@exception SysException Unexpected error.
     *@exception IllegalStateException If txid is no longer an active tx.
     */

    public void addSubTxAwareParticipant( SubTxAwareParticipant subtxaware , 
				   String txid )
        throws SysException, java.lang.IllegalStateException, 
	     RemoteException;
	     
    /**
     *Add a new participant to the transaction of the given tid.
     *
     *@param root The tid for whom to add.
     *@param participantproxy The participant proxy to add.
     *@return RecoveryCoordinatorProxy Whom to ask for indoubt 
     *timeout resolution.
     *@exception SysException Unexpected.
     *@exception IllegalStateException Illegal state.
     */
    public RecoveryCoordinatorProxy addParticipant ( Participant participant , 
                                                                     String txid )
                   throws  SysException ,
                     java.lang.IllegalStateException ,
                     RemoteException;

}
