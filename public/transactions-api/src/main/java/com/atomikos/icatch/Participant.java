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
 * A participant for (distributed) two-phase commit of composite transactions.
 * 
 * Implementations can be added as a 2PC participant in the icatch kernel.
 *
 * VERY IMPORTANT: implementations should also override the 
 * default <code>equals</code>
 * and <code>hashCode</code> methods, in order for two-phase commit to work properly!
 *
 */

public interface Participant extends java.io.Serializable
{
    /**
     * Indicates that no commit/rollback is needed after prepare.
     */

    public static final int READ_ONLY=0x00;

     /**
      *
      * Called by the transaction manager (TM)
      * at recovery time, and this should reconstruct
      * the internal state.
      * 
      * @return boolean False if the instance could not be recovered.
      * It is up to the transaction manager to determine the severity of this case.
      *
      * @exception SysException
      */
      
    public boolean recover() 
    throws SysException;
    
     /**
      * @return String The unique URI for this remote participant, or null for local instances.
      */
      
    public String getURI();
    
    /**
     * For cascading 2PC, this method sets the information needed
     * to cascade. <b>This method is relevant only for 
     * transaction monitors; leave empty otherwise!</b>
     *
     * @param allParticipants The information needed by 
     * the transaction monitor.
     *
     * @exception SysException 
     */

    public void setCascadeList(java.util.Dictionary allParticipants)
        throws SysException;

    /**
     * Set by the root coordinator: the total no of siblings detected.
     * <b>This method is relevant only for 
     * transaction monitors; leave empty otherwise!</b>
     *
     *@param count The global count.
     */

    public void setGlobalSiblingCount(int count);

    /**
     * Prepares the participant.
     * Any locks for this participant's work should be
     * recoverable (either saved to an external file OR be part
     * of the instance's non-transient state so that they can be
     * flushed into the transaction manager's log).
     *
     * @return int READ_ONLY if no second round is needed.
     * Participants that return this value on prepare will not be
     * called by commit or rollback afterwards.
     *
     * @exception RollbackException For a NO vote.
     * This indicates that the participant has already rolled back
     * (or marked for rollback) the work on behalf of this participant.
     *
     * @exception HeurHazardException On possible conflicts.
     * This happens for remote participants instances, in case of 
     * communication failures. 
     *
     * @exception HeurMixedException If some subordinate
     * participants voted YES, timed out and committed heuristically 
     * whereas afterwards some NO votes where received.
     * 
     * @exception SysException 
     */

    public int prepare()
        throws RollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     SysException;

    
    /**
     * Commits the participant's work.
     * NOTE: custom  participant implementations should 
     * preferably be made for the server's local VM 
     * (e.g., it is better not to do this over RMI).
     * Also, they should <b>rely on the transaction manager
     * for heuristic timeout</b> (and NOT decide to terminate 
     * heuristically <b>themselves</b>).
     * In that case, implementations never need to throw any of the heuristic exceptions
     * of this method.
     *
     *
     * @return HeuristicMessage[] An array of messages, null if none.
     *
     * @param onePhase If true, one-phase commit is being started.
     * If the participant has received a prepare call earlier, 
     * then it should throw a SysException here.
     *
     * @exception HeuristicRollbackException If the participant has rolled back.
     *
     * @exception HeuristicMixedException If part of it was rolled back.
     * 
     * @exception HeurHazardException On possible conflicts.
     * 
     * @exception RollbackException In case of one-phase commit,
     * and the transaction has been rolled back at the time
     * commit is called.
     * 
     * @exception SysException
     */

    public HeuristicMessage[] commit ( boolean onePhase )
        throws HeurRollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     RollbackException,
	     SysException;
    
    /**
     * Rollback of the participant's work.
     *
     * NOTE: custom  participant implementations should 
     * preferably be made for the server's local VM 
     * (e.g., it is better not to do this over RMI).
     * Also, they should <b>rely on the icatch transaction manager
     * for heuristic timeout</b> (and NOT decide to terminate 
     * heuristically <b>themselves</b>).
     * In that case, implementations never need to throw any of the heuristic exceptions
     * of this method.
     *
     * @return HeuristicMessage[] An array of messages, null if none.
     *
     * @exception HeurCommitException If the participant committed.
     * @exception HeurHazardException If the participant's final state
     * is unsure.
     * @exception  HeurMixedException If part of the work was rolled back.
     * 
     * @exception SysException
     */

    public HeuristicMessage[] rollback()
        throws HeurCommitException,
	     HeurMixedException,
	     HeurHazardException,
	     SysException;

    

 
    /**
     * Indicates that a heuristic participant can forget about its work.
     *
     * If implementations rely on the transaction manager to 
     * decide when to do heuristics (rather then deciding
     * in the participant implementation), then
     * leave this method empty.
     */

    public void forget();

    /**
     *
     *@return HeuristicMessage[] An array of heuristic messages, or null if none.
     */
     
    public HeuristicMessage[] getHeuristicMessages();
}

