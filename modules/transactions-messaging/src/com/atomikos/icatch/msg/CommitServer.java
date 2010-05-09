//$Id: CommitServer.java,v 1.1.1.1 2006/10/02 15:21:14 guy Exp $
//$Log: CommitServer.java,v $
//Revision 1.1.1.1  2006/10/02 15:21:14  guy
//Import into CVS.
//
//Revision 1.1.1.1  2006/04/29 08:55:46  guy
//Initial import.
//
//Revision 1.2  2006/04/14 12:45:16  guy
//Added properties to TSListener init callback.
//
//Revision 1.1.1.1  2006/03/29 13:21:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:48  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:12  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.12  2005/11/08 16:38:42  guy
//Debugged.
//
//Revision 1.11  2005/10/28 15:23:50  guy
//Corrected URI to use in registration.
//
//Revision 1.10  2005/10/21 08:15:45  guy
//*** empty log message ***
//
//Revision 1.9  2005/10/18 12:39:58  guy
//Corrected registration.
//
//Revision 1.8  2005/10/03 11:46:03  guy
//Added support for WS-T registration (2PC and volatile).
//
//Revision 1.7  2005/09/20 07:28:25  guy
//Updated message address to Object (not String) to allow for WS-Addressing
//to be used.
//
//Revision 1.6  2005/09/01 10:02:21  guy
//Corrected commit/rollback to return state if participant not found.
//
//Revision 1.5  2005/08/31 14:34:39  guy
//Changed URI mechanism: an imported coordinator gets a global URI that is
//a combination of its root ID (needed to detect orphans) and its
//globally unique participant address. Otherwise, orphan detection will
//not work: all related transactions would have the same URI!
//
//Revision 1.4  2005/08/14 21:29:59  guy
//Debugged.
//
//Revision 1.3  2005/08/13 13:41:35  guy
//Added test feedback and some logging.
//
//Revision 1.2  2005/08/05 15:03:46  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.1.4.5  2005/07/29 13:08:18  guy
//Completed and tested.
//
//Revision 1.1.4.4  2005/07/28 12:43:41  guy
//Completed Axis implementation of 2PC/SOAP.
//
//Revision 1.1.4.3  2005/07/25 14:00:01  guy
//Added thread pooling for incoming message requests.
//
//Revision 1.1.4.2  2005/07/25 11:47:23  guy
//Refactored: removed MessageFactory (methods moved to Transport).
//
//Revision 1.1.4.1  2004/06/14 08:09:18  guy
//Merged redesign2002 with redesign2003.
//
//Revision 1.1.2.7  2002/11/08 11:49:49  guy
//Tuned.
//
//Revision 1.1.2.6  2002/11/07 17:58:19  guy
//Corrected after testing misery.
//
//Revision 1.1.2.5  2002/11/07 10:00:10  guy
//Tuned messaging framework.
//
//Revision 1.1.2.4  2002/11/05 11:01:29  guy
//Corrected design a bit.
//
//Revision 1.1.2.3  2002/11/05 09:33:31  guy
//Changed: MsgListener adds Transport to callback, and CommitServer
//now listens on TS start/stop events.
//
//Revision 1.1.2.2  2002/11/04 19:10:19  guy
//Updated.
//
//Revision 1.1.2.1  2002/10/31 16:06:47  guy
//Added basic message framework for 2PC over message systems.
//

package com.atomikos.icatch.msg;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TSListener;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.imp.thread.TaskManager;
import com.atomikos.icatch.system.Configuration;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A CommitServer takes care of <b>incoming</b> 2PC events (prepare, commit,
 * rollback, forget and replay requests). It does this by listening on the
 * transport for these types of messages. This class is generic and can be used
 * with any 2PC message protocol.
 * 
 */

public class CommitServer implements MessageListener, TSListener
{

    /**
     * The query string used to indicate where the local ID part starts in a
     * global URI.
     */
    private static final String QUERY_STRING = "/root?id=";

    /**
     * Creates a globally unique URI from a given (unique) address part and a
     * local ID part. This is needed because the core needs to use the client's
     * ID for an imported coordinator (to be able to detect orphans).
     * Consequently, an imported coordinator's ID is not a valid URI that
     * uniquely identifies the work on our site! By combining with the (globally
     * unique) address of the transaction service core, we get uniqueness.
     * 
     * @param addressPart
     *            The globally unique address part.
     * @param localIdPart
     *            The locally unique ID part.
     * @return A globally unique URI
     */
    public static final String createGlobalUri ( String addressPart ,
            String localIdPart )
    {

        String ret = addressPart + QUERY_STRING + localIdPart;
        return ret;
    }

