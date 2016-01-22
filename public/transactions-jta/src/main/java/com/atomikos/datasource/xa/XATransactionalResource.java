/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

package com.atomikos.datasource.xa;

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
import com.atomikos.recovery.xa.XaResourceRecoveryManager;


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
    protected String servername;
    protected Hashtable rootTransactionToSiblingMapperMap;
    protected XidFactory xidFact;
    private boolean closed;

    private boolean weakCompare;
    // if true: do NOT delegate usesXAResource calls
    // to the xaresource; needed for SONICMQ and other
    // JMS that do not correctly implement isSameRM

    private boolean compareAlwaysTrue;
    // if true, then isSameRM will ALWAYS return true
    // this can be useful for cases where different
    // JOINs don't have to work with lock sharing
    // or for cases where XAResource classes
    // are always non-compliant (like JBoss)

    private String branchIdentifier;

    private static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);
    private static final int MAX_LONG_LEN = MAX_LONG_STR.getBytes().length;
    /**
     * Construct a new instance with a default XidFactory.
     *
     * @param servername
     *            The servername, needed to identify the xid instances for the
     *            current configuration. Max BYTE length is 64!
     */

    public XATransactionalResource ( String servername )
    {

        this.servername = servername;
        this.rootTransactionToSiblingMapperMap = new Hashtable ();
        // name should be less than 64 for xid compatibility

        //branch id is server name + long value!

        if ( servername.getBytes ().length > 64- MAX_LONG_LEN )
            throw new RuntimeException (
                    "Max length of resource name exceeded: should be less than " + ( 64 - MAX_LONG_LEN ) );
        this.xidFact = new DefaultXidFactory ();
        this.closed = false;
        this.weakCompare = false;
        this.compareAlwaysTrue = false;
    }

    /**
     * Construct a new instance with a custom XidFactory.
     *
     * @param servername
     *            The servername, needed to identify the xid instances for the
     *            current configuration. Max BYTE length is 64!
     * @param factory
     *            The custom XidFactory.
     *
     */

    public XATransactionalResource ( String servername , XidFactory factory )
    {
        this ( servername );
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

    SiblingMapper getSiblingMap ( String root )
    {
        synchronized ( this.rootTransactionToSiblingMapperMap ) {
            if ( this.rootTransactionToSiblingMapperMap.containsKey ( root ) )
                return (SiblingMapper) this.rootTransactionToSiblingMapperMap.get ( root );
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
            if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( this.servername
                    + ": XAResource needs refresh?", xa );

        }
        return ret;
    }

    /**
     * Set this instance to use the weak compare mode setting. This method
     * should be called <b>before</b> recovery is done, so before
     * initialization of the transaction service.
     *
     *
     * this is no longer needed at all, and taken care of by the transaction
     * service automatically.
     *
     * @return weakCompare True iff weak compare mode should be used. This mode
     *         is relevant for integration with certain vendors whose XAResource
     *         instances do not correctly implements isSameRM.
     * @exception IllegalStateException
     *                If recovery was already done, meaning that the transaction
     *                service is already running.
     */

    public void useWeakCompare ( boolean weakCompare )
    {
        this.weakCompare = weakCompare;
    }

    /**
     * Test if this instance uses weak compare mode.
     *
     *
     * @return boolean True iff weak compare mode is in use. This mode is
     *         relevant for integration with certain vendors whose XAResource
     *         instances do not correctly implement isSameRM.
     */

    public boolean usesWeakCompare ()
    {
        return this.weakCompare;
    }

    /**
     *
     * Specify whether to entirely shortcut the isSameRM method of the
     * XAResource implementations, and always return true for usesXAResource.
     * The consequence is that branches are always different (even in the same
     * tx) and that the resource names will not entirely match in the logfiles.
     * Besides that, no serious problems should happen.
     *
     * @param val
     */
    public void setAcceptAllXAResources ( boolean val )
    {
        this.compareAlwaysTrue = val;
    }

    /**
     *
     * @return boolean True if usesXAResource is always true.
     */
    public boolean acceptsAllXAResources ()
    {
        return this.compareAlwaysTrue;
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
        // entirely shortcut normal behaviour if desired
        if ( acceptsAllXAResources () )
            return true;

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
                } else if ( usesWeakCompare () ) {
                    // In weak compare mode, it does not matter if the resource
                    // says it is different. The fact that the implementation is
                    // the same is enough. Needed for SONICMQ and others.
                    ret = true;
                } else {
                	LOGGER.logDebug ( "XAResources claim to be different: "
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
        	LOGGER.logDebug ( this.servername + ": refreshing XAResource..." );
            this.xares_ = refreshXAConnection ();
            LOGGER.logInfo ( this.servername + ": refreshed XAResource" );
        }

        return this.xares_;
    }

    /**
     * @see TransactionalResource
     */

    @Override
	public ResourceTransaction getResourceTransaction ( CompositeTransaction ct )
            throws ResourceException, IllegalStateException
    {
        if ( this.closed ) throw new IllegalStateException("XATransactionResource already closed");

        if ( ct == null ) return null; // happens in create method of beans?

        Stack lineage = ct.getLineage ();
        String root = null;
        if (lineage == null || lineage.isEmpty ()) root = ct.getTid ();
        else {
            Stack tmp = (Stack) lineage.clone ();
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
        return this.servername;
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
        if ( xatxres.servername == null || this.servername == null ) {
            return false;
        }

        return xatxres.servername.equals ( this.servername );
    }

    /**
     * @see RecoverableResource
     */

    @Override
	public void setRecoveryService ( RecoveryService recoveryService )
            throws ResourceException
    {

        if ( recoveryService != null ) {
            if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Installing recovery service on resource "
                    + getName () );
            this.branchIdentifier=recoveryService.getName();
            recover();
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
        return getXidFactory().createXid (tid , this.branchIdentifier);
    }

    @Override
    public void recover() {
    	XaResourceRecoveryManager xaResourceRecoveryManager = XaResourceRecoveryManager.getInstance();
    	if (xaResourceRecoveryManager != null) { //null for LogCloud recovery
    		if(getXAResource() != null) { //null if backend down
    			xaResourceRecoveryManager.recover(getXAResource());	
    		}
    		
    	}
    }
}
