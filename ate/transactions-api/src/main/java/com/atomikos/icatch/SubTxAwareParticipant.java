//$Id: SubTxAwareParticipant.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: SubTxAwareParticipant.java,v $
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
//Revision 1.4  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
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
//Revision 1.1  2001/03/21 17:26:51  pardon
//Added proxies and server interfaces / classes.
//


package com.atomikos.icatch;

/**
 *
 *
 *A participant that wants to be notified of local termination of a node in a 
 *nested transaction tree. 
 */

public interface SubTxAwareParticipant 
extends java.io.Serializable
{
    /**
     *Notification of termination.
     *
     *@param tx The composite transaction that has terminated
     *locally at its node.
     */

    public void committed ( CompositeTransaction tx );
    
    /**
     *Notification that some tx has been rolledback.
     *
     *@param parent The tx that has rolled back at its node.
     */

    public void rolledback ( CompositeTransaction tx );
}
