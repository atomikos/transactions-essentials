//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//$Log: XAResourceTransaction.java,v $
//Revision 1.1.1.1.4.5  2007/05/09 06:35:02  guy
//FIXED 20130
//
//Revision 1.1.1.1.4.4  2007/05/08 16:09:38  guy
//FIXED 10100
//
//Revision 1.1.1.1.4.3  2007/04/13 11:02:15  guy
//FIXED 10117
//
//Revision 1.1.1.1.4.2  2007/04/12 11:24:39  guy
//FIXED 10117
//
//Revision 1.1.1.1.4.1  2007/04/12 10:43:48  guy
//FIXED 10117
//
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:36  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:52  guy
//Import.
//
//Revision 1.4  2006/03/21 13:22:32  guy
//Adapted for active recovery and 1 coordinator per subtx.
//
//Revision 1.3  2006/03/15 10:31:30  guy
//Formatted code.
//
//Revision 1.2  2006/03/15 10:22:53  guy
//Refactored to 1 coordinator per subtransaction.
//
//Revision 1.1.1.1  2006/03/09 14:59:06  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.30  2005/09/06 20:44:02  guy
//Added logging statements...
//
//Revision 1.29  2005/08/05 15:05:12  guy
//Merged-in changes of redesign-5-2004 (SOAP branch)
//
//Revision 1.28  2005/05/10 08:45:20  guy
//Merged-in changes of Transactions_2_03 branch.
//
//Revision 1.27  2005/02/19 13:45:18  guy
//Corrected spelling in exception text.
//
//Revision 1.26.2.3  2005/02/25 11:06:06  guy
//Removed getXAResource in constructor: causes problems in MQSeries
//(only one open connection is allowed there, and this closes the
//client's connection!)
//
//Revision 1.26.2.2  2005/02/22 12:49:17  guy
//Added workaround for MQ BUG: 2PC methods should refresh XAResource
//if necessary.
//
//Revision 1.26.2.1  2005/02/10 11:32:49  guy
//Added XA error code to logs.
//
//Revision 1.26  2004/11/24 10:20:34  guy
//Updated error msgs on SysExceptions.
//
//Revision 1.25  2004/11/13 16:58:07  guy
//Added log message for error in resume. Helps diagnose XID problems.
//
//Revision 1.24  2004/11/13 10:24:03  guy
//Added debug output.
//
//Revision 1.23  2004/11/12 10:21:40  guy
//Added logging in DEBUG mode.
//
//Revision 1.22  2004/11/05 13:11:21  guy
//Improved handling of XAException during commit and suspend.
//
//Revision 1.21  2004/10/12 13:04:52  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.20  2004/10/08 08:01:54  guy
//Corrected BUG: 1PC did not throw RollbackException when the resource
//had already done a rollback of the XID.
//
//Revision 1.19  2004/10/08 07:12:23  guy
//Improved logging output.
//
//Revision 1.18  2004/09/06 09:29:29  guy
//Redesigned recovery.
//Redesigned XID generation: this is now done based on the TM name,
//no longer the resource name. This allows one resource to generate
//ALL xids (bootstrapping TM) and nevertheless each XAResource
//can be recovered as it is added later on.
//
//Revision 1.17  2004/09/03 10:02:12  guy
//Redesigned XID and ResTx mapping to allow:
//-second enlist for same underlying resource (without delist of first)
//-each XID is unique even within the same tx
//
//Revision 1.16  2004/09/02 08:21:37  guy
//Included support for commit without prior suspend.
//
//Revision 1.15  2004/09/01 13:40:44  guy
//Merged in TRMI 1.22 changes: logging.
//Added acceptsAllXAResources functionality for JBoss integration.
//
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//Revision 1.14  2004/03/22 15:39:34  guy
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//Added tolerance for DB unavailability in recovery.
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//Added XID information to failures on resume/suspend.
//Revision 1.13  2003/03/11 06:42:57  guy
//Merged in changes from transactionsJTA100 branch.
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//$Id: XAResourceTransaction.java,v 1.1.1.1.4.5 2007/05/09 06:35:02 guy Exp $
//Revision 1.27  2005/02/19 13:45:18  guy
//Corrected spelling in exception text.
//
//Revision 1.26.2.3  2005/02/25 11:06:06  guy
//Removed getXAResource in constructor: causes problems in MQSeries
//(only one open connection is allowed there, and this closes the
//client's connection!)
//
//Revision 1.26.2.2  2005/02/22 12:49:17  guy
//Added workaround for MQ BUG: 2PC methods should refresh XAResource
//if necessary.
//
//Revision 1.26.2.1  2005/02/10 11:32:49  guy
//Added XA error code to logs.
//

