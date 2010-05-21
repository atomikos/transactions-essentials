package com.atomikos.icatch.msg;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Stack;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.system.Configuration;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A participant implementation that can be used on a message-based platform.
 * This participant acts as a core proxy for remote participants that co-operate
 * over a message-based 2PC protocol. By introducing the abstract message
 * interface framework, the same participant instance can be reused for
 * different message systems and different protocols.
 */

public class MessageParticipant implements Participant, Externalizable
{
    private String uri_;
    // the unique URI of the remote participant

    private Object address_;
    // the address where to reach the remote participant

    private int protocol_;
    // the two-phase commit protocol in use

    private int format_;
    // the format (XML,...) of the messages

    private Transport transport_;
    // the transport

    private int transportProtocol_;
    // the transport protocol: HTTP,...

    private Dictionary cascadeList_;
    // the cascade info

    private int globalSiblingCount_;
    // the global sibling count

    private HeuristicMessage[] msgs_;
    // the heuristic messages

    private boolean noRollback_;
    // true for some BTP cases.

    private boolean earlyPrepared_;
    // true if remote is early prepared

    private boolean readOnly_;

    // true if remote was readonly

    public MessageParticipant ()
    {
    }

    /**
     * Constructor for internal use: if a replay request comes in then this
     * creates a temporary proxy to supply to the RecoveryCoordinator.
     * 
     */

    MessageParticipant ( String remoteUri , Object address , Transport transport )
    {
        this ( remoteUri , address , transport , null , false , false );
    }

    public MessageParticipant ( String remoteUri , Object address ,
            Transport transport , HeuristicMessage[] msgs ,
            boolean earlyPrepared , boolean readOnly )
    {

        uri_ = remoteUri;
        address_ = address;
        transport_ = transport;
        protocol_ = transport.getCommitProtocol ();
        format_ = transport_.getFormat ();
        transportProtocol_ = transport_.getTransportProtocol ();
        msgs_ = msgs;
        noRollback_ = false;
        earlyPrepared_ = earlyPrepared;
        readOnly_ = readOnly;
    }

    /**
     * Getter for the transport protocol.
     */

    public int getTransportProtocol ()
    {
        return transportProtocol_;
    }

    /**
     * Getter for the commit protocol.
     */

    public int getCommitProtocol ()
    {
        return protocol_;
    }

    /**
     * Getter for the format.
     */

    public int getFormat ()
    {
        return format_;
    }

    public void writeExternal ( ObjectOutput out ) throws IOException
    {
        out.writeObject ( uri_ );
        out.writeObject ( address_ );
        out.writeInt ( protocol_ );
        out.writeInt ( format_ );
        out.writeInt ( transportProtocol_ );
        out.writeObject ( msgs_ );
        out.writeBoolean ( noRollback_ );
        out.writeBoolean ( earlyPrepared_ );
        out.writeBoolean ( readOnly_ );

    }

    public void readExternal ( ObjectInput in ) throws IOException,
            ClassNotFoundException
    {
        uri_ = (String) in.readObject ();
        address_ = in.readObject ();
        protocol_ = in.readInt ();
        format_ = in.readInt ();
        transportProtocol_ = in.readInt ();
        msgs_ = (HeuristicMessage[]) in.readObject ();
        noRollback_ = in.readBoolean ();
        earlyPrepared_ = in.readBoolean ();
        readOnly_ = in.readBoolean ();
    }

    /**
     * @see Participant
     */

    public boolean recover () throws SysException
    {
        boolean recovered = false;

        Enumeration it = Configuration.getResources ();
        while ( it.hasMoreElements () && !recovered ) {
            RecoverableResource res = (RecoverableResource) it.nextElement ();
            recovered = res.recover ( this );
            if ( recovered ) {
                // if a resource has been found that can recover us,
                // then it must be our transport.
                transport_ = (Transport) res;
            }
        }

        return recovered;
    }

    public Object getAddress ()
    {
        return address_;
    }

    /**
     * @see Participant
     */

    public String getURI ()
    {
        return uri_;
    }

    /**
     * @see Participant.
     */

    public void setCascadeList ( java.util.Dictionary allParticipants )
            throws SysException
    {
        cascadeList_ = allParticipants;
    }

    /**
     * @see Participant.
     */

    public void setGlobalSiblingCount ( int count )
    {
        globalSiblingCount_ = count;
    }

    /**
     * @see Participant
     */

    public HeuristicMessage[] getHeuristicMessages ()
    {

        return msgs_;
    }

    /**
     * @see Participant.
     */
    public int hashCode ()
    {
        return uri_.hashCode ();
    }

    public boolean equals ( Object o )
    {
        boolean ret = false;
        if ( o instanceof MessageParticipant ) {
            MessageParticipant other = (MessageParticipant) o;
            ret = getURI ().equals ( other.getURI () );
        }

        return ret;
    }

    /**
     * Returns a string representation of this participant proxy.
     */

    public String toString ()
    {
        return uri_;
    }

    /**
     * @see Participant
     */

