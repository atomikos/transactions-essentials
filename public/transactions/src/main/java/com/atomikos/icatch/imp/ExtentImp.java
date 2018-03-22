/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.SysException;

/**
 * The extent carries the information about the 'size' of a propagation after it
 * returns: the indirectly invoked servers, and the orphan detection information
 * for those.
 */

public class ExtentImp implements Extent
{

	private static final long serialVersionUID = -1010453448007350422L;

	private Map<String, Integer> participants_ = null;
    private boolean queried_ = false;

    private Stack<Participant> directs_;

    public ExtentImp ()
    {
        participants_ = new Hashtable<String,Integer> ();
        directs_ = new Stack<Participant> ();
    }

    public ExtentImp ( Map<String, Integer> map , Stack<Participant> directs )
    {
        participants_ = new HashMap<String,Integer>(map);
        directs_ = new Stack<Participant>();
        directs_.addAll(directs);
    }

    public void addRemoteParticipants ( Map<String,Integer> participants )
            throws IllegalStateException, SysException
    {
        if ( participants == null )
            return;
        Set<String> enumm = participants.keySet();
        for ( String participant : enumm) {
            Integer count =  participants_.get ( participant );
            if ( count == null )
                count = Integer.valueOf( 0 );

            Integer cnt =  participants.get ( participant );
            count =  count.intValue () + cnt.intValue () ;

            participants_.put ( participant, count );
            // NOTE: this will replace the old participant, and if
            // it is a proxy then the buffered heuristic msgs will
            // also be replaced. This loses info if multiple PARALLEL calls
            // went to the same FIRST-ORDER server (i.e., directly invoked).
            // Never mind, though: it is considered bad practice
            // to execute parallel calls if they might act on the same
            // data. This is the case if they go to the same directly
            // invoked server.
        }
    }

    /**
     * @see Extent
     */

    public Map<String, Integer> getRemoteParticipants ()
    {
        queried_ = true;
        return new HashMap<String, Integer>(participants_);
    }

    /**
     * @see Extent
     */
    @SuppressWarnings("unchecked")
    public Stack<Participant> getParticipants ()
    {
        queried_ = true;
        return (Stack<Participant>) directs_.clone ();
    }

    /**
     * @see Extent
     */

    public synchronized void add ( Participant participant , int count )
            throws SysException, IllegalStateException
    {
        Hashtable<String,Integer> table = new Hashtable<String,Integer> ();
        table.put ( participant.getURI (), new Integer ( count ) );
        addRemoteParticipants ( table );
        directs_.push ( participant );
    }

    /**
     * @see Extent
     */

    public synchronized void add ( Extent extent )
            throws IllegalStateException, SysException
    {
        if ( queried_ )  throw new IllegalStateException ( "Adding extent no longer allowed" );
        addRemoteParticipants ( extent.getRemoteParticipants () );
        Enumeration<Participant> enumm = extent.getParticipants ().elements ();
        while ( enumm.hasMoreElements () ) {
            Participant part =  enumm.nextElement ();
            directs_.push ( part );
        }
    }


}
