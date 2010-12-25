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

package com.atomikos.icatch.imp;

import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;

/**
 * 
 * 
 * An abstract base implementation of CompositeTransaction, for common behaviour
 * of both proxy and local instances.
 */

public abstract class AbstractCompositeTransaction implements CompositeTransaction,
        java.io.Serializable
{

    private static void fail () throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException (
                "This functionality is not available for this instance" );
    }

    protected Stack lineage_;
    // lineage is the ancestor info.

    protected String tid_;

    protected boolean serial_;

    protected HeuristicMessage tag_;
    
    protected Properties properties_;
    
    /**
     * Required for externalization of subclasses
     */

    public AbstractCompositeTransaction ()
    {
    }

    /**
     * Constructor.
     * 
     */

    public AbstractCompositeTransaction ( String tid , Stack lineage ,
            boolean serial  )
    {
        tid_ = tid;
        lineage_ = lineage;
        if ( lineage_ == null ) {
            lineage_ = new Stack ();
            properties_ = new Properties();
        }
        else {
        		if ( ! lineage_.empty() ) {
        			CompositeTransaction parent = ( CompositeTransaction ) lineage_.peek();
        			properties_ = parent.getProperties();
        		}
        }
        if ( properties_ == null ) properties_ = new Properties();
        serial_ = serial;
        
    }       

    /**
     * @see CompositeTransaction.
     */

    public String getTid ()
    {
        return tid_;
    }

    /**
     * @see CompositeTransaction.
     */

    public void setTag ( HeuristicMessage tag )
    {
        tag_ = tag;
    }

    /**
     * @see CompositeTransaction.
     */

    public boolean isSerial ()
    {
        return serial_;
    }

    /**
     * @see CompositeTransaction.
     * 
     * Defaults to false.
     */

    public boolean isLocal ()
    {
        // defaults to false.
        return false;
    }

    /**
     * @see CompositeTransaction.
     */

    public TransactionControl getTransactionControl ()
            throws UnsupportedOperationException
    {
        // return null to make test work
        return null;
    }

    /**
     * @see CompositeTransaction
     */

    public RecoveryCoordinator addParticipant ( Participant participant )
            throws SysException, java.lang.IllegalStateException

    {
        throw new UnsupportedOperationException ( "addParticipant" );
    }

    /**
     * @see CompositeTransaction
     */

    public void registerSynchronization ( Synchronization sync )
            throws IllegalStateException, UnsupportedOperationException, SysException
    {
        throw new UnsupportedOperationException ( "registerSynchronization" );
    }

    /**
     * @see CompositeTransaction.
     */

    public Stack getLineage ()
    {
        return (Stack) lineage_.clone ();
    }

    /**
     * @see CompositeTransaction.
     */

    public boolean isRoot ()
    {
        return ( lineage_ == null || lineage_.size () == 0);
        // for non-roots, this is at least one
    }

    /**
     * @see CompositeTransaction.
     */

    public boolean isAncestorOf ( CompositeTransaction ct )
    {
        return ct.isDescendantOf ( this );
    }

    /**
     * @see CompositeTransaction.
     */

    public boolean isDescendantOf ( CompositeTransaction ct )
    {
        CompositeTransaction parent = null;
        if ( lineage_ != null && (!lineage_.empty ()) )
            parent = (CompositeTransaction) lineage_.peek ();

        return (isSameTransaction ( ct ) || (parent != null && parent
                .isDescendantOf ( ct )));
    }

    /**
     * @see CompositeTransaction.
     */

    public boolean isRelatedTransaction ( CompositeTransaction ct )
    {
        Stack lineage = null;
        if ( lineage_ == null )
            lineage = new Stack ();
        else
            lineage = (Stack) lineage_.clone ();

        if ( lineage.empty () )
            return isAncestorOf ( ct );

        CompositeTransaction root = null;
        while ( !lineage.empty () )
            root = (CompositeTransaction) lineage.pop ();
        return root.isAncestorOf ( ct );
    }

    /**
     * @see CompositeTransaction.
     */

    public boolean isSameTransaction ( CompositeTransaction ct )
    {
        return (equals ( ct ));

        // lock inheritance is determined by resource using this;
        // OK if same invocation or if sibling, but serial mode.
    }

  

    // ***************************************************************
    //
    // Below is default error behaviour
    //
    // ***************************************************************

    
    public int hashCode() 
    {
    	int ret = 0;
    	
    	if ( tid_ == null ) {
    		ret = super.hashCode();
    	} else {
    		ret = getTid().hashCode();
    	}
    	
    	return ret;
    }
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractCompositeTransaction other = (AbstractCompositeTransaction) obj;
		if (tid_ == null) {
			if (other.tid_ != null)
				return false;
		} else if (!tid_.equals(other.tid_))
			return false;
		return true;
	}

	/**
     * @see CompositeTransaction.
     */

    public CompositeCoordinator getCompositeCoordinator () throws SysException,
            UnsupportedOperationException
    {
        throw new UnsupportedOperationException ( "Not implemented: class for testing " );
    }

    /**
     * @see CompositeTransaction
     */

    public void addSubTxAwareParticipant ( SubTxAwareParticipant subtxaware )
            throws SysException, UnsupportedOperationException,
            java.lang.IllegalStateException
    {
        throw new UnsupportedOperationException ( "addSubTxAwareParticipant" );
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#createSubTransaction()
     */
    public CompositeTransaction createSubTransaction () throws SysException,
            IllegalStateException
    {
        fail ();
        return null;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#setSerial()
     */
    public void setSerial () throws IllegalStateException, SysException
    {
        fail ();

    }

    /**
     * 
     * @see com.atomikos.icatch.CompositeTransaction#getLocalSubTxCount()
     */
    public int getLocalSubTxCount ()
    {

        return 0;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getExtent()
     */
    public Extent getExtent ()
    {
        fail ();
        return null;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getTimeout()
     */
    public long getTimeout ()
    {
        fail ();
        return 0;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#setRollbackOnly()
     */
    public void setRollbackOnly ()
    {
        fail ();

    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#commit()
     */
    public void commit () throws HeurRollbackException, HeurMixedException,
            HeurHazardException, SysException, SecurityException,
            RollbackException
    {
        fail ();

    }



    /**
     * @see com.atomikos.icatch.CompositeTransaction#rollback()
     */
    
    public void rollback () throws IllegalStateException, SysException
    {
        fail ();

    }
    
    
    public void setProperty ( String name , String value )
    {
        if ( getProperty ( name ) == null ) 
            properties_.setProperty ( name , value );
    }

    public String getProperty ( String name ) 
    {
        return properties_.getProperty ( name );
    }
    
    public Properties getProperties()
    {
        return ( Properties ) properties_.clone();
    }
    
   
    
    //
    //
    //
    // IMPLEMENTATION OF STATEFUL
    //
    //
    //

    /**
     * @see com.atomikos.finitestates.Stateful.
     */

    public Object getState ()
    {
        throw new UnsupportedOperationException ( "getState" );
    }

}
