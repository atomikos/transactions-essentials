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
 * Represents a nested part of a global
 * composite transaction. Each invocation of a server is 
 * represented by an instance of this type. For transaction
 * proxies (instances that represent non-local transactions),
 * all non-trivial methods are allowed to generate
 * an exception of type UnsupportedOperationException. The only
 * methods that always work for all instances are the simple 
 * getters and test methods.
 *
 *
 *
 */

public interface CompositeTransaction 
    extends Stateful
{
	
	/**
	 * Gets the current state.
	 * @return Object One of the state constants.
	 * @see TxState
	 */
	public Object getState();

    /**
     *
     * @return boolean True if this is the root transaction,
     * i.e. the first transaction created in a (possibly distributed) hierarchy.
     */

    public boolean isRoot();

    /**
     * @return Stack A stack of ancestors, bottom one is the root.
     */

    public Stack getLineage();


    /**
     * 
     * @return String The tid for the tx.
     */

    public String getTid();

    /**
     * 
     * @param otherCompositeTransaction
     * 
     * @return boolean True if this instance is an ancestor of the supplied transaction.
     */

    public boolean isAncestorOf( CompositeTransaction otherCompositeTransaction );

    /**
     * @param otherCompositeTransaction
     * 
     * @return boolean True if this instance is a descendant of the other instance.
     */

    public boolean isDescendantOf( CompositeTransaction otherCompositeTransaction );


    /**
     * @param otherCompositeTransaction
     *
     * @return True if related. That is: if both share the same root transaction.
     */

    public boolean isRelatedTransaction ( CompositeTransaction otherCompositeTransaction );

    
    /**
     * @param otherCompositeTransaction
     * @return True if both are the same.
     */

    public boolean isSameTransaction ( CompositeTransaction otherCompositeTransaction );

    /**
     * @return CompositeCoordinator 
     */
    
    public CompositeCoordinator getCompositeCoordinator() 
    throws SysException;
    

        
    /** 
     * @param participant 
     * 
     * @return RecoveryCoordinator Whom to ask for indoubt timeout resolution.
     */

    public RecoveryCoordinator addParticipant ( Participant participant )
        throws SysException,
	     java.lang.IllegalStateException;
	     

    /**
     * 
     * @param sync
     * @throws IllegalStateException
     * @throws SysException
     */
    
    public void registerSynchronization(Synchronization sync)
        throws
             IllegalStateException,
             SysException;



    /**
     * Resources that support lock inheritance can use this feature
     * to be notified whenever a lock should be inherited.
     *
     * @param subtxaware
     * @throws SysException
     * @throws java.lang.IllegalStateException
     */

    public void addSubTxAwareParticipant( SubTxAwareParticipant subtxaware )
        throws SysException,
	     java.lang.IllegalStateException;

     
    /**
     * Serial mode is an optimized way for lock inheritance:
     * no locks among related transactions are necessary if all related
     * transactions are executed serially with respect to each other.
     * The serial property is set by the root transaction and is 
     * propagated to all its subtransactions.
     * 
     * @return 
     */
    public boolean isSerial();
    
    /**
     *
     * @deprecated As from release 3.0, 
     * the methods in the TransactionControl
     * interface have been moved to this one.
     */
     
    public TransactionControl getTransactionControl();
    
    
    /**
     *
     * @return boolean True if started in the local VM.
     * For imported transactions, this is false.
     */
     
    public boolean isLocal ();


	public CompositeTransaction createSubTransaction()
		throws SysException,
		 IllegalStateException;

   
	/**
	 * Sets the serial mode. 
	 * This only works on the root itself, and can not be undone.
	 * After this, no parallel calls are allowed in any descendant.
	 *
	 * @throws IllegalStateException
	 * @throws SysException
	 */
	
	public void setSerial() throws IllegalStateException, SysException;

	     
    
	/**
     * @deprecated This should not matter outside the core.
	 */
     
	 public int getLocalSubTxCount();
     
	/**
	 * Sets the tag for this transaction. This is returned as a summary of
	 * the local work in case the transaction was imported from a remote
	 * client TM.
	 * 
	 * @param tag
	 */
     
	public void setTag ( HeuristicMessage tag ) ;


	/**
	 * 
	 * @return The extent.
	 */

	 public Extent getExtent();
     
     
	  /**
	   * @return long The transaction timeout in millis.
	   */
       
	 public long getTimeout();
	
	 /**
	  * Marks the transaction so that the only possible
	  * termination is rollback. 
	  *
	  */
	
	 public void setRollbackOnly();
	 
	/**
	 * Commits the composite transaction.
	 *
	 * @exception HeurRollbackException On heuristic rollback.
	 * @exception HeurMixedException On heuristic mixed outcome.
	 * @exception SysException For unexpected failures.
	 * @exception SecurityException If calling thread does not have 
	 * right to commit.
	 * @exception HeurHazardException In case of heuristic hazard.
	 * @exception RollbackException If the transaction was rolled back
	 * before prepare.
	 */

	public void commit() 
		throws 
	  HeurRollbackException,HeurMixedException,
	  HeurHazardException,
	  SysException,java.lang.SecurityException,
	  RollbackException;



	/**
	 * Rollback of the current transaction.
	 * @exception IllegalStateException If prepared or inactive.
	 * @exception SysException If unexpected error.
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






