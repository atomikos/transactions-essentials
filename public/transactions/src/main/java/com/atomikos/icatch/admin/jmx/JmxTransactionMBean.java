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

package com.atomikos.icatch.admin.jmx;

/**
 * An MBean interface for administration of pending transactions.
 */

public interface JmxTransactionMBean
{

    /**
     * Gets the transaction identifier.
     * 
     * @return String The unique id.
     */

    public String getTid ();

    /**
     * Gets the transaction's state.
     * 
     * @return String The state, represented as by its name
     */

    public String getState ();

    /**
     * Gets the high-level heuristic comments. This is what remote clients will
     * see as well.
     * 
     * @return HeuristicMessage The comments giving a summary of the tasks done
     *         in this transaction.
     */

    public String[] getTags ();

    /**
     * Gets the HeuristicMessage detailed info for this transaction.
     * 
     * @return HeuristicMessage[] The detailed heuristic messages. These show
     *         the comments for EACH individual resource that was part of the
     *         transaction.
     */

    public String[] getHeuristicMessages ();

}
