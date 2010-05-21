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


/**
 *
 *
 *A heuristic extension supporting messages.
 */

public class HeurMixedException extends Exception
{
    protected HeuristicMessage[] aborts_=null, commits_=null, msgs_=null;

    /**
     *Constructor.
     *@param msgs the heuristic messages.
     *or null if none.
     */

    public HeurMixedException (HeuristicMessage[] msgs)
    {
        super("Heuristic Exception");
        msgs_=msgs;
    }
    
    /**
     *Constructor.
     *@param aborts an array of heuristic abort messages,
     *@param commits the heuristic commit messages.
     *or null if none.
     */

    public HeurMixedException(HeuristicMessage[] aborts,
			HeuristicMessage[] commits)
    {
        super("Heuristic Exception");
        aborts_=aborts;
        commits_=commits;
    }
    
    /**
     *Get any heuristic rollback messages.
     *
     *@return HeuristicMessage[] A list of rollback messages, or null if none.
     *NOTE: if one-argument constructor was used, this returns null.
     */

    public HeuristicMessage[] getHeuristicRollbackMessages()
    {
        return aborts_;
    }

     /**
     *Get any heuristic commit messages.
     *
     *@return HeuristicMessage[] A list of commit messages, or null if none.
     *NOTE: if one-argument constructor was used, this will be null.
     */
    
    public HeuristicMessage[] getHeuristicCommitMessages()
    {
        return commits_;
    }

    /**
     *Get all heuristic messages.
     *
     *@return HeuristicMessage[] A list of mixed messages, or null if none.
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
