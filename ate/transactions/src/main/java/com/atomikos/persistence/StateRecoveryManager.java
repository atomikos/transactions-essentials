package com.atomikos.persistence;

import java.util.Vector;

/**
 * 
 * 
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

    public Vector recover () throws LogException;

    /**
     * Initialize the recovery mgr before calling the other methods.
     * 
     * @exception LogException
     *                If the underlying log fails.
     */

    public void init () throws LogException;

    /**
     * Register a staterecoverable with the recovery manager service.
     * 
     * @param staterecoverable
     *            The object that wants recoverable states.
     */

    public void register ( StateRecoverable staterecoverable );

    /**
     * Reconstruct an instance of a staterecoverable.
     * 
     * @param Object
     *            The staterecoverable's identifier.
     * @return StateRecoverable The instance, or null if not found.
     * @exception LogException
     *                If underlying object log fails.
     */

    public StateRecoverable recover ( Object id ) throws LogException;

    /**
     * Shutdown.
     * 
     * @exception LogException
     *                For underlying log failure.
     */

    public void close () throws LogException;

    /**
     * Deletes a given image from the underlying logs.
     * 
     * @param id
     *            The id of the image to delete.
     * @exception LogException
     *                On failure.
     */

    public void delete ( Object id ) throws LogException;

}
