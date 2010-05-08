//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//$Log: TransactionManagerImp.java,v $
//Revision 1.1.1.1.4.2  2007/05/09 06:38:53  guy
//FIXED 20130
//
//Revision 1.1.1.1.4.1  2007/05/09 06:35:31  guy
//FIXED 20130
//
//Revision 1.1.1.1  2006/08/29 10:01:10  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.5  2006/03/21 16:13:40  guy
//Added susppend/resume of activity if it exists.
//
//Revision 1.4  2006/03/21 14:11:00  guy
//Added feature: suspend/resume of activity when JTA transaction is started.
//
//Revision 1.3  2006/03/21 13:23:11  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:31:44  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:10  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.13  2005/08/05 15:03:40  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.12  2004/11/10 09:13:16  guy
//Added check on setRollbackOnly: if no tx then exception.
//
//Revision 1.11  2004/10/12 13:03:38  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.10  2004/09/20 14:50:19  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Added init logic in UserTransactionImp.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Changed TransactionManagerImp: installCompositeTransactionManager
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//checks for null.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.9  2004/09/18 12:09:39  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Added automatic resource registration mode.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.8  2004/09/07 14:52:18  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Improved behaviour of setTransactionTimeout to be spec-compliant.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.7  2004/08/30 07:19:00  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Added method getTransaction(tid).
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Needed by JBoss import.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.6  2004/03/22 15:37:39  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.5.2.3  2003/11/16 09:00:53  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Made transaction manager referenceable. Needed for Jetty.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.5.2.2  2003/10/31 07:57:22  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Made TransactionManager referenceable in JNDI (Jetty requires this)
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.5.2.1  2003/05/22 14:22:27  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Cleaned up format.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.5  2003/03/22 16:03:54  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Updated remote usertx to actually use the timeout settings of the user.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Revision 1.4  2003/03/11 06:39:01  guy
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: TransactionManagerImp.java,v 1.1.1.1.4.2 2007/05/09 06:38:53 guy Exp $
//
//Revision 1.3.4.6  2002/11/14 16:52:40  guy
//Corrected setRollbackOnly to delegate to TransactionImp.
//
//Revision 1.3.4.5  2002/11/14 16:33:42  guy
//Added support for remote usertxs.
//
//Revision 1.3.4.4  2002/11/14 15:01:55  guy
//Adapted to new (redesigned) paradigm: getTx based in tid and suspend/resume should not work with a stack.
//
//Revision 1.3.4.3  2002/09/18 13:58:07  guy
//Corrected setSerial on begin: only happens for root.
//
//Revision 1.3.4.2  2002/09/18 08:50:38  guy
//Added toggle for default serial mode or not.
//
//Revision 1.3.4.1  2002/09/16 09:11:29  guy
//Removed MAX_TIMEOUT check.
//
//Revision 1.3  2002/01/07 16:11:33  guy
//Installation of TM now overrides previous singleton; needed for multiple
//init/shutdown calls. No problem, since user does not use this class anyway.
//
//Revision 1.2  2001/10/29 16:38:09  guy
//Changed UniqueId for String.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

package com.atomikos.icatch.jta;

import java.util.Hashtable;
import java.util.Stack;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TxState;
import com.atomikos.icatch.imp.ResumePreviousTransactionSubTxAwareParticipant;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * 
 * The JTA transaction manager implementation.
 */

public class TransactionManagerImp implements TransactionManager,
        SubTxAwareParticipant, Referenceable