//Revision 1.26  2004/11/24 10:20:34  guy
//Updated error msgs on SysExceptions.
//
//Revision 1.25  2004/11/13 16:58:07  guy
//Added log message for error in resume. Helps diagnose XID problems.
//
//Revision 1.24  2004/11/13 10:24:03  guy
//Added debug output.
//
//Revision 1.23  2004/11/12 10:21:40  guy
//Added logging in DEBUG mode.
//
//Revision 1.22  2004/11/05 13:11:21  guy
//Improved handling of XAException during commit and suspend.
//
//Revision 1.21  2004/10/12 13:04:52  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.20  2004/10/08 08:01:54  guy
//Corrected BUG: 1PC did not throw RollbackException when the resource
//had already done a rollback of the XID.
//
//Revision 1.19  2004/10/08 07:12:23  guy
//Improved logging output.
//
//Revision 1.18  2004/09/06 09:29:29  guy
//Redesigned recovery.
//Redesigned XID generation: this is now done based on the TM name,
//no longer the resource name. This allows one resource to generate
//ALL xids (bootstrapping TM) and nevertheless each XAResource
//can be recovered as it is added later on.
//
//Revision 1.17  2004/09/03 10:02:12  guy
//Redesigned XID and ResTx mapping to allow:
//-second enlist for same underlying resource (without delist of first)
//-each XID is unique even within the same tx
//
//Revision 1.16  2004/09/02 08:21:37  guy
//Included support for commit without prior suspend.
//
//Revision 1.15  2004/09/01 13:40:44  guy
//Merged in TRMI 1.22 changes: logging.
//Added acceptsAllXAResources functionality for JBoss integration.
//

//
//Revision 1.12.4.5  2002/11/20 18:35:40  guy
//Added timeout support for XAResource.
//
//Revision 1.12.4.4  2002/11/14 17:05:39  guy
//Added support for XAResource timeout.
//
//Revision 1.12.4.3  2002/11/02 14:35:17  guy
//Corrected BUG: cast to XID in readExternal: must be Xid.
//
//Revision 1.12.4.2  2002/09/12 14:23:26  guy
//Updated to set xaresource_ in constructor. Needed for JMS!
//
//Revision 1.12.4.1  2002/08/30 15:08:08  guy
//Included serialVersionUID for backward log compatibility.
//
//Revision 1.12  2002/03/06 13:43:02  guy
//Corrected rollback with heuristic abort.
//
//Revision 1.11  2002/03/01 10:47:12  guy
//Updated to new prepare exception: HeurMixed.
//
//Revision 1.10  2002/02/27 09:13:34  guy
//Changed XID creation: seed not necessary: inside one LOCAL ct there is no
//internal parallellism -> no violations of isolation possible.
//
//Revision 1.9  2002/02/26 12:45:12  guy
//Added IllegalStateException to resume and suspend. Needed for JTA.
//
//Revision 1.8  2002/02/26 12:00:00  guy
//Corrected recover(): class cast to RecoverableResource, not TransactionalResource.
//
//Revision 1.7  2002/02/26 10:14:47  guy
//Corrected equals and hashCode to use xid_ instead of tid_!
//
//Revision 1.6  2002/02/25 15:56:42  guy
//Corrected bug in commit: on HEURCOMMIT, the case did not have a break.
//
//Revision 1.5  2002/02/25 15:31:21  guy
//Corrected equals() bug: include resource name in comparison.
//
//Revision 1.4  2002/02/18 09:30:07  guy
//Updated?
//
//Revision 1.3  2002/02/12 15:01:34  guy
//Added recover() method for Participant interface.
//
//Revision 1.2  2002/01/29 11:22:35  guy
//Updated CVS to latest state.
//

