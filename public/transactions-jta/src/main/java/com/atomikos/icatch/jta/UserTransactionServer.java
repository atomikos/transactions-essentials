/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

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
