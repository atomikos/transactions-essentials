/**
 * Copyright (C) 2000-2012 Atomikos <info@atomikos.com>
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.datasource.TransactionalResource;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.SysException;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.persistence.StateRecoveryManager;

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
    protected String servername_;
    protected Hashtable recoveredXidMap_;
    protected Hashtable rootTransactionToSiblingMapperMap_;
    protected XidFactory xidFact_;
    private boolean closed_;

    private boolean weakCompare_;
    // if true: do NOT delegate usesXAResource calls
    // to the xaresource; needed for SONICMQ and other
    // JMS that do not correctly implement isSameRM

    private boolean compareAlwaysTrue_;
    // if true, then isSameRM will ALWAYS return true
    // this can be useful for cases where different
    // JOINs don't have to work with lock sharing
    // or for cases where XAResource classes
    // are always non-compliant (like JBoss)

    private RecoveryService recoveryService_;

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

        servername_ = servername;
        rootTransactionToSiblingMapperMap_ = new Hashtable ();
        // name should be less than 64 for xid compatibility

        //branch id is server name + long value!

        if ( servername.getBytes ().length > (64- MAX_LONG_LEN) )
            throw new RuntimeException (
                    "Max length of resource name exceeded: should be less than " + ( 64 - MAX_LONG_LEN ) );
        xidFact_ = new DefaultXidFactory ();
        closed_ = false;
        weakCompare_ = false;
        compareAlwaysTrue_ = false;
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
        xidFact_ = factory;
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
        return xidFact_;
    }

    void removeSiblingMap ( String root )
    {
        synchronized ( rootTransactionToSiblingMapperMap_ ) {
            rootTransactionToSiblingMapperMap_.remove ( root );
        }

    }

    SiblingMapper getSiblingMap ( String root )
    {
        synchronized ( rootTransactionToSiblingMapperMap_ ) {
            if ( rootTransactionToSiblingMapperMap_.containsKey ( root ) )
                return (SiblingMapper) rootTransactionToSiblingMapperMap_.get ( root );
            else {
                SiblingMapper map = new SiblingMapper ( this , root );
                rootTransactionToSiblingMapperMap_.put ( root, map );
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
            if ( xares_ != null ) {
                xares_.isSameRM ( xares_ );
                ret = false;
            }
        } catch ( XAException xa ) {
            // timed out?
            if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( servername_
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
        weakCompare_ = weakCompare;
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
        return weakCompare_;
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
        compareAlwaysTrue_ = val;
    }

    /**
     *
     * @return boolean True if usesXAResource is always true.
     */
    public boolean acceptsAllXAResources ()
    {
        return compareAlwaysTrue_;
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
                Stack errors = new Stack ();
                errors.push ( xe );
                throw new SysException ( "Error in XAResource comparison: "
                        + xe.getMessage (), errors );
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
        	LOGGER.logDebug ( servername_ + ": refreshing XAResource..." );
            xares_ = refreshXAConnection ();
            LOGGER.logInfo ( servername_ + ": refreshed XAResource" );
        }

        return xares_;
    }

    /**
     * @see TransactionalResource
     */

    public ResourceTransaction getResourceTransaction ( CompositeTransaction ct )
            throws ResourceException, IllegalStateException
    {
        if ( closed_ ) throw new IllegalStateException("XATransactionResource already closed");

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
        return (getSiblingMap ( root )).findOrCreateBranchForTransaction ( ct );

    }

    private StateRecoveryManager getRecoveryManager () throws ResourceException
    {
        if (closed_) throw new IllegalStateException("XATransactionResource already closed");
       
        return null;
    }

    /**
     * @see TransactionalResource
     */

    public String getName ()
    {
        return servername_;
    }

    /**
     * The default close operation. Subclasses may need to override this method
     * in order to process XA-specific close procedures such as closing
     * connections.
     *
     */

    public void close () throws ResourceException
    {
        closed_ = true;
    }

    /**
     * Test if the resource is closed.
     *
     * @return boolean True if closed.
     * @throws ResourceException
     */
    public boolean isClosed () throws ResourceException
    {
        return closed_;
    }

    /**
     * @see RecoverableResource
     */

    public boolean isSameRM ( RecoverableResource res )
            throws ResourceException
    {
        if ( res == null || !(res instanceof XATransactionalResource) ) {
        	return false;
        }

        XATransactionalResource xatxres = (XATransactionalResource) res;
        if ( xatxres.servername_ == null || servername_ == null ) {
            return false;
        }

        return xatxres.servername_.equals ( servername_ );
    }

    /**
     * @see RecoverableResource
     */

    public void setRecoveryService ( RecoveryService recoveryService )
            throws ResourceException
    {

        if ( recoveryService != null ) {
            if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Installing recovery service on resource "
                    + getName () );
            this.recoveryService_ = recoveryService;
            recoveryService.recover ();
        }

    }

    /**
     * @see TransactionalResource
     */

    public synchronized boolean recover ( Participant participant )
            throws ResourceException
    {
    	boolean recovered = true;
    	
        if ( closed_ ){ 
        	throw new IllegalStateException ("XATransactionResource already closed");
        }
        if ( !(participant instanceof XAResourceTransaction) ) {
            throw new ResourceException ( "Wrong argument class: " + participant.getClass ().getName () );
        }
        
        XAResource xaresource = getXAResource ();
        
        if ( xaresource == null ) {
            LOGGER.logWarning ( "XATransactionalResource " + getName() + ": XAResource is null" );
            return false;
        }

        XAResourceTransaction xarestx = (XAResourceTransaction) participant;

        recoverXidsFromResourceIfNecessary();

        if ( !recoveredXidMap_.containsKey ( xarestx.getXid() ) ) {
            recovered = false;
        }

        if (recovered || 
        	getName().equals (xarestx.getResourceName())) { //see case 21552
        		xarestx.setRecoveredXAResource ( getXAResource () );
        		xarestx.setResource(this);
        }
        recoveredXidMap_.remove ( xarestx.getXid() );
        return recovered;
    }

    /**
     * Recover the contained XAResource, and retrieve the xid instances that
     * start with our server's name.
     *
     * @exception ResourceException
     *                If a failure occurs.
     */

    protected void recover() throws ResourceException
    {
        recoveredXidMap_ = new Hashtable ();
        Xid[] recoveredlist = null;
        int flags = XAResource.TMSTARTRSCAN;
        boolean allXidsFromResourceHaveBeenRecovered = false;
        Stack errors = new Stack ();
        Vector recoveredXids = new Vector();
        // this vector contains ALL recovered Xids so far,
        // to check if a scan returns duplicate results
        // this is needed for oracle8.1.7
        
        
        if (recoveryService_ == null) throw new IllegalStateException("No recoveryService set yet!");

        String branchIdentifier = recoveryService_.getName();
        
        if(LOGGER.isDebugEnabled()){
        	LOGGER.logDebug( "recovery initiated for resource " + getName ()
                    + " with branchIdentifier " + branchIdentifier);
        }

        do {
            recoveredlist = recoverXidsFromXAResource(flags, errors);
            flags = XAResource.TMNOFLAGS;
            allXidsFromResourceHaveBeenRecovered = (recoveredlist == null || recoveredlist.length == 0);
            if ( !allXidsFromResourceHaveBeenRecovered ) {
                allXidsFromResourceHaveBeenRecovered = true; //tentative: tolerate infinite loops of same Xids like in Oracle 8.1.7
                for ( Xid vendorXid : recoveredlist ) {
                    Xid xid = wrapWithOurOwnXidToHaveCorrectEqualsAndHashCode ( vendorXid );
                    if (notAlreadyRecovered(recoveredXids, xid)) {
                    	allXidsFromResourceHaveBeenRecovered = false;
                    	if(LOGGER.isInfoEnabled()){
                    		LOGGER.logInfo("Resource " + servername_ + " inspecting XID: " + xid);
                    	}
                        recoveredXids.addElement ( xid );
                        
                        // only really 'recover' this xid if it is ours
                        String branch = new String ( vendorXid.getBranchQualifier () );
                        if ( branch.startsWith ( branchIdentifier ) ) {
                            recoveredXidMap_.put ( xid, new Object () );
                            if(LOGGER.isInfoEnabled()){
                            	LOGGER.logInfo("Resource " + servername_ + " recovering XID: " + xid);
                            }
                        } else {
                        	if(LOGGER.isInfoEnabled()){
                        		LOGGER.logInfo("Resource " + servername_ + ": XID " + xid + " with branch " + branch + " is not under my responsibility");
                        	}
                        }
                    }
                }
            }
        } while ( !allXidsFromResourceHaveBeenRecovered );

        recoveredXids = null; // for GC

    }

	private boolean notAlreadyRecovered(Vector recoveredXids, Xid xid) {
		return !recoveredXids.contains ( xid );
	}

	private Xid[] recoverXidsFromXAResource(int flags, Stack errors) {
		Xid[] recoveredlist = null;
		try {
		    recoveredlist = getXAResource().recover(flags);
		} catch ( NullPointerException ora ) {
			//Typical for Oracle without XA setup
				if ( getXAResource ().getClass ().getName ().toLowerCase ().indexOf ( "oracle" ) >= 0 ) {
					LOGGER.logWarning("ORACLE NOT CONFIGURED FOR XA? PLEASE CONTACT YOUR DBA TO FIX THIS...");
				}
				throw ora;

		} catch ( XAException xaerr ) {
		    LOGGER.logWarning ( "Error in recovery", xaerr );
		    errors.push ( xaerr );
		    throw new ResourceException ( "Error in recovery", errors );
		}
		return recoveredlist;
	}

    private Xid wrapWithOurOwnXidToHaveCorrectEqualsAndHashCode(Xid xid) {
		return new XID(xid);
	}

	/**
     * @see TransactionalResource.
     */

    public void endRecovery () throws ResourceException
    {
        if ( closed_ ) throw new IllegalStateException ( "XATransactionResource already closed" );

        if (getXAResource() != null) {
        	recoverXidsFromResourceIfNecessary();       
        	performPresumedAbortForRemainingXids();
        }
    	resetForNextRecoveryScan();
        
        if(LOGGER.isDebugEnabled()){
        	LOGGER.logDebug("endRecovery() done for resource " + getName ());
        }
    }

	private void performPresumedAbortForRemainingXids() {
		Enumeration toAbortList = recoveredXidMap_.keys ();
		XAResource xaresource = getXAResource();
        while ( toAbortList.hasMoreElements () ) {
            XID xid = (XID) toAbortList.nextElement ();
            try {
                xaresource.rollback ( xid );
                if(LOGGER.isInfoEnabled()){
                	LOGGER.logInfo("XAResource.rollback ( " + xid + " ) called " + "on resource " + servername_);
                }
            } catch ( XAException xaerr ) {
                // here, an indoubt tx might remain in resource; we do nothing
                // to prevent this and leave it to admin tools
            }
        }
	}

	private void resetForNextRecoveryScan() {
		recoveredXidMap_ = null;
	}

	private void recoverXidsFromResourceIfNecessary() {
		if (recoveredXidMap_ == null) recover();
	}

    /**
     * Set the XID factory, needed for online management tools.
     *
     * @param factory
     */
    public void setXidFactory(XidFactory factory) {
        xidFact_ = factory;
    }

    /**
     * Create an XID for the given tx.
     *
     * @param tid
     *            The tx id.
     * @return Xid A globally unique Xid that can be recovered by any resource
     *         that connects to the same EIS.
     */

    protected Xid createXid(String tid) {
    	if (recoveryService_ == null) throw new IllegalStateException("Not yet initialized");
    	String branchIdentifier = recoveryService_.getName();
        return getXidFactory().createXid (tid , branchIdentifier);
    }

}
