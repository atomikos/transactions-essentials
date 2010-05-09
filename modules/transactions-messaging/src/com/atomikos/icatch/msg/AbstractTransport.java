package com.atomikos.icatch.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.diagnostics.Console;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.system.Configuration;

/**
 * 
 * Copyright &copy; 2004 Atomikos. All rights reserved.
 * 
 * @author guy
 * 
 * An abstract Transport class that implements the generic functionality.
 * 
 */
public abstract class AbstractTransport implements Transport
{

    private String name;

    private int commitProtocol;

    private int transportProtocol;

    private int format;

    private String address;

    private long defaultTimeout;

    // private MessageFactory messageFactory;

    private String participantAddress;

    private String coordinatorAddress;

    private Map specificListeners;

    private List anyListeners;

    private Console console;

    private Map uriToWaiterMap_;
    // maps URIs to waiters of replies

    private SenderPort senderPort;

    protected AbstractTransport ( String name , String participantAddress ,
            String coordinatorAddress , int commitProtocol ,
            int transportProtocol , int format , long defaultTimeout )
    {
        this.name = name;
        this.participantAddress = participantAddress;
        this.coordinatorAddress = coordinatorAddress;
        this.commitProtocol = commitProtocol;
        this.transportProtocol = transportProtocol;
        this.format = format;
        this.defaultTimeout = defaultTimeout;
        // this.messageFactory = messageFactory;
        uriToWaiterMap_ = new HashMap ();
        specificListeners = new HashMap ();
        anyListeners = new ArrayList ();
    }

    private synchronized void addWaiter ( String uri , MessageWaiter waiter )
    {
        uriToWaiterMap_.put ( uri, waiter );
    }

    private synchronized void removeWaiter ( String uri )
    {
        uriToWaiterMap_.remove ( uri );
    }

    private synchronized MessageWaiter findWaiter ( String uri )
    {
        MessageWaiter ret = null;
        ret = (MessageWaiter) uriToWaiterMap_.get ( uri );
        return ret;
    }

    /**
     * Notification that a reply has come in. Subclasses should call this method
     * to notify any waiters.
     * 
     * @param reply
     *            The incoming reply message.
     */
    protected void replyReceived ( TransactionMessage reply )
    {
        Configuration.logInfo ( "Transport: received reply: " + reply );
        String uri = reply.getTargetURI ();
        MessageWaiter waiter = findWaiter ( uri );
        if ( waiter != null ) {
            waiter.messageReceived ( reply );
        } else
            Configuration.logDebug ( "Transport: target for reply not found: "
                    + uri );
        // if null: just ignore message
    }

    /**
     * Notification that a request has come in. Subclasses should call this
     * method to notify any message listeners.
     * 
     * @param req
     *            The incoming request message.
     */
    protected synchronized void requestReceived ( TransactionMessage req )
    {
        String key = new String ( "" + req.getMessageType () );
        List listeners = (List) specificListeners.get ( key );
        boolean keep = true;
        if ( listeners != null ) {
            Iterator it = listeners.iterator ();
            while ( it.hasNext () ) {

                MessageListener l = (MessageListener) it.next ();
                keep = l.messageReceived ( req, this );
                if ( !keep )
                    it.remove ();
            }
        }
        Iterator it = anyListeners.iterator ();
        while ( it.hasNext () ) {

            MessageListener l = (MessageListener) it.next ();
            keep = l.messageReceived ( req, this );
            if ( !keep )
                it.remove ();
        }
    }

    /**
     * @see com.atomikos.icatch.msg.Transport#getCommitProtocol()
     */
    public int getCommitProtocol ()
    {
        return commitProtocol;
    }

    /**
     * @see com.atomikos.icatch.msg.Transport#getFormat()
     */
    public int getFormat ()
    {
        return format;
    }

    public void setSenderPort ( SenderPort senderPort )
    {

        this.senderPort = senderPort;
    }

    /**
     * @see com.atomikos.icatch.msg.Transport#getTransportProtocol()
     */
    public int getTransportProtocol ()
    {
        return transportProtocol;
    }

    /**
     * @see com.atomikos.icatch.msg.Transport#getDefaultTimeout()
     */
    public long getDefaultTimeout ()
    {
        return defaultTimeout;
    }

