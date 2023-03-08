/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import java.util.Map;




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

public interface Participant 
{
    /**
     * Indicates that no commit/rollback is needed after prepare.
     */

     static final int READ_ONLY=0x00;
    
     /**
      * @return String The unique URI for this remote participant.
      */
      
     String getURI();
    
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

     void setCascadeList(Map<String, Integer> allParticipants)
        throws SysException;

    /**
     * Set by the root coordinator: the total no of siblings detected.
     * <b>This method is relevant only for 
     * transaction monitors; leave empty otherwise!</b>
     *
     *@param count The global count.
     */

     void setGlobalSiblingCount(int count);

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

     int prepare()
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

     void commit ( boolean onePhase )
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
     * @exception HeurCommitException If the participant committed.
     * @exception HeurHazardException If the participant's final state
     * is unsure.
     * @exception  HeurMixedException If part of the work was rolled back.
     * 
     * @exception SysException
     */

     void rollback()
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

     void forget();


	
	/**
	 * @return The (unique) name of the recoverable resource as known in the configuration. Null if not relevant.
	 */
	 String getResourceName();
}

