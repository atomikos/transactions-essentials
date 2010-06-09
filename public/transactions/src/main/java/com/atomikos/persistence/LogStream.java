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

import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * 
 * The LogStream interface is an abstract definition of a stream that
 * encapsulates a physical log. In contrast to a regular input or output stream,
 * a log stream has both input and output facilities to the same underlying log.
 * It also allows for checkpointing, but the client is responsible for
 * determining the checkpoint contents. Checkpointing allows the log stream to
 * decrease in size.
 */

public interface LogStream
{
    /**
     * Get the size of the stream.
     * 
     * @return long The size of the stream.
     * @exception LogException
     *                On error.
     */

    public long getSize () throws LogException;

    /**
     * Reads the log contents, and initializes the data structure. Should be
     * called first.
     * 
     * @return Vector The read objects from the log.
     * @exception LogException
     *                On failure.
     */

    public Vector recover () throws LogException;

    /**
     * After intial recovery, it is good practice to write a checkpoint with
     * only the most recent data. This method does that, and can also be called
     * at random intervals during normal operation.
     * 
     * @param elements
     *            The elements to keep in the log.
     * 
     * 
     * @exception LogException
     *                On failure.
     */

    public void writeCheckpoint ( Enumeration elements ) throws LogException;

    /**
     * Flush (force) an object to the stream. If this method returns then the
     * object is guaranteed to be persisted.
     * 
     * @param o
     *            The object to flush.
     * @throws LogException
     *             On failure.
     */
    public void flushObject ( Object o ) throws LogException;

    /**
     * For proper termination: a close method.
     * 
     * @exception LogException
     *                On failure.
     */

    public void close () throws LogException;

}