    /**
     * @see com.atomikos.icatch.msg.Transport#sendAndReceive(com.atomikos.icatch.msg.TransactionMessage,
     *      long, int[])
     */

    public TransactionMessage sendAndReceive ( TransactionMessage msg ,
            long timeout , int[] expected ) throws TransportException,
            IllegalMessageTypeException
    {
        TransactionMessage ret = null;

        MessageWaiter waiter = new MessageWaiter ( expected );
        // FIRST add waiter to make sure that early replies
        // are not lost (reply can come before this method
        // terminates!
        addWaiter ( msg.getSenderURI (), waiter );
        try {
            send ( msg );
            Configuration
                    .logDebug ( "Transport: about to wait for reply during at most "
                            + timeout + " millis..." );
            ret = waiter.waitForReply ( timeout );
        } catch ( TransportException e ) {
            Configuration.logWarning ( "Transport: error sending message", e );
            throw e;
        } catch ( RuntimeException e ) {
            Configuration.logWarning ( "Transport: error sending message", e );
            throw e;
        } catch ( Throwable e ) {
        	Configuration.logWarning ( "Transport: unexpected throwable" , e );
        	throw new RuntimeException ( e );
        } finally {
            if ( ret == null )
                Configuration
                        .logDebug ( "Transport: wait timed out, no reply received." );

            removeWaiter ( msg.getSenderURI () );
        }

        return ret;
    }

    /**
     * @see com.atomikos.icatch.msg.Transport#registerMessageListener(com.atomikos.icatch.msg.MessageListener)
     */

    public synchronized void registerMessageListener ( MessageListener listener )
            throws TransportException
    {
        if ( ! anyListeners.contains ( listener ) ) anyListeners.add ( listener );

    }

    /**
     * @see com.atomikos.icatch.msg.Transport#registerMessageListener(com.atomikos.icatch.msg.MessageListener,
     *      int)
     */

    public synchronized void registerMessageListener (
            MessageListener listener , int messageType )
            throws TransportException
    {

        String key = new String ( "" + messageType );
        List v = null;
        if ( specificListeners.containsKey ( key ) ) {
            v = (List) specificListeners.get ( key );
        } else {
            v = new ArrayList ();
        }
        if ( ! v.contains ( listener ) ) v.add ( listener );
        specificListeners.put ( key, v );

    }

    /**
     * @see com.atomikos.icatch.msg.Transport#removeMessageListener(com.atomikos.icatch.msg.MessageListener)
     */

    public synchronized void removeMessageListener ( MessageListener listener )
            throws TransportException
    {
        anyListeners.remove ( listener );
        Iterator it = specificListeners.keySet ().iterator ();
        while ( it.hasNext () ) {
            Object nxt = it.next ();
            List v = (List) specificListeners.get ( nxt );
            v.remove ( nxt );
        }
    }

    /**
     * @see com.atomikos.datasource.RecoverableResource#recover(com.atomikos.icatch.Participant)
     */
    public boolean recover ( Participant participant ) throws ResourceException
    {

        boolean ret = false;

        if ( participant instanceof MessageParticipant ) {
            MessageParticipant mp = (MessageParticipant) participant;
            ret = ((mp.getCommitProtocol () == getCommitProtocol ())
                    && (mp.getFormat () == getFormat ()) && (mp
                    .getTransportProtocol () == getTransportProtocol ()));
        }

        return ret;
    }

    /**
     * @see com.atomikos.datasource.RecoverableResource#endRecovery()
     */

    public void endRecovery () throws ResourceException
    {

    }

    /**
     * @see com.atomikos.datasource.RecoverableResource#close()
     */

    public void close () throws ResourceException
    {
    }

    /**
     * @see com.atomikos.datasource.RecoverableResource#getName()
     */

    public String getName ()
    {
        return name;
    }

    /**
     * @see com.atomikos.datasource.RecoverableResource#isSameRM(com.atomikos.datasource.RecoverableResource)
     */

    public boolean isSameRM ( RecoverableResource res )
            throws ResourceException
    {
        boolean ret = false;
        if ( res instanceof Transport ) {
            Transport other = (Transport) res;
            ret = other.getName ().equals ( getName () );
        }

        return ret;
    }

