package com.atomikos.icatch;

/**
 *
 *
 *A participant for 2PC of composite transactions.
 *Implementations can be added
 *as a 2PC participant in the icatch kernel.
 *
 *VERY IMPORTANT: implementations should also override the 
 *default <code>equals</code>
 *and <code>hashCode</code> methods, in order for 2PC to work!
 *
 */

public interface Participant extends java.io.Serializable
{
    /**
     *Indicates that no commit/rollback needed after prepare.
     */

    public static final int READ_ONLY=0x00;

     /**
      *Recover this instance.
      *Called by the transaction manager (TM)
      * at recovery time, and this should reconstruct
      *the internal state.
      *@return boolean False iff the instance could not be recovered.
      *It is up to the TM to determine the severity of this case.
      *
      *@exception SysException On failure.
      */
      
    public boolean recover() 
    throws SysException;
    
     /**
      *Gets a unique URI for this participant. Local participants should
      *merely return null.
      *
      *@return String The URI for this participant, or null if not applicable.
      */
      
    public String getURI();
    
    /**
     *For cascading 2PC, this method sets the information needed
     *to cascade. <b>This method is relevant only for 
     *transaction monitors; leave empty otherwise!</b>
     *
     *@param allParticipants The information needed by 
     *the transaction monitor for 
     *
     *@exception SysException For unexpected failures.
     */

    public void setCascadeList(java.util.Dictionary allParticipants)
        throws SysException;

    /**
     *Set by the root coordinator: the total no of siblings detected.
     *<b>This method is relevant only for 
     *transaction monitors; leave empty otherwise!</b>
     *
     *@param count The global count.
     */

    public void setGlobalSiblingCount(int count);

    /**
     *Prepare the participant.
     *Any locks for this participant's work should be
     *recoverable (either saved to an external file OR be part
     *of the instance's non-transient state so that they can be
     *flushed into the transaction manager's log).
     *
     *@return int If this is READ_ONLY, then no second round is needed.
     *Participants that return this value on prepare will not be
     *called by commit or rollback afterwards.
     *@exception RollbackException For a NO vote.
     *This indicates that the participant has already rolled back
     *(or marked for rollback) the work on behalf of this participant.
     *@exception HeurHazardException On possible conflicts.
     *This happens for remote participants instances, in case of 
     *communication failures. 
     *@exception HeurMixedException If some subordinate
     *participants voted YES, timed out and committed heuristically 
     *whereas afterwards some NO votes where received.
     *@exception SysException Unexpected errors.
     */

    public int prepare()
        throws RollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     SysException;

    
    /**
     *Commit the participant's work.
     *NOTE: custom  participant implementations should 
     *preferably be made for the server's local VM 
     *(e.g., it is better not to do this over RMI).
     *Also, they should <b>rely on the icatch transaction manager
     *for heuristic timeout</b> (and NOT decide to terminate 
     *heuristically <b>themselves</b>).
     *In that case, implementations never need to throw any of the heuristic exceptions
     *of this method.
     *
     *
     *@return HeuristicMessage[] An array of messages, null if none.
     *
     *@param onePhase If true, one-phase commit is being started.
     *If the participant has received a prepare call earlier, 
     *then it should throw a SysException here.
     *
     *@exception HeuristicRollbackException If the participant has rolled back.
     *
     *@exception HeuristicMixedException If part of it was rolled back.
     *@exception HeurHazardException On possible conflicts.
     *@exception RollbackException In case of one-phase commit,
     *and the transaction has been rolled back at the time
     *commit is called.
     *@exception SysException Unexpected failure.
     */

    public HeuristicMessage[] commit ( boolean onePhase )
        throws HeurRollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     RollbackException,
	     SysException;
    
    /**
     *Rollback the participant's work.
     *
     *NOTE: custom  participant implementations should 
     *preferably be made for the server's local VM 
     *(e.g., it is better not to do this over RMI).
     *Also, they should <b>rely on the icatch transaction manager
     *for heuristic timeout</b> (and NOT decide to terminate 
     *heuristically <b>themselves</b>).
     *In that case, implementations never need to throw any of the heuristic exceptions
     *of this method.
     *
     *@return HeuristicMessage[] An array of messages, null if none.
     *
     *@exception HeurCommitException If the participant committed.
     *@exception HeurHazardException If the participant's final state
     *is unsure.
     *@exception  HeurMixedException If part of it was rolled back.
     *@exception SysException Unexpected failure.
     */

    public HeuristicMessage[] rollback()
        throws HeurCommitException,
	     HeurMixedException,
	     HeurHazardException,
	     SysException;

    

 
    /**
     *Indicate that a heuristic participant can forget about its work.
     *
     *If implementations rely on the transaction manager to 
     *decide when to do heuristics (rather then deciding
     *in the participant implementation), then
     *leave this method empty.
     */

    public void forget();

    /**
     *Get any heuristic messages so far.
     *The transaction manager used this method to 
     *get information after preparing this participant.
     *
     *@return HeuristicMessage[] An array of heuristic messages, or null if none.
     */
     
    public HeuristicMessage[] getHeuristicMessages();
}