package com.atomikos.datasource.xa;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
        resource_ = resource;
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
        xid_ = resource_.createXid ( tid_ );
        // xid_ = resource_.getXidFactory().createXid ( tid_ ,
        // resource.getName() );
        state_ = TxState.ACTIVE;
        heuristicMessages_ = new Vector ();
        isXaSuspended_ = false;
        enlisted_ = false;
        // add default heuristic message
        addHeuristicMessage ( new StringHeuristicMessage ( "XA resource '"
                + resource.getName () + "' accessed with Xid '" + xidToHexString (xid_) + "'" ) );
    }

    private static String xidToHexString(Xid xid) {
    	String gtrid = byteArrayToHexString(xid.getGlobalTransactionId());
    	String bqual = byteArrayToHexString(xid.getBranchQualifier());
    	
		return gtrid + ":" + bqual;
	}

	private static String byteArrayToHexString(byte[] byteArray) {
		StringBuffer sb = new StringBuffer();
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
		state_ = state;
	}

	protected void testOrRefreshXAResourceFor2PC ()
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
            Configuration.logDebug ( resourcename_
                    + ": XAResource needs refresh", xa );
            xaresource_ = resource_.getXAResource ();
        }

    }
	
	protected void forceRefreshXAConnection() 
	{
		Configuration.logDebug ( resourcename_ + ": forcing refresh of XAConnection..." );
		try {
			xaresource_ = resource_.refreshXAConnection();
		} catch ( ResourceException re ) {
			Configuration.logWarning ( resourcename_ + ": could not refresh XAConnection" , re );
		}
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
            out.writeBoolean ( true );
            out.writeObject ( xaresource_ );
        } else {
            out.writeBoolean ( false );
        }
    }

    public void readExternal ( ObjectInput in ) throws IOException,
            ClassNotFoundException
    {

        xid_ = (Xid) in.readObject ();
        tid_ = (String) in.readObject ();
        root_ = (String) in.readObject ();
        state_ = (TxState) in.readObject ();

        heuristicMessages_ = (Vector) in.readObject ();
        resourcename_ = (String) in.readObject ();
        boolean xaresSerializable = in.readBoolean();
        if ( xaresSerializable ) {
            // cf case 59238
            xaresource_ = ( XAResource ) in.readObject();
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
	        	printMsg ( "XAResource.end ( " + xidToHexString (xid_)
	                    + " , XAResource.TMSUCCESS ) on resource "
	                    + resourcename_ + " represented by XAResource instance "
	                    + xaresource_, Console.INFO );
	            xaresource_.end ( xid_, XAResource.TMSUCCESS );
	            
	        } catch ( XAException xaerr ) {
	            errors.push ( xaerr );
	            String msg = interpretErrorCode ( resourcename_ , "end" , xid_ , xaerr.errorCode );
	            Configuration.logDebug ( msg, xaerr );
	            throw new ResourceException ( msg, errors );
	        }
	        state_ = TxState.LOCALLY_DONE;
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
        	printMsg ( "XAResource.start ( " + xidToHexString (xid_) + " , " + logFlag
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
        state_ = TxState.ACTIVE;
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
        state_ = TxState.TERMINATED;
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
            Configuration.logDebug ( msg , xaerr );
            if ( (XAException.XA_RBBASE <= xaerr.errorCode)
                    && (xaerr.errorCode <= XAException.XA_RBEND) ) {
                throw new RollbackException ( msg );
            } else {
                errors.push ( xaerr );
                throw new SysException ( msg , errors );
            }
        }
        state_ = TxState.IN_DOUBT;
        if ( ret == XAResource.XA_RDONLY ) {
            printMsg ( "XAResource.prepare ( " + xidToHexString (xid_)
                    + " ) returning XAResource.XA_RDONLY " + "on resource "
                    + resourcename_ + " represented by XAResource instance "
                    + xaresource_, Console.INFO );
            return Participant.READ_ONLY;
        } else {
            printMsg ( "XAResource.prepare ( " + xidToHexString (xid_) + " ) returning OK "
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
            printMsg ( "XAResource.rollback ( " + xidToHexString (xid_) + " ) "
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
                Configuration.logDebug ( msg );
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
                    state_ = TxState.TERMINATED;
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
        state_ = TxState.TERMINATED;
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
            printMsg ( "XAResource.commit ( " + xidToHexString (xid_) + " , " + onePhase
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
                        state_ = TxState.TERMINATED;
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
        state_ = TxState.TERMINATED;
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
        return toString ().hashCode ();
    }

    public String toString ()
    {
        return "XAResourceTransaction: " + xidToHexString (xid_);
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


    	Configuration.logDebug ( this
    			+ ": about to switch to XAResource " + xaresource );
    	xaresource_ = xaresource;

    	try {
    		xaresource_.setTransactionTimeout ( timeout_ );
    	} catch ( XAException e ) {
    		String msg = interpretErrorCode ( resourcename_ , "setTransactionTimeout" , xid_ , e.errorCode );
    		Configuration.logWarning ( msg , e );
    		// we don't care
    	}

    	Configuration.logDebug ( "XAResourceTransaction " + getXid ()
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
    			printMsg ( "XAResource.suspend ( " + xidToHexString (xid_)
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
        		printMsg ( "XAResource.start ( " + xidToHexString (xid_)
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
