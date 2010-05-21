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
