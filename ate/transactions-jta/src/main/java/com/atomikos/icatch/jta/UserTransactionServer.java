/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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
