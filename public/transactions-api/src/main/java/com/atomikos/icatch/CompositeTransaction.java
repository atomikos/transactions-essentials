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
import java.util.Properties;
import java.util.Stack;

import com.atomikos.finitestates.Stateful;

/**
 *
 *
 *A composite transaction is a nested part of a global
 *composite transaction. Each invocation of a server is 
 *represented by an instance of this type. For transaction
 *proxies (instances that represent non-local transactions),
 *all non-trivial methods are allowed to generate
 *an exception of type UnsupportedOperationException. The only
 *methods that always work for all instances are the simple 
 *getters and test methods.
 *
 *
 *
 */

//@todo check exceptions: runtime or not?
//TODO UPDATE SAMPLES AND DOCS TO ADDED METHODS OF CONTROL AND TERMINATOR
public interface CompositeTransaction 
    extends Stateful
{
	
	/**
	 * Get the current state.
	 * @return Object One of the state constants.
	 * @see TxState
	 */
	public Object getState();

    /**
     *Test if this instance is a  root or not.
     *Note: a root is the top-level transaction; that is: the first transaction
     *that was created in the distributed transaction hierarchy.
     *
     *@return True if this is the root.
     */

    public boolean isRoot();

    /**
     *Get the ancestor info.
     *@return Stack A stack of ancestors, bottom one is root.
     */

    public Stack getLineage();


    /**
     *Getter for tid.
     *
     *@return String The tid for the tx.
     */

    public String getTid();

    /**
     *Test if this instance is an  ancestor of ct.
     *
     *@param ct The argument to test for ancestor.
     *
     *@return boolean True iff this is an ancestor of ct.
     */

    public boolean isAncestorOf( CompositeTransaction ct );

    /**
     *Test if this instance is a descendant of ct.
     *
     *@param ct The argument to test for descendant.
     *
     *@return boolean True iff this instance is a descendant of ct.
     */

    public boolean isDescendantOf( CompositeTransaction ct );


    /**
     *Test if this instance is related to ct.
     *@return True if related. That is: if both share the same root transaction.
     */

    public boolean isRelatedTransaction ( CompositeTransaction ct );

    
    /**
     *Test if this intance represents the same transaction as ct.
     *@return True iff the same.
     */

    public boolean isSameTransaction ( CompositeTransaction ct );

    /**
     *Get the coordinator for this tx.
     *
     *@return CompositeCoordinator The composite coordinator instance.
     *
     *@exception SysException On failure.
     */
    
    public CompositeCoordinator getCompositeCoordinator() 
    throws SysException;
    

        
    /**
     *Add a new participant.
     *
     *@param participant The participant to add.
     *@return RecoveryCoordinator Whom to ask for indoubt timeout resolution.
     *@exception SysException Unexpected.
     *@exception IllegalStateException Illegal state.
     *
     */

    public RecoveryCoordinator addParticipant ( Participant participant )
        throws SysException,
	     java.lang.IllegalStateException;
	     
	
    /**
     *Add a synchronization callback.
     *
     *@param sync The callback object.
     *@exception IllegalStateException If no tx longer active.
     *
     *
     *@exception SysException Unexptected failure.
     */

    public void registerSynchronization(Synchronization sync)
        throws
             IllegalStateException,
             SysException;



    /**
     *Register a participant on behalf of a subtransaction, to be 
     *notified as soon as the locally finished state is reached.
     *Resources that support lock inheritance can use this feature
     *to be notified whenever a lock should be inherited.
     *
     *@param subtxaware The participant to be notified 
     *on local termination.
     *
     *@exception IllegalStateException If no longer active.
     */

    public void addSubTxAwareParticipant( SubTxAwareParticipant subtxaware )
        throws SysException,
	     java.lang.IllegalStateException;

    /**
     *Test if serial tx or not.
     *Serial mode is an optimized way for lock inheritance:
     *no locks among related transactions are necessary if all related
     *transactions are executed serially with respect to each other.
     *The serial property is set by the root transaction and is 
     *propagated to all its subtransactions.
     *
     *@return boolean True iff all for root is serial. 
     */
     
    public boolean isSerial();
    
    /**
     *Get a control object for this tx.
     *
     *@deprecated As from release 3.0, 
     *the methods in the TransactionControl
     *interface have been moved to this one.
     *@return TransactionControl a control object.
     */
     
    public TransactionControl getTransactionControl();
    
    
    /**
     *Test if the transaction is a locally started transaction or not.
     *
     *@return boolean True iff started in the local VM.
     *For imported transactions, this is false.
     */
     
    public boolean isLocal ();
    
    
        
	/**
	 *Create a subtx for this transaction.
     *For activities, the subtransaction will also be 
     *an activity.
	 *
	 *@return CompositeTransaction The subtx.
	 *@exception IllegalStateException If no longer active.
	 */

	public CompositeTransaction createSubTransaction()
		throws SysException,
		 IllegalStateException;
	     
	/**
	 *Set serial mode for root.
	 *This only works on the root itself, and can not be undone.
	 *After this, no parallel calls are allowed in any descendant.
	 *@exception IllegalStateException If  called for non-root tx.
	 *@exception SysException For unexpected errors.
	 */

	public void setSerial() throws IllegalStateException, SysException;

	     
    
	/**
	 *Get the number of subtxs that were locally started for this
	 *instance.
	 *@return int The number of locally started subtxs.
     *@deprecated This should not matter outside the core.
	 */
     
	 public int getLocalSubTxCount();
     
	/**
	 *Sets the tag for this transaction. This is returned as a summary of
	 *the local work in case the transaction was imported from a remote
	 *client TM.
	 *
	 *@param tag The tag to add to the transaction.
	 */
     
	public void setTag ( HeuristicMessage tag ) ;


	/**
	 *Get the extent for the transaction.
	 */

	 public Extent getExtent();
     
     
	  /**
	   *Get the timeout in ms.
	   *
	   *@return long The timeout, in ms, of the tx.
	   */
       
	 public long getTimeout();
	
	 /**
	  *Marks the transaction so that the only possible
	  *termination is rollback. 
	  *
	  */
	
	 public void setRollbackOnly();
	 
	/**
	 *Commit the composite transaction.
	 *
	 *@exception HeurRollbackException On heuristic rollback.
	 *@exception HeurMixedException On heuristic mixed outcome.
	 *@exception SysException For unexpected failures.
	 *@exception SecurityException If calling thread does not have 
	 *right to commit.
	 *@exception HeurHazardException In case of heuristic hazard.
	 *@exception RollbackException If the transaction was rolled back
	 *before prepare.
	 */

	public void commit() 
		throws 
	  HeurRollbackException,HeurMixedException,
	  HeurHazardException,
	  SysException,java.lang.SecurityException,
	  RollbackException;



	/**
	 *Rollback the current transaction.
	 *@exception IllegalStateException If prepared or inactive.
	 *@exception SysException If unexpected error.
	 */

	public void rollback()
		throws IllegalStateException, SysException;	
 
    
    /**
     * Sets metadata property information on the transaction object.
     * Once set, metadata properties can't be overwritten. Properties
     * are inherited by all subtransactions created after the set. 
     * Properties may or may not be propagated along with
     * exported transactions (depending on the protocol).
     * 
     * @param name
     * @param value
     */
    
    public void setProperty ( String name , String value );
    
    /**
     * Gets the specified metadata property.
     * @param name The name of the property.
     * @return The property, or null if not set.
     */
    public String getProperty ( String name );
    
    /**
     * Gets all properties of this instance.
     * @return The (cloned) properties of this transaction.
     */
    public Properties getProperties();


}