    /**
     * Extracts the local ID part from a global URI that belongs to a local
     * coordinator. This method should only be called for URIs that correspond
     * to a local coordinator, or the extraction will not work. In practice,
     * this means that only the CommitServer should call this method, since this
     * is the only class that needs to interpret incoming messages for local
     * coordinators.
     * 
     * 
     * @param globalUri
     *            The URI.
     * @return The local ID part.
     */
    public static final String extractLocalIdPart ( String globalUri )
    {
        String ret = null;
        try {

            int index = globalUri.lastIndexOf ( QUERY_STRING );
            ret = globalUri.substring ( index + QUERY_STRING.length () );
        } catch ( RuntimeException e ) {
            Configuration.logWarning (
                    "CommitServer: error extracting local ID from URI", e );
            throw e;
        }
        return ret;
    }

    private Transport[] transports_;
    // the transports that this server listens on

    private TransactionService service_;
    // the transaction service to listen on

    // private Console console_;
    // the console to write to.

    private boolean trustClientTM_;
    // true iff remote forget triggers local forget

    private int poolSize_;
    // the max. number of threads in the pool

    private boolean shutdown_;

    // flag to indicate if shutdown was called

    /**
     * Constructs a new instance.
     */

    public CommitServer ()
    {
        this ( false );
    }

    public CommitServer ( boolean trustClientTM )
    {
        trustClientTM_ = trustClientTM;
        shutdown_ = true;
    }

    private void logError ( String msg )
    {
        try {
            Configuration.logWarning ( msg );
        } catch ( Exception e ) {
        }

    }

    void processCommit ( CommitMessage msg , Transport transport )
    {
        String localId = extractLocalIdPart ( msg.getTargetURI () );
        Configuration
                .logDebug ( "CommitServer: processing commit for participant "
                        + localId );
        Participant p = service_.getParticipant ( localId );

        // null if no longer exists
        if ( p == null ) {
            Configuration.logDebug ( "CommitServer: participant not found: "
                    + localId );
            // happens if hazard replay with intermediate commit
            // in this case, continue and reply state to resolve heuristic state
            // at client TM
        }

        // delegate to participant
        boolean error = false;
        int errorCode = ErrorMessage.UNKNOWN_ERROR;

        try {
            if ( p != null )
                p.commit ( msg.isOnePhase () );
        } catch ( RollbackException rb ) {
            Configuration.logDebug ( "Error processing message: " + rb );
            error = true;
            errorCode = ErrorMessage.ROLLBACK_ERROR;
        } catch ( HeurRollbackException hr ) {
            Configuration.logDebug ( "Error processing message: " + hr );
            error = true;
            errorCode = ErrorMessage.HEUR_ROLLBACK_ERROR;
        } catch ( HeurMixedException hm ) {
            Configuration.logDebug ( "Error processing message: " + hm );
            error = true;
            errorCode = ErrorMessage.HEUR_MIXED_ERROR;
        } catch ( HeurHazardException hh ) {
            Configuration.logDebug ( "Error processing message: " + hh );
            error = true;
            errorCode = ErrorMessage.HEUR_HAZARD_ERROR;
        } catch ( SysException se ) {
            Configuration.logDebug ( "Error processing message: " + se );
            errorCode = ErrorMessage.UNKNOWN_ERROR;
            error = true;
        }

        if ( error ) {
            // an error has occurred -> respond accordingly
            ErrorMessage errmsg = transport.createErrorMessage ( errorCode,
                    createGlobalUri ( transport.getParticipantAddress (), p
                            .getURI () ), msg.getSenderURI (), msg
                            .getSenderAddress () );
            try {
                transport.send ( errmsg );
            } catch ( Exception e ) {
                // no exception allowed in message listener
                logError ( "Error in sending ERROR message: " + e.getMessage () );
            }
        } else {
            // send a state reply
            StateMessage smsg = transport.createStateMessage ( new Boolean (
                    true ), msg.getTargetURI (), msg.getSenderURI (), msg
                    .getSenderAddress () );
            try {
                transport.send ( smsg );
            } catch ( Exception e ) {
                // no exception allowed in message listener
                logError ( "Error in sending STATE COMMITTED message: "
                        + e.getMessage () );
            }
        }
    }

