//$Id: RemoteSubTxAware.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: RemoteSubTxAware.java,v $
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
//Revision 1.3  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:53  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

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
