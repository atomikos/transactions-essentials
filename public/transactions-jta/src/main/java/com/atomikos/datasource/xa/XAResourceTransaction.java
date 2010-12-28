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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.datasource.ResourceTransaction;
import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * An implementation of ResourceTransaction for XA transactions.
 */

public class XAResourceTransaction implements ResourceTransaction,
        Externalizable, Participant
{

    // force-set version ID for backward log compatibility
    static final long serialVersionUID = -8227293322090019196L;
    
    protected static String interpretErrorCode ( String resourceName , String opCode , Xid xid , int errorCode ) {
    		
    		String msg = "unkown";
    		switch ( errorCode ) {
    			case XAException.XAER_RMFAIL: 
    				msg = "the XA resource has become unavailable";
    				break;
    			case XAException.XA_RBROLLBACK: 
    				msg = "the XA resource has rolled back for an unspecified reason";
    				break;
    			case XAException.XA_RBCOMMFAIL:
    				msg = "the XA resource rolled back due to a communication failure";
    				break;
    			case XAException.XA_RBDEADLOCK:
    				msg = "the XA resource has rolled back because of a deadlock";
    				break;
    			case XAException.XA_RBINTEGRITY:
    				msg = "the XA resource has rolled back due to a constraint violation";
    				break;
    			case XAException.XA_RBOTHER:
    				msg = "the XA resource has rolled back for an unknown reason";
    				break;
    			case XAException.XA_RBPROTO:
    				msg = "the XA resource has rolled back because it did not expect this command in the current context";
    				break;
    			case XAException.XA_RBTIMEOUT:
    				msg = "the XA resource has rolled back because the transaction took too long";
    				break;
    			case XAException.XA_RBTRANSIENT:
    				msg = "the XA resource has rolled back for a temporary reason - the transaction can be retried later";
    				break;
    			case XAException.XA_NOMIGRATE:
    				msg = "XA resume attempted in a different place from where suspend happened";
    				break;
    			case XAException.XA_HEURHAZ:
    				msg = "the XA resource may have heuristically completed the transaction";
    				break;
    			case XAException.XA_HEURCOM:
    				msg = "the XA resource has heuristically committed";
    				break;
    			case XAException.XA_HEURRB:
    				msg = "the XA resource has heuristically rolled back";
    				break;
    			case XAException.XA_HEURMIX:
    				msg = "the XA resource has heuristically committed some parts and rolled back other parts";
    				break;
    			case XAException.XA_RETRY:
    				msg = "the XA command had no effect and may be retried";
    				break;	
    			case XAException.XA_RDONLY:
    				msg = "the XA resource had no updates to perform for this transaction";
    				break;
    			case XAException.XAER_RMERR:
    				msg = "the XA resource detected an internal error";
    				break;
    			case XAException.XAER_NOTA:
    				msg = "the supplied XID is invalid for this XA resource";
    				break;
    			case XAException.XAER_INVAL:
    				msg = "invalid arguments were given for the XA operation";
    				break;
    			case XAException.XAER_PROTO:
    				msg = "the XA resource did not expect this command in the current context";
    				break;
    			case XAException.XAER_DUPID:
    				msg = "the supplied XID already exists in this XA resource";
    				break;
    			case XAException.XAER_OUTSIDE:
    				msg = "the XA resource is currently involved in a local (non-XA) transaction";
    				break;
    			default: msg = "unknown";    		
    		}
    		return "XA resource '" + resourceName + "': " + opCode + " for XID '" + xidToHexString ( xid ) + 
    			   "' raised " + errorCode + ": " + msg;
    }

    private String tid_ , root_;

    private boolean isXaSuspended_;
    // true iff XA suspended

    private TxState state_;

    private String resourcename_;
    // the name of the resource, needed on recovery for determining xid branch

    private transient Xid xid_;

    private transient String xidToHexString;
	private transient String toString;
    
    private void setXid_(Xid xid_) {
		this.xid_ = xid_;
		
		xidToHexString=	xidToHexString(xid_).intern();
		toString = "XAResourceTransaction: "+xidToHexString;
	}

	private transient XATransactionalResource resource_;

    private transient XAResource xaresource_;

    //protected transient CompositeCoordinator coordinator_;

    private transient CompositeTransaction transaction_;

    private Vector heuristicMessages_;

    private transient boolean enlisted_;
    // true as soon as registered with XAResource.

    private transient int timeout_;
    


    public XAResourceTransaction ()
    {
        // needed for externalization mechanism
    }

    XAResourceTransaction ( XATransactionalResource resource ,
            CompositeTransaction transaction , String root )
    {
        setResource ( resource );
        transaction_ = transaction;
        // FOLLOWING COMMENTED OUT:
        // setXAResource is called anyhow, and that is
        // the best way to avoid connection limitations
        // such as in MQSeries
        // xaresource_ = resource.getXAResource();
        // re-added xaresource_ setting here, because for JMS
        // there is no threading and no thread-surpassing
        // reuse -> we can use the same and set it here.
        TransactionControl control = transaction.getTransactionControl ();
        // null for some testing programs
        if ( control != null ) {
            timeout_ = (int) transaction.getTransactionControl ().getTimeout () / 1000;

        }
        
        tid_ = transaction.getTid ();
        root_ = root;
        resourcename_ = resource.getName ();
        setXid_( resource_.createXid ( tid_ ));
        // xid_ = resource_.getXidFactory().createXid ( tid_ ,
        // resource.getName() );
        setState ( TxState.ACTIVE );
        heuristicMessages_ = new Vector ();
        isXaSuspended_ = false;
        enlisted_ = false;
        // add default heuristic message
        addHeuristicMessage ( new StringHeuristicMessage ( "XA resource '"
                + resource.getName () + "' accessed with Xid '" + xidToHexString  + "'" ) );
    }
    
	void setResource ( XATransactionalResource resource ) 
	{
		this.resource_ = resource;
	}

	void setState ( TxState state ) 
	{
		this.state_ = state;
	}

   static String xidToHexString(Xid xid) {
    	String gtrid = byteArrayToHexString(xid.getGlobalTransactionId());
    	String bqual = byteArrayToHexString(xid.getBranchQualifier());
    	
		return gtrid + ":" + bqual;
	}

	private static String byteArrayToHexString(byte[] byteArray) {
		StringBuffer sb = new StringBuffer(byteArray.length);
    	for (int i = 0; i < byteArray.length; i++) {
    		String hexByte = Integer.toHexString(byteArray[i]);
			sb.append(hexByte);
		}
    	return sb.toString().toUpperCase();
	}
	
	private void switchToHeuristicState ( String opCode , TxState state , XAException cause ) 
	{
		String errorMsg = interpretErrorCode ( resourcename_ , opCode , xid_ , cause.errorCode );
		addHeuristicMessage ( new StringHeuristicMessage ( errorMsg ) );
		setState ( state );
	}

	protected void testOrRefreshXAResourceFor2PC () throws XAException
    {
        // check if connection has not timed out
        try {

        	//fix for case 31209: refresh entire XAConnection on heur hazard
        	if ( state_.equals ( TxState.HEUR_HAZARD ) ) forceRefreshXAConnection();
        	else if ( xaresource_ != null ) {
                // we should be the same as ourselves!
                // NOTE: xares_ is null if no connection could be gotten
                // in that case we just return true
                // otherwise, test the xaresource liveness
                xaresource_.isSameRM ( xaresource_ );
            }
        } catch ( XAException xa ) {
            // timed out?
           if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( resourcename_
                    + ": XAResource needs refresh", xa );
            
            if ( resource_ == null ) {
    			// cf bug 67951
    			// happens on recovery without resource found
    			throwXAExceptionForUnavailableResource();
    		} else {
    			xaresource_ = resource_.getXAResource ();
    		}
        }

    }
	
	protected void forceRefreshXAConnection() throws XAException
	{
		if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( resourcename_ + ": forcing refresh of XAConnection..." );
		if ( resource_ == null ) {
			// cf bug 67951
			// happens on recovery without resource found
			throwXAExceptionForUnavailableResource();
		}
		
		try {
			xaresource_ = resource_.refreshXAConnection();
		} catch ( ResourceException re ) {
			Configuration.logWarning ( resourcename_ + ": could not refresh XAConnection" , re );
		}
	}

	private void throwXAExceptionForUnavailableResource() throws XAException 
	{
		String msg = resourcename_ + ": resource no longer available - recovery might be at risk!";
		Configuration.logWarning ( msg );
		XAException err = new XAException ( msg );
		err.errorCode = XAException.XAER_RMFAIL;
		throw err;
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

    /**
     * Needed for garbage collection of res tx instances: if no new siblings can
     * arrive, this method removes any pointers to res txs in the resource
     */

    private void terminateInResource ()
    {
        if ( resource_ != null )
            resource_.removeSiblingMap ( root_ );
    }

    public void writeExternal ( ObjectOutput out ) throws IOException
    {
        out.writeObject ( xid_ );
        out.writeObject ( tid_ );
        out.writeObject ( root_ );
        out.writeObject ( state_ );
        // CLONE vector to ensure it gets re-written!
        out.writeObject ( heuristicMessages_.clone () );
        out.writeObject ( resourcename_ );
        if ( xaresource_ instanceof Serializable ) {
            // cf case 59238
            out.writeObject ( Boolean.TRUE );
            out.writeObject ( xaresource_ );
        } else {
            out.writeObject ( Boolean.FALSE );
        }
    }

    public void readExternal ( ObjectInput in ) throws IOException,
            ClassNotFoundException
    {

        setXid_((Xid) in.readObject ());
        tid_ = (String) in.readObject ();
        root_ = (String) in.readObject ();
        state_ = (TxState) in.readObject ();

        heuristicMessages_ = (Vector) in.readObject ();
        resourcename_ = (String) in.readObject ();
        
        try {
			Boolean xaresSerializable = (Boolean) in.readObject();
			if (xaresSerializable !=null && xaresSerializable ) {
			    // cf case 59238
			    xaresource_ = ( XAResource ) in.readObject();
			} 
		} catch (OptionalDataException e) {
			// happens if boolean is missing - like in older logfiles
			Configuration.logDebug ("Ignoring missing field" , e );
		}

    }

    /**
     * @see ResourceTransaction.
     */

    public String getTid ()
    {
        return tid_;
    }

    /**
     * @see ResourceTransaction.
     */

    // public TransactionalResource getResource()
    // {
    // return resource_;
    // }
    /**
     * @see ResourceTransaction.
     */

    public void addCompensationContext ( java.util.Dictionary context )
            throws IllegalStateException
    {
        // throw new RuntimeException("Not supported: addCompensationContext");
    }

    /**
     * @see ResourceTransaction.
     */

    public void addHeuristicMessage ( HeuristicMessage mesg )
            throws IllegalStateException
    {
        heuristicMessages_.addElement ( mesg );
    }

    /**
     * @see ResourceTransaction.
     */

    public HeuristicMessage[] getHeuristicMessages ()
    {
        HeuristicMessage[] heurArray = new HeuristicMessage[1];
        return (HeuristicMessage[]) heuristicMessages_.toArray ( heurArray );

    }

    /**
     * @see ResourceTransaction.
     */

    public java.util.Dictionary getCompensationContext ()
    {
        return null;
    }

    /**
     * @see ResourceTransaction.
     */

    public synchronized void suspend () throws ResourceException
    {

        Stack errors = new Stack ();

        //BugzID: 20545
        //State may be IN_DOUBT or TERMINATED when a connection is closed AFTER commit!
        //In that case, don't call END again, and also don't generate any error!
        //This is required for some hibernate connection release strategies.
        if ( state_.equals( ( TxState.ACTIVE ) ) ) {
	        try {
	        	printMsg ( "XAResource.end ( " + xidToHexString
	                    + " , XAResource.TMSUCCESS ) on resource "
	                    + resourcename_ + " represented by XAResource instance "
	                    + xaresource_, Console.INFO );
	            xaresource_.end ( xid_, XAResource.TMSUCCESS );
	            
	        } catch ( XAException xaerr ) {
	            errors.push ( xaerr );
	            String msg = interpretErrorCode ( resourcename_ , "end" , xid_ , xaerr.errorCode );
	            if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( msg, xaerr );
	            throw new ResourceException ( msg, errors );
	        }
	        setState ( TxState.LOCALLY_DONE );
        }
    }

    /**
     * @see ResourceTransaction.
     */

    public synchronized void resume () throws ResourceException
    {
        int flag = 0;
        Stack errors = new Stack ();
        String logFlag = "";
        if ( state_.equals ( TxState.LOCALLY_DONE ) ) {// reused instance
            flag = XAResource.TMJOIN;
            logFlag = "XAResource.TMJOIN";
        } else if ( !enlisted_ ) {// new instance
            flag = XAResource.TMNOFLAGS;
            logFlag = "XAResource.TMNOFLAGS";
        } else
            throw new IllegalStateException ( "Wrong state for resume: "
                    + state_ );

        try {
        	printMsg ( "XAResource.start ( " + xidToHexString  + " , " + logFlag
                    + " ) on resource " + resourcename_
                    + " represented by XAResource instance " + xaresource_,
                    Console.INFO );
            xaresource_.start ( xid_, flag );
            
        } catch ( XAException xaerr ) {
        		String msg = interpretErrorCode ( resourcename_ , "resume" , xid_ , xaerr.errorCode );
            Configuration.logWarning ( msg ,
                    xaerr );
            errors.push ( xaerr );
            throw new ResourceException ( msg ,
                    errors );
        }
        setState ( TxState.ACTIVE );
        enlisted_ = true;
    }

    /**
     * @see Participant
     */

    public void setCascadeList ( java.util.Dictionary allParticipants )
            throws SysException
    {
        // nothing to be done for this.
    }

    public Object getState ()
    {
        return state_;
    }

    /**
     * @see Participant
     */
    public boolean recover () throws SysException
    {
        boolean recovered = false;
        // perform extra initialization
        
        if ( TxState.ACTIVE.equals ( state_ ) || TxState.LOCALLY_DONE.equals ( state_ ) ) {
        	//see case 23364: recovery before prepare should do nothing
        	//and certainly not reset the xaresource
        	return false;
        }

        // check all available resources to see if we can fully recover
        // our transient but necessary properties.

        Enumeration resources = Configuration.getResources ();

        // changed while condition: removed: "&& !recovered"
        // because the redesigned recovery needs to report
        // this participant to EVERY recoverable resource:
        // recovered XIDs can be shared in two resources
        // if they connect to the same back-end RM
        // (remember: now we use the TM name for the branch!)
        // If so, each resource needs to know that the XID
        // can be recovered, or endRecovery in one of them
        // will incorrectly rollback

        while ( resources.hasMoreElements () ) {
            RecoverableResource res = (RecoverableResource) resources
                    .nextElement ();
            if ( res.recover ( this ) ) {
                recovered = true;
            }

        }

        if ( !recovered && getXAResource() != null ) {
        	// cf case 59238
        	recovered = true;
        }

        enlisted_ = true;
        return recovered;
    }

    /**
     * @see Participant
     */

    public void setGlobalSiblingCount ( int count )
    {
        // nothing to be done here
    }

    /**
     * @see Participant
     */

    public synchronized void forget ()
    {
        terminateInResource ();
        try {
            if ( xaresource_ != null )
                xaresource_.forget ( xid_ );
            // xaresource is null if recovery failed
        } catch ( Exception err ) {
            // we don't care here
        }
        setState ( TxState.TERMINATED );
    }

    /**
     * @see Participant
     */

    public synchronized int prepare () throws RollbackException,
            HeurHazardException, HeurMixedException, SysException
    {
        int ret = 0;
        Stack errors = new Stack ();
        terminateInResource ();

        // ADDED: to tolerate non-delisting appservers
        if ( TxState.ACTIVE.equals ( state_ ) )
            suspend ();

        // duplicate prepares can happen for siblings in serial subtxs!!!
        // in that case, the second prepare just returns READONLY
        if ( state_.equals ( TxState.IN_DOUBT ) )
            return Participant.READ_ONLY;
        else if ( !state_.equals ( TxState.LOCALLY_DONE ) )
            throw new SysException ( "Wrong state for prepare: " + state_ );
        try {
            // refresh xaresource for MQSeries: seems to close XAResource after
            // suspend???
            testOrRefreshXAResourceFor2PC ();
            printMsg ( "About to call prepare on XAResource instance: "
                    + xaresource_, Console.DEBUG );
            ret = xaresource_.prepare ( xid_ );

        } catch ( XAException xaerr ) {
        	    String msg = interpretErrorCode ( resourcename_ , "prepare" , xid_ , xaerr.errorCode );
            if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( msg , xaerr );
            if ( (XAException.XA_RBBASE <= xaerr.errorCode)
                    && (xaerr.errorCode <= XAException.XA_RBEND) ) {
                throw new RollbackException ( msg );
            } else {
                errors.push ( xaerr );
                throw new SysException ( msg , errors );
            }
        }
        setState ( TxState.IN_DOUBT );
        if ( ret == XAResource.XA_RDONLY ) {
            printMsg ( "XAResource.prepare ( " + xidToHexString 
                    + " ) returning XAResource.XA_RDONLY " + "on resource "
                    + resourcename_ + " represented by XAResource instance "
                    + xaresource_, Console.INFO );
            return Participant.READ_ONLY;
        } else {
            printMsg ( "XAResource.prepare ( " + xidToHexString  + " ) returning OK "
                    + "on resource " + resourcename_
                    + " represented by XAResource instance " + xaresource_,
                    Console.INFO );
            return Participant.READ_ONLY + 1;
        }
    }

    /**
     * @see Participant.
     */

    public synchronized HeuristicMessage[] rollback ()
            throws HeurCommitException, HeurMixedException,
            HeurHazardException, SysException
    {
        Stack errors = new Stack ();
        terminateInResource ();

        if ( !enlisted_ )
            return null;
        if ( state_.equals ( TxState.TERMINATED ) )
            return getHeuristicMessages ();

        if ( state_.equals ( TxState.HEUR_MIXED ) )
            throw new HeurMixedException ( getHeuristicMessages () );
        if ( state_.equals ( TxState.HEUR_COMMITTED ) )
            throw new HeurCommitException ( getHeuristicMessages () );
        if ( xaresource_ == null ) {
        		Configuration.logWarning ( "XAResourceTransaction " + getXid ()
                    + ": no XAResource to rollback - the required resource is probably not yet intialized?" );
            throw new HeurHazardException ( getHeuristicMessages () );
        }

        // xaresource is null if recovery failed

        try {
            if ( state_.equals ( TxState.ACTIVE ) )// first suspend xid
                suspend ();

            // refresh xaresource for MQSeries: seems to close XAResource after
            // suspend???
            testOrRefreshXAResourceFor2PC ();
            printMsg ( "XAResource.rollback ( " + xidToHexString  + " ) "
                    + "on resource " + resourcename_
                    + " represented by XAResource instance " + xaresource_,
                    Console.INFO );
            xaresource_.rollback ( xid_ );

        } catch ( ResourceException resErr ) {
            // failure of suspend
            errors.push ( resErr );
            throw new SysException ( "Error in rollback: "
                    + resErr.getMessage (), errors );
        } catch ( XAException xaerr ) {
        	    String msg = interpretErrorCode ( resourcename_ , "rollback" , xid_ , xaerr.errorCode );
            if ( (XAException.XA_RBBASE <= xaerr.errorCode)
                    && (xaerr.errorCode <= XAException.XA_RBEND) ) {
                // do nothing, corresponds with semantics of rollback
                if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( msg );
            } else {
                Configuration.logWarning ( msg , xaerr );
                switch ( xaerr.errorCode ) {
                case XAException.XA_HEURHAZ:
                	switchToHeuristicState ( "rollback" , TxState.HEUR_HAZARD , xaerr );
                	throw new HeurHazardException ( getHeuristicMessages() );
                case XAException.XA_HEURMIX:
                	switchToHeuristicState ( "rollback" , TxState.HEUR_MIXED , xaerr );
                    throw new HeurMixedException ( getHeuristicMessages () );
                case XAException.XA_HEURCOM:
                	switchToHeuristicState ( "rollback", TxState.HEUR_COMMITTED , xaerr );
                    throw new HeurCommitException ( getHeuristicMessages () );
                case XAException.XA_HEURRB:
                    forget ();
                    break;
                case XAException.XAER_NOTA:
                	   //see case 21552
                    printMsg ( "XAResource.rollback: invalid Xid - already rolled back in resource?" , Console.DEBUG );
                    setState ( TxState.TERMINATED );
                    //ignore error - corresponds to semantics of rollback!
                    break;
                default:
                	//fix for bug 31209
                	switchToHeuristicState( "rollback", TxState.HEUR_HAZARD , xaerr );
                    errors.push ( xaerr );
                    throw new SysException ( msg , errors );
                } // switch
            } // else
        }
        setState ( TxState.TERMINATED );
        return getHeuristicMessages ();
    }

    /**
     * @see Participant
     */

    public synchronized HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurHazardException,
            HeurMixedException, RollbackException, SysException
    {
        Stack errors = new Stack ();
        terminateInResource ();
        
        if ( state_.equals ( TxState.TERMINATED ) )
            return getHeuristicMessages ();
        if ( state_.equals ( TxState.HEUR_MIXED ) )
            throw new HeurMixedException ( getHeuristicMessages () );
        if ( state_.equals ( TxState.HEUR_ABORTED ) )
            throw new HeurRollbackException ( getHeuristicMessages () );
        if ( xaresource_ == null ) {
            Configuration.logWarning ( "XAResourceTransaction " + getXid ()
                    + ": no XAResource to commit - the required resource is probably not yet intialized?" );
            throw new HeurHazardException ( getHeuristicMessages () );
        }

        // xaresource is null if recovery failed

        // ADDED: to tolerate non-delisting appservers
        try {

            if ( TxState.ACTIVE.equals ( state_ ) )
                suspend ();
        } catch ( ResourceException re ) {
            // happens if already rolled back or something else;
            // in any case the transaction can be trusted to act
            // as if rollback already happened
            throw new com.atomikos.icatch.RollbackException ( re.getMessage () );
        }

        if ( !(state_.equals ( TxState.LOCALLY_DONE ) || state_
                .equals ( TxState.IN_DOUBT ) || state_.equals ( TxState.HEUR_HAZARD )) )
            throw new SysException ( "Wrong state for commit: " + state_ );
        try {
            // refresh xaresource for MQSeries: seems to close XAResource after
            // suspend???
            testOrRefreshXAResourceFor2PC ();
            printMsg ( "XAResource.commit ( " + xidToHexString + " , " + onePhase
                    + " ) on resource " + resourcename_
                    + " represented by XAResource instance " + xaresource_,
                    Console.INFO );
            xaresource_.commit ( xid_, onePhase );
            
        } catch ( XAException xaerr ) {
        	   String msg = interpretErrorCode ( resourcename_ , "commit" , xid_ , xaerr.errorCode );
            Configuration.logWarning ( msg , xaerr );

            if ( (XAException.XA_RBBASE <= xaerr.errorCode)
                    && (xaerr.errorCode <= XAException.XA_RBEND) ) {
                errors.push ( xaerr );

                if ( !onePhase )
                    throw new SysException ( msg , errors );
                else
                    throw new com.atomikos.icatch.RollbackException (
                            "Already rolled back in resource." );
            } else {
                switch ( xaerr.errorCode ) {
                case XAException.XA_HEURHAZ:
                	switchToHeuristicState ( "commit" , TxState.HEUR_HAZARD , xaerr );
                    throw new HeurHazardException ( getHeuristicMessages () );
                case XAException.XA_HEURMIX:
                	switchToHeuristicState ( "commit", TxState.HEUR_MIXED , xaerr );
                    throw new HeurMixedException ( getHeuristicMessages () );
                case XAException.XA_HEURCOM:
                    forget ();
                    break;
                case XAException.XA_HEURRB:
                	switchToHeuristicState ( "commit", TxState.HEUR_ABORTED , xaerr );
                    throw new HeurRollbackException ( getHeuristicMessages () );
                case XAException.XAER_NOTA:
                    if ( ! onePhase ) {
                    	   //see case 21552
                        printMsg ( "XAResource.commit: invalid Xid - transaction already committed in resource?" , Console.WARN );
                        setState ( TxState.TERMINATED );
                        break;
                    }
                default:
                	//fix for bug 31209
                	switchToHeuristicState( "commit", TxState.HEUR_HAZARD , xaerr );
                    errors.push ( xaerr );
                    throw new SysException ( msg , errors );
                } // switch
            } // else
        }
        setState ( TxState.TERMINATED );
        return getHeuristicMessages ();
    }

    /**
     * Absolutely necessary for coordinator to work correctly
     */

    public boolean equals ( Object o )
    {
        // NOTE: basing equals on the xid means that if two
        // different instances for the same xid exist then these
        // will be considered the same, and a second will
        // NOT be added to the coordinator's participant list.
        // However, this is not a problem, since the first added instance
        // will do all commit work (they are equivalent).
        // Note that this can ONLY happen for two invocations of the
        // SAME local composite transaction to the SAME resource.
        // Because INSIDE ONE local transaction there is no
        // internal parallellism, having two invocations to the same
        // resource execute with the same xid is not a problem.

        if ( o == null || !(o instanceof XAResourceTransaction) )
            return false;

        XAResourceTransaction other = (XAResourceTransaction) o;
        return xid_.equals ( other.xid_ );
    }

    /**
     * Absolutely necessary for coordinator to work correctly
     */

    public int hashCode ()
    {
        return xidToHexString.hashCode();
    }

    public String toString ()
    {
        return toString;
    }

    /**
     * Get the Xid. Needed by jta mappings.
     * 
     * @return Xid The Xid of this restx.
     */

    public Xid getXid ()
    {
        return xid_;
    }
    
    protected void setRecoveredXAResource ( XAResource xaresource ) 
    {
    	// See case 25671: only reset xaresource if NOT enlisted!
    	// Otherwise, the delist will fail since XA does not allow
    	// enlist/delist on different xaresource instances.
    	// This should not interfere with recovery since a recovered
    	// instance will NOT have state ACTIVE...
    	if ( ! TxState.ACTIVE.equals ( state_ ) ) {
    		setXAResource ( xaresource );
    	}
    }

    /**
     * Set the XAResource attribute.
     * 
     * @param xaresource
     *            The new XAResource to use. This new XAResource represents the
     *            new connection to the XA database. Necessary because on reuse,
     *            the old xaresource may be in use by another thread, for
     *            another transaction.
     */

    public void setXAResource ( XAResource xaresource )
    {


    	if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( this
    			+ ": about to switch to XAResource " + xaresource );
    	xaresource_ = xaresource;

    	try {
    		xaresource_.setTransactionTimeout ( timeout_ );
    	} catch ( XAException e ) {
    		String msg = interpretErrorCode ( resourcename_ , "setTransactionTimeout" , xid_ , e.errorCode );
    		Configuration.logWarning ( msg , e );
    		// we don't care
    	}

    	if ( Configuration.isDebugLoggingEnabled() ) Configuration.logDebug ( "XAResourceTransaction " + getXid ()
    			+ ": switched to XAResource " + xaresource );


    }

    /**
     * Perform an XA suspend.
     */

    public void xaSuspend () throws XAException
    {
    	// cf case 61305: make XA suspend idempotent
    	// so appserver suspends do not interfere with
    	// our suspends (triggered by transaction suspend)
    	if ( !isXaSuspended_ ) {
    		try {
    			printMsg ( "XAResource.suspend ( " + xidToHexString 
    					+ " , XAResource.TMSUSPEND ) on resource "
    					+ resourcename_ + " represented by XAResource instance "
    					+ xaresource_, Console.INFO );
    			xaresource_.end ( xid_, XAResource.TMSUSPEND );

    			isXaSuspended_ = true;
    		}
    		catch ( XAException xaerr ) {
    			String msg = interpretErrorCode ( resourcename_ , "suspend" , xid_ , xaerr.errorCode );
    			Configuration.logWarning ( msg , xaerr );
    			throw xaerr;
    		}
    	}
    }

    /**
     * Perform an xa resume
     */

    public void xaResume () throws XAException
    {
        try {
        		printMsg ( "XAResource.start ( " + xidToHexString
                    + " , XAResource.TMRESUME ) on resource "
                    + resourcename_ + " represented by XAResource instance "
                    + xaresource_, Console.INFO );
        		xaresource_.start ( xid_, XAResource.TMRESUME );
        		
             isXaSuspended_ = false;
        }
        catch ( XAException xaerr ) {
			String msg = interpretErrorCode ( resourcename_ , "resume" , xid_ , xaerr.errorCode );
			Configuration.logWarning ( msg , xaerr );
			throw xaerr;
		}
        
    }

    /**
     * Test if the resource has been ended with TMSUSPEND.
     * 
     * @return boolean True if so.
     */
    public boolean isXaSuspended ()
    {
        return isXaSuspended_;
    }

    /**
     * Test if the restx is active (in use).
     * 
     * @return boolean True if so.
     */
    public boolean isActive ()
    {
        return state_.equals ( TxState.ACTIVE );
    }

    /**
     * @see com.atomikos.icatch.Participant#getURI()
     */
    public String getURI ()
    {

        return null;
    }

	String getResourceName() 
	{
		return resourcename_;
	}

	XAResource getXAResource() 
	{
		return xaresource_;
	}

}