    void processRollback ( RollbackMessage msg , Transport transport )
    {
        String localId = extractLocalIdPart ( msg.getTargetURI () );
        Configuration
                .logDebug ( "CommitServer: processing rollback for participant "
                        + localId );
        Participant p = service_.getParticipant ( localId );

        // null if no longer exists
        if ( p == null ) {
            Configuration.logDebug ( "CommitServer: participant not found: "
                    + localId );

        }

        // delegate to participant
        boolean error = false;
        int errorCode = ErrorMessage.UNKNOWN_ERROR;
        try {
            // p is null if timed out already -> don't do this but send state
            // reply afterwards!
            // otherwise, the client TM will get hazards without a good reason
            if ( p != null )
                p.rollback ();
        } catch ( HeurCommitException hc ) {
            Configuration.logDebug ( "Error processing message: " + hc );
            error = true;
            errorCode = ErrorMessage.HEUR_COMMIT_ERROR;
        } catch ( HeurMixedException hm ) {
            Configuration.logDebug ( "Error processing message: " + hm );
            error = true;
            errorCode = ErrorMessage.HEUR_MIXED_ERROR;
        } catch ( HeurHazardException hh ) {
            Configuration.logDebug ( "Error processing message: " + hh );
            error = true;
            errorCode = ErrorMessage.HEUR_HAZARD_ERROR;
        } catch ( SysException se ) {
            Configuration.logDebug ( "Error processing message: " + se );
            errorCode = ErrorMessage.UNKNOWN_ERROR;
            error = true;
        }

        if ( error ) {
            // an error has occurred -> respond accordingly
            ErrorMessage errmsg = transport.createErrorMessage ( errorCode,
                    createGlobalUri ( transport.getParticipantAddress (), p
                            .getURI () ), msg.getSenderURI (), msg
                            .getSenderAddress () );
            try {
                transport.send ( errmsg );
            } catch ( Exception e ) {
                // no exception allowed in message listener
                logError ( "Error in sending ERROR message: " + e.getMessage () );
            }
        } else {
            // send a state reply
            StateMessage smsg = transport.createStateMessage ( new Boolean (
                    false ), msg.getTargetURI (), msg.getSenderURI (), msg
                    .getSenderAddress () );
            try {
                transport.send ( smsg );
            } catch ( Exception e ) {
                // no exception allowed in message listener
                logError ( "Error in sending STATE ROLLEDBACK message: "
                        + e.getMessage () );
            }
        }
    }

    void processPrepare ( PrepareMessage msg , Transport transport )
    {

        String localId = extractLocalIdPart ( msg.getTargetURI () );
        Configuration
                .logDebug ( "CommitServer: processing prepare for participant "
                        + localId );
        Participant p = service_.getParticipant ( localId );

        // null if no longer exists
        if ( p == null ) {
            // respond aborted
            // ErrorMessage errmsg =
            // fact.createErrorMessage (
            // ErrorMessage.ROLLBACK_ERROR , msg.getTargetURI() ,
            // msg.getSenderURI() , msg.getSenderAddress() );
            // try {
            // transport.send ( errmsg );
            // }
            // catch ( Exception e ) {
            // //no exception allowed in message listener
            // logError (
            // "Error in sending ERROR message: "+
            // e.getMessage() );
            // }

            Configuration.logDebug ( "CommitServer: participant not found: "
                    + localId );
            return;
        }

        // determine if orphan info is present
        if ( msg.hasOrphanInfo () ) {
            p.setGlobalSiblingCount ( msg.getGlobalSiblingCount () );
            CascadeInfo[] info = msg.getCascadeInfo ();
            Hashtable table = new Hashtable ();
            for ( int i = 0; i < info.length; i++ ) {
                table.put ( info[i].participant, new Integer ( info[i].count ) );
            }
            p.setCascadeList ( table );
        }

        // delegate prepare to the participant
        int readonly = Participant.READ_ONLY + 1;
        boolean error = false;
        int errorCode = ErrorMessage.UNKNOWN_ERROR;

        try {
            // System.err.println ( "CommitServer: delegating prepare to TS..."
            // );
            readonly = p.prepare ();
            // System.err.println ( "CommitServer: done with delegation" );
        } catch ( HeurHazardException hh ) {
            Configuration.logDebug ( "Error processing message: " + hh );
            error = true;
            errorCode = ErrorMessage.HEUR_HAZARD_ERROR;
        } catch ( HeurMixedException hm ) {
            Configuration.logDebug ( "Error processing message: " + hm );
            error = true;
            errorCode = ErrorMessage.HEUR_MIXED_ERROR;
        } catch ( RollbackException rb ) {
            Configuration.logDebug ( "Error processing message: " + rb );
            error = true;
            errorCode = ErrorMessage.ROLLBACK_ERROR;
        } catch ( SysException se ) {
            Configuration.logDebug ( "Error processing message: " + se );
            error = true;
            errorCode = ErrorMessage.UNKNOWN_ERROR;
        }

        if ( error ) {
            // an error has occurred -> respond accordingly
            ErrorMessage errmsg = transport.createErrorMessage ( errorCode,
                    createGlobalUri ( transport.getParticipantAddress (), p
                            .getURI () ), msg.getSenderURI (), msg
                            .getSenderAddress () );
            try {
                transport.send ( errmsg );
            } catch ( Exception e ) {
                // no exception allowed in message listener
                logError ( "Error in sending ERROR message: " + e.getMessage () );
            }
        } else {
            // send a prepared reply
            boolean ro = (readonly == Participant.READ_ONLY);
            PreparedMessage pmsg = transport.createPreparedMessage ( ro,
                    createGlobalUri ( transport.getParticipantAddress (), p
                            .getURI () ), msg.getSenderURI (), msg
                            .getSenderAddress () );
            try {
                // System.err.println ( "CommitServer: sending reply..." );
                transport.send ( pmsg );
                // System.err.println ( "CommitServer: done sending reply" );
            } catch ( Exception e ) {
                // no exception allowed in message listener
                logError ( "Error in sending PREPARED message: "
                        + e.getMessage () );
            }
        }
    }