    public int prepare () throws RollbackException, HeurHazardException,
            HeurMixedException, SysException
    {
        int ret = Participant.READ_ONLY + 1;

        if ( readOnly_ ) {
            return Participant.READ_ONLY;
        }

        if ( earlyPrepared_ ) {
            // NOTE: for readonly and early prepared, the
            // previous case will have returned already.
            return ret;
        }

        // ask the transport to create a prepare message
        // then send it and wait for the response

        PrepareMessage msg = null;

        if ( cascadeList_ == null ) {
            msg = transport_.createPrepareMessage ( uri_, uri_, address_ );
        } else {
            Enumeration enumm = cascadeList_.keys ();
            CascadeInfo[] info = new CascadeInfo[cascadeList_.size ()];
            int i = 0;
            while ( enumm.hasMoreElements () ) {
                String uri = (String) enumm.nextElement ();
                info[i] = new CascadeInfo ();
                info[i].count = ((Integer) (cascadeList_.get ( uri )))
                        .intValue ();
                info[i].participant = uri;
                i++;
            }
            msg = transport_.createPrepareMessage ( globalSiblingCount_, info,
                    uri_, uri_, address_ );
        }

        TransactionMessage reply = null;
        try {
            int[] expected = { TransactionMessage.ERROR_MESSAGE,
                    TransactionMessage.PREPARED_MESSAGE,
                    TransactionMessage.STATE_MESSAGE };
            // NOTE: state message (ABORTED) is sent in WS-AT!!!

            reply = transport_.sendAndReceive ( msg, transport_
                    .getDefaultTimeout (), expected );

        } catch ( TransportException t ) {
            // remote can stay indoubt; this is taken care of by
            // the heuristic paradigm
            throw new HeurHazardException ( getHeuristicMessages () );
        } catch ( IllegalMessageTypeException e ) {
            Stack errors = new Stack ();
            errors.push ( e );
            throw new SysException ( e.getMessage (), errors );
        }

        // if timed out: the remote participant may be indoubt
        // but we can't be sure -> heuristic hazard.
        if ( reply == null ) {
            // System.err.println ( "MessageParticipant: no reply gotten!" );
            throw new HeurHazardException ( getHeuristicMessages () );
        }

        // here we are if a reply was gotten.
        if ( reply instanceof PreparedMessage ) {
            // remote is prepared
            PreparedMessage preply = (PreparedMessage) reply;
            if ( preply.isReadOnly () ) {
                ret = Participant.READ_ONLY;
            }
            if ( preply.defaultIsRollback () ) {
                noRollback_ = true;
            }
        } else if ( reply instanceof StateMessage ) {
            // WSAT reply
            StateMessage sm = (StateMessage) reply;
            if ( sm.hasCommitted () != null ) {
                if ( sm.hasCommitted ().booleanValue () ) {
                    throw new HeurMixedException ( getHeuristicMessages () );
                } else {
                    noRollback_ = true;
                    throw new RollbackException ( "Remote party voted NO" );
                }
            }

        } else if ( reply instanceof ErrorMessage ) {
            // an error happened
            int code = ((ErrorMessage) reply).getErrorCode ();
            // String[] msgs = reply.getHeuristicMessages();
            //              
            // //if msgs are present then convert the strings
            // if ( msgs != null ) {
            // msgs_ = new HeuristicMessage ( msgs.length );
            // for ( int i = 0 ; i < msgs.length ; i++ ) {
            // msgs_[i] =
            // new StringHeuristicMessage ( msgs[i] );
            // }
            // }

            // based on the error code, throw the right exception
            switch ( code ) {

            case ErrorMessage.ROLLBACK_ERROR:
                // do not propagate later rollback,
                // since remote is rolled back already
                noRollback_ = true;
                throw new RollbackException ( "Remote party voted NO" );

            case ErrorMessage.HEUR_HAZARD_ERROR:
                throw new HeurHazardException ( msgs_ );

            case ErrorMessage.HEUR_MIXED_ERROR:
                throw new HeurMixedException ( msgs_ );

            default:
                throw new SysException ( "Unanticipated messaging error code: "
                        + code );
            }
        } else {
            throw new SysException ( "Prepare: illegal message type returned: "
                    + reply.getClass ().getName () );
        }

        return ret;
    }

    /**
     * @see Participant
     */

