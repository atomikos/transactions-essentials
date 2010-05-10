//$Id: AbstractCompositeTransaction.java,v 1.1.1.1 2006/08/29 10:01:05 guy Exp $
//$Log: AbstractCompositeTransaction.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.3  2006/03/21 16:13:01  guy
//Added active recovery as a setter.
//
//Revision 1.2  2006/03/21 14:10:56  guy
//Replaced UnavailableException with UnsupportedOperationException.
//Added feature: suspend/resume of activity when JTA transaction is started.
//
//Revision 1.1  2006/03/21 13:22:55  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.9  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.8  2005/08/05 15:03:27  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.7  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.6  2002/02/22 17:28:40  guy
//Updated: no RollbackException in addParticipant and registerSynch.
//
//Revision 1.5  2001/11/19 16:52:42  guy
//Updated isSameTransaction
//
//Revision 1.3  2001/10/29 16:38:07  guy
//Changed UniqueId for String.
//
//Revision 1.2  2001/10/29 10:24:44  guy
//Constructor: did not set serial mode correctly. Added this assignment.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.2  2001/03/26 16:01:24  pardon
//Updated Proxy to use serial for SubTxAware notification.
//
//Revision 1.1  2001/03/23 17:00:30  pardon
//Lots of implementations for Terminator and proxies.
//

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

    public boolean equals ( Object o )
    {
        if ( !(o instanceof CompositeTransaction) )
            return false;
        CompositeTransaction ct = (CompositeTransaction) o;
        return ct.getTid ().intern ().equals ( getTid ().intern () );
    }
    
    public int hashCode() 
    {
    	int ret = 0;
    	
    	if ( getTid() == null ) {
    		ret = super.hashCode();
    	} else {
    		ret = getTid().hashCode();
    	}
    	
    	return ret;
    }

    // ***************************************************************
    //
    // Below is default error behaviour
    //
    // ***************************************************************

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
