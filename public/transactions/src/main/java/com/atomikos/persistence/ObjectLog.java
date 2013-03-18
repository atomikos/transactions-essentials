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

import java.util.Vector;

import com.atomikos.icatch.TxState;

/**
 * Interface defining the functionality for logging objects to persistent storage.
 * 
 */

public interface ObjectLog
{
    /**
     * Flushes to permanent memory.
     * 
     * @param recoverable
     *            Recoverable instance. NOTE: if the instance has the same
     *            ObjectId as a previous one, then the latter will be overridden
     *            by this one! More precisely, history() will only return the
     *            last image for a given ObjectId.
     * 
     * @exception LogException
     *                if it did not work.
     */

    public void flush ( Recoverable recoverable ) throws LogException;

    /**
     * Initializes the object log. To be called as the first method.
     * 
     * @exception LogException
     *                If error occurs.
     */

    public void init () throws LogException;

    /**
     * Recovers all non-deleted object images flushed so far.
     * 
     * @return Vector A list of Recoverable instances, reconstructed from their
     *         images.
     */

    public Vector<StateRecoverable<TxState>> recover () throws LogException;

    /**
     * Recovers the instance with given ID.
     * 
     * @param id The id to recover. @return Recoverable The recovered logimage,
     * null if not found. @exception LogException On failure.
     */

    public Recoverable recover ( Object id ) throws LogException;

    /**
     * Deletes the given object from the log.
     * 
     * @param id
     *            The object UID.
     * @exception LogException
     *                on failure.
     */

    public void delete ( Object id ) throws LogException;

    /**
     * Closes the log after use.
     * 
     * @exception LogException
     *                on failure.
     */

    public void close () throws LogException;

}
