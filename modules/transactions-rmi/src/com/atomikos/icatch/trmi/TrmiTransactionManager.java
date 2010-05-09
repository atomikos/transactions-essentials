//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: TrmiTransactionManager.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.3  2006/04/14 12:45:23  guy
//Added properties to TSListener init callback.
//
//Revision 1.2  2006/04/11 11:42:56  guy
//Extracted init properties as constants and replaced all literal references.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.4  2006/03/21 16:14:41  guy
//Added setter for active recovery.
//
//Revision 1.3  2006/03/21 13:24:01  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:24:44  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.34  2005/08/14 10:00:37  guy
//Added prefix and suffix to generated IDs to ensure valid URI format.
//
//Revision 1.33  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.32  2005/08/08 11:24:29  guy
//Added RollbackException to ExportingTM methods.
//
//Revision 1.31  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.30  2005/05/10 08:44:11  guy
//Merged-in changes from Transactions_2_03 branch.
//
//Revision 1.29  2005/02/05 11:09:17  guy
//Trimmed properties for export and JNDI.
//Revision 1.28.2.1  2005/02/05 16:13:17  guy
//Updated release number.
//Now using trimmed properties in TrmiTransactionManager.
//
//Revision 1.28  2004/11/24 10:20:18  guy
//Updated error messages.
//
//Revision 1.27  2004/10/11 13:39:43  guy
//Fixed javadoc and EOL delimiters.
//
//Revision 1.26  2004/09/27 11:36:58  guy
//Added default name generation if no unique name specified.
//
//Revision 1.25  2004/09/06 09:27:25  guy
//Adapted for new recovery.
//
//Revision 1.24  2004/09/01 13:39:23  guy
//Merged changes from TransactionsRMI 1.22.
//
//Revision 1.23  2004/03/25 12:54:11  guy
//Added support for max active transactions.
//
//Revision 1.22  2004/03/22 15:38:14  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.21  2003/09/10 08:57:16  guy
//Modified getInitialContext: first convert properties to hashtable or the
//JNDI will not find the default values.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: TrmiTransactionManager.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.3  2006/04/14 12:45:23  guy
//Added properties to TSListener init callback.
//
//Revision 1.2  2006/04/11 11:42:56  guy
//Extracted init properties as constants and replaced all literal references.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.4  2006/03/21 16:14:41  guy
//Added setter for active recovery.
//
//Revision 1.3  2006/03/21 13:24:01  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:24:44  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.34  2005/08/14 10:00:37  guy
//Added prefix and suffix to generated IDs to ensure valid URI format.
//
//Revision 1.33  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.32  2005/08/08 11:24:29  guy
//Added RollbackException to ExportingTM methods.
//
//Revision 1.31  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.30  2005/05/10 08:44:11  guy
//Merged-in changes from Transactions_2_03 branch.
//
//Revision 1.29  2005/02/05 11:09:17  guy
//Trimmed properties for export and JNDI.
//Revision 1.28.2.1  2005/02/05 16:13:17  guy
//Updated release number.
//Now using trimmed properties in TrmiTransactionManager.
//
//Revision 1.28  2004/11/24 10:20:18  guy
//Updated error messages.
//
//Revision 1.27  2004/10/11 13:39:43  guy
//Fixed javadoc and EOL delimiters.
//
//Revision 1.26  2004/09/27 11:36:58  guy
//Added default name generation if no unique name specified.
//
//Revision 1.25  2004/09/06 09:27:25  guy
//Adapted for new recovery.
//
//Revision 1.24  2004/09/01 13:39:23  guy
//Merged changes from TransactionsRMI 1.22.
//
//Revision 1.20.2.1  2004/04/30 14:33:07  guy
//Included different log levels, and added immediate rollback for extent
//participants.
//
//Revision 1.20  2003/09/01 15:28:04  guy
//Modified exception wrapping in init: more verbose messages.
//Added JRMP native stubs for WebLogic and JBoss compatibility.
//
//Revision 1.19  2003/08/27 19:02:52  guy
//Corrected bug in deserialization of CompositeTransactionProxy
//
//Revision 1.18  2003/08/27 06:24:07  guy
//Adapted to RMI-IIOP.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//$Log: TrmiTransactionManager.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:17  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:45  guy
//Initial import.
//
//Revision 1.3  2006/04/14 12:45:23  guy
//Added properties to TSListener init callback.
//
//Revision 1.2  2006/04/11 11:42:56  guy
//Extracted init properties as constants and replaced all literal references.
//
//Revision 1.1.1.1  2006/03/29 13:21:38  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:01  guy
//Import.
//
//Revision 1.4  2006/03/21 16:14:41  guy
//Added setter for active recovery.
//
//Revision 1.3  2006/03/21 13:24:01  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.2  2006/03/15 10:24:44  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:33  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.34  2005/08/14 10:00:37  guy
//Added prefix and suffix to generated IDs to ensure valid URI format.
//
//Revision 1.33  2005/08/09 15:24:13  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.32  2005/08/08 11:24:29  guy
//Added RollbackException to ExportingTM methods.
//
//Revision 1.31  2005/08/05 15:04:23  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.30  2005/05/10 08:44:11  guy
//Merged-in changes from Transactions_2_03 branch.
//
//Revision 1.29  2005/02/05 11:09:17  guy
//Trimmed properties for export and JNDI.
//
//Revision 1.28  2004/11/24 10:20:18  guy
//Updated error messages.
//
//Revision 1.27  2004/10/11 13:39:43  guy
//Fixed javadoc and EOL delimiters.
//
//Revision 1.26  2004/09/27 11:36:58  guy
//Added default name generation if no unique name specified.
//
//Revision 1.25  2004/09/06 09:27:25  guy
//Adapted for new recovery.
//
//Revision 1.24  2004/09/01 13:39:23  guy
//Merged changes from TransactionsRMI 1.22.
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.23  2004/03/25 12:54:11  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Added support for max active transactions.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.22  2004/03/22 15:38:14  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.17.2.3  2004/01/14 10:38:51  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//*** empty log message ***
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.17.2.2  2003/06/20 16:31:53  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//*** empty log message ***
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.17.2.1  2003/05/22 15:24:53  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Renamed Configuration to UserTransactionServiceFactory.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.17  2003/03/26 19:36:19  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//*** empty log message ***
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.16  2003/03/23 15:20:05  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Corrected BUG in 1PC requested from remote TM in RMI. Added tests for this.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.15  2003/03/23 07:40:56  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Added logging for incoming 2PC calls.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.14  2003/03/11 06:39:16  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.17  2003/03/26 19:36:19  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//*** empty log message ***
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.16  2003/03/23 15:20:05  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Corrected BUG in 1PC requested from remote TM in RMI. Added tests for this.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.15  2003/03/23 07:40:56  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Added logging for incoming 2PC calls.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Revision 1.14  2003/03/11 06:39:16  guy
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: TrmiTransactionManager.java,v 1.1.1.1 2006/10/02 15:21:17 guy Exp $
//
//Revision 1.13.4.3  2003/01/31 15:45:34  guy
//Adapted to set/get Properties in AbstractUserTransactionServiceFactory.
//
//Revision 1.13.4.2  2003/01/29 17:20:08  guy
//Adapted to use JNDI binding instead of Naming of RMI.
//
//Revision 1.13.4.1  2002/11/17 18:36:43  guy
//Changed terminated: does not throw heuristic.
//
//Revision 1.13  2002/03/01 10:48:09  guy
//Updated to new prepare exception of HeurMixed.
//
//Revision 1.12  2002/02/22 17:28:50  guy
//Updated: no RollbackException in addParticipant and registerSynch.
//
//Revision 1.11  2002/02/12 21:20:16  guy
//Made commit and rollback idempotent: no exception if participant is no longer
//there!
//
//Revision 1.10  2002/02/07 10:29:16  guy
//Modified init/unbind to do JNDI binding only if initial context factory is set in the system properties. Otherwise, Naming is used.
//
//Revision 1.9  2002/01/07 12:26:39  guy
//Updated UserTransactionImp to check license, and to allow system propery
//settings.
//
//Revision 1.8  2001/12/26 10:44:20  guy
//Updated unbind to work ONLY IF stub_ != null, to avoid double unexports.
//
//Revision 1.7  2001/12/06 15:30:21  guy
//Adapted framework to allow a UserTransactionServiceFactory facade class for easy setup.
//
//Revision 1.6  2001/11/28 12:53:31  guy
//Updated TransactionService attribute to AbstractTransactionService
//in order to work with the super.init call.
//
//Revision 1.5  2001/11/01 08:42:02  guy
//Changed Extent and ExtentImp to include DIRECT participants.
//Changed TrmiTransactionManager to include this effect.
//
//Revision 1.4  2001/10/29 16:38:11  guy
//Changed UniqueId for String.
//
//Revision 1.2  2001/10/28 16:05:19  guy
//Changed to work with TransactionService.
//
//Revision 1.1.1.1  2001/10/09 12:37:26  guy
//Core module
//

