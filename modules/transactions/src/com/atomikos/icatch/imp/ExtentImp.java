//$Id: ExtentImp.java,v 1.2 2006/09/19 08:03:51 guy Exp $
//$Log: ExtentImp.java,v $
//Revision 1.2  2006/09/19 08:03:51  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:05  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:37  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:27  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:53  guy
//Import.
//
//Revision 1.2  2006/03/15 10:31:39  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:08  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.8  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.7  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.6  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.5  2001/11/06 14:26:40  guy
//Made addRemoteParticipants public, for easier reuse of this class in JTS.
//
//Revision 1.4  2001/11/01 08:41:44  guy
//Changed Extent and ExtentImp to include DIRECT participants.
//Changed CompositeTransactionImp to include this effect.
//
//Revision 1.3  2001/10/29 11:48:18  guy
//Removed obsolete method: getParticipant
//
//Revision 1.2  2001/10/29 11:09:04  guy
//Added comment to Extent.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.1  2001/03/23 17:01:59  pardon
//Added some files to repository.
//

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
