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

/**
 * 
 * 
 * An interface for transactional threads. Implementing this interface allows
 * you to start multithreaded subtransactions that run as subtransactions of
 * the calling thread's transaction. Implementations do NOT need to create,
 * start or commit/rollback these subtransactions: all management is done by the
 * system.
 * 
 * NOTE: in case of a top-level transaction in preferred serial mode, the
 * subtransactional threads will be serialized with respect to each other.
 * 
 */

public interface SubTxCode
{
    /**
     * The method that contains the subtransactional logic. Before this method
     * is called, the system will have started a subtransaction, and associated
     * it with the thread that will execute exec(). If this method exits without
     * an exception, then the subtransaction will be committed.
     * 
     * @exception Exception
     *                On failure. In that case, the corresponding subtransaction
     *                will be rolled back by the system.
     */

    public void exec () throws Exception;
}