{
    /**
     * Transaction property name to indicate that the transaction is a 
     * JTA transaction. If this property is set to an arbitrary non-null
     * value then a JTA transaction is assumed by this class.
     * This property is used for detecting incompatible existing transactions:
     * if a transaction exists without this property then the <b>begin</b> method
     * will fail.
     */
    public static final String JTA_PROPERTY_NAME = "com.atomikos.icatch.jta.transaction";

    private static TransactionManagerImp singleton_ = null;
    // the singleton instance.

    private static int defaultTimeout;
    // the default timeout in secs

    // public static final int MAX_TIMEOUT = 60;
    // more than 60 secs is not allowed.

    // public static final boolean HEURISTIC_COMMIT = true;
    // // default policy for heuristic decisions.

    private static boolean default_serial = false;
    // txs created through JTA are default not serial
    // can be set by setDefaultSerial

    private int timeout_;
    // timeout for new txs, in seconds

    private Hashtable txmap_;
    // all active txs are here, to avoid having different
    // TransactionImp wrappers for the same ct!
    // This is needed because the TransactionImp contains some
    // state information concerning JTA-specific state of the ct.
    // Hence, multiple getTransaction() calls should return the SAME
    // TransactionImp instance if called consecutively. The map
    // makes sure this can be done.

    private CompositeTransactionManager ctm_;
    // the wrapped instance.

    private int count_;
    // how may invocations of setTimeout?

    private boolean automaticResourceRegistration_;

    // should uknown resources be added as a temporary resource?
    
    private static final void raiseNoTransaction() 
    {
    		StringBuffer msg = new StringBuffer();
    		msg.append ( "This method needs a transaction for the calling thread and none exists.\n" );
    		msg.append ( "Possible causes: either you didn't start a transaction,\n" );
    		msg.append ( "it rolledback due to timeout, or it was committed already.\n" );
    		msg.append ( "ACTIONS: You can try one of the following: \n" );
    		msg.append ( "1. Make sure you started a transaction for the thread.\n" );
    		msg.append ( "2. Make sure you didn't terminate it yet.\n" );
    		msg.append ( "3. Increase the transaction timeout to avoid automatic rollback of long transactions;\n" );
    		msg.append ( "   check http://www.atomikos.com/Documentation/JtaProperties for how to do this." );
    		Configuration.logWarning ( msg.toString() );
    		throw new IllegalStateException ( msg.toString() );
    }

    /**
     * Set the default serial mode for new txs.
     * 
     * @param serial
     *            If true, then new txs will be set to serial mode.
     */

    public static void setDefaultSerial ( boolean serial )
    {
        default_serial = serial;
    }

    /**
     * Get the default mode for new txs.
     * 
     * @return boolean If true, then new txs started through here will be in
     *         serial mode.
     */

    public static boolean getDefaultSerial ()
    {
        return default_serial;
    }
    
    /**
     * Set the default transaction timeout value.
     * 
     * @param defaultTimeoutValue
     * 				the default transaction timeout value in seconds.
     */
	public static void setDefaultTimeout ( int defaultTimeoutValue )
	{
		defaultTimeout = defaultTimeoutValue;
	}
	
	/**
	 * Get the default timeout value.
	 * 
	 * @return the default transaction timeout value in seconds.
	 */
	public static int getDefaultTimeout ()
	{
		return defaultTimeout;
	}


    /**
     * Install a transaction manager.
     * 
     * @param ctm
     *            The composite transaction manager to use.
     * @param automaticResourceRegistration
     *            If true, then unknown XAResource instances should lead to the
     *            addition of a new temporary resource. If false, then unknown
     *            resources will lead to an exception.
     * 
     */

    public static synchronized void installTransactionManager (
            CompositeTransactionManager ctm ,
            boolean automaticResourceRegistration )
    {

        if ( ctm == null )
            singleton_ = null;
        else
            singleton_ = new TransactionManagerImp ( ctm,
                    automaticResourceRegistration );

    }

    /**
     * Get the installed transaction manager, if any.
     * 
     * @return TransactionManager The installed instance, null if none.
     */

    public static TransactionManager getTransactionManager ()
    {
        return singleton_;
    }

    /**
     * Private constructor, to enforce Singleton pattern.
     * 
     * @param ctm
     *            The composite tm to wrap.
     * @param automaticResourceRegistration
     */

    private TransactionManagerImp ( CompositeTransactionManager ctm ,
            boolean automaticResourceRegistration )
    {
        ctm_ = ctm;
        count_ = 0;
        timeout_ = defaultTimeout;
        txmap_ = new Hashtable ();
        automaticResourceRegistration_ = automaticResourceRegistration;
    }

    private void addToMap ( String tid , TransactionImp tx )
    {
        synchronized ( txmap_ ) {
            txmap_.put ( tid.toString (), tx );
        }
    }

    private void removeFromMap ( String tid )
    {
        synchronized ( txmap_ ) {
            txmap_.remove ( tid.toString () );
        }
    }

    private CompositeTransaction getCompositeTransaction() throws ExtendedSystemException 
    {
    	CompositeTransaction ct = null;
    	 try {
             ct = ctm_.getCompositeTransaction ();
         } catch ( SysException se ) {
         	String msg = "Error while retrieving the transaction for the calling thread";
         	Configuration.logWarning( msg , se);
            throw new ExtendedSystemException ( msg , se
                     .getErrors () );
         }
         return ct;
    }
    
    /**
     * Get any previous instance representing tid. Needed for consistently
     * returning the same tx handle to client.
     * 
     * @param tid
     *            The tid to look for
     * @return TransactionImp The previous instance, or null.
     */

    TransactionImp getPreviousInstance ( String tid )
    {
        synchronized ( txmap_ ) {
            if ( txmap_.containsKey ( tid.toString () ) )
                return (TransactionImp) txmap_.get ( tid.toString () );
            else
                return null;
        }
    }

    /**
     * Retrieve an existing tx with given tid
     * 
     * @param tid
     *            The tid of the tx.
     * @return Transaction The instance, or null if not found.
     */

    public Transaction getTransaction ( String tid )
    {
        return getPreviousInstance ( tid );
    }

    /**
     * Create a new transaction and associate it with the current thread. If the
     * current thread already has a transaction, then a local subtransaction
     * will be created. NOTE: the default behaviour of JTA-created
     * subtransactions is blocking. That is: if two of them access the same
     * data, one of the will block. The purpose is to allow safe parallellism in
     * the application. Allowing multiple subtransactions to access the same
     * data has the effect of losing fault tolerance, due to the required XA
     * mappings of JTA. In the native Atomikos mode, this behaviour can be
     * tuned, however.
     */

    public void begin () throws NotSupportedException, SystemException
    {
        begin ( timeout_ );
    }

    /**
     * Custom begin to guarantee a timeout value through an argument.
     */

    public void begin ( int timeout ) throws NotSupportedException,
            SystemException
    {
        TransactionImp tx = null;
        CompositeTransaction ct = null;
        ResumePreviousTransactionSubTxAwareParticipant resumeParticipant = null;
        
        ct = ctm_.getCompositeTransaction();
        if ( ct != null && ct.getProperty (  JTA_PROPERTY_NAME ) == null ) {
            Configuration.logWarning ( "JTA: temporarily suspending incompatible transaction: " + ct.getTid() +
                    " (will be resumed after JTA transaction ends)" );
            ct = ctm_.suspend();
            resumeParticipant = new ResumePreviousTransactionSubTxAwareParticipant ( ct );
        }
        
        try {
            ct = ctm_.createCompositeTransaction ( ( ( long ) timeout ) * 1000 );
            if ( resumeParticipant != null ) ct.addSubTxAwareParticipant ( resumeParticipant );
            if ( ct.isRoot () && getDefaultSerial () )
                ct.getTransactionControl ().setSerial ();
            ct.setProperty ( JTA_PROPERTY_NAME , "true" );
        } catch ( SysException se ) {
        	String msg = "Error in begin()";
        	Configuration.logWarning( msg , se );
            throw new ExtendedSystemException ( msg , se
                    .getErrors () );
        }
        // a new tx can not be in the map yet.
        // next, we put it there.
        tx = new TransactionImp ( ct, automaticResourceRegistration_ );
        addToMap ( ct.getTid (), tx );
        ct.addSubTxAwareParticipant ( this );
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public Transaction getTransaction () throws SystemException
    {

        TransactionImp ret = null;
        CompositeTransaction ct = null;
        ct = getCompositeTransaction();

        if ( ct == null || ct.getProperty (  JTA_PROPERTY_NAME ) == null ) // no tx for thread yet
            ret = null;
        else {
            // since tx was created by begin(), it should be in map
            // note: only active txs are relevant, since setRollbackOnly
            // may have been called already
            ret = getPreviousInstance ( ct.getTid () );
            if ( ret == null && ct.getState ().equals ( TxState.ACTIVE ) ) {
                // happens for JTS imported txs
                ret = new TransactionImp ( ct, automaticResourceRegistration_ );
                addToMap ( ct.getTid (), ret );
                ct.addSubTxAwareParticipant ( this );
            }

        }
        return ret;
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public void setTransactionTimeout ( int seconds ) throws SystemException
    {
        // if ( seconds < MAX_TIMEOUT )

        // changed to conform to the JTA specs
        if ( seconds > 0 ) {
            timeout_ = seconds;
        } else if ( seconds == 0 ) {
            timeout_ = defaultTimeout;
        } else {
        	String msg = "setTransactionTimeout: value must be >= 0";
        	Configuration.logWarning( msg );
        	throw new SystemException ( msg );
        }
            
    }

    public int getTransactionTimeout ()
    {
        return timeout_;
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public Transaction suspend () throws SystemException
    {
        // make sure imported txs can be suspended...
        getTransaction ();

        TransactionImp ret = null;
        CompositeTransaction ct = null;
        try {
            ct = ctm_.suspend ();
        } catch ( SysException se ) {
        	String msg = "Unexpected error while suspending the existing transaction for the current thread";
        	Configuration.logWarning( msg , se );
            throw new ExtendedSystemException ( msg , se
                    .getErrors () );
        }
        if ( ct != null ) {

            ret = getPreviousInstance ( ct.getTid () );
            if ( ret != null ) {
            	// cf case 61305: suspend any enlisted XAResource instances
            	ret.suspendEnlistedXaResources();
            }
        }

        return ret;
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public void resume ( Transaction tobj ) throws InvalidTransactionException,
            IllegalStateException, SystemException
    {
        if ( tobj == null || !(tobj instanceof TransactionImp) ) {
        	String msg = "The specified transaction object is invalid for this configuration: " + tobj;
        	Configuration.logWarning( msg );
            throw new InvalidTransactionException ( msg );
        }

        TransactionImp tximp = (TransactionImp) tobj;
        try {
            ctm_.resume ( tximp.getCT () );
        } catch ( SysException se ) {
        	String msg = "Unexpected error while resuming the transaction in the calling thread";
        	Configuration.logWarning( msg , se );
            throw new ExtendedSystemException (  msg , se
                    .getErrors () );
        }

    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public int getStatus () throws SystemException
    {
        int ret = Status.STATUS_NO_TRANSACTION;

        // make sure imported txs can be supported...
        getTransaction ();

        TransactionImp tx = null;
        CompositeTransaction ct = getCompositeTransaction();

        if ( ct == null ) // no tx for thread yet
            ret = Status.STATUS_NO_TRANSACTION;
        else {
            tx = getPreviousInstance ( ct.getTid () );
            ret = tx.getStatus ();
        }

        return ret;
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public void commit () throws javax.transaction.RollbackException,
            javax.transaction.HeuristicMixedException,
            javax.transaction.HeuristicRollbackException,
            javax.transaction.SystemException, java.lang.IllegalStateException,
            java.lang.SecurityException
    {
        TransactionImp tx = null;
        CompositeTransaction ct = null;

        // make sure imported txs can be supported...
        getTransaction ();

        ct = getCompositeTransaction();

        if ( ct == null ) // no tx for thread yet
            raiseNoTransaction();
        else {
            tx = getPreviousInstance ( ct.getTid () );
            tx.commit ();
        }
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public void rollback () throws IllegalStateException, SystemException,
            SecurityException
    {
        TransactionImp tx = null;
        CompositeTransaction ct = null;

        // make sure imported txs can be supported...
        getTransaction ();

        ct = getCompositeTransaction();

        if ( ct == null ) // no tx for thread yet
            raiseNoTransaction();
        else {
            tx = getPreviousInstance ( ct.getTid () );
            tx.rollback ();
        }
    }

    /**
     * @see javax.transaction.TransactionManager
     */

    public void setRollbackOnly () throws IllegalStateException,
            SystemException
    {
        Stack errors = new Stack ();
        // make sure imported txs can be supported...
        Transaction tx = getTransaction ();
        if ( tx == null )
            raiseNoTransaction();
        try {
            tx.setRollbackOnly ();

        } catch ( SecurityException se ) {
            errors.push ( se );
            String msg = "Unexpected error during setRollbackOnly";
            Configuration.logWarning( msg , se );
            throw new ExtendedSystemException ( msg, errors );
        }
    }

    //
    //
    // IMPLEMENTATION OF SUBTXAWARE
    //
    //

    /**
     * @see com.atomikos.icatch.SubTxAwareParticipant
     */

    public void committed ( CompositeTransaction tx )
    {
        // remove for GC!
        removeFromMap ( tx.getTid () );
    }

    /**
     * @see com.atomikos.icatch.SubTxAwareParticipant
     */

    public void rolledback ( CompositeTransaction tx )
    {
        // remove for GC!
        removeFromMap ( tx.getTid () );
    }

    //
    //
    // IMPLEMENTATION OF REFERENCEABLE
    //
    //

    /**
     * @see javax.naming.Referenceable#getReference()
     */

    public Reference getReference () throws NamingException
    {
        return new Reference ( getClass ().getName (), new StringRefAddr (
                "name", "TransactionManager" ), TransactionManagerFactory.class
                .getName (), null );
    }

}
