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
