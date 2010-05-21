package com.atomikos.icatch.trmi;
import java.rmi.Remote;
import java.rmi.RemoteException;

import com.atomikos.icatch.CompositeTransaction;

 /**
  *Copyright &copy; 2001, Atomikos. All rights reserved.
  *
  *In case of distributed transactions over RMI, a remote participant 
  *must implement Remote and thus the SubTxAwareParticipant 
  *interface in itself is not enough.
  *Therefore, we provide this trmi interface that remote subtxawares
  *should implement. Instances should be wrapped in a SubTxAwareWrapper
  *before adding to the composite transaction.
  *
  */
  
public interface RemoteSubTxAware extends Remote
{
    /**
     *The same method as in SubTxAwareParticipant, but
     *with a remote exception.
     */
     
    public void committed ( CompositeTransaction tx )
    throws RemoteException;
    
    /**
     *The same method as in SubTxAwareParticipant, but
     *with a remote exception.
     */
     
    public void rolledback ( CompositeTransaction tx )
    throws RemoteException; 
}
