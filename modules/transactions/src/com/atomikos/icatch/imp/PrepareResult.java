//$Id: PrepareResult.java,v 1.2 2006/09/19 08:03:52 guy Exp $
//$Log: PrepareResult.java,v $
//Revision 1.2  2006/09/19 08:03:52  guy
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
//Revision 1.2  2006/03/15 10:31:40  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:09  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.4  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.3  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//
//Revision 1.4  2001/03/07 18:57:36  pardon
//Continued on CoordinatorImp.
//
//Revision 1.3  2001/03/06 19:05:39  pardon
//Adapted heuristic message passing and completed Participant implementation.
//
//Revision 1.2  2001/03/05 19:14:39  pardon
//Continued working on 2pc messaging.
//
//Revision 1.1  2001/03/01 19:26:57  pardon
//Added more.
//

package com.atomikos.icatch.imp;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;

/**
 * 
 * 
 * A result for prepare messages.
 */

class PrepareResult extends Result
{
    protected Hashtable readonlytable_ = new Hashtable ();
    // for read only voters
    protected Hashtable indoubts_ = new Hashtable ();
    // for indoubt participants
    // should be rolled back in case of failure!

    protected Hashtable heuristics_ = new Hashtable ();

    protected boolean analyzed_ = false;

    protected Vector msgvector_ = new Vector ();

    /**
     * Constructor.
     * 
     * @param count
     *            The number of replies to deal with.
     */

    public PrepareResult ( int count )
    {
        super ( count );
    }

    protected synchronized void analyze () throws IllegalStateException,
            InterruptedException
    {
        if ( analyzed_ )
            return;

        boolean allReadOnly = true;
        boolean allYes = true;
        boolean heurmixed = false;
        boolean heurhazards = false;
        boolean heurcommits = false;
        Stack replies = getReplies ();
        Enumeration enumm = replies.elements ();

        while ( enumm.hasMoreElements () ) {
            boolean yes = false;
            boolean readonly = false;

            Reply reply = (Reply) enumm.nextElement ();

            if ( reply.hasFailed () ) {
                yes = false;
                readonly = false;

                Exception err = reply.getException ();
                if ( err instanceof HeurMixedException ) {
                    heurmixed = true;
                    HeurMixedException hm = (HeurMixedException) err;
                    addMessages ( hm.getHeuristicMessages () );
                } else if ( err instanceof HeurCommitException ) {
                    heurcommits = true;
                    HeurCommitException hc = (HeurCommitException) err;
                    addMessages ( hc.getHeuristicMessages () );
                    heurmixed = (heurmixed || heurhazards);
                } else if ( err instanceof HeurHazardException ) {
                    heurhazards = true;
                    heurmixed = (heurmixed || heurcommits);
                    HeurHazardException hr = (HeurHazardException) err;
                    indoubts_.put ( reply.getParticipant (),
                            new Boolean ( true ) );
                    // REMEMBER: might be indoubt, so HAS to be notified
                    // during rollback!
                    addMessages ( hr.getHeuristicMessages () );
                }

            }// if failed

            else {
                readonly = (reply.getResponse () == null);
                Boolean answer = new Boolean ( false );
                if ( !readonly ) {
                    answer = (Boolean) reply.getResponse ();
                }
                yes = (readonly || answer.booleanValue ());

                // if readonly: remember this fact for logging and second phase
                if ( readonly )
                    readonlytable_.put ( reply.getParticipant (), new Boolean (
                            true ) );
                else
                    indoubts_.put ( reply.getParticipant (),
                            new Boolean ( true ) );
            }

            allYes = (allYes && yes);
            allReadOnly = (allReadOnly && readonly);

        }

        if ( heurmixed )
            result_ = HEUR_MIXED;
        else if ( heurcommits )
            result_ = HEUR_COMMIT;
        else if ( heurhazards )
            result_ = HEUR_HAZARD;
        else if ( allReadOnly )
            result_ = ALL_READONLY;
        else if ( allYes )
            result_ = ALL_OK;

        analyzed_ = true;
    }

    /**
     * Test if all answers represent a yes vote. Blocks until all results
     * arrived.
     * 
     * @return boolean True if all are yes, false if not.
     * @exception InterruptedException
     *                If interrupt on wait.
     */

    public boolean allYes () throws InterruptedException
    {
        analyze ();
        return (result_ == ALL_OK || result_ == ALL_READONLY);

    }

    /**
     * Test if all answers were readonly votes. Blocks till all results known.
     * 
     * @return boolean True if all readonly, false otherwise.
     * @exception InterruptedException
     *                If interrupted.
     */

    public boolean allReadOnly () throws InterruptedException
    {
        analyze ();
        return (result_ == ALL_READONLY);
    }

    /**
     * Get a table of readonly voting participants.
     * 
     * @return Hashtable Contains a key per readonly participant.
     * @exception InterruptedException
     *                If interrupted on wait.
     */

    public Hashtable getReadOnlyTable () throws InterruptedException
    {
        analyze ();
        return readonlytable_;
    }

    /**
     * Get a table of indoubt participants, which have to be notified of commit
     * or rollback.
     * 
     * @return Hashtable A key per indoubt participant.
     * @exception InterruptedException
     *                If interrupted on wait.
     */

    public Hashtable getIndoubtTable () throws InterruptedException
    {
        analyze ();
        return indoubts_;
    }

}