    public HeuristicMessage[] commit ( boolean onePhase )
            throws HeurRollbackException, HeurHazardException,
            HeurMixedException, RollbackException, SysException
    {
        // ask the transport to create a commit message
        // then send it and wait for the response

        Configuration.logDebug ( "About to commit participant: " + getURI () );
        CommitMessage msg = transport_.createCommitMessage ( onePhase, uri_,
                uri_, address_ );

        TransactionMessage reply = null;
        int[] expected = { TransactionMessage.ERROR_MESSAGE,
                TransactionMessage.STATE_MESSAGE };

        try {
            reply = transport_.sendAndReceive ( msg, transport_
                    .getDefaultTimeout (), expected );
        } catch ( TransportException t ) {
            // remote can stay indoubt; this is taken care of by
            // the heuristic paradigm
            throw new HeurHazardException ( getHeuristicMessages () );
        } catch ( IllegalMessageTypeException e ) {
            Stack errors = new Stack ();
            errors.push ( e );
            throw new SysException ( e.getMessage (), errors );
        }

        if ( reply == null ) {
            // happens if receive has timed out; this means that the
            // remote may still be indoubt -> hazard

            throw new HeurHazardException ( getHeuristicMessages () );
        } else {
            // a reply has been received -> check it

            if ( reply instanceof ErrorMessage ) {

                int code = ((ErrorMessage) reply).getErrorCode ();
                switch ( code ) {
                case ErrorMessage.HEUR_HAZARD_ERROR:
                    throw new HeurHazardException ( msgs_ );
                case ErrorMessage.HEUR_MIXED_ERROR:
                    throw new HeurMixedException ( msgs_ );
                case ErrorMessage.HEUR_ROLLBACK_ERROR:
                    throw new HeurRollbackException ( msgs_ );
                case ErrorMessage.ROLLBACK_ERROR:
                    throw new RollbackException ( "Remote has rolled back" );
                default:
                    throw new SysException ( "Unanticipated error code: "
                            + code );
                }
            } else if ( reply instanceof StateMessage ) {
                StateMessage creply = (StateMessage) reply;

                // if no state returned, then remote can be indoubt
                if ( creply == null ) {
                    throw new HeurHazardException ( getHeuristicMessages () );
                }
                // if a rolledback state is returned, then remote
                // has done heuristic rollback
                else if ( !creply.hasCommitted ().booleanValue () ) {
                    throw new HeurRollbackException ( getHeuristicMessages () );
                }

            } else {
                throw new SysException (
                        "Commit: illegal message type returned: "
                                + reply.getClass ().getName () );
            }

        }

        Configuration.logDebug ( "Done commit for participant: " + getURI () );
        return getHeuristicMessages ();

    }

    /**
     * @see Participant
     */

    public HeuristicMessage[] rollback () throws HeurCommitException,
            HeurMixedException, HeurHazardException, SysException
    {
        // ask the transport to create a rollback message
        // then send it and wait for the response

        // check if remote wants to hear about rollback
        if ( noRollback_ ) {
            return getHeuristicMessages ();
        }
        
        Configuration.logDebug ( "About to rollback participant: " + getURI() );

        RollbackMessage msg = transport_.createRollbackMessage ( uri_, uri_,
                address_ );

        TransactionMessage reply = null;
        int[] expected = { TransactionMessage.ERROR_MESSAGE,
                TransactionMessage.STATE_MESSAGE };

        try {
            reply = transport_.sendAndReceive ( msg, transport_
                    .getDefaultTimeout (), expected );
        } catch ( TransportException t ) {
            // remote can stay indoubt; this is taken care of by
            // the heuristic paradigm
            throw new HeurHazardException ( getHeuristicMessages () );
        } catch ( IllegalMessageTypeException e ) {
            Stack errors = new Stack ();
            errors.push ( e );
            throw new SysException ( e.getMessage (), errors );
        }

        if ( reply == null ) {
            // happens if receive has timed out; this means that the
            // remote may still be indoubt -> hazard

            throw new HeurHazardException ( getHeuristicMessages () );
        } else {
            // a reply has been received -> check it

            if ( reply instanceof ErrorMessage ) {

                int code = ((ErrorMessage) reply).getErrorCode ();
                switch ( code ) {
                case ErrorMessage.HEUR_HAZARD_ERROR:
                    throw new HeurHazardException ( msgs_ );
                case ErrorMessage.HEUR_MIXED_ERROR:
                    throw new HeurMixedException ( msgs_ );
                case ErrorMessage.HEUR_COMMIT_ERROR:
                    throw new HeurCommitException ( msgs_ );

                default:
                    throw new SysException ( "Unanticipated error code: "
                            + code );
                }
            } else if ( reply instanceof StateMessage ) {
                StateMessage creply = (StateMessage) reply;

                // if no state returned, then remote can be indoubt
                if ( creply == null ) {
                    throw new HeurHazardException ( getHeuristicMessages () );
                }
                // if a committed state is returned, then remote
                // has done heuristic commit
                else if ( creply.hasCommitted ().booleanValue () ) {
                    throw new HeurCommitException ( getHeuristicMessages () );
                }

            } else {
                throw new SysException (
                        "Commit: illegal message type returned: "
                                + reply.getClass ().getName () );
            }

        }

        return getHeuristicMessages ();
    }

    /**
     * @see Participant
     */

    public void forget ()
    {
        // ask the transport to create a forget message
        // and send it.

        ForgetMessage msg = transport_.createForgetMessage ( uri_, uri_,
                address_ );

        try {
            transport_.send ( msg );
        } catch ( TransportException t ) {
            // remote can stay indoubt; this is taken care of by
            // the heuristic paradigm
        } catch ( IllegalMessageTypeException e ) {
            Stack errors = new Stack ();
            errors.push ( e );
            throw new SysException ( e.getMessage (), errors );
        }

    }

}
