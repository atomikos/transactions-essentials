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
import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.system.Configuration;
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
 */

/*
 * It is assumed that there is at most one instance per (root transaction,
 * server) combination. Otherwise, siblings can not be mapped to the same
 * ResourceTransaction! This instance is responsible for mapping siblings to
 * ResourceTransaction instances.
 */

public abstract class XATransactionalResource implements TransactionalResource
{
	private static final Logger LOGGER = LoggerFactory.createLogger(XATransactionalResource.class);

    protected XAResource xares_;
    // the xa resource for which txs are created.

    protected String servername_;
    // needed for recovery: our xids' branchqualifiers start with this.

    protected Hashtable recoveryMap_;
    // contains all recovered Xid instances, for recovering Restxs.

    protected Hashtable siblingmappers_;
    // maps root

    protected XidFactory xidFact_;
    // factory for creating Xid objects: some databases
    // required their own XID instance format.

    private boolean closed_;
    // true ASA close called.

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

    private String branchIdentifier_;

    // the unique name that is used for all our XID branches

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
        siblingmappers_ = new Hashtable ();
        // name should be less than 64 for xid compatibility
        String maxLong = "" + Long.MAX_VALUE;
        //branch id is server name + long value!
        String testName = servername + maxLong;
        if ( testName.getBytes ().length > 64 )
            throw new RuntimeException (
                    "Max length of resource name exceeded: should be less than " + ( 64 - maxLong.getBytes().length ) );
        xidFact_ = new DefaultXidFactory ();
        closed_ = false;
        weakCompare_ = false;
        compareAlwaysTrue_ = false;
        branchIdentifier_ = servername;
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

    protected void printMsg ( String msg , int level )
    {
        try {
            Console console = Configuration.getConsole ();
            if ( console != null ) {
                console.println ( msg, level );
            }
        } catch ( Exception ignore ) {
        }
    }

    void removeSiblingMap ( String root )
    {
        synchronized ( siblingmappers_ ) {
            siblingmappers_.remove ( root );
        }

    }

