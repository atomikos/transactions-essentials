package com.atomikos.icatch.msg;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;

import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.system.Configuration;

/**
 * Copyright &copy; 2002, Guy Pardon. All rights reserved.
 * 
 * A recovery coordinator that takes the role of adaptor. Instances are
 * responsible of the correct translation of local replay requests into
 * corresponding message requests to the remote coordinator.
 */

public class MessageRecoveryCoordinator implements RecoveryCoordinator,
        Externalizable
{
    private String cUri_;
    // the remote URI of the coordinator at the
    // invoker side

    private Object address_;
    // the remote address to reach the coordinator

    private int protocol_;
    // the two-phase commit protocol in use

    private int format_;
    // the format (XML,...) of the messages

    private Transport transport_;
    // the transport

    private int transportProtocol_;

    // the transport protocol: HTTP,...

    public MessageRecoveryCoordinator ()
    {
        // required for externalization
    }

    /**
     * Creates a new instance.
     * 
     * @param cUri
     *            The URI of the REMOTE coordinator to ask replay to.
     * @param address
     *            The address where the REMOTE can be reached.
     * @param transport
     *            The transport to use.
     */

    public MessageRecoveryCoordinator ( String cUri , Object address ,
            Transport transport )
    {
        cUri_ = cUri;
        address_ = address;
        transport_ = transport;
        protocol_ = transport.getCommitProtocol ();
        format_ = transport_.getFormat ();
        transportProtocol_ = transport_.getTransportProtocol ();

    }

    public void writeExternal ( ObjectOutput out ) throws IOException
    {

        out.writeObject ( cUri_ );
        out.writeObject ( address_ );
        out.writeInt ( protocol_ );
        out.writeInt ( format_ );
        out.writeInt ( transportProtocol_ );

        
    }
    
    private void tryInitTransport()
    {
        // now, try to find the transport_ in the configuration
        Enumeration enumm = Configuration.getResources ();
        boolean found = transport_ != null;
        while ( enumm.hasMoreElements () && !found ) {
            Object nxt = enumm.nextElement ();
            if ( nxt instanceof Transport ) {
                Transport t = (Transport) nxt;
                if ( t.getFormat () == format_
                        && t.getCommitProtocol () == protocol_
                        && t.getTransportProtocol () == transportProtocol_ )
                    found = true;
                    transport_ = t;
            }
        }

        if ( !found )
            Configuration.logWarning ( "Transport not found in Configuration: protocol_" );
    }

    public void readExternal ( ObjectInput in ) throws IOException,
            ClassNotFoundException
    {
        cUri_ = (String) in.readObject ();
        address_ = in.readObject ();
        protocol_ = in.readInt ();
        format_ = in.readInt ();
        transportProtocol_ = in.readInt ();

        //disabled because it will fail: deserialization is done
        //before notification of listeners -> before transport is there
        //tryInitTransport();

    }

    /**
     * Transforms a replay request on behalf of a local coordinator into a
     * request that conforms to a message-based two-phase commit.
     * 
     * @param localCoordinator
     *            The local coordinator making the request.
     */

    public Boolean replayCompletion ( Participant localCoordinator )
            throws IllegalStateException
    {

    	   
        // address might be null if prepare in WS-T comes before
        // the response to registration!!! in that case: merely
        // return null
        if ( address_ == null )
            return null;

        //reinit transport for deserialized instance
        tryInitTransport();
        
        CompositeCoordinator c = (CompositeCoordinator) localCoordinator;

        String pUri = localCoordinator.getURI ();

        // constructs a ReplayMessage and send it.
        // the remote node is expected to
        // replay the commit/rollback if any.

        ReplayMessage msg = transport_.createReplayMessage ( CommitServer
                .createGlobalUri ( transport_.getParticipantAddress (), pUri ),
                cUri_, address_ );

        try {
            transport_.send ( msg );
        } catch ( TransportException t ) {
            // ignore, a next replay will happen
            // or heuristics will apply
        } catch ( IllegalMessageTypeException e ) {
            throw new RuntimeException ( e.getMessage () );
        }

        // since not all message protocols may reply,
        // we do NOT wait for the answer.
        // the remote will replay completion anyway

        return null;

    }

    /**
     * @see RecoveryCoordinator
     */

    public String getURI ()
    {
        return cUri_;
    }

    /**
     * Gets the address
     * 
     * @return Object the address.
     */
    public Object getAddress ()
    {
        return address_;
    }

    /**
     * Sets the address (needed for WS-T).
     * 
     * @param address
     */
    public void setAddress ( Object address )
    {
        address_ = address;
    }

}