    void processForget ( ForgetMessage msg , Transport transport )
    {
        String localId = extractLocalIdPart ( msg.getTargetURI () );
        Configuration
                .logDebug ( "CommitServer: processing forget for participant "
                        + localId );
        Participant p = service_.getParticipant ( localId );
        // null if no longer exists
        if ( p == null ) {
            Configuration.logDebug ( "CommitServer: participant not found: "
                    + localId );
            return;
        }

        if ( !trustClientTM_ ) {
            Configuration
                    .logDebug ( "CommitServer: ignoring forget: com.atomikos.icatch.trust_client_tm is false" );
            return;
        }

        p.forget ();
    }

    void processReplay ( ReplayMessage msg , Transport transport )
    {

        Configuration
                .logDebug ( "CommitServer: processing replay for participant "
                        + msg.getSenderURI () );
        // a replay request comes in
        // create a DUMMY participant because
        // the original participant is hard to retrieve
        MessageParticipant p = new MessageParticipant ( msg.getSenderURI (),
                msg.getSenderAddress (), transport );

        String localId = extractLocalIdPart ( msg.getTargetURI () );
        CompositeCoordinator coord = service_
                .getCompositeCoordinator ( localId );

        if ( coord != null ) {

            RecoveryCoordinator reccoord = coord.getRecoveryCoordinator ();
            reccoord.replayCompletion ( p );
        }

        // we DO NOT return anything, since the message-based
        // 2PC protocols usually don't do that
    }

    void processRegister ( RegisterMessage msg , Transport transport )
    {
        Configuration
                .logDebug ( "CommitServer: processing registration of participant: "
                        + msg.getSenderURI ()
                        + " for transaction: "
                        + msg.getTargetURI () );

        boolean error = false;
        int errorCode = ErrorMessage.UNKNOWN_ERROR;

        // for registration, the TARGET address is what we need:
        // the SENDER address is merely where to reply to
        MessageParticipant p = new MessageParticipant ( msg.getSenderURI (),
                msg.getTargetAddress (), transport, null, false, false );

        CompositeTransactionManager ctm = Configuration
                .getCompositeTransactionManager ();
        if ( ctm == null ) {
            error = true;
            errorCode = ErrorMessage.UNKNOWN_ERROR;

        } else {

            CompositeTransaction ct = ctm.getCompositeTransaction ( msg
                    .getTargetURI () );

            if ( ct == null ) {
                error = true;
                errorCode = ErrorMessage.ROLLBACK_ERROR;
                Configuration
                        .logDebug ( "CommitServer: transaction not found: "
                                + msg.getTargetURI () );
            } else {
                Configuration
                        .logDebug ( "CommitServer: registering participant for 2PC: "
                                + p.getURI () );
                if ( msg.registerForTwo2PC () ) {
                    ct.getExtent ().add ( p, 1 );
                    //explicitly add participant too, to avoid that 
                    //concurrent timeout/rollback ignores the extent participant
                    //(which could be a problem if p is early prepared)
                    ct.addParticipant ( p );
                } else {
                    Configuration
                            .logDebug ( "CommitServer: registering participant for subtx termination: "
                                    + p.getURI () );
                    ct
                            .addSubTxAwareParticipant ( new SubTxAwareMessageParticipant (
                                    p ) );
                }

                // send reply
                RegisteredMessage reply = transport.createRegisteredMessage (
                        msg.getTargetURI (), msg.getSenderURI (), msg
                                .getSenderAddress () );
                try {
                    transport.send ( reply );
                } catch ( Exception e ) {
                    logError ( "CommitServer: error sending REGISTERED message: "
                            + e.getMessage () );
                }
            }
        }

        if ( error ) {
            ErrorMessage errMsg = transport.createErrorMessage ( errorCode, msg
                    .getTargetURI (), msg.getSenderURI (), msg
                    .getSenderAddress () );
            try {
                transport.send ( errMsg );
            } catch ( Exception e ) {
                logError ( "CommitServer: error sending ERROR message: "
                        + e.getMessage () );
            }

        }

    }


