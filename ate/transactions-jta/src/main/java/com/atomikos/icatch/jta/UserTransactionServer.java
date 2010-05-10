//$Id: UserTransactionServer.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//$Log: UserTransactionServer.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:44  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:11  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2004/10/13 14:15:24  guy
//Updated javadocs and improved getReference.
//
//Revision 1.4  2004/10/11 13:39:37  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: UserTransactionServer.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.3  2003/03/22 16:03:54  guy
//$Id: UserTransactionServer.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Updated remote usertx to actually use the timeout settings of the user.
//$Id: UserTransactionServer.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//$Id: UserTransactionServer.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Revision 1.2  2003/03/11 06:39:01  guy
//$Id: UserTransactionServer.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: UserTransactionServer.java,v 1.1.1.1 2006/08/29 10:01:10 guy Exp $
//
//Revision 1.1.2.2  2002/11/16 13:57:52  guy
//Finished remote usertx implementation.
//
//Revision 1.1.2.1  2002/11/14 16:33:42  guy
//Added support for remote usertxs.
//

package com.atomikos.icatch.jta;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

/**
 * 
 * 
 * An RMI-based server interface that allows remote clients to do
 * client-demarcated transaction management. This interface is used by our
 * RemoteClientUserTransaction.
 */

public interface UserTransactionServer extends Remote
{

    /**
     * Create a new transaction.
     * 
     * @param timeout
     *            The timeout setting of the client UserTx.
     * @return String The tid of the transaction.
     */

    public String begin ( int timeout ) throws RemoteException,
            SystemException, NotSupportedException;

    /**
     * Commit the transaction.
     * 
     * @param tid
     *            The tid of the tx to commit.
     */

    public void commit ( String tid ) throws RemoteException,
            RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException,
            IllegalStateException, SystemException;

    /**
     * Rollback the transaction.
     * 
     * @param tid
     *            The tid of the transaction to rollback.
     */

    public void rollback ( String tid ) throws RemoteException,
            IllegalStateException, SecurityException, SystemException;

    /**
     * Mark the transaction for rollback only.
     * 
     * @param tid
     *            The tid of the transaction to mark.
     */

    public void setRollbackOnly ( String tid ) throws RemoteException,
            java.lang.IllegalStateException, SystemException;

    /**
     * Get the status of the transaction.
     * 
     * @param tid
     *            The tid.
     * @return int The status, as defined in JTA.
     */

    public int getStatus ( String tid ) throws RemoteException, SystemException;

}
