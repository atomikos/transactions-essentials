package com.atomikos.icatch.trmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Dictionary;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SysException;

/**
 *Copyright &copy; 2001, Atomikos. All rights reserved.
 *
 *A participant server maps 2PC calls to the proper 
 *Participant instance.
 *NOTE: implementations should also implement the 
 *<code>equals</code> and <code>hashCode</code> methods correctly!
 */

public interface ParticipantServer extends Remote
{

    /**
     *Prepare the participant of the given root.
     *
     *@param root The root String of the participant.
     *@param cascadelist The list of participants to cascade to, if any.
     *@param siblings The count of siblings for the given participant.
     *
     *@see Participant For more information on the exceptions and return value.
     */

    public int prepare ( String root , int siblings  , Dictionary cascadelist )
        throws RollbackException ,
	     HeurHazardException ,
	     HeurMixedException,
	     SysException,
	     RemoteException ;
    
    /**
    *Commit the participant of the given root, after prepare was sent.
     *
     *@param root The String of the given root.
     *
     *@see Participant For more info on the exceptions and return value.
     */

    public HeuristicMessage[] commit ( String root )
        throws HeurRollbackException,
	     HeurHazardException,
	     HeurMixedException,
             RollbackException,
	     SysException,
	     RemoteException ;

    /**
     *Perform one-phase commit for the given root.
     *@param root The root id.
     *@param siblings The count of siblings for the given participant.
     *@param cascadeList The info needed for orphan detection.
     */
    public HeuristicMessage[] commitOnePhase ( String root , int siblings , Dictionary cascadeList )
        throws HeurRollbackException,
        HeurHazardException,
        HeurMixedException,
        RollbackException,
        SysException,
        RemoteException ;

    /**
     *Rollback the participant for the given root.
     *
     *@param root The given root.
     *
     *@see Participant for more exception info.
     */

    public HeuristicMessage[] rollback ( String root )
        throws HeurCommitException,
	     HeurMixedException,
	     HeurHazardException,
	     SysException,
	     RemoteException ;
    

    /**
     *Forget all about a given root.
     *
     *@param root The root to forget about.
     */

    public void forget ( String root ) throws SysException,RemoteException;

 
    
}
