/**
 * Copyright (C) 2000-2023 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.recovery.TxState;

/**
 *
 *
 * An abstract base implementation of CompositeTransaction, for common behaviour
 * of both proxy and local instances.
 */

public abstract class AbstractCompositeTransaction implements CompositeTransaction,
        java.io.Serializable
{

	private static final long serialVersionUID = 3522422565305065464L;


    protected Stack<CompositeTransaction> lineage_;

    protected String tid_;

    protected boolean serial_;


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

    public AbstractCompositeTransaction ( String tid , Stack<CompositeTransaction> lineage ,
            boolean serial  )
    {
        tid_ = tid;
        lineage_ = lineage;
        if ( lineage_ == null ) {
            lineage_ = new Stack<CompositeTransaction> ();
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
        return false;
    }

    /**
     * @see CompositeTransaction
     */

    public RecoveryCoordinator addParticipant ( Participant participant )
            throws SysException, java.lang.IllegalStateException

    {
    	throw new UnsupportedOperationException();
    }

    /**
     * @see CompositeTransaction
     */

    public void registerSynchronization ( Synchronization sync )
            throws IllegalStateException, UnsupportedOperationException, SysException
    {
    	throw new UnsupportedOperationException();
    }

    /**
     * @see CompositeTransaction.
     */
    @SuppressWarnings("unchecked")
	public Stack<CompositeTransaction> getLineage ()
    {
        return (Stack<CompositeTransaction>) lineage_.clone ();
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
    @SuppressWarnings("unchecked")
    public boolean isRelatedTransaction ( CompositeTransaction ct )
    {
        Stack<CompositeTransaction> lineage = null;
        if ( lineage_ == null )
            lineage = new Stack<CompositeTransaction> ();
        else
            lineage = (Stack<CompositeTransaction>) lineage_.clone ();

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
		if (!(obj instanceof AbstractCompositeTransaction) )
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
    	throw new UnsupportedOperationException();
    }

    /**
     * @see CompositeTransaction
     */

    public void addSubTxAwareParticipant ( SubTxAwareParticipant subtxaware )
            throws SysException, UnsupportedOperationException,
            java.lang.IllegalStateException
    {
        throw new UnsupportedOperationException ();
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#createSubTransaction()
     */
    public CompositeTransaction createSubTransaction () throws SysException,
            IllegalStateException
    {
    	 throw new UnsupportedOperationException();
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#setSerial()
     */
    public void setSerial () throws IllegalStateException, SysException
    {
    	throw new UnsupportedOperationException();
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getExtent()
     */
    public Extent getExtent ()
    {
    	throw new UnsupportedOperationException();
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getTimeout()
     */
    public long getTimeout ()
    {
    	return 0;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#setRollbackOnly()
     */
    public void setRollbackOnly ()
    {
    	throw new UnsupportedOperationException();

    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#commit()
     */
    public void commit () throws HeurMixedException,
            HeurHazardException, SysException, SecurityException,
            RollbackException
    {
    	throw new UnsupportedOperationException();

    }



    /**
     * @see com.atomikos.icatch.CompositeTransaction#rollback()
     */

    public void rollback () throws IllegalStateException, SysException
    {
    	throw new UnsupportedOperationException();

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


    /**
     * @see com.atomikos.finitestates.Stateful.
     */

    public TxState getState ()
    {
    	throw new UnsupportedOperationException();
    }
   

}
