package com.atomikos.icatch.imp;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.SysException;

/**
 * 
 * 
 * The extent carries the information about the 'size' of a propagation after it
 * returns: the indirectly invoked servers, and the orphan detection information
 * for those.
 */

public class ExtentImp implements Extent
{
    private Hashtable participants_ = null;
    private boolean queried_ = false;
    // protected Participant participant_ = null;

    private Stack directs_;

    public ExtentImp ()
    {
        participants_ = new Hashtable ();
        directs_ = new Stack ();
    }

    public ExtentImp ( Hashtable participants , Stack directs )
    {
        participants_ = (Hashtable) participants.clone ();
        directs_ = (Stack) directs.clone ();
    }

    public void addRemoteParticipants ( Dictionary participants )
            throws IllegalStateException, SysException
    {
        if ( participants == null )
            return;
        Enumeration enumm = participants.keys ();
        while ( enumm.hasMoreElements () ) {
            String participant = (String) enumm.nextElement ();
            Integer count = (Integer) participants_.get ( participant );
            if ( count == null )
                count = new Integer ( 0 );

            Integer cnt = (Integer) participants.get ( participant );
            count = new Integer ( count.intValue () + cnt.intValue () );

            participants_.put ( participant, count );
            // NOTE: this will replace the old participant, and if
            // it is a proxy then the buffered heuristic msgs will
            // also be replaced. This loses info if multiple PARALLEL calls
            // went to the same FIRST-ORDER server
            // (i.e., directly invoked).
            // Never mind, though: it is considered bad practice
            // to execute parallel calls if they might act on the same
            // data. This is the case if they go to the same directly
            // invoked server.
        }
    }

    /**
     * @see Extent
     */

    public Hashtable getRemoteParticipants ()
    {
        queried_ = true;
        return (Hashtable) participants_.clone ();
    }

    /**
     * @see Extent
     */

    public Stack getParticipants ()
    {
        queried_ = true;
        return (Stack) directs_.clone ();
    }

    /**
     * @see Extent
     */

    public synchronized void add ( Participant participant , int count )
            throws SysException, IllegalStateException
    {
        Hashtable table = new Hashtable ();
        table.put ( participant.getURI (), new Integer ( count ) );
        addRemoteParticipants ( table );
        directs_.push ( participant );
        // participant_ = participant;
    }

    /**
     * @see Extent
     */

    public synchronized void add ( Extent extent )
            throws IllegalStateException, SysException
    {
        if ( queried_ )
            throw new IllegalStateException ( "Adding extent no longer allowed" );
        addRemoteParticipants ( extent.getRemoteParticipants () );
        Enumeration enumm = extent.getParticipants ().elements ();
        while ( enumm.hasMoreElements () ) {
            Participant part = (Participant) enumm.nextElement ();
            directs_.push ( part );
        }
    }

    // /**
    // *@see Extent
    // */
    //     
    // public synchronized Participant getParticipant()
    // throws SysException
    // {
    // return participant_;
    // }

}
