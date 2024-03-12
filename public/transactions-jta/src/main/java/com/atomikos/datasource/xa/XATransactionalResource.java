/**
 * Copyright (C) 2000-2024 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource.xa;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Stack;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.recovery.PendingTransactionRecord;
import com.atomikos.recovery.xa.XARecoveryManager;


/**
 *
 *
 * An abstract XA implementation of a transactional resource.
 *
 * For a particular XA data source, it is necessary to implement the
 * refreshXAConnection method, because in general there is no standard way of
 * getting XAResource instances. Therefore, this class is agnostic about it.
 *
 * It is assumed that there is at most one instance per (root transaction,
 * server) combination. Otherwise, siblings can not be mapped to the same
 * ResourceTransaction! This instance is responsible for mapping siblings to
 * ResourceTransaction instances.
 */

public abstract class XATransactionalResource implements TransactionalResource
{
	private static final Logger LOGGER = LoggerFactory.createLogger(XATransactionalResource.class);

    protected XAResource xares_;
    private String uniqueResourceName;
    private Hashtable<String,SiblingMapper> rootTransactionToSiblingMapperMap;
    private XidFactory xidFact;
    private boolean closed;
    private String branchIdentifier;

    private static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);
    private static final int MAX_LONG_LEN = MAX_LONG_STR.getBytes().length;
    /**
     * Construct a new instance with a default XidFactory.
     *
     */

    public XATransactionalResource ( String uniqueResourceName )
    {

        this.uniqueResourceName = uniqueResourceName;
        this.rootTransactionToSiblingMapperMap = new Hashtable<String,SiblingMapper>();
        // name should be less than 64 for xid compatibility

        //branch id is server name + long value!

        if ( uniqueResourceName.getBytes ().length > 64- MAX_LONG_LEN )
            throw new RuntimeException (
                    "Max length of resource name exceeded: should be less than " + ( 64 - MAX_LONG_LEN ) );
        this.xidFact = new DefaultXidFactory ();
        this.closed = false;
    }

    /**
     * Construct a new instance with a custom XidFactory.
     *
     *
     */

    public XATransactionalResource ( String uniqueResourceName , XidFactory factory )
    {
        this ( uniqueResourceName );
        this.xidFact = factory;
    }

    /**
     * Utility method to establish and refresh the XAResource. An XAResource is
     * actually a connection to a back-end resource, and this connection needs
     * to stay open for the transactional resource instance. The resource uses
     * the XAResource regularly, but sometimes the back-end server can close the
     * connection after a time-out. At intialization time and also after such a
     * time-out, this method is called to refresh the XAResource instance. This
     * is typically done by (re-)establishing a connection to the server and
     * <b>keeping this connection open!</b>.
     *
     * @return XAResource A XAResource instance that will be used to represent
     *         the server.
     * @exception ResourceException
     *                On failure.
     */

    protected abstract XAResource refreshXAConnection ()
            throws ResourceException;

    /**
     * Get the xidFactory for this instance. Needed by XAResourceTransaction to
     * create new XID.
     *
     * @return XidFactory The XidFactory for the resource.
     */

    public XidFactory getXidFactory ()
    {
        return this.xidFact;
    }

    void removeSiblingMap ( String root )
    {
        synchronized ( this.rootTransactionToSiblingMapperMap ) {
            this.rootTransactionToSiblingMapperMap.remove ( root );
        }

    }

    private SiblingMapper getSiblingMap ( String root )
    {
        synchronized ( this.rootTransactionToSiblingMapperMap ) {
            if ( this.rootTransactionToSiblingMapperMap.containsKey ( root ) )
                return this.rootTransactionToSiblingMapperMap.get ( root );
            else {
                SiblingMapper map = new SiblingMapper ( this , root );
                this.rootTransactionToSiblingMapperMap.put ( root, map );
                return map;
            }
        }
    }

    /**
     * Check if the XAResource needs to be refreshed.
     *
     * @return boolean True if the XAResource needs refresh.
     */

    protected boolean needsRefresh ()
    {
        boolean ret = true;

        // check if connection has not timed out
        try {
            // we should be the same as ourselves!
            // NOTE: xares_ is null if no connection could be gotten
            // in that case we just return true
            // otherwise, test the xaresource liveness
            if ( this.xares_ != null ) {
                this.xares_.isSameRM ( this.xares_ );
                ret = false;
            }
        } catch ( XAException xa ) {
            // timed out?
            if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( this.uniqueResourceName
                    + ": XAResource needs refresh?", xa );

        }
        return ret;
    }

   
    /**
     * Test if the XAResource is used by this instance.
     *
     * @param xares
     *            The XAResource to test.
     * @return boolean True iff this instance uses the same back-end resource,
     *         <b>in as far as this can be determined by this instance</b>.
     */

    public boolean usesXAResource ( XAResource xares )
    {
        XAResource xaresource = getXAResource ();
        if (xaresource == null) return false;
        // if no connection could be gotten

        boolean ret = false;

        if ( !xares.getClass ().getName ().equals (
                xaresource.getClass ().getName () ) ) {
            // if the implementation classes are different,
            // the resources are not the same
            // this check is needed to cope with
            // vendor-specific errors in XAResource.isSameRM()
            ret = false;
        } else {
            // in this case, the implementation class names are the same
            // so delegate to xares instances
            try {
                if ( xares.isSameRM ( xaresource ) ) {
                    ret = true;
                } else {
                	LOGGER.logTrace ( "XAResources claim to be different: "
                                    + xares + " and " + xaresource );
                }
            } catch ( XAException xe ) {
                throw new SysException ( "Error in XAResource comparison: "
                        + xe.getMessage (), xe );
            }
        }
        return ret;
    }

    /**
     * Get the XAResource instance that this instance is using.
     *
     * @return XAResource The XAResource instance.
     */

    public synchronized XAResource getXAResource ()
    {
        // null on first invocation
        if ( needsRefresh () ) {
        	refreshXAResource();
        }

        return this.xares_;
    }

    /**
     * @see TransactionalResource
     */
    @SuppressWarnings("unchecked")
    @Override
	public ResourceTransaction getResourceTransaction ( CompositeTransaction ct )
            throws ResourceException, IllegalStateException
    {
        if ( this.closed ) throw new IllegalStateException("XATransactionResource already closed");

        if ( ct == null ) return null; // happens in create method of beans?

        Stack<CompositeTransaction> lineage = ct.getLineage ();
        String root = null;
        if (lineage == null || lineage.isEmpty ()) root = ct.getTid ();
        else {
            Stack<CompositeTransaction> tmp = (Stack<CompositeTransaction>) lineage.clone ();
            while ( !tmp.isEmpty() ) {
                CompositeTransaction next = (CompositeTransaction) tmp.pop();
                if (next.isRoot()) root = next.getTid ();
            }
        }
        return getSiblingMap ( root ).findOrCreateBranchForTransaction ( ct );

    }


    /**
     * @see TransactionalResource
     */

    @Override
	public String getName ()
    {
        return this.uniqueResourceName;
    }

    /**
     * The default close operation. Subclasses may need to override this method
     * in order to process XA-specific close procedures such as closing
     * connections.
     *
     */

    @Override
	public void close () throws ResourceException
    {
        this.closed = true;
    }

    /**
     * Test if the resource is closed.
     *
     * @return boolean True if closed.
     * @throws ResourceException
     */
    @Override
	public boolean isClosed () throws ResourceException
    {
        return this.closed;
    }

    /**
     * @see RecoverableResource
     */

    @Override
	public boolean isSameRM ( RecoverableResource res )
            throws ResourceException
    {
        if ( res == null || !(res instanceof XATransactionalResource) ) {
        	return false;
        }

        XATransactionalResource xatxres = (XATransactionalResource) res;
        if ( xatxres.uniqueResourceName == null || this.uniqueResourceName == null ) {
            return false;
        }

        if (xatxres.uniqueResourceName.equals ( this.uniqueResourceName )) {
            return true;
        }
        try {
            if (xares_ != null) {
                XAResource other = xatxres.getXAResource();
                if (other != null) {
                    return xares_.isSameRM(other); //cf case 170618
                }
            }
        } catch (XAException e) {
            String msg = "Failed to compare XAResources";
            XAExceptionHelper.formatLogMessage(msg, e, "pessimistically assuming they are different"); 
        }
        return false;
    }

    /**
     * @see RecoverableResource
     */

    @Override
	public void setRecoveryService ( RecoveryService recoveryService )
            throws ResourceException
    {

        if ( recoveryService != null ) {
            if ( LOGGER.isTraceEnabled() ) LOGGER.logTrace ( "Installing recovery service on resource "
                    + getName () );
            this.branchIdentifier=recoveryService.getName();
        }

    }

    /**
     * Set the XID factory, needed for online management tools.
     *
     * @param factory
     */
    public void setXidFactory(XidFactory factory) {
        this.xidFact = factory;
    }

    /**
     * Create an XID for the given tx.
     *
     * @param tid
     *            The tx id.
     * @return Xid A globally unique Xid that can be recovered by any resource
     *         that connects to the same EIS.
     */

    protected XID createXid(String tid) {
    	if (this.branchIdentifier == null) throw new IllegalStateException("Not yet initialized");
        return getXidFactory().createXid (tid , this.branchIdentifier, this.uniqueResourceName);
    }

    @Override
    public boolean recover(long startOfRecoveryScan, Collection<PendingTransactionRecord> expiredCommittingCoordinators, Collection<PendingTransactionRecord> indoubtForeignCoordinatorsToKeep) {
    	boolean ret = false;
    	XARecoveryManager xaResourceRecoveryManager = XARecoveryManager.getInstance();
    	if (xaResourceRecoveryManager != null) { //null for LogCloud recovery
    		if(getXAResource() != null) { //null if backend down
    		try {
    			ret = xaResourceRecoveryManager.recover(getXAResource(), startOfRecoveryScan, expiredCommittingCoordinators, indoubtForeignCoordinatorsToKeep, uniqueResourceName);	
			} catch (Exception e) {
				LOGGER.logWarning(e.getMessage(),e); //cf case 164148 & 164147
				refreshXAResource(); //cf case 156968
			    }
    		}
    	}
    	return ret;
    }
    
  
    
    private void refreshXAResource() {
	LOGGER.logTrace ( this.uniqueResourceName + ": refreshing XAResource..." );
        this.xares_ = refreshXAConnection ();
        LOGGER.logInfo ( this.uniqueResourceName + ": refreshed XAResource" );	
    }
    
    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        if (o instanceof RecoverableResource) {
            RecoverableResource other = (RecoverableResource) o;
            return other.isSameRM(this);
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        return 0; // pessimistic - since xaresource can be null if backend down
    }
    
    @Override
    public boolean hasPendingParticipantsFromLastRecoveryScan() {
    	return XARecoveryManager.getInstance().hasPendingXids(uniqueResourceName);
    }
   
}
