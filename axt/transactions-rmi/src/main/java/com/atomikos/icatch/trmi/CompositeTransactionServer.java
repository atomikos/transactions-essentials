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