package com.atomikos.icatch.trmi;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.ExportingTransactionManager;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.ImportingTransactionManager;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.config.TSInitInfo;
import com.atomikos.icatch.config.imp.AbstractUserTransactionServiceFactory;
import com.atomikos.icatch.imp.BaseTransactionManager;
import com.atomikos.icatch.imp.ExtentImp;
import com.atomikos.icatch.imp.PropagationImp;
import com.atomikos.icatch.imp.TransactionServiceImp;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.util.UniqueIdMgr;

/**
 * Copyright &copy; 2001-2004, Atomikos. All rights reserved.
 * 
 * A transaction manager implementation for RMI-based composite systems.
 */

public final class TrmiTransactionManager extends BaseTransactionManager
        implements CompositeTransactionServer, ParticipantServer,
        RecoveryServer, Remote, ImportingTransactionManager,
        ExportingTransactionManager
{
    private static final int EVAL = 50;
    // how many restarts are allowed?
    // set this to -1 for unlimited version
    // or to something positive for the eval version

    // private String rminame_ ;
    // //name on which to listen in rmi

    // private String jndiname_;
    // //name to bind in jndi, null if not bound in JNDI

    //

    private String jndiName_;
    // the name to bind in JNDI

    private String initialContextFactory_;
    // the name of the initial context factory

    private String providerUrl_;
    // the URL of the naming provider

    private boolean trustClientTM_;
    // if false, then a forget() on behalf of a client TM will
    // have no effect.

    private Properties properties_;
    // the JNDI context environment

    private Remote stub_;
    // needed for getting a reference to a LOCAL instance,
    // for constructing the coordinator adaptor.
    // otherwise, the entire local TM will be saved to disk
    // in the coordinator logimage (flush triggers a flush of the
    // recovery coordinator adaptor too!)

    private TransactionServiceImp service_;
    // the tx service to use.

    private Console console_;

    // for logging

    /**
     * Create a new instance that listens on the given name.
     * 
     * @param trustClientTM
     *            If true, then a forget() on behalf of a remote client TM will
     *            be executed. This means that problem cases of 2PC will
     *            disappear from the log, and should be used with great care!
     *            The advantage of this is less administration on intranet
     *            clusters.
     * 
     * @param srecmgr
     *            The state recovery manager to use.
     * @param jndiName
     *            The <b>unique</b> jndi name to listen on. Max BYTE length is
     *            64!
     * @param console
     *            For messages from system.
     * @param outputDirPath
     *            The output directory path (including ending slash) where the
     *            server can write its files.
     * @param maxTimeout
     *            The max timeout for new or imported txs.
     * @param maxActives
     *            The max no of active txs, or negative if no such number.
     * @param threaded2PC 
     *			  Whether commit should be multi-threaded or not.
     * @exception UnknownHostException
     *                If local hostIP can not be found.
     */

    public TrmiTransactionManager ( boolean trustClientTM ,
            StateRecoveryManager srecmgr , String jndiName , Console console ,
            String outputDirPath , long maxTimeout , int maxActives , boolean threaded2PC )
            throws java.net.UnknownHostException
    {

        super ();

        UniqueIdMgr idMgr = null;

        idMgr = new UniqueIdMgr ( jndiName, outputDirPath );
        // add prefix and suffix to conform to valid URI scheme
        // required for SOAP transactions
        idMgr.setPrefix ( "atomikos://" );
        idMgr.setSuffix ( "/tx" );

        service_ = new TransactionServiceImp ( jndiName, srecmgr, idMgr,
                console, maxTimeout, maxActives , threaded2PC );
        
        jndiName_ = jndiName;
        if ( console != null ) {
            try {
                console.println ( "Server JNDI name: " + jndiName_ );
            } catch ( Exception ioerr ) {
                System.err.println ( "WARNING: console failure" );
            }
        }
        // name should be less than 64 chars ( for XID compatibility )
        if ( jndiName_.getBytes ().length > 64 )
            throw new RuntimeException ( "Max length of TM name exceeded." );
        trustClientTM_ = trustClientTM;
        console_ = console;
    }

    private String getTrimmedProperty ( String name )
    {
        String ret = null;
        if ( properties_ != null ) {
            ret = properties_.getProperty ( name );
            if ( ret != null )
                ret = ret.trim ();
        }
        return ret;
    }

    /**
     * Utility method to return an initial context based on the contents of the
     * properties. This is needed because the JNDI does not recognize the
     * default properties unless they are explicitly converted to a Hashtable.
     * 
     * @return An initial context whose environment depends on the properties.
     * @throws NamingException
     */
    private Context getInitialContext () throws NamingException
    {
        Hashtable env = new Hashtable ();
        Enumeration enumm = properties_.propertyNames ();
        while ( enumm.hasMoreElements () ) {
            String name = (String) enumm.nextElement ();
            String value = getTrimmedProperty ( name );
            env.put ( name, value );
        }
        return new InitialContext ( env );
    }

    /**
     * Unbind instance in JNDI and RMI.
     * 
     * @param force
     *            If true, unexport is forced even if active calls exist.
     */

    void unbind ( boolean force ) throws SysException
    {
        try {
            if ( stub_ != null ) {

                javax.naming.Context ctx = getInitialContext ();
                ctx.unbind ( jndiName_ );
                String exportClass = getTrimmedProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME );
                if ( "PortableRemoteObject".equals ( exportClass ) ) {
                    PortableRemoteObject.unexportObject ( this );
                } else if ( "UnicastRemoteObject".equals ( exportClass ) ) {
                    UnicastRemoteObject.unexportObject ( this, true );
                }
                stub_ = null;
            }
        } catch ( Exception e ) {
            Stack errors = new Stack ();
            errors.push ( e );
            throw new SysException ( "Error in shutdown: " + e.getMessage (),
                    errors );
        }
    }

    private void log ( String msg , int level )
    {
        try {
            if ( console_ != null ) {
                console_.println ( msg, level );
            }
        } catch ( Exception ignore ) {
        }
    }

    /**
     * Log errors.
     */

    private void log ( String msg , Exception error )
    {
        try {
            if ( console_ != null ) {
                console_.println ( msg );
                if ( error != null ) {
                    console_.println ( error.getMessage () + " "
                            + error.getClass ().getName () );
                }
            }
        } catch ( Exception e ) {
            // ignore
        }
    }

    /**
     * Log a message.
     */

    private void log ( String msg )
    {
        log ( msg, null );
    }

    /**
     * Get the transaction service for this TM.
     * 
     * @return TransactionServiceImp
     */

    TransactionServiceImp getTransactionService ()
    {
        return service_;
    }

    /**
     * Should be called as first method, to initialize the internal state. This
     * method first exports the object to RMI. Then a binding is done into JNDI.
     * 
     * @param properties
     *            The properties.
     * 
     */

    public synchronized void init ( Properties properties ) throws SysException
    {
        Stack errors = new Stack ();
        properties_ = properties;
        providerUrl_ = getTrimmedProperty ( Context.PROVIDER_URL );
        if ( providerUrl_ == null ) {
            String msg = "Startup property " + Context.PROVIDER_URL
                    + " is not set!";
            log ( msg );
            throw new SysException ( msg );
        }
        if ( console_ != null ) {
            try {
                console_.println ( "JNDI provider url: " + providerUrl_ );
            } catch ( Exception ioerr ) {
                System.err.println ( "WARNING: console failure" );
            }
        }

        initialContextFactory_ = getTrimmedProperty ( Context.INITIAL_CONTEXT_FACTORY );
        if ( initialContextFactory_ == null
                || initialContextFactory_.equals ( "" ) ) {
            String msg = "Startup property " + Context.INITIAL_CONTEXT_FACTORY
                    + " is not set!";
            log ( msg );
            throw new SysException ( msg );
        }

        if ( console_ != null ) {
            try {
                console_.println ( "JNDI initial context factory: "
                        + initialContextFactory_ );
            } catch ( Exception ioerr ) {
                System.err.println ( "WARNING: console failure" );
            }
        }

        try {

            String exportClass = getTrimmedProperty ( AbstractUserTransactionServiceFactory.RMI_EXPORT_CLASS_PROPERTY_NAME );
            boolean exported = false;
            if ( "PortableRemoteObject".equals ( exportClass ) ) {
                log ( "About to export on PortableRemoteObject..." );
                PortableRemoteObject.exportObject ( this );
                // Naming.bind ( rminame_ , this );
                // if already bound: will fail -> GOOD! for safety
                log ( "Export done." );
                stub_ = PortableRemoteObject.toStub ( this );
                exported = true;
            } else if ( "UnicastRemoteObject".equals ( exportClass ) ) {
                log ( "About to export on UnicastRemoteObject..." );
                stub_ = UnicastRemoteObject.exportObject ( this );
                log ( "Export done." );
                exported = true;
            } else {
                log ( "RMI-IIOP transactions disabled: com.atomikos.icatch.rmi_export_class="
                        + exportClass );
            }

            if ( exported ) {
                // if not exported, then don't care about getting a context
                // since this could be a limited version that doesn't allow
                // RMI export/import

                javax.naming.Context ctx = getInitialContext ();
                // following does REbind because otherwise availability option
                // will not work: restart of server does not mean restart of
                // JNDI server
                ctx.rebind ( jndiName_, this );
            }

        } catch ( SysException se ) {
            log ( "SysException in TM init!", se );
            throw se;
        } catch ( Exception e ) {
            log ( "Error in TM init!", e );
            errors.push ( e );
            // e.printStackTrace();
            throw new SysException ( "Error in init(): " + e.getMessage (),
                    errors );
        }

        super.init ( service_ , properties );
    }

    RecoveryCoordinator createAdaptor ( RecoveryCoordinator rc )
            throws SysException
    {
        if ( rc == null )
            return null;
        else {
            try {
                // create adaptor with the STUB for the local instance, since
                // this
                // adaptor is meant for flushing out to LOCAL logs too
                return new RecoveryCoordinatorAdaptor ( rc, jndiName_,
                        initialContextFactory_, providerUrl_,
                        (ParticipantServer) stub_ );
            } catch ( Exception e ) {
                Stack errors = new Stack ();
                errors.push ( e );
                throw new SysException ( "Error in adaptor creation: "
                        + e.getMessage (), errors );
            }
        }
    }

    protected CompositeTransaction createProxy ( CompositeTransaction current )
            throws SysException
    {

        // if not exported: merely return current tx
        if ( stub_ == null )
            return current;

        CompositeCoordinatorProxy ccp = new CompositeCoordinatorProxy ( current
                .getCompositeCoordinator (), jndiName_, initialContextFactory_,
                providerUrl_ );
        CompositeTransactionProxy ctp = new CompositeTransactionProxy (
                current, (CompositeTransactionServer) stub_, ccp );

        return ctp;

    }

    /**
     * @see ExportingTransactionManager
     */

    public void addExtent ( Extent extent ) throws SysException,
            RollbackException
    {

        if ( stub_ == null ) {
            String msg = "addExtent ( " + extent
                    + " ) is not allowed because RMI-IIOP is not enabled.";
            log ( msg );
            throw new SysException ( msg );
        }

        CompositeTransaction ct = getCompositeTransaction ();
        if ( ct == null )
            throw new RollbackException ( "No tx for calling thread" );
        Extent ext = ct.getTransactionControl ().getExtent ();
        // check added extent and filter out recursive participants;
        // i.e., participants that are actually the same as this one,
        // and arise due to recursive calls.
        // these must be removed, since otherwise
        // 2PC methods will DEADLOCK!
        CompositeCoordinator coord = ct.getCompositeCoordinator ();
        ParticipantProxy pp = new ParticipantProxy ( jndiName_,
                initialContextFactory_, providerUrl_, this, coord );
        Stack participants = extent.getParticipants ();
        Enumeration parts = participants.elements ();
        Stack filtered = new Stack ();
        while ( parts.hasMoreElements () ) {
            Participant next = (Participant) parts.nextElement ();
            if ( !next.equals ( pp ) ) {
                filtered.push ( next );
            }
        }
        Extent filteredExtent = new ExtentImp (
                extent.getRemoteParticipants (), filtered );
        ext.add ( filteredExtent );
        log ( "addExtent ( " + extent + " ) done for transaction "
                + ct.getTid (), Console.INFO );
    }

    /**
     * @see com.atomikos.icatch.ImportingTransactionManager
     */

    public Extent terminated ( boolean commit ) throws SysException,
            RollbackException
    // , HeurRollbackException,
    // HeurMixedException, HeurHazardException

    {
        Stack errors = new Stack ();
        CompositeTransaction current = getCompositeTransaction ();

        if ( current == null )
            throw new SysException ( "no tx for calling thread" );
        CompositeTerminator terminator = current.getTransactionControl ()
                .getTerminator ();

        if ( stub_ == null && commit ) {
            // not exported -> don't commit because the
            // 2PC stubs will not work anyway
            String msg = "terminated ( "
                    + commit
                    + " ) for tx "
                    + current.getTid ()
                    + " is not allowed because RMI-IIOP is not enabled; rolling back instead.";
            log ( msg );
            terminator.rollback ();
            throw new RollbackException ( msg );
        }

        try {

            // make sure that commit or rollback is called to
            // allow internal cleanup of data structures!
            // because this is an imported tx, this should be done here

            if ( commit )
                terminator.commit ();
            else
                terminator.rollback ();
        } catch ( RollbackException rb ) {
            throw rb;
        }
        // catch ( HeurRollbackException hrb ) {
        // throw hrb;
        // }
        // catch ( HeurMixedException hm ) {
        // throw hm;
        // }
        // catch ( HeurHazardException hh ) {
        // throw hh;
        // }
        catch ( Exception e ) {
            errors.push ( e );
            throw new SysException (
                    "Error in termination: " + e.getMessage (), errors );
        }

        CompositeCoordinator coord = current.getCompositeCoordinator ();
        ParticipantProxy pp = new ParticipantProxy ( jndiName_,
                initialContextFactory_, providerUrl_, this, coord );
        Extent extent = current.getTransactionControl ().getExtent ();

        // add proxy for this one to extent.
        // this frees the client proxy from having to do any adding:
        // either it gets the return value ( no comm. failures ) and
        // we are included in the extent, or it does not get the extent
        // (comm. failure) and we will NOT be included in it.
        // this is what we want.

        extent.add ( pp, 1 );

        log ( "terminated ( " + commit + " ) done for transaction "
                + current.getTid (), Console.INFO );
        return extent;
    }

    /**
     * @see ParticipantServer.
     */

    public int prepare ( String root , int siblings , Dictionary cascadelist )
            throws RollbackException, HeurHazardException, HeurMixedException,
            SysException, RemoteException
    {
        log ( "prepare ( ... ) received for root " + root, Console.INFO );

        int result = -1;
        Participant part = service_.getParticipant ( root );
        part.setGlobalSiblingCount ( siblings );
        part.setCascadeList ( cascadelist );
        try {
            result = part.prepare ();
        } catch ( RollbackException rb ) {
            log ( "Error in prepare for root " + root, rb );
            throw rb;
        } catch ( HeurHazardException hh ) {
            log ( "Error in prepare for root " + root, hh );
            throw hh;
        } catch ( HeurMixedException hm ) {
            log ( "Error in prepare for root " + root, hm );
            throw hm;
        } catch ( SysException se ) {
            log ( "Error in prepare for root " + root, se );
            throw se;
        }

        return result;
    }

    /**
     * @see ParticipantServer.
     */

    public HeuristicMessage[] commit ( String root )
            throws HeurRollbackException, HeurHazardException,
            HeurMixedException, RollbackException, SysException,
            RemoteException
    {
        log ( "commit ( " + root + ") received", Console.INFO );
        HeuristicMessage[] ret = null;
        Participant part = service_.getParticipant ( root );
        if ( part != null ) {
            // null happens if hazard replay of super coordinator, in those
            // cases where the previous commit actually succeeded here.
            try {
                ret = part.commit ( false );
            } catch ( RollbackException rb ) {
                // Added to detect problem in 1PC with two queries in target VM
                log ( "Error in commit for root " + root, rb );
                throw rb;
            } catch ( HeurRollbackException hrb ) {
                log ( "Error in commit for root " + root, hrb );
                throw hrb;
            } catch ( HeurHazardException hh ) {
                log ( "Error in commit for root " + root, hh );
                throw hh;
            } catch ( HeurMixedException hm ) {
                log ( "Error in commit for root " + root, hm );
                throw hm;
            } catch ( SysException se ) {
                log ( "Error in commit for root " + root, se );
                throw se;
            }

        }
        return ret;
    }

    /**
     * @see ParticipantServer.
     */
    public HeuristicMessage[] commitOnePhase ( String root , int siblings ,
            Dictionary cascadeList ) throws HeurRollbackException,
            HeurHazardException, HeurMixedException, RollbackException,
            SysException, RemoteException
    {
        log ( "commitOnePhase ( ... ) received for root " + root, Console.INFO );
        HeuristicMessage[] ret = null;
        Participant part = service_.getParticipant ( root );
        if ( part != null ) {
            // null happens if hazard replay of super coordinator, in those
            // cases where the previous commit actually succeeded here.
            try {
                part.setGlobalSiblingCount ( siblings );
                part.setCascadeList ( cascadeList );
                ret = part.commit ( true );
            } catch ( RollbackException rb ) {
                // Added to detect problem in 1PC with two queries in target VM
                log ( "Error in commit for root " + root, rb );
                throw rb;
            } catch ( HeurRollbackException hrb ) {
                log ( "Error in commit for root " + root, hrb );
                throw hrb;
            } catch ( HeurHazardException hh ) {
                log ( "Error in commit for root " + root, hh );
                throw hh;
            } catch ( HeurMixedException hm ) {
                log ( "Error in commit for root " + root, hm );
                throw hm;
            } catch ( SysException se ) {
                log ( "Error in commit for root " + root, se );
                throw se;
            }
        }
        return ret;
    }

    /**
     * @see ParticipantServer
     */

    public HeuristicMessage[] rollback ( String root )
            throws HeurCommitException, HeurMixedException,
            HeurHazardException, SysException, RemoteException
    {
        log ( "rollback ( " + root + " ) received", Console.INFO );
        HeuristicMessage[] ret = null;
        Participant part = service_.getParticipant ( root );

        if ( part != null ) {
            // null on hazard replay, if the first rollback actually worked.
            try {
                ret = part.rollback ();

            } catch ( HeurCommitException hc ) {
                log ( "Error in rollback for root " + root, hc );
                throw hc;
            } catch ( HeurMixedException hm ) {
                log ( "Error in rollback for root " + root, hm );
                throw hm;
            } catch ( HeurHazardException hh ) {
                log ( "Error in rollback for root " + root, hh );
                throw hh;
            } catch ( SysException se ) {
                log ( "Error in rollback for root " + root, se );
                throw se;
            }
        }
        // NOTE: if null then do NOT return any exception, to make the
        // hazard state of the coordinator disappear

        return ret;
    }

    /**
     * @see ParticipantServer.
     */

    public void forget ( String root ) throws SysException, RemoteException
    {
        log ( "forget ( " + root + " ) received" + root, Console.INFO );
        // only perform forget if the server is run in trust mode
        // otherwise, remote clients should NOT be able to
        // order the forgetting of problem cases, since
        // these might affect OUR business!

        if ( trustClientTM_ ) {
            Participant part = service_.getParticipant ( root );
            part.forget ();
        }
    }

    public boolean equals ( Object o )
    {
        if ( o == null || !(o instanceof TrmiTransactionManager) )
            return false;
        TrmiTransactionManager server = (TrmiTransactionManager) o;
        return jndiName_.equals ( server.jndiName_ )
                && providerUrl_.equals ( server.providerUrl_ );
    }

    public int hashCode ()
    {
        return jndiName_.hashCode () + providerUrl_.hashCode ();
    }

    /**
     * @see CompositeTransactionServer
     */

    public void addSubTxAwareParticipant ( SubTxAwareParticipant subtxaware ,
            String txid ) throws SysException, java.lang.IllegalStateException,
            RemoteException
    {
        CompositeTransaction ct = service_.getCompositeTransaction ( txid );
        // construct adaptor to convert callback tx argument
        // to a proxy before notifying remote party
        SubTxAwareAdaptor adaptor = new SubTxAwareAdaptor ( this, subtxaware );
        ct.addSubTxAwareParticipant ( adaptor );
    }

    public RecoveryCoordinatorProxy addParticipant (
            Participant participantproxy , String txid ) throws SysException,
            java.lang.IllegalStateException,
            // RollbackException,
            RemoteException
    {
        CompositeTransaction ct = service_.getCompositeTransaction ( txid );
        ct.addParticipant ( participantproxy );
        String root = ct.getCompositeCoordinator ().getCoordinatorId ();
        return new RecoveryCoordinatorProxy ( root, jndiName_,
                initialContextFactory_, providerUrl_ );
    }

    public Boolean replayCompletion ( String root , Participant participant )
            throws RemoteException, SysException
    {
        CompositeCoordinator coord = service_.getCompositeCoordinator ( root );
        RecoveryCoordinator reccoord = coord.getRecoveryCoordinator ();

        return reccoord.replayCompletion ( participant );
    }

    /**
     * Shuts down the server.
     * 
     * @exception SysException
     *                On failure.
     * @exception IllegalStateException
     *                If active txs and not force.
     * @param force
     *            If true, possibly indoubt txs will not be taken into account.
     */

    public void shutdown ( boolean force ) throws SysException,
            IllegalStateException
    {
        Stack errors = new Stack ();

        super.shutdown ( force );
        unbind ( force );
    }

    public CompositeTransaction importTransaction ( Propagation propagation ,
            boolean orphancheck , boolean heur_commit ) throws SysException
    {
        Stack lineage = propagation.getLineage ();
        CompositeTransaction parent = (CompositeTransaction) lineage.peek ();
        RecoveryCoordinator rc = parent.getCompositeCoordinator ()
                .getRecoveryCoordinator ();
        RecoveryCoordinator adaptor = createAdaptor ( rc );
        propagation = PropagationImp.adaptPropagation ( propagation, adaptor );
        return recreateCompositeTransaction ( propagation, orphancheck,
                heur_commit );
    }

    public Propagation getPropagation () throws SysException, RollbackException
    {
        CompositeTransaction current = getCompositeTransaction ();
        if ( current == null )
            throw new RollbackException ( "no tx for calling thread" );
        Stack lineage = current.getLineage ();
        // first, check if all in lineage are remote, and

        Stack tmp = new Stack ();
        Stack remoteLineage = new Stack ();

        while ( lineage != null && !lineage.empty () ) {
            CompositeTransaction ancestor = (CompositeTransaction) lineage
                    .pop ();
            if ( ancestor.isLocal () ) {
                // replace by proxy
                tmp.push ( createProxy ( ancestor ) );
                // System.err.println ( "proxy added to propagation" );
            } else {
                // use this one in propagation
                tmp.push ( ancestor );
            }
        }
        // here, tmp contains all ancestor proxies, but in wrong order
        while ( !tmp.empty () ) {
            remoteLineage.push ( tmp.pop () );
        }

        CompositeTransaction ctp = createProxy ( current );

        remoteLineage.push ( ctp );
        CompositeCoordinator coord = (CompositeCoordinator) current
                .getCompositeCoordinator ();

        return new PropagationImp ( remoteLineage, current.isSerial (), current
                .getTransactionControl ().getTimeout ()  );

    }

 

}