    SiblingMapper getSiblingMap ( String root )
    {
        synchronized ( siblingmappers_ ) {
            if ( siblingmappers_.containsKey ( root ) )
                return (SiblingMapper) siblingmappers_.get ( root );
            else {
                SiblingMapper map = new SiblingMapper ( this , root );
                siblingmappers_.put ( root, map );
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
        // FOLLOWING COMMENTED OUT TO ALLOW JMX ONLINE CONFIG
        // if ( recoveryMap_ != null )
        // throw new IllegalStateException (
        // "useWeakCompare should not be called after recovery was done" );
        // weakCompare_ = weakCompare;
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
        if ( xaresource == null )
            return false;
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
                    // the
                    // same is enough. Needed for SONICMQ and others.
                    ret = true;
                } else {
                	LOGGER
                            .logDebug ( "XAResources claim to be different: "
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
        	LOGGER
                    .logDebug ( servername_ + ": refreshing XAResource..." );
            xares_ = refreshXAConnection ();
            LOGGER.logInfo ( servername_ + ": refreshed XAResource" );
        }

        // first, check if connection has not timed out
        // try {
        // //we should be the same as ourselves!
        // //NOTE: xares_ is null if no connection could be gotten
        // //in that case we just return null
        // //otherwise, test the xaresource liveness
        // if ( xares_ != null ) xares_.isSameRM ( xares_ );
        // }
        // catch ( XAException xa ) {
        // //timed out?
        // xares_ = refreshXAConnection();
        // }
        return xares_;
    }

    /**
     * @see TransactionalResource
     */

    public ResourceTransaction getResourceTransaction ( CompositeTransaction ct )
            throws ResourceException, IllegalStateException
    {
        if ( closed_ )
            throw new IllegalStateException (
                    "XATransactionResource already closed" );

        // because instances are created on a per-root basis, here we can assume
        // that the last used ResourceTransaction was for a sibling!
        if ( ct == null )
            return null; // happens in create method of beans


        // String root = ct.getCompositeCoordinator().getRootTid();

        // CHANGED FOR 3.0: Take TOPMOST root ID, since local subtxs have
        // different root ID
        Stack lineage = ct.getLineage ();
        String root = null;
        if ( lineage == null || lineage.isEmpty () )
            root = ct.getTid ();
        else {
            Stack tmp = (Stack) lineage.clone ();
            while ( !tmp.isEmpty () ) {
                CompositeTransaction next = (CompositeTransaction) tmp.pop ();
                if ( next.isRoot () )
                    root = next.getTid ();
            }
        }
        return (getSiblingMap ( root )).map ( ct );

    }

    private StateRecoveryManager getRecoveryManager () throws ResourceException
    {
        if ( closed_ )
            throw new IllegalStateException (
                    "XATransactionResource already closed" );

        // for XA resources, there is no compensation and hence
        // no internal recovery manager.
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
        if ( res == null || !(res instanceof XATransactionalResource) )
            return false;

        XATransactionalResource xatxres = (XATransactionalResource) res;
        if ( xatxres.servername_ == null || servername_ == null )
            return false;

        return xatxres.servername_.equals ( servername_ );
    }

    /**
     * @see RecoverableResource
     */

    public void setRecoveryService ( RecoveryService recoveryService )
            throws ResourceException
    {

        // null during testing
        if ( recoveryService != null ) {
            if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "Installing recovery service on resource "
                    + getName () );
            branchIdentifier_ = recoveryService.getName ();

            recoveryService.recover ();
        }

        // DON'T call endRecovery here, since the TM
        // will do this (otherwise, only this resource
        // will know).
    }

    /**
     * @see TransactionalResource
     */

    public synchronized boolean recover ( Participant participant )
            throws ResourceException
    {
    		boolean recovered = true;
        if ( closed_ )
            throw new IllegalStateException (
                    "XATransactionResource already closed" );

        if ( !(participant instanceof XAResourceTransaction) )
            throw new ResourceException ( "Wrong argument class: "
                    + participant.getClass ().getName () );
        XAResource xaresource = getXAResource ();
        // if no connection then we can't recover the participant
        if ( xaresource == null ) {
            LOGGER.logWarning ( "XATransactionalResource " + getName() +
                ": XAResource is NULL!" );

            return false;
        }

        XAResourceTransaction xarestx = (XAResourceTransaction) participant;

        if ( recoveryMap_ == null )
            recover ();

        if ( !recoveryMap_.containsKey ( xarestx.getXid() ) ) {
        	//TAKE CARE: if multiple resources 'recover' the same Xid from the same backend
        	//then this will be a problem here: endRecovery will rollback a transaction
        	//as per presumed abort!
            recovered = false;
        }

        //also set xaresource if resource name is the same:
        //this happens if VM exits between XA commit and log flush
        //-> should lead to NOTA in commit
        //see case 21552
        if ( recovered || getName().equals ( xarestx.getResourceName() ) )
        		xarestx.setRecoveredXAResource ( getXAResource () );
        recoveryMap_.remove ( xarestx.getXid() );
        return recovered;
    }

    /**
     * Recover the contained XAResource, and retrieve the xid instances that
     * start with our server's name.
     *
     * @exception ResourceException
     *                If a failure occurs.
     */

    protected void recover () throws ResourceException
    {
        recoveryMap_ = new Hashtable ();
        Xid[] recoveredlist = null;
        int flags = XAResource.TMSTARTRSCAN;
        boolean done = false;
        Stack errors = new Stack ();
        Vector recoveredXids = new Vector ();
        // this vector contains ALL recovered Xids so far,
        // to check if a scan returns duplicate results
        // this is needed for oracle8.1.7

        // if ( branchIdentifier_ == null ) throw new ResourceException (
        // "Recovery service not set for resource " + getName() );

        printMsg ( "recovery initiated for resource " + getName ()
                + " with branchIdentifier " + branchIdentifier_, Console.DEBUG );

        do {
            try {
                recoveredlist = getXAResource ().recover ( flags );
            } catch ( NullPointerException ora ) {
        		//Typical for Oracle without XA setup
            		if ( getXAResource ().getClass ().getName ().toLowerCase ().indexOf ( "oracle" ) >= 0 ) {
            			printMsg ( "ORACLE NOT CONFIGURED FOR XA? PLEASE CONTACT YOUR DBA TO FIX THIS..." , Console.WARN );
            		}
            		throw ora;

            } catch ( XAException xaerr ) {
                LOGGER.logWarning ( "Error in recovery", xaerr );
                errors.push ( xaerr );
                throw new ResourceException ( "Error in recovery", errors );
            }
            flags = XAResource.TMNOFLAGS;
            done = (recoveredlist == null || recoveredlist.length == 0);
            if ( !done ) {

                // TEMPTATIVELY SET done TO TRUE
                // TO TOLERATE ORACLE 8.1.7 INFINITE
                // LOOP (ALWAYS RETURNS SAME RECOVER
                // SET). IF A NEW SET OF XIDS IS RETURNED
                // THEN done WILL BE RESET TO FALSE

                done = true;
                for ( int i = 0; i < recoveredlist.length; i++ ) {

                    Xid xid = new XID ( recoveredlist[i] );
                    // our own XID implements equals and hashCode properly

                    if ( !recoveredXids.contains ( xid ) ) {
                        // a new xid is returned -> we can not be in a
                        // recovery loop -> go on
                        printMsg ( "Resource " + servername_
                                + " inspecting XID: " + xid, Console.INFO );
                        recoveredXids.addElement ( xid );
                        done = false;
                        // only really 'recover' this xid if it is from
                        // this server
                        String branch = new String ( recoveredlist[i]
                                .getBranchQualifier () );
                        if ( branch.startsWith ( branchIdentifier_ ) ) {
                            recoveryMap_.put ( xid, new Object () );
                            printMsg ( "Resource " + servername_
                                    + " recovering XID: " + xid, Console.INFO );
                        } else {
                            printMsg ( "Resource " + servername_ + ": XID "
                                    + xid + " with branch " + branch
                                    + " is not under my responsibility",
                                    Console.INFO );
                        }
                    }
                }
            }
        } while ( !done );

        // allow early GC of recovered Xid list
        recoveredXids = null;

    }

    /**
     * @see TransactionalResource.
     */

    public void endRecovery () throws ResourceException
    {
        if ( closed_ )
            throw new IllegalStateException (
                    "XATransactionResource already closed" );

        XAResource xaresource = getXAResource ();
        // if xaresource is null then we can't do rollback
        // this is acceptable, since the only xids that
        // will be aborted are those who are ONLY indoubt
        // in the database, not in the TM. The DB
        // administrator can rollback those manually without
        // anything else
        if ( xaresource == null )
            return;

        // recovery map is null if no logged coordinators existed
        // in that case, make sure that possible indoubts (of
        // VOTING ergo non-recoverable coordinators)
        // are recovered now. Otherwise, they will not be rolled back.
        if ( recoveryMap_ == null )
            recover ();

        Enumeration toAbortList = recoveryMap_.keys ();
        while ( toAbortList.hasMoreElements () ) {
            XID xid = (XID) toAbortList.nextElement ();
            try {
                xaresource.rollback ( xid );
                // getXAResource().rollback ( xid );
                printMsg ( "XAResource.rollback ( " + xid + " ) called "
                        + "on resource " + servername_, Console.INFO );
            } catch ( XAException xaerr ) {
                // here, an indoubt tx might remain in resource; we do nothing
                // to prevent this and leave it to admin tools
            }
        }

        // ADDED: to enable repeated recovery calls
        recoveryMap_ = null;

        printMsg ( "endRecovery() done for resource " + getName (),
                Console.DEBUG );
    }

    /**
     * Set the XID factory, needed for online management tools.
     *
     * @param factory
     */
    public void setXidFactory ( XidFactory factory )
    {
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

    protected Xid createXid ( String tid )
    {
        return getXidFactory ().createXid ( tid, branchIdentifier_ );
    }

}
