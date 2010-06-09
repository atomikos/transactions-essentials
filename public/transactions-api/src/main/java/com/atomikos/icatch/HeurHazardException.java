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
 *A heuristic hazard exception propagates to the root
 *as indication of cases where 2PC commit or abort was not
 *acknowledge by all participants.
 *Heuristic information about the identity and nature of the
 *lost participants can be included.
 */

public class HeurHazardException extends Exception
{
    protected HeuristicMessage[] msgs_=null;

    /**
     *Constructor.
     *@param msgs An array of heuristic messages, or null if none.
     */

    public HeurHazardException(HeuristicMessage[] msgs)
    {
        super ("Heuristic Exception");
        msgs_=msgs;
    }

    /**
     *Get the heuristic messages.
     *
     *@return HeuristicMessage[] An array, possibly null.
     */

    public HeuristicMessage[] getHeuristicMessages()
    {
        return msgs_;
    }
}