    /**
     * Create a new commit message.
     */

    public CommitMessage createCommitMessage ( boolean onephase ,
            String senderURI , String targetURI , Object targetAddress )
    {
        return new CommitMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getCoordinatorAddress (), senderURI,
                onephase );
    }

    /**
     * Create a new error message.
     */

    public ErrorMessage createErrorMessage ( int code , String senderURI ,
            String targetURI , Object targetAddress )
    {
        return new ErrorMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getParticipantAddress (), senderURI,
                code );
    }

    /**
     * Create a new forget message.
     */

    public ForgetMessage createForgetMessage ( String senderURI ,
            String targetURI , Object targetAddress )
    {
        return new ForgetMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getCoordinatorAddress (), senderURI );
    }

    /**
     * Create a new prepare message.
     */

    public PrepareMessage createPrepareMessage ( String senderURI ,
            String targetURI , Object targetAddress )
    {
        return new PrepareMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getCoordinatorAddress (), senderURI );
    }

    /**
     * Create a new prepare message.
     */

    public PrepareMessage createPrepareMessage ( int globalSiblingCount ,
            CascadeInfo[] cascadeInfo , String senderURI , String targetURI ,
            Object targetAddress )
    {
        return new PrepareMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getCoordinatorAddress (), senderURI,
                globalSiblingCount, cascadeInfo );
    }

    /**
     * Create a new prepared message. The option default_rollback is not used by
     * our software, so we do not allow setting it to other than the default
     * value.
     */

    public PreparedMessage createPreparedMessage ( boolean readonly ,
            String senderURI , String targetURI , Object targetAddress )
    {
        return new PreparedMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getParticipantAddress (), senderURI,
                readonly, false );
    }

    /**
     * Create a new replay message.
     */

    public ReplayMessage createReplayMessage ( String senderURI ,
            String targetURI , Object targetAddress )
    {

        return new ReplayMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getParticipantAddress (), senderURI );
    }

    /**
     * Create a new rollback message.
     */

    public RollbackMessage createRollbackMessage ( String senderURI ,
            String targetURI , Object targetAddress )
    {
        return new RollbackMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getCoordinatorAddress (), senderURI );
    }

    /**
     * Create a new state message.
     */

    public StateMessage createStateMessage ( Boolean committed ,
            String senderURI , String targetURI , Object targetAddress )
    {
        return new StateMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getParticipantAddress (), senderURI,
                committed );
    }

    /**
     * Create a new registration message.
     */

    public RegisterMessage createRegisterMessage ( String senderURI ,
            String targetURI , Object targetAddress , boolean for2PC )
    {
        return new RegisterMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getParticipantAddress (), senderURI,
                for2PC );
    }

    /**
     * Create a new registration confirmation message.
     */
    public RegisteredMessage createRegisteredMessage ( String senderURI ,
            String targetURI , Object targetAddress )
    {
        return new RegisteredMessageImp ( getCommitProtocol (), getFormat (),
                targetAddress, targetURI, getCoordinatorAddress (), senderURI );
    }

    /**
     * @see com.atomikos.icatch.msg.AbstractTransport#getCoordinatorAddress()
     */
    public String getCoordinatorAddress ()
    {
        return this.coordinatorAddress;
    }

    /**
     * @see com.atomikos.icatch.msg.AbstractTransport#getParticipantAddress()
     */
    public String getParticipantAddress ()
    {
        return this.participantAddress;
    }

    public boolean isClosed ()
    {
        return false;
    }

    public void setRecoveryService ( RecoveryService recoveryService )
            throws ResourceException
    {

        // null during testing
        if ( recoveryService != null ) {

            recoveryService.recover ();
        }

        // DON'T call endRecovery here, since the TM
        // will do this (otherwise, only this resource
        // will know).
    }

    /**
     * @see com.atomikos.icatch.msg.Transport#send(com.atomikos.icatch.msg.TransactionMessage)
     */
    public void send ( TransactionMessage msg ) throws TransportException,
            IllegalMessageTypeException
    {
        if ( senderPort == null )
            throw new TransportException ( "No SenderPort set." );
        senderPort.send ( msg );

    }

}
