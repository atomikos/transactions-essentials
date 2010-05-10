//$Id: AdminTransaction.java,v 1.1.1.1 2006/08/29 10:01:08 guy Exp $
//$Log: AdminTransaction.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:08  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:23  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.5  2005/08/05 15:03:34  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.4  2004/10/12 13:03:30  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/08/30 07:16:28  guy
//Added admin tx state: active.
//
//Revision 1.2  2002/02/22 09:02:44  guy
//Corrected doc comments.
//
//Revision 1.1  2002/01/23 11:39:42  guy
//Added admin package to CVS.
//

package com.atomikos.icatch.admin;
import java.io.Serializable;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.SysException;

 /**
  *
  *
  *An administrator interface for a transaction.
  *Allows inspection of heuristic info, as well as forced 2PC methods.
  */

public interface AdminTransaction extends Serializable
{
	 public static final int STATE_ACTIVE = -3;
	 
	 public static final int STATE_PREPARING = -2;
  
     public static final int STATE_UNKNOWN = -1;
     
     public static final int STATE_PREPARED = 0;
     
     public static final int STATE_HEUR_COMMITTED = 1;
     
     public static final int STATE_HEUR_ABORTED = 2;
     
     public static final int STATE_HEUR_HAZARD = 3;
     
     public static final int STATE_HEUR_MIXED = 4;
     
     public static final int STATE_ABORTING = 5;
     
     public static final int STATE_COMMITTING = 6;
     
     public static final int STATE_TERMINATED = 7;
     

      
       /**
        *Get the transaction identifier.
        *
        *@return String The unique id.
        */
        
      public String getTid();
      
       /**
        *Get the transaction's state.
        *
        *@return int The state, one of the predefined states.
        *NOTE: the state is an int rather than the generic Object,
        *because instances need to be Serializable.
        */
      
      public int getState();
      
       /**
        *Get the high-level heuristic comments.
        *This is what remote clients will see as well.
        *@return HeuristicMessage The comments giving a summary
        *of the tasks done in this transaction.
        */
        
      public HeuristicMessage[] getTags();
      
       /**
        *Get the HeuristicMessage detailed info for this transaction.
        *@return HeuristicMessage[] The detailed heuristic messages.
        *These show the comments for EACH individual resource 
        *that was part of the transaction.
        */
      
      public HeuristicMessage[] getHeuristicMessages();
        
        /**
         *Get the heuristic messages for work in the given state.
         *This method is useful in particular for STATE_HEUR_MIXED
         *and STATE_HEUR_HAZARD.
         *
         *@return HeuristicMessage[] The description of all work in 
         *the given state, or null if no such work exists.
         */
         
      public HeuristicMessage[] getHeuristicMessages ( int state );
      
       /**
        *Test if the transaction's 2PC outcome was commit.
        *Needed especially for the heuristic states, if the
        *desired outcome (instead of the actual state) needs
        *to be retrieved. For instance, if the state is STATE_HEUR_HAZARD
        *then extra information is needed for determining if the desired
        *outcome was commit or rollback. This method helps here.
        *
        *
        *@return boolean True iff commit decided (either heuristically
        *or by the super coordinator).
        */
      
      public boolean wasCommitted();
      
      
      /**
       *Force commit of the transaction.
       *
       *@exception HeurRollbackException If rolled back in the meantime.
       *
       *@exception HeurMixedException If part of it was rolled back.
       *@exception HeurHazardException On possible conflicts.
       *@exception SysException Unexpected failure.
       */


      public void forceCommit()
      throws HeurRollbackException,
             HeurHazardException,
             HeurMixedException,
             SysException;

      
      /**
       *Force rollback of the transaction.
       *
       *@exception HeurCommitException If heuristically committed in 
       *the meantime.
       *@exception HeurHazardException If the state is not certain.
       *@exception  HeurMixedException If partially rolled back.
       *@exception SysException Unexpected failure.
       */

      
      public void forceRollback() 
      throws HeurCommitException,
             HeurMixedException,
             HeurHazardException,
             SysException;
      
        /**
         *Force the system to forget about the transaction.
         */
         
      public void forceForget();
       
}
