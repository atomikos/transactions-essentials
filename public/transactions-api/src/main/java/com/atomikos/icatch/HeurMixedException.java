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


//
//Revision 1.2  2001/03/01 19:26:57  pardon
//Added more.
//
//Revision 1.1  2001/02/21 19:51:23  pardon
//Redesign!
//

package com.atomikos.icatch;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;


/**
 * An exception signaling that some participants 
 * have committed whereas others performed a rollback.
 */

public class HeurMixedException extends Exception
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.createLogger(HeurMixedException.class);

    protected HeuristicMessage[] aborts_=null, commits_=null, msgs_=null;

 
    public HeurMixedException (HeuristicMessage[] msgs)
    {
        super("Heuristic Exception");
        msgs_=msgs;
    }

    public HeurMixedException(HeuristicMessage[] aborts,
			HeuristicMessage[] commits)
    {
        super("Heuristic Exception");
        aborts_=aborts;
        commits_=commits;
    }
    
    /**
     *
     * @return HeuristicMessage[] The list of application-level messages for those participants that did rollback, or null if none.
     */

    public HeuristicMessage[] getHeuristicRollbackMessages()
    {
        return aborts_;
    }

    /**
     * @return HeuristicMessage[] The list of messages describing the work at those participants that committed, or null if none.
     *
     */
    
    public HeuristicMessage[] getHeuristicCommitMessages()
    {
        return commits_;
    }

    /**
     * Gets all heuristic messages.
     *
     * @return HeuristicMessage[] The list of messages, or null if none.
     */

    public HeuristicMessage[] getHeuristicMessages()
    {
    	if (msgs_!=null) 
    		return msgs_;

    	if (aborts_==null) 
    		return getHeuristicCommitMessages();
    	else if (commits_==null)
    		return getHeuristicRollbackMessages();

    	int i=0,j=0;
    	int len = aborts_.length + commits_.length;

    	HeuristicMessage[] msgs=new HeuristicMessage[len];

    	for (i=0;i<aborts_.length;i++)
    		msgs[i]=aborts_[i];
    	for (j=0;j<commits_.length;j++)
    		msgs[i+j]=commits_[j];

    	return msgs;
    }

    
}
