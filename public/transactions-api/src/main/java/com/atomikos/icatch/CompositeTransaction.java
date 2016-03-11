/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */


package com.atomikos.icatch;

import java.util.Properties;
import java.util.Stack;

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
{
	
	/**
	 * Gets the current state.
	 * @return Object One of the state constants.
	 * @see TxState
	 */
	 TxState getState();

    /**
     *
     * @return boolean True if this is the root transaction,
     * i.e. the first transaction created in a (possibly distributed) hierarchy.
     */

     boolean isRoot();

    /**
     * @return Stack A stack of ancestors, bottom one is the root.
     */

     Stack<CompositeTransaction> getLineage();


    /**
     * 
     * @return String The tid for the tx.
     */

     String getTid();

    /**
     * 
     * @param otherCompositeTransaction
     * 
     * @return boolean True if this instance is an ancestor of the supplied transaction.
     */

     boolean isAncestorOf( CompositeTransaction otherCompositeTransaction );

    /**
     * @param otherCompositeTransaction
     * 
     * @return boolean True if this instance is a descendant of the other instance.
     */

     boolean isDescendantOf( CompositeTransaction otherCompositeTransaction );


    /**
     * @param otherCompositeTransaction
     *
     * @return True if related. That is: if both share the same root transaction.
     */

    boolean isRelatedTransaction ( CompositeTransaction otherCompositeTransaction );

    
    /**
     * @param otherCompositeTransaction
     * @return True if both are the same.
     */

    boolean isSameTransaction ( CompositeTransaction otherCompositeTransaction );

    /**
     * @return CompositeCoordinator 
     */
    
    CompositeCoordinator getCompositeCoordinator() 
    throws SysException;
    

        
    /** 
     * @param participant 
     * 
     * @return RecoveryCoordinator Whom to ask for indoubt timeout resolution.
     */

    RecoveryCoordinator addParticipant ( Participant participant )
        throws SysException,
	     java.lang.IllegalStateException;
	     

    /**
     * 
     * @param sync
     * @throws IllegalStateException
     * @throws SysException
     */
    
     void registerSynchronization(Synchronization sync)
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

     void addSubTxAwareParticipant( SubTxAwareParticipant subtxaware )
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
     boolean isSerial();
    
    /**
     *
     * @return boolean True if started in the local VM.
     * For imported transactions, this is false.
     */
     
     boolean isLocal ();
   

	/**
	 * 
	 * @return The extent.
	 */

	 Extent getExtent();
     
     
	  /**
	   * @return long The transaction timeout in millis.
	   */
       
	 long getTimeout();
	
	 /**
	  * Marks the transaction so that the only possible
	  * termination is rollback. 
	  *
	  */
	
	 void setRollbackOnly();
	 
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

	 void commit() 
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

	 void rollback()
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
    
     void setProperty ( String name , String value );
    
    /**
     * Gets the specified metadata property.
     * @param name The name of the property.
     * @return The property, or null if not set.
     */
    
     String getProperty ( String name );
    
    /**
     * Gets all properties of this instance.
     * @return The (cloned) properties of this transaction.
     */
    
     Properties getProperties();

	 CompositeTransaction createSubTransaction();

    /**
     *Set serial mode for root.
     *This only works on the root itself, and can not be undone.
     *After this, no parallel calls are allowed in any descendant.
     *@exception IllegalStateException If  called for non-root tx.
     *@exception SysException For unexpected errors.
     */

     void setSerial() throws IllegalStateException, SysException;

    /**
    *@return int The number of locally started subtxs.
    */
    
     int getLocalSubTxCount();



}