    /**
     * Initializes the instance. This should be the first method called.
     * 
     * @param service
     *            The transaction service.
     * @param transports
     *            The transport list to listen on.
     * @exception SysException
     *                On failure.
     */

    public synchronized void init ( TransactionService service ,
            Transport[] transports ) throws SysException
    {
        service_ = service;
        transports_ = transports;
        
        service_.addTSListener ( this );

    }

    /**
     * @see TSListener
     */

    public synchronized void init ( boolean before , Properties p )
    {
        // System.err.println ( "CommitServer.init!" );
        if ( shutdown_ ) {
            shutdown_ = false;
        }

        if ( before )    
        		for ( int i = 0; i < transports_.length; i++ ) {
                Transport transport = transports_[i];
                try {
                	   Configuration.logDebug ( "CommitServer " + this + ": registering with transport: " + transport.getName() );
                    transport.registerMessageListener ( this,
                            TransactionMessage.PREPARE_MESSAGE );
                    transport.registerMessageListener ( this,
                            TransactionMessage.COMMIT_MESSAGE );
                    transport.registerMessageListener ( this,
                            TransactionMessage.ROLLBACK_MESSAGE );
                    transport.registerMessageListener ( this,
                            TransactionMessage.FORGET_MESSAGE );
                    transport.registerMessageListener ( this,
                            TransactionMessage.REPLAY_MESSAGE );
                    // replies to replay requests are also new requests
                    // since the requesting thread is not blocked!
                    transport.registerMessageListener ( this,
                            TransactionMessage.STATE_MESSAGE );
                    transport.registerMessageListener ( this,
                            TransactionMessage.REGISTER_MESSAGE );
                    // transport.start();
                } catch ( TransportException e ) {
                    Stack errors = new Stack ();
                    errors.push ( e );
                    throw new SysException ( e.getMessage (), errors );
                }
          }
        
    }

    /**
     * @see TSListener
     */

    public synchronized void shutdown ( boolean before )
    {
        // make sure TS is already stopped, or other msgs might
        // still come in and resolve indoubts
        if ( !before ) {
            for ( int i = 0; i < transports_.length; i++ ) {
                Transport transport = transports_[i];
                try {
                    transport.removeMessageListener ( this );
                } catch ( TransportException e ) {
                    Stack errors = new Stack ();
                    errors.push ( e );
                    throw new SysException ( e.getMessage (), errors );
                }
            }
            service_.removeTSListener ( this );
            shutdown_ = true;
            
        }


    }

    /**
     * @see MessageListener
     */

    public synchronized boolean messageReceived ( TransactionMessage msg ,
            Transport transport )
    {

        Configuration.logInfo ( "CommitServer: " + this + ": received message: "
                + msg.toString () );
        if ( msg instanceof RegisterMessage ) {
            // process registration ASAP to avoid that we start committing
            // before
            // the background registration works! Otherwise, we commit without
            // the remote participant
            processRegister ( (RegisterMessage) msg, transport );
        } else if ( !shutdown_ ) {
        	TaskManager.getInstance().executeTask ( new RequestHandler ( this , msg , transport ) );
        }

        // if shutdown: just ignore the message: SOAP messages can be lost!
        return true;
    }

}
