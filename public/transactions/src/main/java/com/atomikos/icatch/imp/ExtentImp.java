/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch.imp;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
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

	private Hashtable<String,Integer> participants_ = null;
    private boolean queried_ = false;

    private Stack<Participant> directs_;

    public ExtentImp ()
    {
        participants_ = new Hashtable<String,Integer> ();
        directs_ = new Stack<Participant> ();
    }

    public ExtentImp ( Hashtable<String,Integer> participants , Stack<Participant> directs )
    {
        participants_ = (Hashtable<String,Integer>) participants.clone ();
        directs_ = (Stack<Participant>) directs.clone ();
    }

    public void addRemoteParticipants ( Dictionary<String,Integer> participants )
            throws IllegalStateException, SysException
    {
        if ( participants == null )
            return;
        Enumeration<String> enumm = participants.keys ();
        while ( enumm.hasMoreElements () ) {
            String participant =  enumm.nextElement ();
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

    public Hashtable<String,Integer> getRemoteParticipants ()
    {
        queried_ = true;
        return (Hashtable<String,Integer>) participants_.clone ();
    }

    /**
     * @see Extent
     */

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
