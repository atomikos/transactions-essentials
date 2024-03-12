/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.persistence;

import com.atomikos.recovery.LogException;
/**
 * A state recovery manager is responsible for reconstructing StateRecoverable
 * instances based on the history.
 */

public interface StateRecoveryManager 
{

    /**
     * Register a staterecoverable with the recovery manager service.
     * 
     * @param staterecoverable
     *            The object that wants recoverable states.
     */

    public void register ( RecoverableCoordinator staterecoverable );

    /**
     * Shutdown.
     * 
     * @exception LogException
     *                For underlying log failure.
     */

    public void close () throws com.atomikos.recovery.LogException;


}
