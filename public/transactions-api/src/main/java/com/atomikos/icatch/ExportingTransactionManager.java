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

package com.atomikos.icatch;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

/**
 * An interface for a TM that allows outgoing remote calls to be transactional.
 */
 
 public interface ExportingTransactionManager 
 {
    /** 
     * Gets the propagation info of the transaction for the calling thread.
     * Should be called before doing the remote call.
     *
     * @return Propagation The propagation for the current thread.
     *
     * @exception RollbackException If no such transaction exists, e.g. after a prior rollback.
     * 
     * @exception SysException 
     *
     */
    
    public Propagation getPropagation () 
    throws SysException, RollbackException;
    
    /**
     * Should be called after call returns successfully:
     * adds the extent of the call to the current transaction.
     *
     * If a remote call has failed, this method should NOT be called.
     *
     * @param extent The extent of the call.
     * 
     * @exception RollbackException If the current transaction
     * has already rolled back.
     * 
     * @exception SysException On failure.
     */
     
     
    public void addExtent ( Extent extent ) 
    throws SysException, RollbackException;
 }
