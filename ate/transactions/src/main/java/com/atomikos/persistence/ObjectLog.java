package com.atomikos.persistence;

import java.util.Vector;

/**
 * 
 * manager, being some entity that allows the following.
 * 
 */

public interface ObjectLog
{
    /**
     * Flush to permanent memory.
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
     * Initialize the object log. To be called as the first method.
     * 
     * @exception LogException
     *                If error occurs.
     */

    public void init () throws LogException;

    /**
     * Recover all non-deleted object images flushed so far.
     * 
     * @return Vector A list of Recoverable instances, reconstructed from their
     *         images.
     */

    public Vector recover () throws LogException;

    /*
     * Recover the instance with given ID.
     * 
     * @param id The id to recover. @return Recoverable The recovered logimage,
     * null if not found. @exception LogException On failure.
     */

    public Recoverable recover ( Object id ) throws LogException;

    /**
     * Delete the given object from the log.
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
