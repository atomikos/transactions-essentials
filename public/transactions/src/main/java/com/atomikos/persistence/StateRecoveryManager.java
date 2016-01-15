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

package com.atomikos.persistence;

import java.util.Properties;
import java.util.Vector;

import com.atomikos.icatch.TxState;
/**
 * A state recovery manager is responsible for reconstructing StateRecoverable
 * instances based on the history.
 */

public interface StateRecoveryManager 
{
    /**
     * Recover all recorded recoverable instances in their latest state.
     * 
     * 
     * @return Vector A vector of reconstructed StateRecoverables.
     * @exception LogException
     *                If the log fails.
     */

    public Vector<RecoverableCoordinator<TxState>> recover () throws  com.atomikos.recovery.LogException;


    /**
     * Initialize the recovery mgr before calling the other methods.
     * 
     * @exception LogException
     *                If the underlying log fails.
     */

    public void init (Properties properties) throws com.atomikos.recovery.LogException;
    /**
     * Register a staterecoverable with the recovery manager service.
     * 
     * @param staterecoverable
     *            The object that wants recoverable states.
     */

    public void register ( RecoverableCoordinator<TxState> staterecoverable );

    /**
     * Reconstruct an instance of a staterecoverable.
     * 
     * @param Object
     *            The staterecoverable's identifier.
     * @return StateRecoverable The instance, or null if not found.
     * @exception LogException
     *                If underlying object log fails.
     */

    public RecoverableCoordinator<TxState> recover ( Object id ) throws com.atomikos.recovery.LogException;

    /**
     * Shutdown.
     * 
     * @exception LogException
     *                For underlying log failure.
     */

    public void close () throws com.atomikos.recovery.LogException;

    /**
     * Deletes a given image from the underlying logs.
     * 
     * @param id
     *            The id of the image to delete.
     * @exception LogException
     *                On failure.
     */

    public void delete ( Object id ) throws com.atomikos.recovery.LogException;

}
